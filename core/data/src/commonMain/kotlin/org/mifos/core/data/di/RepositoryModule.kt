/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See See https://github.com/openMF/kmp-project-template/blob/main/LICENSE
 */
package org.mifos.core.data.di

import io.github.mobilebytelabs.kmptoolkit.networkmonitor.NetworkMonitorProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module
import org.mifos.core.base.common.di.CommonModule
import org.mifos.core.base.store.infra.FetchedAtRepository
import org.mifos.core.base.store.submit.OfflineSubmitSyncer
import org.mifos.core.base.store.submit.SubmitOutbox
import org.mifos.core.data.alerts.AlertsRepository
import org.mifos.core.data.alerts.impl.AlertsRepositoryImpl
import org.mifos.core.data.auth.AuthenticationRepository
import org.mifos.core.data.auth.impl.AuthenticationRepositoryImpl
import org.mifos.core.data.banking.BillReminderRepository
import org.mifos.core.data.banking.LoanRepository
import org.mifos.core.data.banking.impl.BillReminderRepositoryImpl
import org.mifos.core.data.banking.impl.LoanRepositoryImpl
import org.mifos.core.data.client.ClientRepository
import org.mifos.core.data.client.impl.ClientRepositoryImpl
import org.mifos.core.data.group.GroupRepository
import org.mifos.core.data.group.impl.GroupRepositoryImpl
import org.mifos.core.data.infra.NetworkMonitor
import org.mifos.core.data.infra.impl.RoomFetchedAtRepository
import org.mifos.core.data.infra.impl.RoomSubmitOutbox
import org.mifos.core.data.user.UserDataRepository
import org.mifos.core.data.user.UserLogoutManager
import org.mifos.core.data.user.impl.UserDataRepositoryImpl
import org.mifos.core.data.user.impl.UserLogoutManagerImpl
import org.mifos.core.data.watchlist.WatchlistRepository
import org.mifos.core.data.watchlist.impl.WatchlistRepositoryImpl
import org.mifos.core.database.AppDatabase
import org.mifos.core.database.di.DatabaseModule
import org.mifos.core.datastore.di.DatastoreModule
import org.mifos.core.model.alerts.PriceAlert
import org.mifos.core.model.banking.BillReminder
import org.mifos.core.model.banking.Loan
import org.mifos.core.model.banking.LoanCalcScenario
import org.mifos.core.network.di.NetworkModule
import org.mifos.core.store.AppStoreRegistry

val DataModule = module {
    includes(platformModule, CommonModule, DatabaseModule, DatastoreModule, NetworkModule)

    single<NetworkMonitor> { NetworkMonitorProvider.install() }
    singleOf(::UserDataRepositoryImpl) bind UserDataRepository::class

    // Framework FetchedAtRepository — durable lastFetchedAt persistence backing
    // DataFreshnessIndicator timestamps. Room-only by design (no in-memory fallback).
    single<FetchedAtRepository> { RoomFetchedAtRepository(get<AppDatabase>().fetchedAtDao) }

    // Framework DraftDao — backing store for SubmitOutbox / DraftSubmitHandler
    single { get<AppDatabase>().draftDao }

    // Personal watchlist — local-only persistence for the SubmitHandler showcase.
    single { get<org.mifos.core.database.AppDatabase>().watchlistDao }
    single<WatchlistRepository> { WatchlistRepositoryImpl(get()) }

    // Banking domain — purely local Loan + Bill Reminder persistence.
    // No remote sync; the DraftSubmitHandler outboxes below give the UX
    // polish (saving badge, retry on failure) for a local commit "submit".
    single { get<AppDatabase>().loanDao }
    single { get<AppDatabase>().billReminderDao }
    single<LoanRepository> {
        LoanRepositoryImpl(
            loansStore = get(AppStoreRegistry.Loans),
            loanDao = get(),
        )
    }
    single<BillReminderRepository> {
        BillReminderRepositoryImpl(
            billRemindersStore = get(AppStoreRegistry.BillReminders),
            billReminderDao = get(),
        )
    }

    // Outboxes — each form payload type gets its own RoomSubmitOutbox so
    // formKey collisions across features are impossible. The "submit" target
    // for both is the local repository's `upsert`, simulating remote sync.
    // All four SubmitOutbox bindings MUST declare their qualifier — Koin matches
    // single<> definitions by raw type (SubmitOutbox::class), not full KType, so
    // multiple SubmitOutbox<*> bindings collide and the last one wins regardless of
    // the generic parameter. See `OutboxQualifiers` KDoc for full background.
    single<SubmitOutbox<Loan>>(qualifier = OutboxQualifiers.Loan) {
        RoomSubmitOutbox(dao = get(), serializer = Loan.serializer())
    }
    single<SubmitOutbox<BillReminder>>(qualifier = OutboxQualifiers.BillReminder) {
        RoomSubmitOutbox(dao = get(), serializer = BillReminder.serializer())
    }
    single<SubmitOutbox<LoanCalcScenario>>(qualifier = OutboxQualifiers.LoanCalcScenario) {
        RoomSubmitOutbox(dao = get(), serializer = LoanCalcScenario.serializer())
    }

    // OfflineSubmitSyncer eagerly retries pending drafts when connectivity
    // returns. For purely-local features (no real network), the submitBlock
    // commits to the repository directly — `networkStatusFlow` is still
    // required by the syncer contract.
    //
    // We register each syncer behind a unique marker singleton so Koin's
    // type resolution doesn't collide with other `OfflineSubmitSyncer<*, *>`
    // bindings (PriceAlert below uses the same pattern by virtue of being
    // declared as the bare `OfflineSubmitSyncer` type — see note there).
    single<LoanSubmitSyncer>(createdAtStart = true) {
        LoanSubmitSyncer(
            syncer = OfflineSubmitSyncer<Loan, Loan>(
                scope = get(),
                outbox = get(qualifier = OutboxQualifiers.Loan),
                networkStatusFlow = get<NetworkMonitor>().networkStatus,
                submitBlock = { payload ->
                    get<LoanRepository>().upsert(payload)
                    payload
                },
            ).also { it.start() },
        )
    }
    single<BillReminderSubmitSyncer>(createdAtStart = true) {
        BillReminderSubmitSyncer(
            syncer = OfflineSubmitSyncer<BillReminder, BillReminder>(
                scope = get(),
                outbox = get(qualifier = OutboxQualifiers.BillReminder),
                networkStatusFlow = get<NetworkMonitor>().networkStatus,
                submitBlock = { payload ->
                    get<BillReminderRepository>().upsert(payload)
                    payload
                },
            ).also { it.start() },
        )
    }

    single<UserLogoutManager> { UserLogoutManagerImpl(get(), get(), get()) }

    // Price alerts — Store-backed (OFFLINE_LOCAL_ONLY archetype).
    // AlertsStore is the source of truth; AlertDao is the write target.
    single { get<AppDatabase>().alertDao }
    single<AlertsRepository> {
        AlertsRepositoryImpl(
            alertsStore = get(AppStoreRegistry.Alerts),
            alertDao = get(),
        )
    }

    // Outbox for PriceAlert payloads — RoomSubmitOutbox writes to framework_submit_drafts.
    single<SubmitOutbox<PriceAlert>>(qualifier = OutboxQualifiers.PriceAlert) {
        RoomSubmitOutbox(dao = get(), serializer = PriceAlert.serializer())
    }

    // App-scoped CoroutineScope for cross-VM long-running coroutines (e.g., OfflineSubmitSyncer).
    single<CoroutineScope> { CoroutineScope(SupervisorJob() + Dispatchers.Default) }

    // Eager singleton — starts watching network online events at Koin start; retries
    // any pending alerts when connectivity returns. For the local-only alerts feature,
    // the submit block commits directly to the repository (simulating offline resilience).
    single(createdAtStart = true) {
        val syncer = OfflineSubmitSyncer<PriceAlert, PriceAlert>(
            scope = get(),
            outbox = get(qualifier = OutboxQualifiers.PriceAlert),
            networkStatusFlow = get<NetworkMonitor>().networkStatus,
            submitBlock = { payload -> get<AlertsRepository>().submitAlert(payload) },
        )
        syncer.start()
        syncer
    }

    single {
        AuthenticationRepositoryImpl(
            get(),
            get(),
            get(),
        )
    } bind AuthenticationRepository::class

    single {
        GroupRepositoryImpl(
            dataManager = get(),
            networkMonitor = get(),
            dispatcher = get(),
        )
    } bind GroupRepository::class

    single {
        ClientRepositoryImpl(
            dataManager = get(),
            networkMonitor = get(),
            dispatcher = get(),
        )
    } bind ClientRepository::class
}

expect val platformModule: Module

/**
 * Marker singleton wrapping the Loan offline submit syncer.
 *
 * Exists so Koin can resolve the binding by a unique type — bare
 * `OfflineSubmitSyncer<*, *>` would erase to the same runtime [kotlin.reflect.KClass]
 * across every payload type and collide with the PriceAlert syncer.
 */
internal class LoanSubmitSyncer internal constructor(
    @Suppress("unused") val syncer: OfflineSubmitSyncer<Loan, Loan>,
)

/** Marker singleton wrapping the BillReminder offline submit syncer. */
internal class BillReminderSubmitSyncer internal constructor(
    @Suppress("unused") val syncer: OfflineSubmitSyncer<BillReminder, BillReminder>,
)
