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

import kotlinx.datetime.LocalDate

data class SavingDetail(
    val id: Long,
    val accountNo: String?,
    val depositType: SavingPeriodType?,
    val clientId: Long,
    val clientName: String?,
    val savingsProductId: Long,
    val savingsProductName: String?,
    val fieldOfficerId: Long,
    val status: SavingDetailStatus?,
    val subStatus: SavingDetailSubStatus?,
    val timeline: SavingDetailTimeline?,
    val currency: SavingDetailCurrency?,
    val nominalAnnualInterestRate: Double,
    val interestCompoundingPeriodType: SavingPeriodType?,
    val interestPostingPeriodType: SavingPeriodType?,
    val interestCalculationType: SavingPeriodType?,
    val interestCalculationDaysInYearType: SavingPeriodType?,
    val withdrawalFeeForTransfers: Boolean,
    val allowOverdraft: Boolean,
    val enforceMinRequiredBalance: Boolean,
    val lienAllowed: Boolean,
    val withHoldTax: Boolean,
    val lastActiveTransactionDate: LocalDate?,
    val isDormancyTrackingActive: Boolean,
    val summary: SavingDetailSummary?,
    val transactions: List<SavingDetailTransaction>,
    val interestCalculationDaysInYearTypeId: Int,
    val activationLocalDate: LocalDate?,
    val interestCalculationTypeId: Int,
    val existingTransactionIds: List<Long>,
    val depositTypeId: Int,
    val existingReversedTransactionIds: List<Long>,
    val newSavingsAccountTransactionData: List<SavingDetailTransaction>,
    val savingsAccountTransactionData: List<SavingDetailTransaction>,
    val interestPostingPeriodTypeId: Int,
    val interestCompoundingPeriodTypeId: Int,
    val savingsAccountTransactionsWithPivotConfig: List<SavingDetailTransaction>,
    val startInterestCalculationDate: LocalDate?,
    val cashBasedAccountingEnabledOnSavingsProduct: Boolean,
    val accrualBasedAccountingEnabledOnSavingsProduct: Boolean,
)

data class SavingDetailStatus(
    val id: Long,
    val code: String?,
    val value: String?,
    val submittedAndPendingApproval: Boolean,
    val approved: Boolean,
    val rejected: Boolean,
    val withdrawnByApplicant: Boolean,
    val active: Boolean,
    val closed: Boolean,
    val prematureClosed: Boolean,
    val transferInProgress: Boolean,
    val transferOnHold: Boolean,
    val matured: Boolean,
)

data class SavingDetailSubStatus(
    val id: Long,
    val code: String?,
    val value: String?,
    val none: Boolean,
    val inactive: Boolean,
    val dormant: Boolean,
    val escheat: Boolean,
    val block: Boolean,
    val blockCredit: Boolean,
    val blockDebit: Boolean,
)

data class SavingDetailTimeline(
    val submittedOnDate: LocalDate?,
    val submittedByUsername: String?,
    val submittedByFirstname: String?,
    val submittedByLastname: String?,
    val approvedOnDate: LocalDate?,
    val approvedByUsername: String?,
    val approvedByFirstname: String?,
    val approvedByLastname: String?,
    val activatedOnDate: LocalDate?,
    val activatedByUsername: String?,
    val activatedByFirstname: String?,
    val activatedByLastname: String?,
)

data class SavingDetailCurrency(
    val code: String?,
    val name: String?,
    val decimalPlaces: Int,
    val displaySymbol: String?,
    val nameCode: String?,
    val displayLabel: String?,
)

data class SavingPeriodType(
    val id: Long,
    val code: String?,
    val value: String?,
)

data class SavingDetailSummary(
    val currency: SavingDetailCurrency?,
    val totalDeposits: Double,
    val totalInterestPosted: Double,
    val accountBalance: Double,
    val totalOverdraftInterestDerived: Double,
    val interestNotPosted: Double,
    val availableBalance: Double,
    val runningBalanceOnInterestPostingTillDate: Double,
    val runningBalanceOnPivotDate: Double,
)

data class SavingDetailTransaction(
    val id: Long,
    val transactionType: SavingDetailTransactionType?,
    val paymentDetailData: SavingDetailPaymentDetailData?,
    val externalId: String?,
    val currency: SavingDetailCurrency?,
    val amount: Double,
    val runningBalance: Double,
    val reversed: Boolean,
    val transfer: SavingDetailTransfer?,
    val submittedOnDate: LocalDate?,
    val interestedPostedAsOn: Boolean,
    val submittedByUsername: String?,
    val isReversal: Boolean,
    val originalTransactionId: Long,
    val isManualTransaction: Boolean,
    val lienTransaction: Boolean,
    val releaseTransactionId: Long,
    val isOverdraft: Boolean,
    val entryType: String?,
    val accountId: Long,
    val accountNo: String?,
    val date: LocalDate?,
    val chargesPaidByData: List<SavingDetailChargePaid>,
    val feeCharge: Boolean,
    val notReversed: Boolean,
    val waiveCharge: Boolean,
    val chargeTransaction: Boolean,
    val manualTransaction: Boolean,
    val reversalTransaction: Boolean,
    val savingsAccountChargesPaid: List<SavingDetailChargePaid>,
    val interestPostingAndNotReversed: Boolean,
    val overdraftInterestAndNotReversed: Boolean,
    val payCharge: Boolean,
    val amountOnHold: Boolean,
    val chargeTransactionAndNotReversed: Boolean,
    val waiveFeeCharge: Boolean,
    val waivePenaltyCharge: Boolean,
    val debit: Boolean,
    val taxDetails: List<SavingDetailTaxDetail>,
    val credit: Boolean,
    val depositAndNotReversed: Boolean,
    val annualFeeAndNotReversed: Boolean,
    val feeChargeAndNotReversed: Boolean,
    val withHoldTaxAndNotReversed: Boolean,
    val dividendPayoutAndNotReversed: Boolean,
    val withdrawalFeeAndNotReversed: Boolean,
    val waiveFeeChargeAndNotReversed: Boolean,
    val penaltyChargeAndNotReversed: Boolean,
    val waivePenaltyChargeAndNotReversed: Boolean,
    val interestPosting: Boolean,
    val penaltyCharge: Boolean,
    val accrual: Boolean,
    val withdrawal: Boolean,
    val deposit: Boolean,
    val amountRelease: Boolean,
    val annualFee: Boolean,
)

data class SavingDetailTransactionType(
    val id: Long,
    val code: String?,
    val value: String?,
    val deposit: Boolean,
    val dividendPayout: Boolean,
    val withdrawal: Boolean,
    val interestPosting: Boolean,
    val feeDeduction: Boolean,
    val initiateTransfer: Boolean,
    val approveTransfer: Boolean,
    val withdrawTransfer: Boolean,
    val rejectTransfer: Boolean,
    val overdraftInterest: Boolean,
    val writtenoff: Boolean,
    val overdraftFee: Boolean,
    val withholdTax: Boolean,
    val escheat: Boolean,
    val amountHold: Boolean,
    val amountRelease: Boolean,
    val accrual: Boolean,
    val depositOrWithdrawal: Boolean,
    val transactionTypeEnum: String?,
    val chargeTransaction: Boolean,
    val withdrawalFee: Boolean,
    val payCharge: Boolean,
    val overDraftInterestPosting: Boolean,
    val incomeFromInterest: Boolean,
    val debit: Boolean,
    val credit: Boolean,
    val entryType: String?,
    val annualFee: Boolean,
)

data class SavingDetailPaymentDetailData(
    val id: Long,
    val paymentType: SavingPeriodType?,
    val accountNumber: String?,
    val checkNumber: String?,
    val routingCode: String?,
    val receiptNumber: String?,
    val bankNumber: String?,
)

data class SavingDetailTransfer(
    val id: Long,
    val reversed: Boolean,
    val currency: SavingDetailCurrency?,
    val transferAmount: Double,
    val transferDate: String?,
    val transferDescription: String?,
)

data class SavingDetailChargePaid(
    val id: Long,
)

data class SavingDetailTaxDetail(
    val id: Long,
)

val SavingDetailTransaction.isCredit: Boolean
    get() = credit || deposit ||
        transactionType?.credit == true || transactionType?.deposit == true ||
        transactionType?.interestPosting == true || transactionType?.dividendPayout == true

val SavingDetailTransaction.isDebit: Boolean
    get() = debit || withdrawal ||
        transactionType?.debit == true || transactionType?.withdrawal == true ||
        transactionType?.feeDeduction == true || transactionType?.overdraftFee == true ||
        transactionType?.withholdTax == true || transactionType?.escheat == true
