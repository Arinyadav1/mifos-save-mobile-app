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

data class SavingsTransactionTemplate(
    val accountId: Long,
    val accountNo: String?,
    val date: LocalDate?,
    val currency: SavingDetailCurrency?,
    val paymentTypeOptions: List<PaymentTypeOption>,
)

data class PaymentTypeOption(
    val id: Long,
    val name: String?,
    val position: Int?,
)
