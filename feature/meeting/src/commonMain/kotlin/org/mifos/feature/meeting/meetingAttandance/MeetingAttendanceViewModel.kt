/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See See https://github.com/openMF/kmp-project-template/blob/main/LICENSE
 */
package org.mifos.feature.meeting.meetingAttandance

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.mifos.core.base.store.screen.DataFreshness
import org.mifos.core.base.store.screen.ScreenState
import org.mifos.core.base.store.submit.SubmitState
import org.mifos.core.base.ui.viewmodel.BaseViewModel
import org.mifos.core.data.group.GroupRepository
import org.mifos.core.data.meeting.MeetingRepository
import org.mifos.core.model.group.ClientMember
import org.mifos.core.model.meeting.AttendanceStatus
import org.mifos.core.model.meeting.Meeting
import org.mifos.core.model.meeting.MeetingAttendance

class MeetingAttendanceViewModel(
    savedStateHandle: SavedStateHandle,
    private val meetingRepository: MeetingRepository,
    private val groupRepository: GroupRepository,
) : BaseViewModel<MeetingAttendanceState, MeetingAttendanceEvent, MeetingAttendanceAction>(
    MeetingAttendanceState(
        groupId = savedStateHandle.toRoute<MeetingAttendanceRoute>().groupId,
        meetingId = savedStateHandle.toRoute<MeetingAttendanceRoute>().meetingId,
    ),
) {
    val groupId = stateFlow.value.groupId
    val meetingId = stateFlow.value.meetingId

    init {
        loadData()
    }

    private fun loadData() {
        mutableStateFlow.update {
            it.copy(screenState = ScreenState.Loading)
        }
        viewModelScope.launch {
            combine(
                groupRepository.getGroupDetails(groupId),
                meetingRepository.getMeetingAttendance(groupId),
                meetingRepository.getAttendanceStatuses(),
                meetingRepository.getGroupMeetings(groupId),
            ) { groupState, attendanceState, statusesState, meetingsState ->
                val error = getErrorState(groupState, attendanceState, statusesState, meetingsState)
                if (error != null) return@combine error

                if (isAnyLoading(groupState, attendanceState, statusesState, meetingsState)) {
                    return@combine ScreenState.Loading
                }

                if (isAllContent(groupState, attendanceState, statusesState, meetingsState)) {
                    val group = (groupState as ScreenState.Content<org.mifos.core.model.group.Group>).data
                    val attendance = (attendanceState as ScreenState.Content<List<MeetingAttendance>>).data
                    val statuses = (statusesState as ScreenState.Content<List<AttendanceStatus>>).data
                    val mtgs = (meetingsState as ScreenState.Content<List<Meeting>>).data
                    handleSuccessStates(
                        group = group,
                        attendanceHistory = attendance,
                        attendanceStatuses = statuses,
                        meetings = mtgs,
                    )
                    ScreenState.Content(Unit, DataFreshness.FRESH)
                } else {
                    ScreenState.Loading
                }
            }.collect { screenState ->
                mutableStateFlow.update { it.copy(screenState = screenState) }
            }
        }
    }

    private fun getErrorState(vararg states: ScreenState<*>): ScreenState.Error? {
        return states.filterIsInstance<ScreenState.Error>().firstOrNull()
    }

    private fun isAnyLoading(vararg states: ScreenState<*>): Boolean {
        return states.any { it is ScreenState.Loading }
    }

    private fun isAllContent(vararg states: ScreenState<*>): Boolean {
        return states.all { it is ScreenState.Content }
    }

    private fun handleSuccessStates(
        group: org.mifos.core.model.group.Group,
        attendanceHistory: List<MeetingAttendance>,
        attendanceStatuses: List<AttendanceStatus>,
        meetings: List<Meeting>,
    ) {
        val clientMembers = group.clientMembers ?: emptyList()
        val initialStatuses = mutableMapOf<Long, Long?>()
        val initialRemarks = mutableMapOf<Long, String>()
        clientMembers.forEach { member ->
            val record = attendanceHistory.find {
                it.meetingId == meetingId && it.memberId == member.id
            }
            initialStatuses[member.id] = record?.attendanceStatusCdStatus
            initialRemarks[member.id] = record?.remark ?: ""
        }

        mutableStateFlow.update { state ->
            state.copy(
                clientMembers = clientMembers,
                attendanceHistory = attendanceHistory,
                attendanceStatuses = attendanceStatuses,
                meetings = meetings,
                bulkStatuses = if (state.bulkStatuses.isEmpty()) initialStatuses else state.bulkStatuses,
                bulkRemarks = if (state.bulkRemarks.isEmpty()) initialRemarks else state.bulkRemarks,
            )
        }
    }

    override fun handleAction(action: MeetingAttendanceAction) {
        when (action) {
            MeetingAttendanceAction.OnBackClick -> sendEvent(MeetingAttendanceEvent.NavigateBack)
            MeetingAttendanceAction.Retry -> loadData()
            is MeetingAttendanceAction.OnSearchQueryChange -> mutableStateFlow.update {
                it.copy(searchQuery = action.query)
            }
            is MeetingAttendanceAction.ChangeStatus -> mutableStateFlow.update { state ->
                val newStatuses = state.bulkStatuses.toMutableMap()
                newStatuses[action.memberId] = action.statusId
                state.copy(bulkStatuses = newStatuses)
            }
            is MeetingAttendanceAction.ChangeRemark -> mutableStateFlow.update { state ->
                val newRemarks = state.bulkRemarks.toMutableMap()
                newRemarks[action.memberId] = action.remark
                state.copy(bulkRemarks = newRemarks)
            }
            MeetingAttendanceAction.MarkAllPresent -> markAllPresent()
            MeetingAttendanceAction.SubmitAttendance -> submitAttendance()
            MeetingAttendanceAction.DismissSuccessDialog -> {
                mutableStateFlow.update {
                    it.copy(showSuccessDialog = false, submitState = SubmitState.Idle)
                }
                loadData()
            }
        }
    }

    private fun markAllPresent() {
        mutableStateFlow.update { state ->
            val newStatuses = state.bulkStatuses.toMutableMap()
            state.clientMembers.forEach { member ->
                val hasRecord = state.attendanceHistory.any {
                    it.meetingId == meetingId && it.memberId == member.id
                }
                if (!hasRecord && newStatuses[member.id] == null) {
                    newStatuses[member.id] = 801L
                }
            }
            state.copy(bulkStatuses = newStatuses)
        }
    }

    private fun submitAttendance() {
        val state = stateFlow.value
        val records = state.clientMembers.mapNotNull { member ->
            val statusId = state.bulkStatuses[member.id] ?: return@mapNotNull null
            val hasSavedRecord = state.attendanceHistory.any {
                it.meetingId == meetingId && it.memberId == member.id
            }
            if (hasSavedRecord) return@mapNotNull null

            val remark = state.bulkRemarks[member.id]
            MeetingAttendance(
                id = 0L,
                groupId = groupId,
                meetingId = meetingId,
                memberId = member.id,
                attendanceStatusCdStatus = statusId,
                memberName = member.displayName,
                memberAccountNumber = member.accountNo,
                remark = remark?.takeIf { it.isNotEmpty() },
                createdAt = null,
                updatedAt = null,
            )
        }

        if (records.isEmpty()) return

        mutableStateFlow.update {
            it.copy(submitState = SubmitState.Submitting())
        }

        viewModelScope.launch {
            val result = meetingRepository.saveBulkMeetingAttendance(
                groupId = groupId,
                meetingId = meetingId,
                records = records,
            )

            if (result is ScreenState.Content) {
                meetingRepository.updateMeetingStatus(groupId, meetingId, 799L)

                mutableStateFlow.update {
                    it.copy(
                        submitState = SubmitState.Submitted(Unit),
                        showSuccessDialog = true,
                    )
                }
            } else if (result is ScreenState.Error) {
                mutableStateFlow.update {
                    it.copy(submitState = SubmitState.Failed(error = result.error))
                }
            }
        }
    }
}

data class MeetingAttendanceState(
    val groupId: Long,
    val meetingId: Long,
    val clientMembers: List<ClientMember> = emptyList(),
    val attendanceHistory: List<MeetingAttendance> = emptyList(),
    val attendanceStatuses: List<AttendanceStatus> = emptyList(),
    val meetings: List<Meeting> = emptyList(),
    val searchQuery: String = "",
    val bulkStatuses: Map<Long, Long?> = emptyMap(),
    val bulkRemarks: Map<Long, String> = emptyMap(),
    val screenState: ScreenState<Unit> = ScreenState.Loading,
    val submitState: SubmitState<Unit> = SubmitState.Idle,
    val showSuccessDialog: Boolean = false,
)

sealed interface MeetingAttendanceAction {
    data object OnBackClick : MeetingAttendanceAction
    data object Retry : MeetingAttendanceAction
    data class OnSearchQueryChange(val query: String) : MeetingAttendanceAction
    data class ChangeStatus(val memberId: Long, val statusId: Long) : MeetingAttendanceAction
    data class ChangeRemark(val memberId: Long, val remark: String) : MeetingAttendanceAction
    data object MarkAllPresent : MeetingAttendanceAction
    data object SubmitAttendance : MeetingAttendanceAction
    data object DismissSuccessDialog : MeetingAttendanceAction
}

sealed interface MeetingAttendanceEvent {
    data object NavigateBack : MeetingAttendanceEvent
}
