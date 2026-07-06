/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See See https://github.com/openMF/kmp-project-template/blob/main/LICENSE
 */
package org.mifos.core.store.di

import org.koin.core.module.Module
import org.koin.dsl.module
import org.mifos.core.store.AppStoreRegistry
import org.mifos.core.store.alerts.impl.provideAlertsStore
import org.mifos.core.store.infra.StoreCacheManager
import org.mifos.core.store.infra.impl.StoreCacheManagerImpl

/**
 * Koin module for app-level Store wiring.
 *
 * Forks register their `Store` instances here, qualifier-bound via [AppStoreRegistry].
 * The 4 demo stores ship as forkable examples — add your own `single(qualifier = ...)`
 * blocks next to them.
 *
 * Wire into the Koin start-up:
 * ```kotlin
 * startKoin {
 *     modules(appStoreModule, /* ...other modules */)
 * }
 * ```
 */
val appStoreModule: Module = module {
    // Store cache manager — clears all registered caches on logout (registration-based)
    single<StoreCacheManager> {
        StoreCacheManagerImpl(
            bookkeeperDao = get(),
            draftDao = get(),
        )
    }

    // Banking Utility Toolkit — offline-local stores (OFFLINE_LOCAL_ONLY archetype)
    single(AppStoreRegistry.Alerts) {
        provideAlertsStore(dao = get())
    }
}
