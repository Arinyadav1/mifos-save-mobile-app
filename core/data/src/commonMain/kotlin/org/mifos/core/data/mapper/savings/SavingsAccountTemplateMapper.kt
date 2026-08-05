/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See See https://github.com/openMF/kmp-project-template/blob/main/LICENSE
 */
package org.mifos.core.data.mapper.savings

import org.mifos.core.model.savings.AccountOption
import org.mifos.core.model.savings.AccountingMappingOptions
import org.mifos.core.model.savings.ChargeOption
import org.mifos.core.model.savings.SavingsAccountTemplate
import org.mifos.core.model.savings.SavingsProductOption
import org.mifos.core.model.savings.StaffOption
import org.mifos.core.network.fineract.group.dto.StaffOptionDto
import org.mifos.core.network.fineract.savings.dto.AccountOptionDto
import org.mifos.core.network.fineract.savings.dto.AccountingMappingOptionsDto
import org.mifos.core.network.fineract.savings.dto.ChargeOptionDto
import org.mifos.core.network.fineract.savings.dto.SavingsAccountTemplateDto
import org.mifos.core.network.fineract.savings.dto.SavingsProductOptionDto

fun SavingsAccountTemplateDto.toModel(): SavingsAccountTemplate =
    SavingsAccountTemplate(
        currency = currency?.toModel(),
        interestCompoundingPeriodType = interestCompoundingPeriodType?.toModel(),
        interestPostingPeriodType = interestPostingPeriodType?.toModel(),
        interestCalculationType = interestCalculationType?.toModel(),
        interestCalculationDaysInYearType = interestCalculationDaysInYearType?.toModel(),
        accountingRule = accountingRule?.toModel(),
        currencyOptions = currencyOptions?.map { it.toModel() } ?: emptyList(),
        interestCompoundingPeriodTypeOptions = interestCompoundingPeriodTypeOptions
            ?.map { it.toModel() } ?: emptyList(),
        interestPostingPeriodTypeOptions = interestPostingPeriodTypeOptions?.map { it.toModel() } ?: emptyList(),
        interestCalculationTypeOptions = interestCalculationTypeOptions?.map { it.toModel() } ?: emptyList(),
        interestCalculationDaysInYearTypeOptions = interestCalculationDaysInYearTypeOptions
            ?.map { it.toModel() } ?: emptyList(),
        lockinPeriodFrequencyTypeOptions = lockinPeriodFrequencyTypeOptions?.map { it.toModel() } ?: emptyList(),
        withdrawalFeeTypeOptions = withdrawalFeeTypeOptions?.map { it.toModel() } ?: emptyList(),
        paymentTypeOptions = paymentTypeOptions?.map { it.toModel() } ?: emptyList(),
        chargeOptions = chargeOptions?.map { it.toModel() } ?: emptyList(),
        productOptions = productOptions?.map { it.toModel() } ?: emptyList(),
        fieldOfficerOptions = fieldOfficerOptions?.map { it.toModel() } ?: emptyList(),
        nominalAnnualInterestRate = nominalAnnualInterestRate,
        minRequiredOpeningBalance = minRequiredOpeningBalance,
        lockinPeriodFrequency = lockinPeriodFrequency,
        lockinPeriodFrequencyType = lockinPeriodFrequencyType?.toModel(),

        // Additional fields mapping
        withdrawalFeeForTransfers = withdrawalFeeForTransfers,
        allowOverdraft = allowOverdraft,
        enforceMinRequiredBalance = enforceMinRequiredBalance,
        lienAllowed = lienAllowed,
        withHoldTax = withHoldTax,
        accountMappingForPayment = accountMappingForPayment,
        accountingRuleOptions = accountingRuleOptions?.map { it.toModel() } ?: emptyList(),
        accountingMappingOptions = accountingMappingOptions?.toModel(),
        taxGroupOptions = taxGroupOptions?.map { it.toModel() } ?: emptyList(),
        isDormancyTrackingActive = isDormancyTrackingActive,
    )

fun SavingsProductOptionDto.toModel(): SavingsProductOption =
    SavingsProductOption(
        id = id,
        name = name,
        withdrawalFeeForTransfers = withdrawalFeeForTransfers,
        allowOverdraft = allowOverdraft,
    )

fun ChargeOptionDto.toModel(): ChargeOption =
    ChargeOption(
        active = active,
        amount = amount,
        chargeAppliesTo = chargeAppliesTo?.toModel(),
        chargeCalculationType = chargeCalculationType?.toModel(),
        chargePaymentMode = chargePaymentMode?.toModel(),
        chargeTimeType = chargeTimeType?.toModel(),
        currency = currency?.toModel(),
        id = id,
        name = name,
        penalty = penalty,
    )

fun StaffOptionDto.toModel(): StaffOption =
    StaffOption(
        id = id,
        firstname = firstname,
        lastname = lastname,
        displayName = displayName,
        mobileNo = mobileNo,
        officeId = officeId,
        officeName = officeName,
        isLoanOfficer = isLoanOfficer,
        isActive = isActive,
    )

fun AccountingMappingOptionsDto.toModel(): AccountingMappingOptions =
    AccountingMappingOptions(
        liabilityAccountOptions = liabilityAccountOptions?.map { it.toModel() } ?: emptyList(),
        expenseAccountOptions = expenseAccountOptions?.map { it.toModel() } ?: emptyList(),
        assetAccountOptions = assetAccountOptions?.map { it.toModel() } ?: emptyList(),
        incomeAccountOptions = incomeAccountOptions?.map { it.toModel() } ?: emptyList(),
    )

fun AccountOptionDto.toModel(): AccountOption =
    AccountOption(
        id = id,
        name = name,
        glCode = glCode,
        disabled = disabled,
        manualEntriesAllowed = manualEntriesAllowed,
        type = type?.toModel(),
        usage = usage?.toModel(),
        description = description,
        nameDecorated = nameDecorated,
    )
