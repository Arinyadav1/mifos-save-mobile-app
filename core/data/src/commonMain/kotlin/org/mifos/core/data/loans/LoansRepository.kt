/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See See https://github.com/openMF/kmp-project-template/blob/main/LICENSE
 */
package org.mifos.core.data.loans

import kotlinx.coroutines.flow.Flow
import org.mifos.core.base.store.screen.ScreenState
import org.mifos.core.model.loans.LoanDetail
import org.mifos.core.model.loans.LoanTransaction

interface LoansRepository {
    fun getLoanDetails(loanId: Long): Flow<ScreenState<LoanDetail>>

    fun getLoanTransactionDetails(loanId: Long, transactionId: Long): Flow<ScreenState<LoanTransaction>>

    suspend fun approveLoan(
        loanId: Long,
        approvedOnDate: String,
        expectedDisbursementDate: String?,
        note: String?,
        dateFormat: String,
        locale: String,
    ): ScreenState<Unit>

    suspend fun rejectLoan(
        loanId: Long,
        rejectedOnDate: String,
        note: String?,
        dateFormat: String,
        locale: String,
    ): ScreenState<Unit>

    suspend fun disburseLoan(
        loanId: Long,
        actualDisbursementDate: String,
        note: String?,
        dateFormat: String,
        locale: String,
    ): ScreenState<Unit>
}
