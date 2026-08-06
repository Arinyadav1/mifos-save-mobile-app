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
data class SavingDetailDto(
    val id: Long = 0,
    val accountNo: String? = null,
    val depositType: SavingPeriodTypeDto? = null,
    val clientId: Long = 0,
    val clientName: String? = null,
    val savingsProductId: Long = 0,
    val savingsProductName: String? = null,
    val fieldOfficerId: Long = 0,
    val status: SavingDetailStatusDto? = null,
    val subStatus: SavingDetailSubStatusDto? = null,
    val timeline: SavingDetailTimelineDto? = null,
    val currency: SavingDetailCurrencyDto? = null,
    val nominalAnnualInterestRate: Double = 0.0,
    val interestCompoundingPeriodType: SavingPeriodTypeDto? = null,
    val interestPostingPeriodType: SavingPeriodTypeDto? = null,
    val interestCalculationType: SavingPeriodTypeDto? = null,
    val interestCalculationDaysInYearType: SavingPeriodTypeDto? = null,
    val withdrawalFeeForTransfers: Boolean = false,
    val allowOverdraft: Boolean = false,
    val enforceMinRequiredBalance: Boolean = false,
    val lienAllowed: Boolean = false,
    val withHoldTax: Boolean = false,
    val lastActiveTransactionDate: List<Int>? = null,
    val isDormancyTrackingActive: Boolean = false,
    val summary: SavingDetailSummaryDto? = null,
    val transactions: List<SavingDetailTransactionDto> = emptyList(),
    val interestCalculationDaysInYearTypeId: Int = 0,
    val activationLocalDate: List<Int>? = null,
    val interestCalculationTypeId: Int = 0,
    val existingTransactionIds: List<Long> = emptyList(),
    val depositTypeId: Int = 0,
    val existingReversedTransactionIds: List<Long> = emptyList(),
    val newSavingsAccountTransactionData: List<SavingDetailTransactionDto> = emptyList(),
    val savingsAccountTransactionData: List<SavingDetailTransactionDto> = emptyList(),
    val interestPostingPeriodTypeId: Int = 0,
    val interestCompoundingPeriodTypeId: Int = 0,
    val savingsAccountTransactionsWithPivotConfig: List<SavingDetailTransactionDto> = emptyList(),
    val startInterestCalculationDate: List<Int>? = null,
    val cashBasedAccountingEnabledOnSavingsProduct: Boolean = false,
    val accrualBasedAccountingEnabledOnSavingsProduct: Boolean = false,
)

@Serializable
data class SavingDetailStatusDto(
    val id: Long = 0,
    val code: String? = null,
    val value: String? = null,
    val submittedAndPendingApproval: Boolean = false,
    val approved: Boolean = false,
    val rejected: Boolean = false,
    val withdrawnByApplicant: Boolean = false,
    val active: Boolean = false,
    val closed: Boolean = false,
    val prematureClosed: Boolean = false,
    val transferInProgress: Boolean = false,
    val transferOnHold: Boolean = false,
    val matured: Boolean = false,
)

@Serializable
data class SavingDetailSubStatusDto(
    val id: Long = 0,
    val code: String? = null,
    val value: String? = null,
    val none: Boolean = false,
    val inactive: Boolean = false,
    val dormant: Boolean = false,
    val escheat: Boolean = false,
    val block: Boolean = false,
    val blockCredit: Boolean = false,
    val blockDebit: Boolean = false,
)

@Serializable
data class SavingDetailTimelineDto(
    val submittedOnDate: List<Int>? = null,
    val submittedByUsername: String? = null,
    val submittedByFirstname: String? = null,
    val submittedByLastname: String? = null,
    val approvedOnDate: List<Int>? = null,
    val approvedByUsername: String? = null,
    val approvedByFirstname: String? = null,
    val approvedByLastname: String? = null,
    val activatedOnDate: List<Int>? = null,
    val activatedByUsername: String? = null,
    val activatedByFirstname: String? = null,
    val activatedByLastname: String? = null,
)

@Serializable
data class SavingDetailCurrencyDto(
    val code: String? = null,
    val name: String? = null,
    val decimalPlaces: Int = 0,
    val displaySymbol: String? = null,
    val nameCode: String? = null,
    val displayLabel: String? = null,
)

@Serializable
data class SavingPeriodTypeDto(
    val id: Long = 0,
    val code: String? = null,
    val value: String? = null,
)

@Serializable
data class SavingDetailSummaryDto(
    val currency: SavingDetailCurrencyDto? = null,
    val totalDeposits: Double = 0.0,
    val totalInterestPosted: Double = 0.0,
    val accountBalance: Double = 0.0,
    val totalOverdraftInterestDerived: Double = 0.0,
    val interestNotPosted: Double = 0.0,
    val availableBalance: Double = 0.0,
    val runningBalanceOnInterestPostingTillDate: Double = 0.0,
    val runningBalanceOnPivotDate: Double = 0.0,
)

@Serializable
data class SavingDetailTransactionDto(
    val id: Long = 0,
    val transactionType: SavingDetailTransactionTypeDto? = null,
    val paymentDetailData: SavingDetailPaymentDetailDataDto? = null,
    val externalId: String? = null,
    val currency: SavingDetailCurrencyDto? = null,
    val amount: Double = 0.0,
    val runningBalance: Double = 0.0,
    val reversed: Boolean = false,
    val transfer: SavingDetailTransferDto? = null,
    val submittedOnDate: List<Int>? = null,
    val interestedPostedAsOn: Boolean = false,
    val submittedByUsername: String? = null,
    val isReversal: Boolean = false,
    val originalTransactionId: Long = 0,
    val isManualTransaction: Boolean = false,
    val lienTransaction: Boolean = false,
    val releaseTransactionId: Long = 0,
    val isOverdraft: Boolean = false,
    val entryType: String? = null,
    val accountId: Long = 0,
    val accountNo: String? = null,
    val date: List<Int>? = null,
    val chargesPaidByData: List<SavingDetailChargePaidDto> = emptyList(),
    val feeCharge: Boolean = false,
    val notReversed: Boolean = false,
    val waiveCharge: Boolean = false,
    val chargeTransaction: Boolean = false,
    val manualTransaction: Boolean = false,
    val reversalTransaction: Boolean = false,
    val savingsAccountChargesPaid: List<SavingDetailChargePaidDto> = emptyList(),
    val interestPostingAndNotReversed: Boolean = false,
    val overdraftInterestAndNotReversed: Boolean = false,
    val payCharge: Boolean = false,
    val amountOnHold: Boolean = false,
    val chargeTransactionAndNotReversed: Boolean = false,
    val waiveFeeCharge: Boolean = false,
    val waivePenaltyCharge: Boolean = false,
    val debit: Boolean = false,
    val taxDetails: List<SavingDetailTaxDetailDto> = emptyList(),
    val credit: Boolean = false,
    val depositAndNotReversed: Boolean = false,
    val annualFeeAndNotReversed: Boolean = false,
    val feeChargeAndNotReversed: Boolean = false,
    val withHoldTaxAndNotReversed: Boolean = false,
    val dividendPayoutAndNotReversed: Boolean = false,
    val withdrawalFeeAndNotReversed: Boolean = false,
    val waiveFeeChargeAndNotReversed: Boolean = false,
    val penaltyChargeAndNotReversed: Boolean = false,
    val waivePenaltyChargeAndNotReversed: Boolean = false,
    val interestPosting: Boolean = false,
    val penaltyCharge: Boolean = false,
    val accrual: Boolean = false,
    val withdrawal: Boolean = false,
    val deposit: Boolean = false,
    val amountRelease: Boolean = false,
    val annualFee: Boolean = false,
)

@Serializable
data class SavingDetailTransactionTypeDto(
    val id: Long = 0,
    val code: String? = null,
    val value: String? = null,
    val deposit: Boolean = false,
    val dividendPayout: Boolean = false,
    val withdrawal: Boolean = false,
    val interestPosting: Boolean = false,
    val feeDeduction: Boolean = false,
    val initiateTransfer: Boolean = false,
    val approveTransfer: Boolean = false,
    val withdrawTransfer: Boolean = false,
    val rejectTransfer: Boolean = false,
    val overdraftInterest: Boolean = false,
    val writtenoff: Boolean = false,
    val overdraftFee: Boolean = false,
    val withholdTax: Boolean = false,
    val escheat: Boolean = false,
    val amountHold: Boolean = false,
    val amountRelease: Boolean = false,
    val accrual: Boolean = false,
    val depositOrWithdrawal: Boolean = false,
    val transactionTypeEnum: String? = null,
    val chargeTransaction: Boolean = false,
    val withdrawalFee: Boolean = false,
    val payCharge: Boolean = false,
    val overDraftInterestPosting: Boolean = false,
    val incomeFromInterest: Boolean = false,
    val debit: Boolean = false,
    val credit: Boolean = false,
    val entryType: String? = null,
    val annualFee: Boolean = false,
)

@Serializable
data class SavingDetailPaymentDetailDataDto(
    val id: Long = 0,
    val paymentType: SavingPeriodTypeDto? = null,
    val accountNumber: String? = null,
    val checkNumber: String? = null,
    val routingCode: String? = null,
    val receiptNumber: String? = null,
    val bankNumber: String? = null,
)

@Serializable
data class SavingDetailTransferDto(
    val id: Long = 0,
    val reversed: Boolean = false,
    val currency: SavingDetailCurrencyDto? = null,
    val transferAmount: Double = 0.0,
    val transferDate: String? = null,
    val transferDescription: String? = null,
)

@Serializable
data class SavingDetailChargePaidDto(
    val id: Long = 0,
)

@Serializable
data class SavingDetailTaxDetailDto(
    val id: Long = 0,
)
