/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See See https://github.com/openMF/kmp-project-template/blob/main/LICENSE
 */
package org.mifos.core.data.loans.impl

import io.ktor.http.isSuccess
import kotlinx.coroutines.flow.Flow
import org.mifos.core.base.common.manager.DispatcherManager
import org.mifos.core.base.store.screen.ScreenState
import org.mifos.core.data.infra.NetworkMonitor
import org.mifos.core.data.loans.LoansRepository
import org.mifos.core.data.mapper.loans.toModel
import org.mifos.core.data.util.asScreenStateFlow
import org.mifos.core.data.util.extractErrorMessage
import org.mifos.core.data.util.runAsDataState
import org.mifos.core.model.loans.LoanDetail
import org.mifos.core.model.loans.LoanTransaction
import org.mifos.core.network.DataManager
import org.mifos.core.network.fineract.loans.dto.ApproveLoanRequestDto

class LoansRepositoryImpl(
    private val dataManager: DataManager,
    private val networkMonitor: NetworkMonitor,
    private val dispatcher: DispatcherManager,
) : LoansRepository {

    override fun getLoanDetails(loanId: Long): Flow<ScreenState<LoanDetail>> {
        return dataManager.fineract.loansApi.getLoanDetails(loanId).asScreenStateFlow(
            networkMonitor = networkMonitor,
            dispatcher = dispatcher.io,
        ) { dto ->
            dto.toModel()
        }
    }

    override fun getLoanTransactionDetails(
        loanId: Long,
        transactionId: Long,
    ): Flow<ScreenState<LoanTransaction>> {
        return dataManager.fineract.loansApi.getLoanTransaction(loanId, transactionId).asScreenStateFlow(
            networkMonitor = networkMonitor,
            dispatcher = dispatcher.io,
        ) { dto ->
            dto.toModel()
        }
    }

    override suspend fun approveLoan(
        loanId: Long,
        approvedOnDate: String,
        expectedDisbursementDate: String?,
        note: String?,
        dateFormat: String,
        locale: String,
    ): ScreenState<Unit> {
        return runAsDataState(
            networkMonitor = networkMonitor,
            context = dispatcher.io,
        ) {
            val response = dataManager.fineract.loansApi.approveLoan(
                loanId = loanId,
                request = ApproveLoanRequestDto(
                    approvedOnDate = approvedOnDate,
                    expectedDisbursementDate = expectedDisbursementDate,
                    note = note,
                    dateFormat = dateFormat,
                    locale = locale,
                ),
            )
            if (!response.status.isSuccess()) {
                val errorMessage = extractErrorMessage(response)
                throw Exception(errorMessage)
            }
        }
    }
}
