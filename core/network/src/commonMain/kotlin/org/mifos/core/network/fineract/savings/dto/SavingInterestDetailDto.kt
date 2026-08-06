/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See See https://github.com/openMF/kmp-project-template/blob/main/LICENSE
 */
package org.mifos.core.network.fineract.savings.dto

import kotlinx.serialization.Serializable

@Serializable
data class SavingInterestDetailDto(
    val id: Long = 0,
    val accountNo: String? = null,
    val depositType: SavingPeriodTypeDto? = null,
    val groupId: Long = 0,
    val groupName: String? = null,
    val savingsProductId: Long = 0,
    val savingsProductName: String? = null,
    val fieldOfficerId: Long = 0,
    val status: SavingInterestStatusDto? = null,
    val subStatus: SavingInterestSubStatusDto? = null,
    val timeline: SavingInterestTimelineDto? = null,
    val currency: SavingDetailCurrencyDto? = null,
    val nominalAnnualInterestRate: Double = 0.0,
    val interestCompoundingPeriodType: SavingPeriodTypeDto? = null,
    val interestPostingPeriodType: SavingPeriodTypeDto? = null,
    val interestCalculationType: SavingPeriodTypeDto? = null,
    val interestCalculationDaysInYearType: SavingPeriodTypeDto? = null,
    val summary: SavingInterestSummaryDto? = null,
)

@Serializable
data class SavingInterestStatusDto(
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
data class SavingInterestSubStatusDto(
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
data class SavingInterestTimelineDto(
    val submittedOnDate: List<Int>? = null,
    val submittedByUsername: String? = null,
    val submittedByFirstname: String? = null,
    val submittedByLastname: String? = null,
)

@Serializable
data class SavingInterestSummaryDto(
    val currency: SavingDetailCurrencyDto? = null,
    val totalInterestPosted: Double = 0.0,
    val accountBalance: Double = 0.0,
    val totalOverdraftInterestDerived: Double = 0.0,
    val interestNotPosted: Double = 0.0,
    val availableBalance: Double = 0.0,
    val runningBalanceOnInterestPostingTillDate: Double = 0.0,
    val runningBalanceOnPivotDate: Double = 0.0,
)
