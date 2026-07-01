/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See See https://github.com/openMF/kmp-project-template/blob/main/LICENSE
 */
package org.mifos.core.data.banking.impl

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.map
import org.mifos.core.base.database.invalidation.daoFlow
import org.mifos.core.base.database.invalidation.notifyingWrite
import org.mifos.core.data.banking.LoanRepository
import org.mifos.core.model.banking.Loan
import org.mobilenativefoundation.store.store5.Store
import org.mobilenativefoundation.store.store5.StoreReadRequest
import org.mobilenativefoundation.store.store5.StoreReadResponse

/**
 * Local-only impl of [LoanRepository].
 *
 * Writes are wrapped with [notifyingWrite] and direct-DAO `Flow` reads are wrapped with
 * [daoFlow] so the wasmJs target's long-lived collectors (e.g. the Home dashboard's
 * outstanding-balance tile) re-emit after writes even when Room 3 alpha05's async
 * InvalidationTracker fails to fan out. On Android/Desktop/iOS the wraps are a
 * microsecond-cost no-op alongside Room's native invalidation.
 *
 * See `core-base/database/.../invalidation/README.md` for the rationale, integration
 * recipe, and removal plan.
 */
internal class LoanRepositoryImpl(
    private val loansStore: Store<Unit, List<org.mifos.core.database.banking.entity.LoanEntity>>,
    private val loanDao: org.mifos.core.database.banking.dao.LoanDao,
) : LoanRepository {

    override fun observeAll(): Flow<List<org.mifos.core.model.banking.Loan>> =
        loansStore.stream(StoreReadRequest.cached(Unit, refresh = false))
            .filterIsInstance<StoreReadResponse.Data<List<org.mifos.core.database.banking.entity.LoanEntity>>>()
            .map { response -> response.value.map { it.toDomain() } }

    override fun observeById(id: String): Flow<org.mifos.core.model.banking.Loan?> =
        daoFlow(LOANS_TABLE) { loanDao.observeById(id) }.map { it?.toDomain() }

    override suspend fun getById(id: String): org.mifos.core.model.banking.Loan? = loanDao.getById(id)?.toDomain()

    override suspend fun upsert(loan: org.mifos.core.model.banking.Loan) {
        notifyingWrite(LOANS_TABLE) {
            loanDao.upsert(loan.toEntity())
        }
    }

    override suspend fun delete(id: String) {
        notifyingWrite(LOANS_TABLE) {
            loanDao.deleteById(id)
        }
    }

    override fun observeTotalMonthlyEmi(): Flow<Double> =
        daoFlow(LOANS_TABLE) { loanDao.observeAll() }.map { rows ->
            rows.sumOf { it.monthlyPayment }
        }

    override fun observeTotalPrincipalRemaining(): Flow<Double> =
        daoFlow(LOANS_TABLE) { loanDao.observeAll() }.map { rows ->
            rows.sumOf { it.principalRemaining }
        }

    override fun observeCount(): Flow<Int> = daoFlow(LOANS_TABLE) { loanDao.count() }

    private companion object {
        /** Room `@Entity(tableName = …)` for [org.mifos.core.database.banking.entity.LoanEntity]. */
        const val LOANS_TABLE = "banking_loans"
    }
}

private fun org.mifos.core.database.banking.entity.LoanEntity.toDomain(): org.mifos.core.model.banking.Loan =
    _root_ide_package_.org.mifos.core.model.banking.Loan(
        id = id,
        name = name,
        kind = kind,
        principal = principal,
        principalRemaining = principalRemaining,
        annualRatePercent = annualRatePercent,
        tenureMonths = tenureMonths,
        monthsRemaining = monthsRemaining,
        monthlyPayment = monthlyPayment,
        nextDueDate = nextDueDate,
        totalPaid = totalPaid,
        createdAtMs = createdAtMs,
        updatedAtMs = updatedAtMs,
    )

private fun org.mifos.core.model.banking.Loan.toEntity(): org.mifos.core.database.banking.entity.LoanEntity =
    _root_ide_package_.org.mifos.core.database.banking.entity.LoanEntity(
        id = id,
        name = name,
        kind = kind,
        principal = principal,
        principalRemaining = principalRemaining,
        annualRatePercent = annualRatePercent,
        tenureMonths = tenureMonths,
        monthsRemaining = monthsRemaining,
        monthlyPayment = monthlyPayment,
        nextDueDate = nextDueDate,
        totalPaid = totalPaid,
        createdAtMs = createdAtMs,
        updatedAtMs = updatedAtMs,
    )
