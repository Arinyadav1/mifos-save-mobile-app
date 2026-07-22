/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See See https://github.com/openMF/kmp-project-template/blob/main/LICENSE
 */
package org.mifos.core.model.client

data class ClientAccounts(
    val loanAccounts: List<LoanAccount>,
)

data class LoanAccount(
    val id: Long,
    val accountNo: String?,
    val externalId: String?,
    val productId: Long,
    val productName: String?,
    val status: LoanAccountStatus?,
    val loanType: LoanType?,
    val loanCycle: Int,
)

data class LoanAccountStatus(
    val id: Long,
    val code: String?,
    val value: String?,
    val pendingApproval: Boolean,
    val waitingForDisbursal: Boolean,
    val active: Boolean,
    val closedObligationsMet: Boolean,
    val closedWrittenOff: Boolean,
    val closedRescheduled: Boolean,
    val closed: Boolean,
    val overpaid: Boolean,
)

data class LoanType(
    val id: Long,
    val code: String?,
    val value: String?,
)
