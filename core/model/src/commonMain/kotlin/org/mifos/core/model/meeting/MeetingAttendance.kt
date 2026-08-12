/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See See https://github.com/openMF/kmp-project-template/blob/main/LICENSE
 */
package org.mifos.core.model.meeting

import kotlinx.datetime.LocalDateTime

data class MeetingAttendance(
    val id: Long,
    val groupId: Long,
    val meetingId: Long,
    val memberId: Long,
    val attendanceStatusCdStatus: Long,
    val memberName: String?,
    val memberAccountNumber: String?,
    val remark: String?,
    val createdAt: LocalDateTime?,
    val updatedAt: LocalDateTime?,
)
