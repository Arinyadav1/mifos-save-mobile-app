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
data class CreateSavingAccountRequestDto(
    val productId: Long? = null,
    val groupId: Long? = null,
    val fieldOfficerId: Long? = null,
    val locale: String? = null,
    val dateFormat: String? = null,
    val submittedOnDate: String? = null,
    val externalId: String? = null,
    val nominalAnnualInterestRate: String? = null,
    val interestCompoundingPeriodType: Int? = null,
    val interestCalculationType: Int? = null,
    val interestCalculationDaysInYearType: Int? = null,
    val interestPostingPeriodType: Int? = null,
    val allowOverdraft: Boolean? = null,
    val enforceMinRequiredBalance: Boolean? = null,
    val minRequiredOpeningBalance: String? = null,
    val minRequiredBalance: String? = null,
    val lockinPeriodFrequency: Int? = null,
    val lockinPeriodFrequencyType: Int? = null,
    val charges: List<ChargesRequestDto>? = null,
    val nominalAnnualInterestRateOverdraft: String? = null,
    val overdraftLimit: String? = null,
    val minOverdraftForInterestCalculation: String? = null,
)

@Serializable
data class ChargesRequestDto(
    val chargeId: Long? = null,
    val amount: String? = null,
)
