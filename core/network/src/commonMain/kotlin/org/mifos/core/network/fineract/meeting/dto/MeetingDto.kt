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

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MeetingDto(
    val id: Long,
    @SerialName("group_id") val groupId: Long,
    val meetingDate: String?,
    val startTime: String?,
    val endTime: String?,
    val title: String?,
    @SerialName("meetingStatus_cd_status") val meetingStatusCdStatus: Long,
    val location: String?,
    val meetingLink: String?,
    val description: String?,
    @SerialName("created_at") val createdAt: List<Int>?,
    @SerialName("updated_at") val updatedAt: List<Int>?,
)
