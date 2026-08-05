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

data class SavingsAccountTemplate(
    val currency: SavingDetailCurrency?,
    val interestCompoundingPeriodType: SavingPeriodType?,
    val interestPostingPeriodType: SavingPeriodType?,
    val interestCalculationType: SavingPeriodType?,
    val interestCalculationDaysInYearType: SavingPeriodType?,
    val accountingRule: SavingPeriodType?,
    val currencyOptions: List<SavingDetailCurrency>,
    val interestCompoundingPeriodTypeOptions: List<SavingPeriodType>,
    val interestPostingPeriodTypeOptions: List<SavingPeriodType>,
    val interestCalculationTypeOptions: List<SavingPeriodType>,
    val interestCalculationDaysInYearTypeOptions: List<SavingPeriodType>,
    val lockinPeriodFrequencyTypeOptions: List<SavingPeriodType>,
    val withdrawalFeeTypeOptions: List<SavingPeriodType>,
    val paymentTypeOptions: List<PaymentTypeOption>,
    val chargeOptions: List<ChargeOption>,
    val productOptions: List<SavingsProductOption>,
    val fieldOfficerOptions: List<StaffOption>,
    val nominalAnnualInterestRate: Double? = null,
    val minRequiredOpeningBalance: Double? = null,
    val lockinPeriodFrequency: Int? = null,
    val lockinPeriodFrequencyType: SavingPeriodType? = null,

    // Additional matched fields from template JSON
    val withdrawalFeeForTransfers: Boolean? = null,
    val allowOverdraft: Boolean? = null,
    val enforceMinRequiredBalance: Boolean? = null,
    val lienAllowed: Boolean? = null,
    val withHoldTax: Boolean? = null,
    val accountMappingForPayment: String? = null,
    val accountingRuleOptions: List<SavingPeriodType> = emptyList(),
    val accountingMappingOptions: AccountingMappingOptions? = null,
    val taxGroupOptions: List<SavingPeriodType> = emptyList(),
    val isDormancyTrackingActive: Boolean? = null,
)

data class SavingsProductOption(
    val id: Long,
    val name: String?,
    val withdrawalFeeForTransfers: Boolean? = null,
    val allowOverdraft: Boolean? = null,
)

data class ChargeOption(
    val active: Boolean?,
    val amount: Double?,
    val chargeAppliesTo: SavingPeriodType?,
    val chargeCalculationType: SavingPeriodType?,
    val chargePaymentMode: SavingPeriodType?,
    val chargeTimeType: SavingPeriodType?,
    val currency: SavingDetailCurrency?,
    val id: Long,
    val name: String?,
    val penalty: Boolean?,
)

data class StaffOption(
    val id: Long,
    val firstname: String?,
    val lastname: String?,
    val displayName: String?,
    val mobileNo: String?,
    val officeId: Long?,
    val officeName: String?,
    val isLoanOfficer: Boolean?,
    val isActive: Boolean?,
)

data class AccountingMappingOptions(
    val liabilityAccountOptions: List<AccountOption> = emptyList(),
    val expenseAccountOptions: List<AccountOption> = emptyList(),
    val assetAccountOptions: List<AccountOption> = emptyList(),
    val incomeAccountOptions: List<AccountOption> = emptyList(),
)

data class AccountOption(
    val id: Long,
    val name: String?,
    val glCode: String?,
    val disabled: Boolean?,
    val manualEntriesAllowed: Boolean?,
    val type: SavingPeriodType?,
    val usage: SavingPeriodType?,
    val description: String?,
    val nameDecorated: String?,
)
