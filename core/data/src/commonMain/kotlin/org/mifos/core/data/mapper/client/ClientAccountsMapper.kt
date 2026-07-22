/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See See https://github.com/openMF/kmp-project-template/blob/main/LICENSE
 */
package org.mifos.core.data.mapper.client

import org.mifos.core.model.client.ClientAccounts
import org.mifos.core.model.client.LoanAccount
import org.mifos.core.model.client.LoanAccountStatus
import org.mifos.core.model.client.LoanType
import org.mifos.core.network.fineract.client.dto.ClientAccountsDto
import org.mifos.core.network.fineract.client.dto.LoanAccountDto
import org.mifos.core.network.fineract.client.dto.LoanAccountStatusDto
import org.mifos.core.network.fineract.client.dto.LoanTypeDto

fun ClientAccountsDto.toModel(): ClientAccounts =
    ClientAccounts(
        loanAccounts = loanAccounts?.map { it.toModel() } ?: emptyList(),
    )

fun LoanAccountDto.toModel(): LoanAccount =
    LoanAccount(
        id = id,
        accountNo = accountNo,
        externalId = externalId,
        productId = productId,
        productName = productName,
        status = status?.toModel(),
        loanType = loanType?.toModel(),
        loanCycle = loanCycle,
    )

fun LoanAccountStatusDto.toModel(): LoanAccountStatus =
    LoanAccountStatus(
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

fun LoanTypeDto.toModel(): LoanType =
    LoanType(
        id = id,
        code = code,
        value = value,
    )
