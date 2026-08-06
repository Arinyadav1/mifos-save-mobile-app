/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See See https://github.com/openMF/kmp-project-template/blob/main/LICENSE
 */
package org.mifos.core.data.savings.impl

import io.ktor.http.isSuccess
import kotlinx.coroutines.flow.Flow
import org.mifos.core.base.common.manager.DispatcherManager
import org.mifos.core.base.store.screen.ScreenState
import org.mifos.core.data.infra.NetworkMonitor
import org.mifos.core.data.mapper.savings.toDto
import org.mifos.core.data.mapper.savings.toModel
import org.mifos.core.data.savings.SavingsRepository
import org.mifos.core.data.util.asScreenStateFlow
import org.mifos.core.data.util.extractErrorMessage
import org.mifos.core.data.util.runAsDataState
import org.mifos.core.model.savings.CreateSavingAccountRequest
import org.mifos.core.model.savings.SavingDetail
import org.mifos.core.model.savings.SavingInterestDetail
import org.mifos.core.model.savings.SavingsAccountTemplate
import org.mifos.core.model.savings.SavingsTransactionTemplate
import org.mifos.core.network.DataManager
import org.mifos.core.network.fineract.savings.dto.ActivateSavingRequestDto
import org.mifos.core.network.fineract.savings.dto.ApproveSavingRequestDto
import org.mifos.core.network.fineract.savings.dto.DepositRequestDto

class SavingsRepositoryImpl(
    private val dataManager: DataManager,
    private val networkMonitor: NetworkMonitor,
    private val dispatcher: DispatcherManager,
) : SavingsRepository {

    override fun getSavingDetails(accountId: Long): Flow<ScreenState<SavingDetail>> {
        return dataManager.fineract.savingsApi.getSavingDetails(accountId).asScreenStateFlow(
            networkMonitor = networkMonitor,
            dispatcher = dispatcher.io,
        ) { dto ->
            dto.toModel()
        }
    }

    override fun calculateSavingInterest(accountId: Long): Flow<ScreenState<SavingInterestDetail>> {
        return dataManager.fineract.savingsApi.calculateSavingInterest(accountId).asScreenStateFlow(
            networkMonitor = networkMonitor,
            dispatcher = dispatcher.io,
        ) { dto ->
            dto.toModel()
        }
    }

    override suspend fun approveSaving(
        savingsId: Long,
        approvedOnDate: String,
        dateFormat: String,
        locale: String,
    ): ScreenState<Unit> {
        return runAsDataState(
            networkMonitor = networkMonitor,
            context = dispatcher.io,
        ) {
            val response = dataManager.fineract.savingsApi.approveSaving(
                savingsId = savingsId,
                request = ApproveSavingRequestDto(
                    approvedOnDate = approvedOnDate,
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

    override fun getSavingsTransactionTemplate(accountId: Long): Flow<ScreenState<SavingsTransactionTemplate>> {
        return dataManager.fineract.savingsApi.getSavingsTransactionTemplate(accountId).asScreenStateFlow(
            networkMonitor = networkMonitor,
            dispatcher = dispatcher.io,
        ) { dto ->
            dto.toModel()
        }
    }

    override suspend fun depositTransaction(
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
    ): ScreenState<Unit> {
        return runAsDataState(
            networkMonitor = networkMonitor,
            context = dispatcher.io,
        ) {
            val response = dataManager.fineract.savingsApi.depositTransaction(
                accountId = accountId,
                request = DepositRequestDto(
                    locale = locale,
                    dateFormat = dateFormat,
                    transactionDate = transactionDate,
                    transactionAmount = transactionAmount,
                    paymentTypeId = paymentTypeId,
                    accountNumber = accountNumber,
                    checkNumber = checkNumber,
                    routingCode = routingCode,
                    receiptNumber = receiptNumber,
                    bankNumber = bankNumber,
                ),
            )
            if (!response.status.isSuccess()) {
                val errorMessage = extractErrorMessage(response)
                throw Exception(errorMessage)
            }
        }
    }

    override suspend fun withdrawTransaction(
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
    ): ScreenState<Unit> {
        return runAsDataState(
            networkMonitor = networkMonitor,
            context = dispatcher.io,
        ) {
            val response = dataManager.fineract.savingsApi.withdrawTransaction(
                accountId = accountId,
                request = DepositRequestDto(
                    locale = locale,
                    dateFormat = dateFormat,
                    transactionDate = transactionDate,
                    transactionAmount = transactionAmount,
                    paymentTypeId = paymentTypeId,
                    accountNumber = accountNumber,
                    checkNumber = checkNumber,
                    routingCode = routingCode,
                    receiptNumber = receiptNumber,
                    bankNumber = bankNumber,
                ),
            )
            if (!response.status.isSuccess()) {
                val errorMessage = extractErrorMessage(response)
                throw Exception(errorMessage)
            }
        }
    }

    override suspend fun activateSaving(
        savingsId: Long,
        activatedOnDate: String,
        dateFormat: String,
        locale: String,
    ): ScreenState<Unit> {
        return runAsDataState(
            networkMonitor = networkMonitor,
            context = dispatcher.io,
        ) {
            val response = dataManager.fineract.savingsApi.activateSaving(
                savingsId = savingsId,
                request = ActivateSavingRequestDto(
                    activatedOnDate = activatedOnDate,
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

    override fun getSavingsAccountTemplate(): Flow<ScreenState<SavingsAccountTemplate>> {
        return dataManager.fineract.savingsApi.getSavingsAccountsTemplate().asScreenStateFlow(
            networkMonitor = networkMonitor,
            dispatcher = dispatcher.io,
        ) { dto ->
            dto.toModel()
        }
    }

    override suspend fun createSavingsAccount(
        createSavingAccountRequest: CreateSavingAccountRequest,
    ): ScreenState<Unit> {
        return runAsDataState(
            networkMonitor = networkMonitor,
            context = dispatcher.io,
        ) {
            val response = dataManager.fineract.savingsApi.createSavingsAccount(createSavingAccountRequest.toDto())
            if (!response.status.isSuccess()) {
                val errorMessage = extractErrorMessage(response)
                throw Exception(errorMessage)
            }
        }
    }
}
