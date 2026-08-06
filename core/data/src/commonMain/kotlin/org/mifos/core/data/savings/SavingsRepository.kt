/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See See https://github.com/openMF/kmp-project-template/blob/main/LICENSE
 */
package org.mifos.core.data.savings

import kotlinx.coroutines.flow.Flow
import org.mifos.core.base.store.screen.ScreenState
import org.mifos.core.model.savings.CreateSavingAccountRequest
import org.mifos.core.model.savings.SavingDetail
import org.mifos.core.model.savings.SavingInterestDetail
import org.mifos.core.model.savings.SavingsAccountTemplate
import org.mifos.core.model.savings.SavingsTransactionTemplate

interface SavingsRepository {
    fun getSavingDetails(accountId: Long): Flow<ScreenState<SavingDetail>>

    fun calculateSavingInterest(accountId: Long): Flow<ScreenState<SavingInterestDetail>>

    suspend fun approveSaving(
        savingsId: Long,
        approvedOnDate: String,
        dateFormat: String,
        locale: String,
    ): ScreenState<Unit>

    fun getSavingsTransactionTemplate(accountId: Long): Flow<ScreenState<SavingsTransactionTemplate>>

    suspend fun depositTransaction(
        accountId: Long,
        locale: String,
        dateFormat: String,
        transactionDate: String,
        transactionAmount: String,
        paymentTypeId: String,
        accountNumber: String?,
        checkNumber: String?,
        routingCode: String?,
        receiptNumber: String?,
        bankNumber: String?,
    ): ScreenState<Unit>

    suspend fun withdrawTransaction(
        accountId: Long,
        locale: String,
        dateFormat: String,
        transactionDate: String,
        transactionAmount: String,
        paymentTypeId: String,
        accountNumber: String?,
        checkNumber: String?,
        routingCode: String?,
        receiptNumber: String?,
        bankNumber: String?,
    ): ScreenState<Unit>

    suspend fun activateSaving(
        savingsId: Long,
        activatedOnDate: String,
        dateFormat: String,
        locale: String,
    ): ScreenState<Unit>

    fun getSavingsAccountTemplate(): Flow<ScreenState<SavingsAccountTemplate>>

    suspend fun createSavingsAccount(
        createSavingAccountRequest: CreateSavingAccountRequest,
    ): ScreenState<Unit>
}
