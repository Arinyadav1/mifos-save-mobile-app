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
data class SavingDetailDto(
    val id: Long = 0,
    val accountNo: String? = null,
    val clientId: Long = 0,
    val clientName: String? = null,
    val savingsProductId: Long = 0,
    val savingsProductName: String? = null,
    val fieldOfficerId: Long = 0,
    val status: SavingDetailStatusDto? = null,
    val timeline: SavingDetailTimelineDto? = null,
    val currency: SavingDetailCurrencyDto? = null,
    val nominalAnnualInterestRate: Double = 0.0,
    val interestCompoundingPeriodType: SavingPeriodTypeDto? = null,
    val interestPostingPeriodType: SavingPeriodTypeDto? = null,
    val interestCalculationType: SavingPeriodTypeDto? = null,
    val interestCalculationDaysInYearType: SavingPeriodTypeDto? = null,
    val summary: SavingDetailSummaryDto? = null,
)

@Serializable
data class SavingDetailStatusDto(
    val id: Long = 0,
    val code: String? = null,
    val value: String? = null,
    val submittedAndPendingApproval: Boolean = false,
    val approved: Boolean = false,
    val rejected: Boolean = false,
    val withdrawnByApplicant: Boolean = false,
    val active: Boolean = false,
    val closed: Boolean = false,
)

@Serializable
data class SavingDetailTimelineDto(
    val submittedOnDate: List<Int>? = null,
)

@Serializable
data class SavingDetailCurrencyDto(
    val code: String? = null,
    val name: String? = null,
    val decimalPlaces: Int = 0,
    val displaySymbol: String? = null,
    val nameCode: String? = null,
    val displayLabel: String? = null,
)

@Serializable
data class SavingPeriodTypeDto(
    val id: Long = 0,
    val code: String? = null,
    val value: String? = null,
)

@Serializable
data class SavingDetailSummaryDto(
    val currency: SavingDetailCurrencyDto? = null,
    val accountBalance: Double = 0.0,
    val availableBalance: Double = 0.0,
)
