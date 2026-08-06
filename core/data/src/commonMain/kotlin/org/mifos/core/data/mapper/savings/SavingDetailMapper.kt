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
import org.mifos.core.model.savings.SavingDetailChargePaid
import org.mifos.core.model.savings.SavingDetailCurrency
import org.mifos.core.model.savings.SavingDetailPaymentDetailData
import org.mifos.core.model.savings.SavingDetailStatus
import org.mifos.core.model.savings.SavingDetailSubStatus
import org.mifos.core.model.savings.SavingDetailSummary
import org.mifos.core.model.savings.SavingDetailTaxDetail
import org.mifos.core.model.savings.SavingDetailTimeline
import org.mifos.core.model.savings.SavingDetailTransaction
import org.mifos.core.model.savings.SavingDetailTransactionType
import org.mifos.core.model.savings.SavingDetailTransfer
import org.mifos.core.model.savings.SavingPeriodType
import org.mifos.core.network.fineract.savings.dto.SavingDetailChargePaidDto
import org.mifos.core.network.fineract.savings.dto.SavingDetailCurrencyDto
import org.mifos.core.network.fineract.savings.dto.SavingDetailDto
import org.mifos.core.network.fineract.savings.dto.SavingDetailPaymentDetailDataDto
import org.mifos.core.network.fineract.savings.dto.SavingDetailStatusDto
import org.mifos.core.network.fineract.savings.dto.SavingDetailSubStatusDto
import org.mifos.core.network.fineract.savings.dto.SavingDetailSummaryDto
import org.mifos.core.network.fineract.savings.dto.SavingDetailTaxDetailDto
import org.mifos.core.network.fineract.savings.dto.SavingDetailTimelineDto
import org.mifos.core.network.fineract.savings.dto.SavingDetailTransactionDto
import org.mifos.core.network.fineract.savings.dto.SavingDetailTransactionTypeDto
import org.mifos.core.network.fineract.savings.dto.SavingDetailTransferDto
import org.mifos.core.network.fineract.savings.dto.SavingPeriodTypeDto

fun SavingDetailDto.toModel(): SavingDetail =
    SavingDetail(
        id = id,
        accountNo = accountNo,
        depositType = depositType?.toModel(),
        clientId = clientId,
        clientName = clientName,
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
        withdrawalFeeForTransfers = withdrawalFeeForTransfers,
        allowOverdraft = allowOverdraft,
        enforceMinRequiredBalance = enforceMinRequiredBalance,
        lienAllowed = lienAllowed,
        withHoldTax = withHoldTax,
        lastActiveTransactionDate = lastActiveTransactionDate.toLocalDateOrNull(),
        isDormancyTrackingActive = isDormancyTrackingActive,
        summary = summary?.toModel(),
        transactions = transactions.map { it.toModel() },
        interestCalculationDaysInYearTypeId = interestCalculationDaysInYearTypeId,
        activationLocalDate = activationLocalDate.toLocalDateOrNull(),
        interestCalculationTypeId = interestCalculationTypeId,
        existingTransactionIds = existingTransactionIds,
        depositTypeId = depositTypeId,
        existingReversedTransactionIds = existingReversedTransactionIds,
        newSavingsAccountTransactionData = newSavingsAccountTransactionData.map { it.toModel() },
        savingsAccountTransactionData = savingsAccountTransactionData.map { it.toModel() },
        interestPostingPeriodTypeId = interestPostingPeriodTypeId,
        interestCompoundingPeriodTypeId = interestCompoundingPeriodTypeId,
        savingsAccountTransactionsWithPivotConfig = savingsAccountTransactionsWithPivotConfig.map { it.toModel() },
        startInterestCalculationDate = startInterestCalculationDate.toLocalDateOrNull(),
        cashBasedAccountingEnabledOnSavingsProduct = cashBasedAccountingEnabledOnSavingsProduct,
        accrualBasedAccountingEnabledOnSavingsProduct = accrualBasedAccountingEnabledOnSavingsProduct,
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
        prematureClosed = prematureClosed,
        transferInProgress = transferInProgress,
        transferOnHold = transferOnHold,
        matured = matured,
    )

fun SavingDetailSubStatusDto.toModel(): SavingDetailSubStatus =
    SavingDetailSubStatus(
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

fun SavingDetailTimelineDto.toModel(): SavingDetailTimeline =
    SavingDetailTimeline(
        submittedOnDate = submittedOnDate.toLocalDateOrNull(),
        submittedByUsername = submittedByUsername,
        submittedByFirstname = submittedByFirstname,
        submittedByLastname = submittedByLastname,
        approvedOnDate = approvedOnDate.toLocalDateOrNull(),
        approvedByUsername = approvedByUsername,
        approvedByFirstname = approvedByFirstname,
        approvedByLastname = approvedByLastname,
        activatedOnDate = activatedOnDate.toLocalDateOrNull(),
        activatedByUsername = activatedByUsername,
        activatedByFirstname = activatedByFirstname,
        activatedByLastname = activatedByLastname,
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
        totalDeposits = totalDeposits,
        totalInterestPosted = totalInterestPosted,
        accountBalance = accountBalance,
        totalOverdraftInterestDerived = totalOverdraftInterestDerived,
        interestNotPosted = interestNotPosted,
        availableBalance = availableBalance,
        runningBalanceOnInterestPostingTillDate = runningBalanceOnInterestPostingTillDate,
        runningBalanceOnPivotDate = runningBalanceOnPivotDate,
    )

fun SavingDetailTransactionDto.toModel(): SavingDetailTransaction =
    SavingDetailTransaction(
        id = id,
        transactionType = transactionType?.toModel(),
        paymentDetailData = paymentDetailData?.toModel(),
        externalId = externalId,
        currency = currency?.toModel(),
        amount = amount,
        runningBalance = runningBalance,
        reversed = reversed,
        transfer = transfer?.toModel(),
        submittedOnDate = submittedOnDate.toLocalDateOrNull(),
        interestedPostedAsOn = interestedPostedAsOn,
        submittedByUsername = submittedByUsername,
        isReversal = isReversal,
        originalTransactionId = originalTransactionId,
        isManualTransaction = isManualTransaction,
        lienTransaction = lienTransaction,
        releaseTransactionId = releaseTransactionId,
        isOverdraft = isOverdraft,
        entryType = entryType,
        accountId = accountId,
        accountNo = accountNo,
        date = date.toLocalDateOrNull(),
        chargesPaidByData = chargesPaidByData.map { it.toModel() },
        feeCharge = feeCharge,
        notReversed = notReversed,
        waiveCharge = waiveCharge,
        chargeTransaction = chargeTransaction,
        manualTransaction = manualTransaction,
        reversalTransaction = reversalTransaction,
        savingsAccountChargesPaid = savingsAccountChargesPaid.map { it.toModel() },
        interestPostingAndNotReversed = interestPostingAndNotReversed,
        overdraftInterestAndNotReversed = overdraftInterestAndNotReversed,
        payCharge = payCharge,
        amountOnHold = amountOnHold,
        chargeTransactionAndNotReversed = chargeTransactionAndNotReversed,
        waiveFeeCharge = waiveFeeCharge,
        waivePenaltyCharge = waivePenaltyCharge,
        debit = debit,
        taxDetails = taxDetails.map { it.toModel() },
        credit = credit,
        depositAndNotReversed = depositAndNotReversed,
        annualFeeAndNotReversed = annualFeeAndNotReversed,
        feeChargeAndNotReversed = feeChargeAndNotReversed,
        withHoldTaxAndNotReversed = withHoldTaxAndNotReversed,
        dividendPayoutAndNotReversed = dividendPayoutAndNotReversed,
        withdrawalFeeAndNotReversed = withdrawalFeeAndNotReversed,
        waiveFeeChargeAndNotReversed = waiveFeeChargeAndNotReversed,
        penaltyChargeAndNotReversed = penaltyChargeAndNotReversed,
        waivePenaltyChargeAndNotReversed = waivePenaltyChargeAndNotReversed,
        interestPosting = interestPosting,
        penaltyCharge = penaltyCharge,
        accrual = accrual,
        withdrawal = withdrawal,
        deposit = deposit,
        amountRelease = amountRelease,
        annualFee = annualFee,
    )

fun SavingDetailTransactionTypeDto.toModel(): SavingDetailTransactionType =
    SavingDetailTransactionType(
        id = id,
        code = code,
        value = value,
        deposit = deposit,
        dividendPayout = dividendPayout,
        withdrawal = withdrawal,
        interestPosting = interestPosting,
        feeDeduction = feeDeduction,
        initiateTransfer = initiateTransfer,
        approveTransfer = approveTransfer,
        withdrawTransfer = withdrawTransfer,
        rejectTransfer = rejectTransfer,
        overdraftInterest = overdraftInterest,
        writtenoff = writtenoff,
        overdraftFee = overdraftFee,
        withholdTax = withholdTax,
        escheat = escheat,
        amountHold = amountHold,
        amountRelease = amountRelease,
        accrual = accrual,
        depositOrWithdrawal = depositOrWithdrawal,
        transactionTypeEnum = transactionTypeEnum,
        chargeTransaction = chargeTransaction,
        withdrawalFee = withdrawalFee,
        payCharge = payCharge,
        overDraftInterestPosting = overDraftInterestPosting,
        incomeFromInterest = incomeFromInterest,
        debit = debit,
        credit = credit,
        entryType = entryType,
        annualFee = annualFee,
    )

fun SavingDetailPaymentDetailDataDto.toModel(): SavingDetailPaymentDetailData =
    SavingDetailPaymentDetailData(
        id = id,
        paymentType = paymentType?.toModel(),
        accountNumber = accountNumber,
        checkNumber = checkNumber,
        routingCode = routingCode,
        receiptNumber = receiptNumber,
        bankNumber = bankNumber,
    )

fun SavingDetailTransferDto.toModel(): SavingDetailTransfer =
    SavingDetailTransfer(
        id = id,
        reversed = reversed,
        currency = currency?.toModel(),
        transferAmount = transferAmount,
        transferDate = transferDate,
        transferDescription = transferDescription,
    )

fun SavingDetailChargePaidDto.toModel(): SavingDetailChargePaid =
    SavingDetailChargePaid(
        id = id,
    )

fun SavingDetailTaxDetailDto.toModel(): SavingDetailTaxDetail =
    SavingDetailTaxDetail(
        id = id,
    )

private fun List<Int>?.toLocalDateOrNull(): LocalDate? {
    if (this == null || this.size < 3) return null
    return try {
        LocalDate(this[0], this[1], this[2])
    } catch (e: Exception) {
        null
    }
}
