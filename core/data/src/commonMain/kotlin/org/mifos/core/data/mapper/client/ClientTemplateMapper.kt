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

import org.mifos.core.model.client.ClientTemplate
import org.mifos.core.model.client.ColumnHeaderData
import org.mifos.core.model.client.ColumnValue
import org.mifos.core.model.client.Datatable
import org.mifos.core.network.fineract.client.dto.ClientTemplateDto
import org.mifos.core.network.fineract.client.dto.ColumnHeaderDataDto
import org.mifos.core.network.fineract.client.dto.ColumnValueDto
import org.mifos.core.network.fineract.client.dto.DatatableDto
import org.mifos.core.data.mapper.group.toModel as toOfficeModel
import org.mifos.core.data.mapper.savings.toModel as toSavingsModel

fun ClientTemplateDto.toModel(): ClientTemplate =
    ClientTemplate(
        activationDate = activationDate,
        officeId = officeId,
        officeOptions = officeOptions?.map { it.toOfficeModel() } ?: emptyList(),
        staffOptions = staffOptions?.map { it.toSavingsModel() } ?: emptyList(),
        savingProductOptions = savingProductOptions?.map { it.toSavingsModel() } ?: emptyList(),
        datatables = datatables?.filterNotNull()?.map { it.toModel() } ?: emptyList(),
    )

fun DatatableDto.toModel(): Datatable =
    Datatable(
        applicationTableName = applicationTableName,
        registeredTableName = registeredTableName,
        columnHeaderData = columnHeaderData?.map { it.toModel() } ?: emptyList(),
    )

fun ColumnHeaderDataDto.toModel(): ColumnHeaderData =
    ColumnHeaderData(
        columnName = columnName,
        columnType = columnType,
        columnLength = columnLength,
        columnDisplayType = columnDisplayType,
        isColumnNullable = isColumnNullable,
        isColumnPrimaryKey = isColumnPrimaryKey,
        columnValues = columnValues?.map { it.toModel() } ?: emptyList(),
        columnCode = columnCode,
    )

fun ColumnValueDto.toModel(): ColumnValue =
    ColumnValue(
        id = id,
        value = value,
        score = score,
    )
