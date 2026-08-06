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
import org.mifos.core.network.fineract.group.dto.OfficeOptionDto
import org.mifos.core.network.fineract.group.dto.StaffOptionDto
import org.mifos.core.network.fineract.savings.dto.SavingsProductOptionDto

@Serializable
data class ClientTemplateDto(
    val activationDate: List<Int>? = null,
    val officeId: Long? = null,
    val officeOptions: List<OfficeOptionDto>? = null,
    val staffOptions: List<StaffOptionDto>? = null,
    val savingProductOptions: List<SavingsProductOptionDto>? = null,
    val datatables: List<DatatableDto?>? = null,
)

@Serializable
data class DatatableDto(
    val applicationTableName: String? = null,
    val registeredTableName: String? = null,
    val columnHeaderData: List<ColumnHeaderDataDto>? = null,
)

@Serializable
data class ColumnHeaderDataDto(
    val columnName: String? = null,
    val columnType: String? = null,
    val columnLength: Int? = null,
    val columnDisplayType: String? = null,
    val isColumnNullable: Boolean? = null,
    val isColumnPrimaryKey: Boolean? = null,
    val columnValues: List<ColumnValueDto>? = null,
    val columnCode: String? = null,
)

@Serializable
data class ColumnValueDto(
    val id: Long? = null,
    val value: String? = null,
    val score: Int? = null,
)
