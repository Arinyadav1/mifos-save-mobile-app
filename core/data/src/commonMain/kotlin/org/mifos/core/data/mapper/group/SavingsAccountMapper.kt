/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See See https://github.com/openMF/kmp-project-template/blob/main/LICENSE
 */
package org.mifos.core.data.mapper.group

import kotlinx.datetime.LocalDate
import org.mifos.core.model.group.GroupAccounts
import org.mifos.core.model.group.SavingsAccount
import org.mifos.core.model.group.SavingsAccountCurrency
import org.mifos.core.model.group.SavingsAccountDepositType
import org.mifos.core.model.group.SavingsAccountStatus
import org.mifos.core.model.group.SavingsAccountSubStatus
import org.mifos.core.model.group.SavingsAccountTimeline
import org.mifos.core.model.group.SavingsAccountType
import org.mifos.core.network.fineract.group.dto.GroupAccountsDto
import org.mifos.core.network.fineract.group.dto.SavingsAccountCurrencyDto
import org.mifos.core.network.fineract.group.dto.SavingsAccountDepositTypeDto
import org.mifos.core.network.fineract.group.dto.SavingsAccountDto
import org.mifos.core.network.fineract.group.dto.SavingsAccountStatusDto
import org.mifos.core.network.fineract.group.dto.SavingsAccountSubStatusDto
import org.mifos.core.network.fineract.group.dto.SavingsAccountTimelineDto
import org.mifos.core.network.fineract.group.dto.SavingsAccountTypeDto

fun GroupAccountsDto.toModel(): GroupAccounts =
    GroupAccounts(
        savingsAccounts = savingsAccounts?.map { it.toModel() } ?: emptyList(),
        memberSavingsAccounts = memberSavingsAccounts?.map { it.toModel() } ?: emptyList(),
    )

fun SavingsAccountDto.toModel(): SavingsAccount =
    SavingsAccount(
        id = id,
        accountNo = accountNo,
        productId = productId,
        productName = productName,
        shortProductName = shortProductName,
        status = status?.toModel(),
        currency = currency?.toModel(),
        accountType = accountType?.toModel(),
        timeline = timeline?.toModel(),
        subStatus = subStatus?.toModel(),
        depositType = depositType?.toModel(),
        lastActiveTransactionDate = lastActiveTransactionDate.toLocalDateOrNull(),
    )

fun SavingsAccountStatusDto.toModel(): SavingsAccountStatus =
    SavingsAccountStatus(
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

fun SavingsAccountCurrencyDto.toModel(): SavingsAccountCurrency =
    SavingsAccountCurrency(
        code = code,
        name = name,
        decimalPlaces = decimalPlaces,
        displaySymbol = displaySymbol,
        nameCode = nameCode,
        displayLabel = displayLabel,
    )

fun SavingsAccountTypeDto.toModel(): SavingsAccountType =
    SavingsAccountType(
        id = id,
        code = code,
        value = value,
    )

fun SavingsAccountTimelineDto.toModel(): SavingsAccountTimeline =
    SavingsAccountTimeline(
        submittedOnDate = submittedOnDate.toLocalDateOrNull(),
        submittedByUsername = submittedByUsername,
        submittedByFirstname = submittedByFirstname,
        submittedByLastname = submittedByLastname,
        approvedOnDate = approvedOnDate.toLocalDateOrNull(),
        approvedByUsername = approvedByUsername,
        approvedByFirstname = approvedByFirstname,
        approvedByLastname = approvedByLastname,
        activatedOnDate = activatedOnDate.toLocalDateOrNull(),
    )

fun SavingsAccountSubStatusDto.toModel(): SavingsAccountSubStatus =
    SavingsAccountSubStatus(
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

fun SavingsAccountDepositTypeDto.toModel(): SavingsAccountDepositType =
    SavingsAccountDepositType(
        id = id,
        code = code,
        value = value,
    )

private fun List<Int>?.toLocalDateOrNull(): LocalDate? {
    if (this == null || this.size < 3) return null
    return try {
        LocalDate(this[0], this[1], this[2])
    } catch (e: Exception) {
        null
    }
}
