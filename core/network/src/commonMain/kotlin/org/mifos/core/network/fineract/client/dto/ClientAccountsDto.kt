/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See See https://github.com/openMF/kmp-project-template/blob/main/LICENSE
 */
package org.mifos.core.network.fineract.client.dto

import kotlinx.serialization.Serializable

@Serializable
data class ClientAccountsDto(
    val loanAccounts: List<LoanAccountDto>? = null,
)

@Serializable
data class LoanAccountDto(
    val id: Long = 0,
    val accountNo: String? = null,
    val externalId: String? = null,
    val productId: Long = 0,
    val productName: String? = null,
    val status: LoanAccountStatusDto? = null,
    val loanType: LoanTypeDto? = null,
    val loanCycle: Int = 0,
)

@Serializable
data class LoanAccountStatusDto(
    val id: Long = 0,
    val code: String? = null,
    val value: String? = null,
    val pendingApproval: Boolean = false,
    val waitingForDisbursal: Boolean = false,
    val active: Boolean = false,
    val closedObligationsMet: Boolean = false,
    val closedWrittenOff: Boolean = false,
    val closedRescheduled: Boolean = false,
    val closed: Boolean = false,
    val overpaid: Boolean = false,
)

@Serializable
data class LoanTypeDto(
    val id: Long = 0,
    val code: String? = null,
    val value: String? = null,
)

@Serializable
data class ClientStatusDto(
    val id: Long = 0,
    val code: String? = null,
    val value: String? = null,
)

@Serializable
data class ClientTimelineDto(
    val submittedOnDate: List<Int>? = null,
    val submittedByUsername: String? = null,
    val submittedByFirstname: String? = null,
    val submittedByLastname: String? = null,
    val activatedOnDate: List<Int>? = null,
    val activatedByUsername: String? = null,
    val activatedByFirstname: String? = null,
    val activatedByLastname: String? = null,
)