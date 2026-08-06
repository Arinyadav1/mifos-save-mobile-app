/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See See https://github.com/openMF/kmp-project-template/blob/main/LICENSE
 */
package org.mifos.core.model.savings

import kotlinx.datetime.LocalDate

data class SavingInterestDetail(
    val id: Long,
    val accountNo: String?,
    val depositType: SavingPeriodType?,
    val groupId: Long,
    val groupName: String?,
    val savingsProductId: Long,
    val savingsProductName: String?,
    val fieldOfficerId: Long,
    val status: SavingInterestStatus?,
    val subStatus: SavingInterestSubStatus?,
    val timeline: SavingInterestTimeline?,
    val currency: SavingDetailCurrency?,
    val nominalAnnualInterestRate: Double,
    val interestCompoundingPeriodType: SavingPeriodType?,
    val interestPostingPeriodType: SavingPeriodType?,
    val interestCalculationType: SavingPeriodType?,
    val interestCalculationDaysInYearType: SavingPeriodType?,
    val summary: SavingInterestSummary?,
)

data class SavingInterestStatus(
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

data class SavingInterestSubStatus(
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

data class SavingInterestTimeline(
    val submittedOnDate: LocalDate?,
    val submittedByUsername: String?,
    val submittedByFirstname: String?,
    val submittedByLastname: String?,
)

data class SavingInterestSummary(
    val currency: SavingDetailCurrency?,
    val totalInterestPosted: Double,
    val accountBalance: Double,
    val totalOverdraftInterestDerived: Double,
    val interestNotPosted: Double,
    val availableBalance: Double,
    val runningBalanceOnInterestPostingTillDate: Double,
    val runningBalanceOnPivotDate: Double,
)
