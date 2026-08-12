/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See See https://github.com/openMF/kmp-project-template/blob/main/LICENSE
 */
package org.mifos.core.network.fineract.meeting.apis

import de.jensklingenberg.ktorfit.http.Body
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.POST
import de.jensklingenberg.ktorfit.http.PUT
import de.jensklingenberg.ktorfit.http.Path
import io.ktor.client.statement.HttpResponse
import kotlinx.coroutines.flow.Flow
import org.mifos.core.network.fineract.meeting.dto.AttendanceStatusDto
import org.mifos.core.network.fineract.meeting.dto.CreateMeetingRequestDto
import org.mifos.core.network.fineract.meeting.dto.MeetingAttendanceRequestDto
import org.mifos.core.network.fineract.meeting.dto.MeetingAttendanceResponseDto
import org.mifos.core.network.fineract.meeting.dto.MeetingDto
import org.mifos.core.network.fineract.meeting.dto.MeetingStatusDto
import org.mifos.core.network.fineract.meeting.dto.UpdateMeetingStatusRequestDto

interface MeetingApi {
    @GET("datatables/group_meeting/{groupId}")
    fun getGroupMeetings(
        @Path("groupId") groupId: Long,
    ): Flow<List<MeetingDto>>

    @GET("codes/138/codevalues")
    fun getMeetingStatuses(): Flow<List<MeetingStatusDto>>

    @POST("datatables/group_meeting/{groupId}")
    suspend fun createGroupMeeting(
        @Path("groupId") groupId: Long,
        @Body request: CreateMeetingRequestDto,
    ): HttpResponse

    @POST("datatables/meeting_attendance/{groupId}")
    suspend fun saveMeetingAttendance(
        @Path("groupId") groupId: Long,
        @Body request: MeetingAttendanceRequestDto,
    ): HttpResponse

    @GET("datatables/meeting_attendance/{groupId}")
    fun getMeetingAttendance(
        @Path("groupId") groupId: Long,
    ): Flow<List<MeetingAttendanceResponseDto>>

    @GET("codes/139/codevalues")
    fun getAttendanceStatuses(): Flow<List<AttendanceStatusDto>>

    @PUT("datatables/group_meeting/{groupId}/{meetingId}")
    suspend fun updateMeetingStatus(
        @Path("groupId") groupId: Long,
        @Path("meetingId") meetingId: Long,
        @Body request: UpdateMeetingStatusRequestDto,
    ): HttpResponse
}
