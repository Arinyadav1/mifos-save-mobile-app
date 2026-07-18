/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See See https://github.com/openMF/kmp-project-template/blob/main/LICENSE
 */
package org.mifos.core.network.commonDto

import kotlinx.serialization.Serializable
import org.mifos.core.network.fineract.group.dto.GroupDto

@Serializable
data class PageResponseDto(
    val totalFilteredRecords: Int = 0,
    val pageItems: List<GroupDto> = emptyList(),
)
