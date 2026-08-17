/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See See https://github.com/openMF/kmp-project-template/blob/main/LICENSE
 */
package org.mifos.feature.meeting.meetingDashboard

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlinx.datetime.todayIn
import org.mifos.core.base.store.screen.DataFreshness
import org.mifos.core.base.store.screen.ScreenState
import org.mifos.core.base.ui.viewmodel.BaseViewModel
import org.mifos.core.data.group.GroupRepository
import org.mifos.core.data.meeting.MeetingRepository
import org.mifos.core.model.group.Group
import org.mifos.core.model.meeting.Meeting
import org.mifos.core.model.meeting.MeetingStatus
import kotlin.time.Clock

class MeetingDashboardViewModel(
    private val groupRepository: GroupRepository,
    private val meetingRepository: MeetingRepository,
) : BaseViewModel<MeetingDashboardState, MeetingDashboardEvent, MeetingDashboardAction>(
    MeetingDashboardState(),
) {

    private var cachedGroups: List<Group> = emptyList()
    private var cachedStatuses: List<MeetingStatus> = emptyList()
    private var cachedMeetings: List<Meeting> = emptyList()
    private var selectedGroup: Group? = null

    private var groupsJob: Job? = null
    private var statusesJob: Job? = null
    private var meetingsJob: Job? = null

    init {
        loadDashboardData()
    }

    private fun loadDashboardData() {
        updateState { copy(screenState = ScreenState.Loading) }

        // Load groups
        groupsJob?.cancel()
        groupsJob = viewModelScope.launch {
            groupRepository.getGroups().collect { groupsResult ->
                when (groupsResult) {
                    is ScreenState.Content -> {
                        cachedGroups = groupsResult.data
                        if (selectedGroup == null) {
                            selectedGroup = cachedGroups.firstOrNull()
                        }
                        checkAndEmit()
                    }
                    is ScreenState.Error -> updateState { copy(screenState = ScreenState.Error(groupsResult.error)) }
                    is ScreenState.Loading -> { /* Keep loading */ }
                    is ScreenState.NoNetwork -> updateState { copy(screenState = ScreenState.NoNetwork()) }
                    is ScreenState.Empty -> updateState { copy(screenState = ScreenState.Empty) }
                    is ScreenState.Unauthenticated -> updateState { copy(screenState = ScreenState.Unauthenticated) }
                }
            }
        }

        // Load statuses
        statusesJob?.cancel()
        statusesJob = viewModelScope.launch {
            meetingRepository.getMeetingStatuses().collect { statusesResult ->
                when (statusesResult) {
                    is ScreenState.Content -> {
                        cachedStatuses = statusesResult.data
                        checkAndEmit()
                    }
                    is ScreenState.Error -> updateState { copy(screenState = ScreenState.Error(statusesResult.error)) }
                    is ScreenState.Loading -> { /* Keep loading */ }
                    is ScreenState.NoNetwork -> updateState { copy(screenState = ScreenState.NoNetwork()) }
                    is ScreenState.Empty -> { /* Keep loading */ }
                    is ScreenState.Unauthenticated -> updateState { copy(screenState = ScreenState.Unauthenticated) }
                }
            }
        }
    }

    private fun checkAndEmit() {
        val currentGroup = selectedGroup
        if (currentGroup != null) {
            loadMeetingsForGroup(currentGroup.id)
        } else if (cachedGroups.isNotEmpty()) {
            selectedGroup = cachedGroups.first()
            loadMeetingsForGroup(cachedGroups.first().id)
        } else if (cachedGroups.isEmpty() && state.screenState !is ScreenState.Loading) {
            updateState { copy(screenState = ScreenState.Empty) }
        }
    }

    private fun loadMeetingsForGroup(groupId: Long) {
        updateState { copy(screenState = ScreenState.Loading) }
        meetingsJob?.cancel()
        meetingsJob = viewModelScope.launch {
            meetingRepository.getGroupMeetings(groupId).collect { meetingsResult ->
                when (meetingsResult) {
                    is ScreenState.Content -> {
                        cachedMeetings = meetingsResult.data
                        updateState {
                            copy(
                                screenState = ScreenState.Content(
                                    data = MeetingDashboardData(
                                        groups = cachedGroups,
                                        meetings = cachedMeetings,
                                        statuses = cachedStatuses,
                                        selectedGroup = selectedGroup,
                                    ),
                                    freshness = meetingsResult.freshness,
                                ),
                            )
                        }
                    }
                    is ScreenState.Error -> updateState { copy(screenState = ScreenState.Error(meetingsResult.error)) }
                    is ScreenState.Loading -> {
                        updateState { copy(screenState = ScreenState.Loading) }
                    }
                    is ScreenState.NoNetwork -> updateState { copy(screenState = ScreenState.NoNetwork()) }
                    is ScreenState.Empty -> {
                        cachedMeetings = emptyList()
                        updateState {
                            copy(
                                screenState = ScreenState.Content(
                                    data = MeetingDashboardData(
                                        groups = cachedGroups,
                                        meetings = cachedMeetings,
                                        statuses = cachedStatuses,
                                        selectedGroup = selectedGroup,
                                    ),
                                    freshness = DataFreshness.FRESH,
                                ),
                            )
                        }
                    }
                    is ScreenState.Unauthenticated -> updateState { copy(screenState = ScreenState.Unauthenticated) }
                }
            }
        }
    }

    private fun updateMeetingStatus(groupId: Long, meetingId: Long, statusId: Long) {
        updateState { copy(isUpdatingStatus = true, updateStatusError = null) }
        viewModelScope.launch {
            val statusResult = meetingRepository
                .updateMeetingStatus(groupId, meetingId, statusId)
            when (statusResult) {
                is ScreenState.Content -> {
                    updateState {
                        copy(
                            isUpdatingStatus = false,
                            statusUpdatedSuccessfully = true,
                        )
                    }
                    selectedGroup?.let { loadMeetingsForGroup(it.id) }
                }
                is ScreenState.Error -> {
                    updateState {
                        copy(
                            isUpdatingStatus = false,
                            updateStatusError = statusResult.error.message ?: "An error occurred",
                        )
                    }
                }
                is ScreenState.Loading -> { /* Keep loading */ }
                is ScreenState.NoNetwork -> {
                    updateState {
                        copy(
                            isUpdatingStatus = false,
                            updateStatusError = "No network connection",
                        )
                    }
                }
                else -> {
                    updateState { copy(isUpdatingStatus = false) }
                }
            }
        }
    }

    override fun handleAction(action: MeetingDashboardAction) {
        when (action) {
            MeetingDashboardAction.LoadGroupsAndStatuses -> {
                loadDashboardData()
            }
            is MeetingDashboardAction.SelectGroup -> {
                selectedGroup = action.group
                checkAndEmit()
            }
            is MeetingDashboardAction.UpdateMeetingStatus -> {
                val group = selectedGroup
                if (group != null) {
                    updateMeetingStatus(group.id, action.meetingId, action.statusId)
                }
            }
            MeetingDashboardAction.ClearUpdateStatusSuccess -> {
                updateState { copy(statusUpdatedSuccessfully = false, updateStatusError = null) }
            }
            MeetingDashboardAction.RetryLoadMeetings -> {
                selectedGroup?.let { loadMeetingsForGroup(it.id) }
            }
        }
    }
}

data class MeetingDashboardData(
    val groups: List<Group>,
    val meetings: List<Meeting>,
    val statuses: List<MeetingStatus>,
    val selectedGroup: Group?,
) {
    val upcomingCount: Int = meetings.count { it.isUpcoming() }
    val completedCount: Int = meetings.count { it.isCompleted() }
    val cancelledCount: Int = meetings.count { it.isCancelled() }

    val upcomingMeetings: List<Meeting> = meetings.filter { it.isUpcoming() }
    val completedMeetings: List<Meeting> = meetings.filter { it.isCompleted() }
    val nextUpcomingMeeting: Meeting? = upcomingMeetings.minByOrNull {
        it.meetingDate ?: kotlinx.datetime.LocalDate(9999, 12, 31)
    }
}

data class MeetingDashboardState(
    val screenState: ScreenState<MeetingDashboardData> = ScreenState.Loading,
    val isUpdatingStatus: Boolean = false,
    val updateStatusError: String? = null,
    val statusUpdatedSuccessfully: Boolean = false,
)

sealed interface MeetingDashboardAction {
    data object LoadGroupsAndStatuses : MeetingDashboardAction
    data class SelectGroup(val group: Group) : MeetingDashboardAction
    data class UpdateMeetingStatus(val meetingId: Long, val statusId: Long) : MeetingDashboardAction
    data object ClearUpdateStatusSuccess : MeetingDashboardAction
    data object RetryLoadMeetings : MeetingDashboardAction
}

sealed interface MeetingDashboardEvent {
    data object NavigateBack : MeetingDashboardEvent
    data class NavigateToScheduleMeeting(val groupId: Long) : MeetingDashboardEvent
    data class NavigateToMeetingAttendance(val groupId: Long, val meetingId: Long) : MeetingDashboardEvent
}

private fun String.toLocalTime(): LocalTime? {
    return try {
        val parts = this.split(":")
        if (parts.size >= 2) {
            val hour = parts[0].toIntOrNull() ?: 0
            val minute = parts[1].toIntOrNull() ?: 0
            val second = if (parts.size >= 3) parts[2].toIntOrNull() ?: 0 else 0
            LocalTime(hour, minute, second)
        } else {
            null
        }
    } catch (e: Exception) {
        null
    }
}

private fun Meeting.isUpcoming(): Boolean {
    val isScheduled = statusName?.contains("Scheduled", ignoreCase = true) == true
    val isUpcomingStatus = statusName?.contains("Upcoming", ignoreCase = true) == true
    val isStatusUpcoming = meetingStatusCdStatus == 798L ||
        isScheduled ||
        isUpcomingStatus

    if (!isStatusUpcoming) {
        return false
    }

    val date = meetingDate
    val hasNotPassed = if (date == null) {
        true
    } else {
        val timeZone = TimeZone.currentSystemDefault()
        val today = Clock.System.todayIn(timeZone)
        when {
            date < today -> false
            date > today -> true
            else -> {
                val timeString = endTime ?: startTime
                val meetingTime = timeString?.toLocalTime()
                if (meetingTime == null) {
                    true
                } else {
                    val currentLocalTime = Clock.System.now()
                        .toLocalDateTime(timeZone).time
                    currentLocalTime <= meetingTime
                }
            }
        }
    }

    return hasNotPassed
}

private fun Meeting.isCompleted(): Boolean {
    return meetingStatusCdStatus == 799L || statusName?.contains("Completed", ignoreCase = true) == true
}

private fun Meeting.isCancelled(): Boolean {
    return meetingStatusCdStatus == 800L || statusName?.contains("Cancelled", ignoreCase = true) == true
}
