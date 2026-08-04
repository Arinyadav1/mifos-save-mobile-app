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
import org.mifos.core.model.savings.PaymentTypeOption
import org.mifos.core.model.savings.SavingsTransactionTemplate
import org.mifos.core.network.fineract.savings.dto.PaymentTypeOptionDto
import org.mifos.core.network.fineract.savings.dto.SavingsTransactionTemplateDto

fun SavingsTransactionTemplateDto.toModel(): SavingsTransactionTemplate =
    SavingsTransactionTemplate(
        accountId = accountId ?: 0L,
        accountNo = accountNo,
        date = date.toLocalDateOrNull(),
        currency = currency?.toModel(),
        paymentTypeOptions = paymentTypeOptions?.map { it.toModel() }.orEmpty(),
    )

fun PaymentTypeOptionDto.toModel(): PaymentTypeOption =
    PaymentTypeOption(
        id = id ?: 0L,
        name = name,
        position = position,
    )

private fun List<Int>?.toLocalDateOrNull(): LocalDate? {
    if (this == null || this.size < 3) return null
    return try {
        LocalDate(this[0], this[1], this[2])
    } catch (e: Exception) {
        null
    }
}
