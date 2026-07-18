/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See See https://github.com/openMF/kmp-project-template/blob/main/LICENSE
 */
package org.mifos.core.model.group

import kotlinx.datetime.LocalDate

data class GroupAccounts(
    val savingsAccounts: List<SavingsAccount>,
    val memberSavingsAccounts: List<SavingsAccount>,
)

data class SavingsAccount(
    val id: Long,
    val accountNo: String?,
    val productId: Long,
    val productName: String?,
    val shortProductName: String?,
    val status: SavingsAccountStatus?,
    val currency: SavingsAccountCurrency?,
    val accountType: SavingsAccountType?,
    val timeline: SavingsAccountTimeline?,
    val subStatus: SavingsAccountSubStatus?,
    val depositType: SavingsAccountDepositType?,
    val lastActiveTransactionDate: LocalDate?,
)

data class SavingsAccountStatus(
    val id: Long,
    val code: String?,
    val value: String?,
    val submittedAndPendingApproval: Boolean,
    val approved: Boolean,
    val rejected: Boolean,
    val withdrawnByApplicant: Boolean,
    val active: Boolean,
    val closed: Boolean,
    val prematureClosed: Boolean,
    val transferInProgress: Boolean,
    val transferOnHold: Boolean,
    val matured: Boolean,
)

data class SavingsAccountCurrency(
    val code: String?,
    val name: String?,
    val decimalPlaces: Int,
    val displaySymbol: String?,
    val nameCode: String?,
    val displayLabel: String?,
)

data class SavingsAccountType(
    val id: Long,
    val code: String?,
    val value: String?,
)

data class SavingsAccountTimeline(
    val submittedOnDate: LocalDate?,
    val submittedByUsername: String?,
    val submittedByFirstname: String?,
    val submittedByLastname: String?,
    val approvedOnDate: LocalDate?,
    val approvedByUsername: String?,
    val approvedByFirstname: String?,
    val approvedByLastname: String?,
    val activatedOnDate: LocalDate?,
)

data class SavingsAccountSubStatus(
    val id: Long,
    val code: String?,
    val value: String?,
    val none: Boolean,
    val inactive: Boolean,
    val dormant: Boolean,
    val escheat: Boolean,
    val block: Boolean,
    val blockCredit: Boolean,
    val blockDebit: Boolean,
)

data class SavingsAccountDepositType(
    val id: Long,
    val code: String?,
    val value: String?,
)
