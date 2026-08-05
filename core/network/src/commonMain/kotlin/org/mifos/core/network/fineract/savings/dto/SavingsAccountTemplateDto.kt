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
import org.mifos.core.network.fineract.group.dto.StaffOptionDto

@Serializable
data class SavingsAccountTemplateDto(
    val currency: SavingDetailCurrencyDto? = null,
    val interestCompoundingPeriodType: SavingPeriodTypeDto? = null,
    val interestPostingPeriodType: SavingPeriodTypeDto? = null,
    val interestCalculationType: SavingPeriodTypeDto? = null,
    val interestCalculationDaysInYearType: SavingPeriodTypeDto? = null,
    val accountingRule: SavingPeriodTypeDto? = null,
    val currencyOptions: List<SavingDetailCurrencyDto>? = null,
    val interestCompoundingPeriodTypeOptions: List<SavingPeriodTypeDto>? = null,
    val interestPostingPeriodTypeOptions: List<SavingPeriodTypeDto>? = null,
    val interestCalculationTypeOptions: List<SavingPeriodTypeDto>? = null,
    val interestCalculationDaysInYearTypeOptions: List<SavingPeriodTypeDto>? = null,
    val lockinPeriodFrequencyTypeOptions: List<SavingPeriodTypeDto>? = null,
    val withdrawalFeeTypeOptions: List<SavingPeriodTypeDto>? = null,
    val paymentTypeOptions: List<PaymentTypeOptionDto>? = null,
    val chargeOptions: List<ChargeOptionDto>? = null,
    val productOptions: List<SavingsProductOptionDto>? = null,
    val fieldOfficerOptions: List<StaffOptionDto>? = null,
    val nominalAnnualInterestRate: Double? = null,
    val minRequiredOpeningBalance: Double? = null,
    val lockinPeriodFrequency: Int? = null,
    val lockinPeriodFrequencyType: SavingPeriodTypeDto? = null,

    // Additional matched fields from template JSON
    val withdrawalFeeForTransfers: Boolean? = null,
    val allowOverdraft: Boolean? = null,
    val enforceMinRequiredBalance: Boolean? = null,
    val lienAllowed: Boolean? = null,
    val withHoldTax: Boolean? = null,
    val accountMappingForPayment: String? = null,
    val accountingRuleOptions: List<SavingPeriodTypeDto>? = null,
    val accountingMappingOptions: AccountingMappingOptionsDto? = null,
    // Can be list of SavingPeriodTypeDto or empty
    val taxGroupOptions: List<SavingPeriodTypeDto>? = null,
    val isDormancyTrackingActive: Boolean? = null,
)

@Serializable
data class PaymentTypeOptionDto(
    val id: Long,
    val name: String? = null,
    val position: Int? = null,
)

@Serializable
data class SavingsProductOptionDto(
    val id: Long,
    val name: String? = null,
    val withdrawalFeeForTransfers: Boolean? = null,
    val allowOverdraft: Boolean? = null,
)

@Serializable
data class ChargeOptionDto(
    val active: Boolean? = null,
    val amount: Double? = null,
    val chargeAppliesTo: SavingPeriodTypeDto? = null,
    val chargeCalculationType: SavingPeriodTypeDto? = null,
    val chargePaymentMode: SavingPeriodTypeDto? = null,
    val chargeTimeType: SavingPeriodTypeDto? = null,
    val currency: SavingDetailCurrencyDto? = null,
    val id: Long,
    val name: String? = null,
    val penalty: Boolean? = null,
)

@Serializable
data class AccountingMappingOptionsDto(
    val liabilityAccountOptions: List<AccountOptionDto>? = null,
    val expenseAccountOptions: List<AccountOptionDto>? = null,
    val assetAccountOptions: List<AccountOptionDto>? = null,
    val incomeAccountOptions: List<AccountOptionDto>? = null,
)

@Serializable
data class AccountOptionDto(
    val id: Long,
    val name: String? = null,
    val glCode: String? = null,
    val disabled: Boolean? = null,
    val manualEntriesAllowed: Boolean? = null,
    val type: SavingPeriodTypeDto? = null,
    val usage: SavingPeriodTypeDto? = null,
    val description: String? = null,
    val nameDecorated: String? = null,
)
