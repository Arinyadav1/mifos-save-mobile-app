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

import kotlinx.datetime.LocalDate
import org.mifos.core.model.savings.SavingDetail
import org.mifos.core.model.savings.SavingDetailCurrency
import org.mifos.core.model.savings.SavingDetailStatus
import org.mifos.core.model.savings.SavingDetailSummary
import org.mifos.core.model.savings.SavingDetailTimeline
import org.mifos.core.model.savings.SavingPeriodType
import org.mifos.core.network.fineract.savings.dto.SavingDetailCurrencyDto
import org.mifos.core.network.fineract.savings.dto.SavingDetailDto
import org.mifos.core.network.fineract.savings.dto.SavingDetailStatusDto
import org.mifos.core.network.fineract.savings.dto.SavingDetailSummaryDto
import org.mifos.core.network.fineract.savings.dto.SavingDetailTimelineDto
import org.mifos.core.network.fineract.savings.dto.SavingPeriodTypeDto

fun SavingDetailDto.toModel(): SavingDetail =
    SavingDetail(
        id = id,
        accountNo = accountNo,
        clientId = clientId,
        clientName = clientName,
        savingsProductId = savingsProductId,
        savingsProductName = savingsProductName,
        fieldOfficerId = fieldOfficerId,
        status = status?.toModel(),
        timeline = timeline?.toModel(),
        currency = currency?.toModel(),
        nominalAnnualInterestRate = nominalAnnualInterestRate,
        interestCompoundingPeriodType = interestCompoundingPeriodType?.toModel(),
        interestPostingPeriodType = interestPostingPeriodType?.toModel(),
        interestCalculationType = interestCalculationType?.toModel(),
        interestCalculationDaysInYearType = interestCalculationDaysInYearType?.toModel(),
        summary = summary?.toModel(),
    )

fun SavingDetailStatusDto.toModel(): SavingDetailStatus =
    SavingDetailStatus(
        id = id,
        code = code,
        value = value,
        submittedAndPendingApproval = submittedAndPendingApproval,
        approved = approved,
        rejected = rejected,
        withdrawnByApplicant = withdrawnByApplicant,
        active = active,
        closed = closed,
    )

fun SavingDetailTimelineDto.toModel(): SavingDetailTimeline =
    SavingDetailTimeline(
        submittedOnDate = submittedOnDate.toLocalDateOrNull(),
    )

fun SavingDetailCurrencyDto.toModel(): SavingDetailCurrency =
    SavingDetailCurrency(
        code = code,
        name = name,
        decimalPlaces = decimalPlaces,
        displaySymbol = displaySymbol,
        nameCode = nameCode,
        displayLabel = displayLabel,
    )

fun SavingPeriodTypeDto.toModel(): SavingPeriodType =
    SavingPeriodType(
        id = id,
        code = code,
        value = value,
    )

fun SavingDetailSummaryDto.toModel(): SavingDetailSummary =
    SavingDetailSummary(
        currency = currency?.toModel(),
        accountBalance = accountBalance,
        availableBalance = availableBalance,
    )

private fun List<Int>?.toLocalDateOrNull(): LocalDate? {
    if (this == null || this.size < 3) return null
    return try {
        LocalDate(this[0], this[1], this[2])
    } catch (e: Exception) {
        null
    }
}
