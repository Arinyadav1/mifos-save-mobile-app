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

import org.mifos.core.model.savings.ChargeRequest
import org.mifos.core.model.savings.CreateSavingAccountRequest
import org.mifos.core.network.fineract.savings.dto.ChargesRequestDto
import org.mifos.core.network.fineract.savings.dto.CreateSavingAccountRequestDto

fun CreateSavingAccountRequest.toDto(): CreateSavingAccountRequestDto =
    CreateSavingAccountRequestDto(
        productId = productId,
        groupId = groupId,
        fieldOfficerId = fieldOfficerId,
        locale = locale,
        dateFormat = dateFormat,
        submittedOnDate = submittedOnDate,
        externalId = externalId,
        nominalAnnualInterestRate = nominalAnnualInterestRate,
        interestCompoundingPeriodType = interestCompoundingPeriodType,
        interestCalculationType = interestCalculationType,
        interestCalculationDaysInYearType = interestCalculationDaysInYearType,
        interestPostingPeriodType = interestPostingPeriodType,
        allowOverdraft = allowOverdraft,
        enforceMinRequiredBalance = enforceMinRequiredBalance,
        minRequiredOpeningBalance = minRequiredOpeningBalance,
        minRequiredBalance = minRequiredBalance,
        lockinPeriodFrequency = lockinPeriodFrequency,
        lockinPeriodFrequencyType = lockinPeriodFrequencyType,
        charges = charges?.map { it.toDto() },
        nominalAnnualInterestRateOverdraft = nominalAnnualInterestRateOverdraft,
        overdraftLimit = overdraftLimit,
        minOverdraftForInterestCalculation = minOverdraftForInterestCalculation,
    )

fun ChargeRequest.toDto(): ChargesRequestDto =
    ChargesRequestDto(
        chargeId = chargeId,
        amount = amount,
    )
