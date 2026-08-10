/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See See https://github.com/openMF/kmp-project-template/blob/main/LICENSE
 */
package org.mifos.core.network.fineract.meeting.dto

import kotlinx.serialization.Serializable

@Serializable
data class MeetingStatusDto(
    val id: Long,
    val name: String?,
    val position: Int?,
    val description: String?,
    val active: Boolean?,
    val mandatory: Boolean?,
)
