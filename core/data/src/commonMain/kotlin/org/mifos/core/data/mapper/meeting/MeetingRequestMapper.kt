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

import kotlinx.datetime.number
import org.mifos.core.model.meeting.CreateMeetingRequest
import org.mifos.core.network.fineract.meeting.dto.CreateMeetingRequestDto

fun CreateMeetingRequest.toDto(): CreateMeetingRequestDto {
    val day = meetingDate.day.toString().padStart(2, '0')
    val month = meetingDate.month.number.toString().padStart(2, '0')
    val year = meetingDate.year
    val formattedDate = "$year-$month-$day"

    return CreateMeetingRequestDto(
        meetingDate = formattedDate,
        startTime = startTime,
        endTime = endTime,
        title = title,
        location = location,
        meetingLink = meetingLink,
        description = description,
        meetingStatusCdStatus = "798",
    )
}
