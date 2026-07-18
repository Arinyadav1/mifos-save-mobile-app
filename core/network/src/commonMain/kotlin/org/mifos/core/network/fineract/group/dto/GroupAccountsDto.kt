/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See See https://github.com/openMF/kmp-project-template/blob/main/LICENSE
 */
package org.mifos.core.network.fineract.group.dto

import kotlinx.serialization.Serializable

@Serializable
data class GroupAccountsDto(
    val savingsAccounts: List<SavingsAccountDto>? = null,
    val memberSavingsAccounts: List<SavingsAccountDto>? = null,
)

@Serializable
data class SavingsAccountDto(
    val id: Long = 0,
    val accountNo: String? = null,
    val productId: Long = 0,
    val productName: String? = null,
    val shortProductName: String? = null,
    val status: SavingsAccountStatusDto? = null,
    val currency: SavingsAccountCurrencyDto? = null,
    val accountType: SavingsAccountTypeDto? = null,
    val timeline: SavingsAccountTimelineDto? = null,
    val subStatus: SavingsAccountSubStatusDto? = null,
    val depositType: SavingsAccountDepositTypeDto? = null,
    val lastActiveTransactionDate: List<Int>? = null,
)

@Serializable
data class SavingsAccountStatusDto(
    val id: Long = 0,
    val code: String? = null,
    val value: String? = null,
    val submittedAndPendingApproval: Boolean = false,
    val approved: Boolean = false,
    val rejected: Boolean = false,
    val withdrawnByApplicant: Boolean = false,
    val active: Boolean = false,
    val closed: Boolean = false,
    val prematureClosed: Boolean = false,
    val transferInProgress: Boolean = false,
    val transferOnHold: Boolean = false,
    val matured: Boolean = false,
)

@Serializable
data class SavingsAccountCurrencyDto(
    val code: String? = null,
    val name: String? = null,
    val decimalPlaces: Int = 0,
    val displaySymbol: String? = null,
    val nameCode: String? = null,
    val displayLabel: String? = null,
)

@Serializable
data class SavingsAccountTypeDto(
    val id: Long = 0,
    val code: String? = null,
    val value: String? = null,
)

@Serializable
data class SavingsAccountTimelineDto(
    val submittedOnDate: List<Int>? = null,
    val submittedByUsername: String? = null,
    val submittedByFirstname: String? = null,
    val submittedByLastname: String? = null,
    val approvedOnDate: List<Int>? = null,
    val approvedByUsername: String? = null,
    val approvedByFirstname: String? = null,
    val approvedByLastname: String? = null,
    val activatedOnDate: List<Int>? = null,
)

@Serializable
data class SavingsAccountSubStatusDto(
    val id: Long = 0,
    val code: String? = null,
    val value: String? = null,
    val none: Boolean = false,
    val inactive: Boolean = false,
    val dormant: Boolean = false,
    val escheat: Boolean = false,
    val block: Boolean = false,
    val blockCredit: Boolean = false,
    val blockDebit: Boolean = false,
)

@Serializable
data class SavingsAccountDepositTypeDto(
    val id: Long = 0,
    val code: String? = null,
    val value: String? = null,
)
