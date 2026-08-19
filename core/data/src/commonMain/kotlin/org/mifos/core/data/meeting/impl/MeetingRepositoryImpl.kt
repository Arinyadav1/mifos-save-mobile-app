/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See See https://github.com/openMF/kmp-project-template/blob/main/LICENSE
 */
package org.mifos.core.data.meeting.impl

import io.ktor.http.isSuccess
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.datetime.LocalDate
import org.mifos.core.base.common.manager.DispatcherManager
import org.mifos.core.base.store.screen.ScreenState
import org.mifos.core.data.infra.NetworkMonitor
import org.mifos.core.data.mapper.meeting.toDto
import org.mifos.core.data.mapper.meeting.toModel
import org.mifos.core.data.meeting.MeetingRepository
import org.mifos.core.data.util.asScreenStateFlow
import org.mifos.core.data.util.extractErrorMessage
import org.mifos.core.data.util.runAsDataState
import org.mifos.core.model.meeting.AttendanceStatus
import org.mifos.core.model.meeting.CreateMeetingRequest
import org.mifos.core.model.meeting.Meeting
import org.mifos.core.model.meeting.MeetingAttendance
import org.mifos.core.model.meeting.MeetingRepetitionType
import org.mifos.core.model.meeting.MeetingStatus
import org.mifos.core.network.DataManager
import org.mifos.core.network.fineract.meeting.dto.MeetingAttendanceRequestDto
import org.mifos.core.network.fineract.meeting.dto.UpdateMeetingStatusRequestDto

class MeetingRepositoryImpl(
    private val dataManager: DataManager,
    private val networkMonitor: NetworkMonitor,
    private val dispatcher: DispatcherManager,
) : MeetingRepository {

    override fun getGroupMeetings(groupId: Long): Flow<ScreenState<List<Meeting>>> {
        return combine(
            dataManager.fineract.meetingApi.getGroupMeetings(groupId),
            dataManager.fineract.meetingApi.getMeetingStatuses(),
        ) { meetingsDto, statusesDto ->
            val statusMap = statusesDto.associate { it.id to it.name }
            meetingsDto.map { it.toModel(statusMap) }
        }.asScreenStateFlow(
            networkMonitor = networkMonitor,
            dispatcher = dispatcher.io,
        )
    }

    override fun getMeetingStatuses(): Flow<ScreenState<List<MeetingStatus>>> {
        return dataManager.fineract.meetingApi.getMeetingStatuses()
            .map { list -> list.map { it.toModel() } }
            .asScreenStateFlow(
                networkMonitor = networkMonitor,
                dispatcher = dispatcher.io,
            )
    }

    override fun getMeetingAttendance(groupId: Long): Flow<ScreenState<List<MeetingAttendance>>> {
        return dataManager.fineract.meetingApi.getMeetingAttendance(groupId)
            .map { list -> list.map { it.toModel() } }
            .asScreenStateFlow(
                networkMonitor = networkMonitor,
                dispatcher = dispatcher.io,
            )
    }

    override fun getAttendanceStatuses(): Flow<ScreenState<List<AttendanceStatus>>> {
        return dataManager.fineract.meetingApi.getAttendanceStatuses()
            .map { list -> list.map { it.toModel() } }
            .asScreenStateFlow(
                networkMonitor = networkMonitor,
                dispatcher = dispatcher.io,
            )
    }

    override suspend fun saveMeetingAttendance(
        groupId: Long,
        meetingId: Long,
        memberId: Long,
        attendanceStatusCdStatus: Long,
        memberName: String?,
        memberAccountNumber: String?,
        remark: String?,
    ): ScreenState<Unit> {
        return runAsDataState(
            networkMonitor = networkMonitor,
            context = dispatcher.io,
        ) {
            val response = dataManager.fineract.meetingApi.saveMeetingAttendance(
                groupId = groupId,
                request = MeetingAttendanceRequestDto(
                    meetingId = meetingId.toString(),
                    memberId = memberId.toString(),
                    attendanceStatusCdStatus = attendanceStatusCdStatus.toString(),
                    memberName = memberName,
                    memberAccountNumber = memberAccountNumber,
                    remark = remark,
                ),
            )
            if (!response.status.isSuccess()) {
                val errorMessage = extractErrorMessage(response)
                throw Exception(errorMessage)
            }
        }
    }

    override suspend fun saveBulkMeetingAttendance(
        groupId: Long,
        meetingId: Long,
        records: List<MeetingAttendance>,
    ): ScreenState<Unit> {
        return runAsDataState(
            networkMonitor = networkMonitor,
            context = dispatcher.io,
        ) {
            coroutineScope {
                records.map { record ->
                    async {
                        val response = dataManager.fineract.meetingApi.saveMeetingAttendance(
                            groupId = groupId,
                            request = MeetingAttendanceRequestDto(
                                meetingId = meetingId.toString(),
                                memberId = record.memberId.toString(),
                                attendanceStatusCdStatus = record.attendanceStatusCdStatus.toString(),
                                memberName = record.memberName,
                                memberAccountNumber = record.memberAccountNumber,
                                remark = record.remark,
                            ),
                        )
                        if (!response.status.isSuccess()) {
                            val errorMessage = extractErrorMessage(response)
                            throw Exception(errorMessage)
                        }
                    }
                }.awaitAll()
            }
        }
    }

    override suspend fun updateMeetingStatus(
        groupId: Long,
        meetingId: Long,
        meetingStatusCdStatus: Long,
    ): ScreenState<Unit> {
        return runAsDataState(
            networkMonitor = networkMonitor,
            context = dispatcher.io,
        ) {
            val response = dataManager.fineract.meetingApi.updateMeetingStatus(
                groupId = groupId,
                meetingId = meetingId,
                request = UpdateMeetingStatusRequestDto(
                    meetingStatusCdStatus = meetingStatusCdStatus.toString(),
                ),
            )
            if (!response.status.isSuccess()) {
                val errorMessage = extractErrorMessage(response)
                throw Exception(errorMessage)
            }
        }
    }

    override suspend fun scheduleMeeting(
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
        customInterval: Int,
    ): ScreenState<Unit> {
        return runAsDataState(
            networkMonitor = networkMonitor,
            context = dispatcher.io,
        ) {
            val dates = mutableListOf<LocalDate>()
            var current = startDate
            while (current <= endDate) {
                val shouldAdd = when (repetitionType) {
                    MeetingRepetitionType.DAILY -> true
                    MeetingRepetitionType.WEEKLY -> current.dayOfWeek == startDate.dayOfWeek
                    MeetingRepetitionType.MONTHLY -> current.day == startDate.day
                    MeetingRepetitionType.CUSTOM -> true
                }
                if (shouldAdd) {
                    dates.add(current)
                }
                val step = if (repetitionType == MeetingRepetitionType.CUSTOM) customInterval else 1
                current = LocalDate.fromEpochDays(current.toEpochDays() + step)
            }

            for (date in dates) {
                val createRequest = CreateMeetingRequest(
                    meetingDate = date,
                    startTime = startTime,
                    endTime = endTime,
                    title = title,
                    location = location.orEmpty().ifBlank { null },
                    meetingLink = meetingLink.orEmpty().ifBlank { null },
                    description = description.orEmpty().ifBlank { null },
                )
                val response = dataManager.fineract.meetingApi.createGroupMeeting(
                    groupId = groupId,
                    request = createRequest.toDto(),
                )
                if (!response.status.isSuccess()) {
                    val errorMessage = extractErrorMessage(response)
                    throw Exception(errorMessage)
                }
            }
        }
    }
}
