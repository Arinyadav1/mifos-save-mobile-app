/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See See https://github.com/openMF/kmp-project-template/blob/main/LICENSE
 */
package org.mifos.feature.meeting.scheduleMeeting

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.jetbrains.compose.resources.StringResource
import org.mifos.core.base.store.screen.DataFreshness
import org.mifos.core.base.store.screen.ScreenState
import org.mifos.core.base.store.submit.SubmitState
import org.mifos.core.base.ui.viewmodel.BaseViewModel
import org.mifos.core.data.meeting.MeetingRepository
import org.mifos.core.model.meeting.MeetingRepetitionType
import org.mifos.feature.meeting.generated.resources.Res
import org.mifos.feature.meeting.generated.resources.feature_meeting_custom_interval_empty_error
import org.mifos.feature.meeting.generated.resources.feature_meeting_custom_interval_invalid_error
import org.mifos.feature.meeting.generated.resources.feature_meeting_date_range_invalid_error
import org.mifos.feature.meeting.generated.resources.feature_meeting_end_date_empty_error
import org.mifos.feature.meeting.generated.resources.feature_meeting_end_time_empty_error
import org.mifos.feature.meeting.generated.resources.feature_meeting_start_date_empty_error
import org.mifos.feature.meeting.generated.resources.feature_meeting_start_time_empty_error
import org.mifos.feature.meeting.generated.resources.feature_meeting_title_empty_error

class ScheduleMeetingViewModel(
    private val meetingRepository: MeetingRepository,
    savedStateHandle: SavedStateHandle,
) : BaseViewModel<ScheduleMeetingState, ScheduleMeetingEvent, ScheduleMeetingAction>(
    initialState = ScheduleMeetingState(),
) {
    private val route = savedStateHandle.toRoute<ScheduleMeetingRoute>()
    val groupId = route.groupId

    init {
        updateState { copy(groupId = route.groupId) }
    }

    override fun handleAction(action: ScheduleMeetingAction) {
        if (handleDateTimeAction(action)) return
        if (handleTextInputAction(action)) return
        handleLifecycleAction(action)
    }

    private fun formatTime(hour: Int, minute: Int): String {
        val h = hour.toString().padStart(2, '0')
        val m = minute.toString().padStart(2, '0')
        return "$h:$m"
    }

    private fun handleDateTimeAction(action: ScheduleMeetingAction): Boolean {
        when (action) {
            is ScheduleMeetingAction.OnStartDatePickerToggle -> {
                updateState { copy(isStartDatePickerVisible = action.visible) }
            }
            is ScheduleMeetingAction.OnStartDateSelected -> {
                if (action.millis != null) {
                    val date = kotlin.time.Instant.fromEpochMilliseconds(action.millis)
                        .toLocalDateTime(TimeZone.UTC)
                        .date
                    updateState { copy(startDate = date, startDateError = null) }
                }
            }
            is ScheduleMeetingAction.OnEndDatePickerToggle -> {
                updateState { copy(isEndDatePickerVisible = action.visible) }
            }
            is ScheduleMeetingAction.OnEndDateSelected -> {
                if (action.millis != null) {
                    val date = kotlin.time.Instant.fromEpochMilliseconds(action.millis)
                        .toLocalDateTime(TimeZone.UTC)
                        .date
                    updateState { copy(endDate = date, endDateError = null) }
                }
            }
            is ScheduleMeetingAction.OnStartTimePickerToggle -> {
                updateState { copy(isStartTimePickerVisible = action.visible) }
            }
            is ScheduleMeetingAction.OnStartTimeSelected -> {
                val formattedTime = formatTime(action.hour, action.minute)
                updateState { copy(startTime = formattedTime, startTimeError = null) }
            }
            is ScheduleMeetingAction.OnEndTimePickerToggle -> {
                updateState { copy(isEndTimePickerVisible = action.visible) }
            }
            is ScheduleMeetingAction.OnEndTimeSelected -> {
                val formattedTime = formatTime(action.hour, action.minute)
                updateState { copy(endTime = formattedTime, endTimeError = null) }
            }
            else -> return false
        }
        return true
    }

    private fun handleTextInputAction(action: ScheduleMeetingAction): Boolean {
        when (action) {
            is ScheduleMeetingAction.OnTitleChange -> {
                updateState { copy(title = action.title, titleError = null) }
            }
            is ScheduleMeetingAction.OnLocationChange -> {
                updateState { copy(location = action.location) }
            }
            is ScheduleMeetingAction.OnMeetingLinkChange -> {
                updateState { copy(meetingLink = action.link) }
            }
            is ScheduleMeetingAction.OnDescriptionChange -> {
                updateState { copy(description = action.desc) }
            }
            is ScheduleMeetingAction.OnRepetitionTypeChange -> {
                updateState { copy(repetitionType = action.type, customIntervalError = null) }
            }
            is ScheduleMeetingAction.OnCustomIntervalChange -> {
                updateState { copy(customInterval = action.interval, customIntervalError = null) }
            }
            else -> return false
        }
        return true
    }

    private fun handleLifecycleAction(action: ScheduleMeetingAction) {
        when (action) {
            ScheduleMeetingAction.OnScheduleClick -> {
                validateAndSchedule()
            }
            ScheduleMeetingAction.DismissSuccessDialog -> {
                updateState { copy(isSuccessDialogVisible = false) }
                sendEvent(ScheduleMeetingEvent.NavigateBackWithUpdateData(route.groupId))
            }
            ScheduleMeetingAction.OnBackClick -> {
                sendEvent(ScheduleMeetingEvent.NavigateBack)
            }
            else -> Unit
        }
    }

    private fun validateCustomInterval(): Int? {
        if (state.repetitionType != MeetingRepetitionType.CUSTOM) return 1
        val parsed = state.customInterval.toIntOrNull()
        return when {
            state.customInterval.isBlank() -> {
                updateState { copy(customIntervalError = Res.string.feature_meeting_custom_interval_empty_error) }
                null
            }
            parsed == null || parsed <= 0 -> {
                updateState { copy(customIntervalError = Res.string.feature_meeting_custom_interval_invalid_error) }
                null
            }
            else -> parsed
        }
    }

    private fun validateFields(): Int? {
        var hasError = false

        if (state.title.isBlank()) {
            updateState { copy(titleError = Res.string.feature_meeting_title_empty_error) }
            hasError = true
        }
        val sDate = state.startDate
        val eDate = state.endDate
        if (sDate == null) {
            updateState { copy(startDateError = Res.string.feature_meeting_start_date_empty_error) }
            hasError = true
        }
        if (eDate == null) {
            updateState { copy(endDateError = Res.string.feature_meeting_end_date_empty_error) }
            hasError = true
        }
        if (sDate != null && eDate != null && eDate < sDate) {
            updateState { copy(endDateError = Res.string.feature_meeting_date_range_invalid_error) }
            hasError = true
        }
        if (state.startTime.isBlank()) {
            updateState { copy(startTimeError = Res.string.feature_meeting_start_time_empty_error) }
            hasError = true
        }
        if (state.endTime.isBlank()) {
            updateState { copy(endTimeError = Res.string.feature_meeting_end_time_empty_error) }
            hasError = true
        }

        val customIntervalInt = validateCustomInterval()
        if (customIntervalInt == null) {
            hasError = true
        }

        return if (hasError) null else customIntervalInt
    }

    private fun validateAndSchedule() {
        val customIntervalInt = validateFields()
        val sDate = state.startDate
        val eDate = state.endDate

        if (customIntervalInt == null || sDate == null || eDate == null) return

        schedule(sDate, eDate, customIntervalInt)
    }

    private fun schedule(sDate: LocalDate, eDate: LocalDate, customIntervalInt: Int) {
        updateState { copy(submitState = SubmitState.Submitting()) }

        viewModelScope.launch {
            val result = meetingRepository.scheduleMeeting(
                groupId = state.groupId,
                title = state.title,
                startDate = sDate,
                endDate = eDate,
                startTime = state.startTime,
                endTime = state.endTime,
                repetitionType = state.repetitionType,
                location = state.location.ifBlank { null },
                meetingLink = state.meetingLink.ifBlank { null },
                description = state.description.ifBlank { null },
                customInterval = customIntervalInt,
            )

            when (result) {
                is ScreenState.Content -> {
                    updateState {
                        copy(
                            submitState = SubmitState.Submitted(Unit),
                            isSuccessDialogVisible = true,
                        )
                    }
                }
                is ScreenState.Error -> {
                    updateState {
                        copy(
                            submitState = SubmitState.Failed(result.error),
                        )
                    }
                }
                else -> {
                    updateState { copy(isLoading = false) }
                }
            }
        }
    }
}

data class ScheduleMeetingState(
    val groupId: Long = 0L,
    val title: String = "",
    val startDate: LocalDate? = null,
    val endDate: LocalDate? = null,
    val startTime: String = "",
    val endTime: String = "",
    val location: String = "",
    val meetingLink: String = "",
    val description: String = "",
    val repetitionType: MeetingRepetitionType = MeetingRepetitionType.DAILY,
    val customInterval: String = "2",
    val isStartDatePickerVisible: Boolean = false,
    val isEndDatePickerVisible: Boolean = false,
    val isStartTimePickerVisible: Boolean = false,
    val isEndTimePickerVisible: Boolean = false,
    val isSuccessDialogVisible: Boolean = false,
    val titleError: StringResource? = null,
    val startDateError: StringResource? = null,
    val endDateError: StringResource? = null,
    val startTimeError: StringResource? = null,
    val endTimeError: StringResource? = null,
    val customIntervalError: StringResource? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
    val screenState: ScreenState<Unit> = ScreenState.Content(Unit, DataFreshness.FRESH),
    val submitState: SubmitState<Unit> = SubmitState.Idle,
)

sealed interface ScheduleMeetingEvent {
    data object NavigateBack : ScheduleMeetingEvent
    data class NavigateBackWithUpdateData(val groupId: Long) : ScheduleMeetingEvent
}
sealed interface ScheduleMeetingAction {
    data class OnTitleChange(val title: String) : ScheduleMeetingAction
    data class OnStartDatePickerToggle(val visible: Boolean) : ScheduleMeetingAction
    data class OnStartDateSelected(val millis: Long?) : ScheduleMeetingAction
    data class OnEndDatePickerToggle(val visible: Boolean) : ScheduleMeetingAction
    data class OnEndDateSelected(val millis: Long?) : ScheduleMeetingAction
    data class OnStartTimePickerToggle(val visible: Boolean) : ScheduleMeetingAction
    data class OnStartTimeSelected(val hour: Int, val minute: Int) : ScheduleMeetingAction
    data class OnEndTimePickerToggle(val visible: Boolean) : ScheduleMeetingAction
    data class OnEndTimeSelected(val hour: Int, val minute: Int) : ScheduleMeetingAction
    data class OnLocationChange(val location: String) : ScheduleMeetingAction
    data class OnMeetingLinkChange(val link: String) : ScheduleMeetingAction
    data class OnDescriptionChange(val desc: String) : ScheduleMeetingAction
    data class OnRepetitionTypeChange(val type: MeetingRepetitionType) : ScheduleMeetingAction
    data class OnCustomIntervalChange(val interval: String) : ScheduleMeetingAction
    data object OnScheduleClick : ScheduleMeetingAction
    data object DismissSuccessDialog : ScheduleMeetingAction
    data object OnBackClick : ScheduleMeetingAction
}
