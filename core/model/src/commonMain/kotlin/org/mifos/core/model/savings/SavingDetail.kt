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

data class SavingDetail(
    val id: Long,
    val accountNo: String?,
    val clientId: Long,
    val clientName: String?,
    val savingsProductId: Long,
    val savingsProductName: String?,
    val fieldOfficerId: Long,
    val status: SavingDetailStatus?,
    val timeline: SavingDetailTimeline?,
    val currency: SavingDetailCurrency?,
    val nominalAnnualInterestRate: Double,
    val interestCompoundingPeriodType: SavingPeriodType?,
    val interestPostingPeriodType: SavingPeriodType?,
    val interestCalculationType: SavingPeriodType?,
    val interestCalculationDaysInYearType: SavingPeriodType?,
    val summary: SavingDetailSummary?,
)

data class SavingDetailStatus(
    val id: Long,
    val code: String?,
    val value: String?,
    val submittedAndPendingApproval: Boolean,
    val approved: Boolean,
    val rejected: Boolean,
    val withdrawnByApplicant: Boolean,
    val active: Boolean,
    val closed: Boolean,
)

data class SavingDetailTimeline(
    val submittedOnDate: LocalDate?,
)

data class SavingDetailCurrency(
    val code: String?,
    val name: String?,
    val decimalPlaces: Int,
    val displaySymbol: String?,
    val nameCode: String?,
    val displayLabel: String?,
)

data class SavingPeriodType(
    val id: Long,
    val code: String?,
    val value: String?,
)

data class SavingDetailSummary(
    val currency: SavingDetailCurrency?,
    val accountBalance: Double,
    val availableBalance: Double,
)
