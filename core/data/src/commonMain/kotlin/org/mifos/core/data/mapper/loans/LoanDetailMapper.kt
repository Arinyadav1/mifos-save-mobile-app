/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See See https://github.com/openMF/kmp-project-template/blob/main/LICENSE
 */
package org.mifos.core.data.mapper.loans

import kotlinx.datetime.LocalDate
import org.mifos.core.model.loans.LoanDetail
import org.mifos.core.model.loans.LoanDetailCurrency
import org.mifos.core.model.loans.LoanDetailStatus
import org.mifos.core.model.loans.LoanDetailSummary
import org.mifos.core.model.loans.LoanDetailTimeline
import org.mifos.core.model.loans.LoanDetailType
import org.mifos.core.model.loans.LoanRepaymentPeriod
import org.mifos.core.model.loans.LoanRepaymentSchedule
import org.mifos.core.model.loans.LoanTransaction
import org.mifos.core.model.loans.LoanTransactionType
import org.mifos.core.network.fineract.loans.dto.LoanDetailCurrencyDto
import org.mifos.core.network.fineract.loans.dto.LoanDetailDto
import org.mifos.core.network.fineract.loans.dto.LoanDetailStatusDto
import org.mifos.core.network.fineract.loans.dto.LoanDetailSummaryDto
import org.mifos.core.network.fineract.loans.dto.LoanDetailTimelineDto
import org.mifos.core.network.fineract.loans.dto.LoanDetailTypeDto
import org.mifos.core.network.fineract.loans.dto.LoanRepaymentPeriodDto
import org.mifos.core.network.fineract.loans.dto.LoanRepaymentScheduleDto
import org.mifos.core.network.fineract.loans.dto.LoanTransactionDto
import org.mifos.core.network.fineract.loans.dto.LoanTransactionTypeDto

fun LoanDetailDto.toModel(): LoanDetail =
    LoanDetail(
        id = id,
        accountNo = accountNo,
        status = status?.toModel(),
        clientId = clientId,
        clientAccountNo = clientAccountNo,
        clientName = clientName,
        clientOfficeId = clientOfficeId,
        loanProductId = loanProductId,
        loanProductName = loanProductName,
        loanProductDescription = loanProductDescription,
        isLoanProductLinkedToFloatingRate = isLoanProductLinkedToFloatingRate,
        loanType = loanType?.toModel(),
        currency = currency?.toModel(),
        principal = principal,
        approvedPrincipal = approvedPrincipal,
        proposedPrincipal = proposedPrincipal,
        netDisbursalAmount = netDisbursalAmount,
        termFrequency = termFrequency,
        termPeriodFrequencyType = termPeriodFrequencyType?.toModel(),
        numberOfRepayments = numberOfRepayments,
        actualNoTerm = actualNoTerm,
        repaymentEvery = repaymentEvery,
        repaymentFrequencyType = repaymentFrequencyType?.toModel(),
        interestRatePerPeriod = interestRatePerPeriod,
        interestRateFrequencyType = interestRateFrequencyType?.toModel(),
        annualInterestRate = annualInterestRate,
        isFloatingInterestRate = isFloatingInterestRate,
        amortizationType = amortizationType?.toModel(),
        interestType = interestType?.toModel(),
        interestCalculationPeriodType = interestCalculationPeriodType?.toModel(),
        allowPartialPeriodInterestCalculation = allowPartialPeriodInterestCalculation,
        transactionProcessingStrategyCode = transactionProcessingStrategyCode,
        transactionProcessingStrategyName = transactionProcessingStrategyName,
        syncDisbursementWithMeeting = syncDisbursementWithMeeting,
        disallowExpectedDisbursements = disallowExpectedDisbursements,
        timeline = timeline?.toModel(),
        summary = summary?.toModel(),
        repaymentSchedule = repaymentSchedule?.toModel(),
        transactions = transactions.map { it.toModel() },
    )

fun LoanDetailStatusDto.toModel(): LoanDetailStatus =
    LoanDetailStatus(
        id = id,
        code = code,
        value = value,
        pendingApproval = pendingApproval,
        waitingForDisbursal = waitingForDisbursal,
        active = active,
        closedObligationsMet = closedObligationsMet,
        closedWrittenOff = closedWrittenOff,
        closedRescheduled = closedRescheduled,
        closed = closed,
        overpaid = overpaid,
    )

fun LoanDetailTypeDto.toModel(): LoanDetailType =
    LoanDetailType(
        id = id,
        code = code,
        value = value,
    )

fun LoanDetailCurrencyDto.toModel(): LoanDetailCurrency =
    LoanDetailCurrency(
        code = code,
        name = name,
        decimalPlaces = decimalPlaces,
        inMultiplesOf = inMultiplesOf,
        displaySymbol = displaySymbol,
        nameCode = nameCode,
        displayLabel = displayLabel,
    )

fun LoanDetailTimelineDto.toModel(): LoanDetailTimeline =
    LoanDetailTimeline(
        submittedOnDate = submittedOnDate.toLocalDateOrNull(),
        submittedByUsername = submittedByUsername,
        submittedByFirstname = submittedByFirstname,
        submittedByLastname = submittedByLastname,
        approvedOnDate = approvedOnDate.toLocalDateOrNull(),
        approvedByUsername = approvedByUsername,
        approvedByFirstname = approvedByFirstname,
        approvedByLastname = approvedByLastname,
        expectedDisbursementDate = expectedDisbursementDate.toLocalDateOrNull(),
        actualDisbursementDate = actualDisbursementDate.toLocalDateOrNull(),
        disbursedByUsername = disbursedByUsername,
        disbursedByFirstname = disbursedByFirstname,
        disbursedByLastname = disbursedByLastname,
        actualMaturityDate = actualMaturityDate.toLocalDateOrNull(),
        expectedMaturityDate = expectedMaturityDate.toLocalDateOrNull(),
    )

fun LoanDetailSummaryDto.toModel(): LoanDetailSummary =
    LoanDetailSummary(
        currency = currency?.toModel(),
        totalPrincipal = totalPrincipal,
        totalCapitalizedIncome = totalCapitalizedIncome,
        totalCapitalizedIncomeAdjustment = totalCapitalizedIncomeAdjustment,
        principalDisbursed = principalDisbursed,
        principalAdjustments = principalAdjustments,
        principalPaid = principalPaid,
        principalWrittenOff = principalWrittenOff,
        principalOutstanding = principalOutstanding,
        principalOverdue = principalOverdue,
        interestCharged = interestCharged,
        interestPaid = interestPaid,
        interestWaived = interestWaived,
        interestWrittenOff = interestWrittenOff,
        interestOutstanding = interestOutstanding,
        interestOverdue = interestOverdue,
        feeChargesCharged = feeChargesCharged,
        feeAdjustments = feeAdjustments,
        feeChargesDueAtDisbursementCharged = feeChargesDueAtDisbursementCharged,
        feeChargesPaid = feeChargesPaid,
        feeChargesWaived = feeChargesWaived,
        feeChargesWrittenOff = feeChargesWrittenOff,
        feeChargesOutstanding = feeChargesOutstanding,
        feeChargesOverdue = feeChargesOverdue,
        penaltyChargesCharged = penaltyChargesCharged,
        penaltyAdjustments = penaltyAdjustments,
        penaltyChargesPaid = penaltyChargesPaid,
        penaltyChargesWaived = penaltyChargesWaived,
        penaltyChargesWrittenOff = penaltyChargesWrittenOff,
        penaltyChargesOutstanding = penaltyChargesOutstanding,
        penaltyChargesOverdue = penaltyChargesOverdue,
        totalExpectedRepayment = totalExpectedRepayment,
        totalRepayment = totalRepayment,
        totalExpectedCostOfLoan = totalExpectedCostOfLoan,
        totalCostOfLoan = totalCostOfLoan,
        totalWaived = totalWaived,
        totalWrittenOff = totalWrittenOff,
        totalOutstanding = totalOutstanding,
        totalOverdue = totalOverdue,
        totalRecovered = totalRecovered,
    )

fun LoanRepaymentScheduleDto.toModel(): LoanRepaymentSchedule =
    LoanRepaymentSchedule(
        currency = currency?.toModel(),
        loanTermInDays = loanTermInDays,
        totalPrincipalDisbursed = totalPrincipalDisbursed,
        totalPrincipalExpected = totalPrincipalExpected,
        totalPrincipalPaid = totalPrincipalPaid,
        totalInterestCharged = totalInterestCharged,
        totalFeeChargesCharged = totalFeeChargesCharged,
        totalPenaltyChargesCharged = totalPenaltyChargesCharged,
        totalWaived = totalWaived,
        totalWrittenOff = totalWrittenOff,
        totalRepaymentExpected = totalRepaymentExpected,
        totalRepayment = totalRepayment,
        totalPaidInAdvance = totalPaidInAdvance,
        totalPaidLate = totalPaidLate,
        totalOutstanding = totalOutstanding,
        totalCredits = totalCredits,
        periods = periods.map { it.toModel() },
    )

fun LoanRepaymentPeriodDto.toModel(): LoanRepaymentPeriod =
    LoanRepaymentPeriod(
        period = period,
        fromDate = fromDate.toLocalDateOrNull(),
        dueDate = dueDate.toLocalDateOrNull(),
        complete = complete,
        daysInPeriod = daysInPeriod,
        principalOriginalDue = principalOriginalDue,
        principalDue = principalDue,
        principalPaid = principalPaid,
        principalWrittenOff = principalWrittenOff,
        principalOutstanding = principalOutstanding,
        principalLoanBalanceOutstanding = principalLoanBalanceOutstanding,
        interestOriginalDue = interestOriginalDue,
        interestDue = interestDue,
        interestPaid = interestPaid,
        interestWaived = interestWaived,
        interestWrittenOff = interestWrittenOff,
        interestOutstanding = interestOutstanding,
        feeChargesDue = feeChargesDue,
        feeChargesPaid = feeChargesPaid,
        feeChargesWaived = feeChargesWaived,
        feeChargesWrittenOff = feeChargesWrittenOff,
        feeChargesOutstanding = feeChargesOutstanding,
        penaltyChargesDue = penaltyChargesDue,
        penaltyChargesPaid = penaltyChargesPaid,
        penaltyChargesWaived = penaltyChargesWaived,
        penaltyChargesWrittenOff = penaltyChargesWrittenOff,
        penaltyChargesOutstanding = penaltyChargesOutstanding,
        totalOriginalDueForPeriod = totalOriginalDueForPeriod,
        totalDueForPeriod = totalDueForPeriod,
        totalPaidForPeriod = totalPaidForPeriod,
        totalPaidInAdvanceForPeriod = totalPaidInAdvanceForPeriod,
        totalPaidLateForPeriod = totalPaidLateForPeriod,
        totalWaivedForPeriod = totalWaivedForPeriod,
        totalWrittenOffForPeriod = totalWrittenOffForPeriod,
        totalOutstandingForPeriod = totalOutstandingForPeriod,
        totalActualCostOfLoanForPeriod = totalActualCostOfLoanForPeriod,
        totalInstallmentAmountForPeriod = totalInstallmentAmountForPeriod,
        totalCredits = totalCredits,
        totalAccruedInterest = totalAccruedInterest,
        downPaymentPeriod = downPaymentPeriod,
    )

fun LoanTransactionDto.toModel(): LoanTransaction =
    LoanTransaction(
        id = id,
        loanId = loanId,
        officeId = officeId,
        officeName = officeName,
        type = type?.toModel(),
        date = date.toLocalDateOrNull(),
        currency = currency?.toModel(),
        amount = amount,
        netDisbursalAmount = netDisbursalAmount,
        principalPortion = principalPortion,
        interestPortion = interestPortion,
        feeChargesPortion = feeChargesPortion,
        penaltyChargesPortion = penaltyChargesPortion,
        overpaymentPortion = overpaymentPortion,
        unrecognizedIncomePortion = unrecognizedIncomePortion,
        outstandingLoanBalance = outstandingLoanBalance,
        submittedOnDate = submittedOnDate.toLocalDateOrNull(),
        manuallyReversed = manuallyReversed,
    )

fun LoanTransactionTypeDto.toModel(): LoanTransactionType =
    LoanTransactionType(
        id = id,
        code = code,
        value = value,
        disbursement = disbursement,
        repaymentAtDisbursement = repaymentAtDisbursement,
        repayment = repayment,
        merchantIssuedRefund = merchantIssuedRefund,
        payoutRefund = payoutRefund,
        goodwillCredit = goodwillCredit,
        interestPaymentWaiver = interestPaymentWaiver,
        chargeRefund = chargeRefund,
        contra = contra,
        waiveInterest = waiveInterest,
        waiveCharges = waiveCharges,
        accrual = accrual,
        writeOff = writeOff,
        recoveryRepayment = recoveryRepayment,
        initiateTransfer = initiateTransfer,
        approveTransfer = approveTransfer,
        withdrawTransfer = withdrawTransfer,
        rejectTransfer = rejectTransfer,
        chargePayment = chargePayment,
        refund = refund,
        refundForActiveLoans = refundForActiveLoans,
        creditBalanceRefund = creditBalanceRefund,
        chargeAdjustment = chargeAdjustment,
        chargeback = chargeback,
        chargeoff = chargeoff,
        downPayment = downPayment,
        reAge = reAge,
        reAmortize = reAmortize,
        accrualActivity = accrualActivity,
        interestRefund = interestRefund,
        accrualAdjustment = accrualAdjustment,
        capitalizedIncome = capitalizedIncome,
        capitalizedIncomeAmortization = capitalizedIncomeAmortization,
        capitalizedIncomeAdjustment = capitalizedIncomeAdjustment,
        capitalizedIncomeAmortizationAdjustment = capitalizedIncomeAmortizationAdjustment,
        contractTermination = contractTermination,
        buyDownFee = buyDownFee,
        buyDownFeeAdjustment = buyDownFeeAdjustment,
        buyDownFeeAmortization = buyDownFeeAmortization,
        buyDownFeeAmortizationAdjustment = buyDownFeeAmortizationAdjustment,
    )

private fun List<Int>?.toLocalDateOrNull(): LocalDate? {
    if (this == null || this.size < 3) return null
    return try {
        LocalDate(this[0], this[1], this[2])
    } catch (e: Exception) {
        null
    }
}
