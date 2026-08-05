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

import org.mifos.core.model.group.OfficeOption
import org.mifos.core.model.savings.SavingsProductOption
import org.mifos.core.model.savings.StaffOption

data class ClientTemplate(
    val activationDate: List<Int>?,
    val officeId: Long?,
    val officeOptions: List<OfficeOption>,
    val staffOptions: List<StaffOption>,
    val savingProductOptions: List<SavingsProductOption>,
    val datatables: List<Datatable>,
)

data class Datatable(
    val applicationTableName: String?,
    val registeredTableName: String?,
    val columnHeaderData: List<ColumnHeaderData>,
)

data class ColumnHeaderData(
    val columnName: String?,
    val columnType: String?,
    val columnLength: Int?,
    val columnDisplayType: String?,
    val isColumnNullable: Boolean?,
    val isColumnPrimaryKey: Boolean?,
    val columnValues: List<ColumnValue>,
    val columnCode: String?,
)

data class ColumnValue(
    val id: Long?,
    val value: String?,
    val score: Int?,
)
