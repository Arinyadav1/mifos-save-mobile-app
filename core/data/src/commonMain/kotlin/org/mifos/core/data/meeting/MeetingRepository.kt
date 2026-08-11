/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See See https://github.com/openMF/kmp-project-template/blob/main/LICENSE
 */
package org.mifos.core.data.meeting

import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.LocalDate
import org.mifos.core.base.store.screen.ScreenState
import org.mifos.core.model.meeting.Meeting
import org.mifos.core.model.meeting.MeetingRepetitionType

interface MeetingRepository {
    fun getGroupMeetings(groupId: Long): Flow<ScreenState<List<Meeting>>>

    suspend fun scheduleMeeting(
        groupId: Long,
        title: String,
        startDate: LocalDate,
        endDate: LocalDate,
        startTime: String,
        endTime: String,
        repetitionType: MeetingRepetitionType,
        location: String?,
        meetingLink: String?,
        description: String?,
        customInterval: Int = 1,
    ): ScreenState<Unit>
}
