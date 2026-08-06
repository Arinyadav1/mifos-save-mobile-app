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
import org.mifos.core.model.savings.SavingInterestDetail
import org.mifos.core.model.savings.SavingInterestStatus
import org.mifos.core.model.savings.SavingInterestSubStatus
import org.mifos.core.model.savings.SavingInterestSummary
import org.mifos.core.model.savings.SavingInterestTimeline
import org.mifos.core.network.fineract.savings.dto.SavingInterestDetailDto
import org.mifos.core.network.fineract.savings.dto.SavingInterestStatusDto
import org.mifos.core.network.fineract.savings.dto.SavingInterestSubStatusDto
import org.mifos.core.network.fineract.savings.dto.SavingInterestSummaryDto
import org.mifos.core.network.fineract.savings.dto.SavingInterestTimelineDto

fun SavingInterestDetailDto.toModel(): SavingInterestDetail =
    SavingInterestDetail(
        id = id,
        accountNo = accountNo,
        depositType = depositType?.toModel(),
        groupId = groupId,
        groupName = groupName,
        savingsProductId = savingsProductId,
        savingsProductName = savingsProductName,
        fieldOfficerId = fieldOfficerId,
        status = status?.toModel(),
        subStatus = subStatus?.toModel(),
        timeline = timeline?.toModel(),
        currency = currency?.toModel(),
        nominalAnnualInterestRate = nominalAnnualInterestRate,
        interestCompoundingPeriodType = interestCompoundingPeriodType?.toModel(),
        interestPostingPeriodType = interestPostingPeriodType?.toModel(),
        interestCalculationType = interestCalculationType?.toModel(),
        interestCalculationDaysInYearType = interestCalculationDaysInYearType?.toModel(),
        summary = summary?.toModel(),
    )

fun SavingInterestStatusDto.toModel(): SavingInterestStatus =
    SavingInterestStatus(
        id = id,
        code = code,
        value = value,
        submittedAndPendingApproval = submittedAndPendingApproval,
        approved = approved,
        rejected = rejected,
        withdrawnByApplicant = withdrawnByApplicant,
        active = active,
        closed = closed,
        prematureClosed = prematureClosed,
        transferInProgress = transferInProgress,
        transferOnHold = transferOnHold,
        matured = matured,
    )

fun SavingInterestSubStatusDto.toModel(): SavingInterestSubStatus =
    SavingInterestSubStatus(
        id = id,
        code = code,
        value = value,
        none = none,
        inactive = inactive,
        dormant = dormant,
        escheat = escheat,
        block = block,
        blockCredit = blockCredit,
        blockDebit = blockDebit,
    )

fun SavingInterestTimelineDto.toModel(): SavingInterestTimeline =
    SavingInterestTimeline(
        submittedOnDate = submittedOnDate.toLocalDateOrNull(),
        submittedByUsername = submittedByUsername,
        submittedByFirstname = submittedByFirstname,
        submittedByLastname = submittedByLastname,
    )

fun SavingInterestSummaryDto.toModel(): SavingInterestSummary =
    SavingInterestSummary(
        currency = currency?.toModel(),
        totalInterestPosted = totalInterestPosted,
        accountBalance = accountBalance,
        totalOverdraftInterestDerived = totalOverdraftInterestDerived,
        interestNotPosted = interestNotPosted,
        availableBalance = availableBalance,
        runningBalanceOnInterestPostingTillDate = runningBalanceOnInterestPostingTillDate,
        runningBalanceOnPivotDate = runningBalanceOnPivotDate,
    )

private fun List<Int>?.toLocalDateOrNull(): LocalDate? {
    if (this == null || this.size < 3) return null
    return try {
        LocalDate(this[0], this[1], this[2])
    } catch (e: Exception) {
        null
    }
}
