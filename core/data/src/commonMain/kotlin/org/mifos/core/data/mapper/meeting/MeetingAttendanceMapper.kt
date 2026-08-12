/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See See https://github.com/openMF/kmp-project-template/blob/main/LICENSE
 */
package org.mifos.core.data.mapper.meeting

import kotlinx.datetime.LocalDateTime
import org.mifos.core.model.meeting.AttendanceStatus
import org.mifos.core.model.meeting.MeetingAttendance
import org.mifos.core.network.fineract.meeting.dto.AttendanceStatusDto
import org.mifos.core.network.fineract.meeting.dto.MeetingAttendanceResponseDto

fun MeetingAttendanceResponseDto.toModel(): MeetingAttendance {
    return MeetingAttendance(
        id = id,
        groupId = groupId,
        meetingId = meetingId.toLongOrNull() ?: 0L,
        memberId = memberId.toLongOrNull() ?: 0L,
        attendanceStatusCdStatus = attendanceStatusCdStatus,
        memberName = memberName,
        memberAccountNumber = memberAccountNumber,
        remark = remark,
        createdAt = createdAt.toLocalDateTimeOrNull(),
        updatedAt = updatedAt.toLocalDateTimeOrNull(),
    )
}

fun AttendanceStatusDto.toModel(): AttendanceStatus {
    return AttendanceStatus(
        id = id,
        name = name.orEmpty(),
        position = position,
        description = description,
        active = active ?: false,
        mandatory = mandatory ?: false,
    )
}

private fun List<Int>?.toLocalDateTimeOrNull(): LocalDateTime? {
    if (this == null || this.size < 3) return null
    return try {
        val year = this[0]
        val month = this[1]
        val day = this[2]
        val hour = this.getOrNull(3) ?: 0
        val minute = this.getOrNull(4) ?: 0
        val second = this.getOrNull(5) ?: 0
        val nanosecond = this.getOrNull(6) ?: 0
        LocalDateTime(year, month, day, hour, minute, second, nanosecond)
    } catch (e: Exception) {
        null
    }
}
