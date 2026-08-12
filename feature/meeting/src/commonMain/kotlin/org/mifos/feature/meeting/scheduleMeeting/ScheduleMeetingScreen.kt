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

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import org.mifos.core.base.designsystem.component.KptDoubleButton
import org.mifos.core.base.designsystem.component.KptSuccessDialog
import org.mifos.core.base.designsystem.theme.KptTheme
import org.mifos.core.base.store.submit.SubmitState
import org.mifos.core.base.ui.effects.EventsEffect
import org.mifos.core.base.ui.submit.MutationScreenContent
import org.mifos.core.common.FormatDate
import org.mifos.core.designsystem.component.KptDatePickerDialog
import org.mifos.core.designsystem.component.KptHeader
import org.mifos.core.designsystem.component.KptHeaderBackButton
import org.mifos.core.designsystem.component.KptHeaderTitle
import org.mifos.core.designsystem.component.KptTimePickerDialog
import org.mifos.core.designsystem.icon.AppIcons
import org.mifos.core.model.meeting.MeetingRepetitionType
import org.mifos.core.ui.input.KptDropdownTextField
import org.mifos.core.ui.input.KptTextField
import org.mifos.core.ui.scaffold.KptScaffold
import org.mifos.feature.meeting.generated.resources.Res
import org.mifos.feature.meeting.generated.resources.feature_meeting_cancel
import org.mifos.feature.meeting.generated.resources.feature_meeting_description_hint
import org.mifos.feature.meeting.generated.resources.feature_meeting_description_label
import org.mifos.feature.meeting.generated.resources.feature_meeting_end_date_hint
import org.mifos.feature.meeting.generated.resources.feature_meeting_end_date_label
import org.mifos.feature.meeting.generated.resources.feature_meeting_end_time_hint
import org.mifos.feature.meeting.generated.resources.feature_meeting_end_time_label
import org.mifos.feature.meeting.generated.resources.feature_meeting_link_hint
import org.mifos.feature.meeting.generated.resources.feature_meeting_link_label
import org.mifos.feature.meeting.generated.resources.feature_meeting_location_hint
import org.mifos.feature.meeting.generated.resources.feature_meeting_location_label
import org.mifos.feature.meeting.generated.resources.feature_meeting_ok
import org.mifos.feature.meeting.generated.resources.feature_meeting_repetition_custom
import org.mifos.feature.meeting.generated.resources.feature_meeting_repetition_custom_interval_hint
import org.mifos.feature.meeting.generated.resources.feature_meeting_repetition_custom_interval_label
import org.mifos.feature.meeting.generated.resources.feature_meeting_repetition_daily
import org.mifos.feature.meeting.generated.resources.feature_meeting_repetition_label
import org.mifos.feature.meeting.generated.resources.feature_meeting_repetition_monthly
import org.mifos.feature.meeting.generated.resources.feature_meeting_repetition_weekly
import org.mifos.feature.meeting.generated.resources.feature_meeting_schedule_btn
import org.mifos.feature.meeting.generated.resources.feature_meeting_schedule_meeting_title
import org.mifos.feature.meeting.generated.resources.feature_meeting_scheduled_successfully
import org.mifos.feature.meeting.generated.resources.feature_meeting_scheduled_successfully_desc
import org.mifos.feature.meeting.generated.resources.feature_meeting_start_date_hint
import org.mifos.feature.meeting.generated.resources.feature_meeting_start_date_label
import org.mifos.feature.meeting.generated.resources.feature_meeting_start_time_hint
import org.mifos.feature.meeting.generated.resources.feature_meeting_start_time_label
import org.mifos.feature.meeting.generated.resources.feature_meeting_title_hint
import org.mifos.feature.meeting.generated.resources.feature_meeting_title_label

@Composable
fun ScheduleMeetingScreen(
    onBackClick: () -> Unit,
    onBackWithUpdateData: (Long) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ScheduleMeetingViewModel = koinViewModel(),
) {
    val state by viewModel.stateFlow.collectAsStateWithLifecycle()

    EventsEffect(viewModel.eventFlow) { event ->
        when (event) {
            ScheduleMeetingEvent.NavigateBack -> onBackClick()
            is ScheduleMeetingEvent.NavigateBackWithUpdateData -> onBackWithUpdateData(event.groupId)
        }
    }

    if (state.isSuccessDialogVisible) {
        KptSuccessDialog(
            title = stringResource(Res.string.feature_meeting_scheduled_successfully),
            message = stringResource(Res.string.feature_meeting_scheduled_successfully_desc),
            buttonText = stringResource(Res.string.feature_meeting_ok),
            onConfirm = { viewModel.trySendAction(ScheduleMeetingAction.DismissSuccessDialog) },
        )
    }

    if (state.isStartDatePickerVisible) {
        val initialMillis = state.startDate?.toEpochDays()?.times(86400000)
        KptDatePickerDialog(
            onDateSelected = { millis ->
                viewModel.trySendAction(ScheduleMeetingAction.OnStartDateSelected(millis))
            },
            onDismiss = {
                viewModel.trySendAction(ScheduleMeetingAction.OnStartDatePickerToggle(false))
            },
            initialSelectedDateMillis = initialMillis,
        )
    }

    if (state.isEndDatePickerVisible) {
        val initialMillis = state.endDate?.toEpochDays()?.times(86400000)
        KptDatePickerDialog(
            onDateSelected = { millis ->
                viewModel.trySendAction(ScheduleMeetingAction.OnEndDateSelected(millis))
            },
            onDismiss = {
                viewModel.trySendAction(ScheduleMeetingAction.OnEndDatePickerToggle(false))
            },
            initialSelectedDateMillis = initialMillis,
        )
    }

    if (state.isStartTimePickerVisible) {
        KptTimePickerDialog(
            onTimeSelected = { hour, minute ->
                viewModel.trySendAction(ScheduleMeetingAction.OnStartTimeSelected(hour, minute))
            },
            onDismiss = {
                viewModel.trySendAction(ScheduleMeetingAction.OnStartTimePickerToggle(false))
            },
        )
    }

    if (state.isEndTimePickerVisible) {
        KptTimePickerDialog(
            onTimeSelected = { hour, minute ->
                viewModel.trySendAction(ScheduleMeetingAction.OnEndTimeSelected(hour, minute))
            },
            onDismiss = {
                viewModel.trySendAction(ScheduleMeetingAction.OnEndTimePickerToggle(false))
            },
        )
    }

    ScheduleMeetingScreenContent(
        state = state,
        onAction = viewModel::trySendAction,
        modifier = modifier,
    )
}

@Composable
internal fun ScheduleMeetingScreenContent(
    state: ScheduleMeetingState,
    onAction: (ScheduleMeetingAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    KptScaffold(
        modifier = modifier,
        containerColor = KptTheme.colorScheme.primary,
        topBar = {
            KptHeader(
                navigationIcon = {
                    KptHeaderBackButton(onClick = { onAction(ScheduleMeetingAction.OnBackClick) })
                },
                title = {
                    KptHeaderTitle(
                        title = stringResource(Res.string.feature_meeting_schedule_meeting_title),
                    )
                },
            )
        },
        bottomBar = {
            if (state.submitState is SubmitState.Idle || state.submitState is SubmitState.Submitting) {
                KptDoubleButton(
                    onLeftButtonClick = { onAction(ScheduleMeetingAction.OnBackClick) },
                    onRightButtonClick = { onAction(ScheduleMeetingAction.OnScheduleClick) },
                    leftButtonText = stringResource(Res.string.feature_meeting_cancel),
                    rightButtonText = stringResource(Res.string.feature_meeting_schedule_btn),
                    modifier = Modifier.navigationBarsPadding(),
                )
            }
        },
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    color = KptTheme.colorScheme.surface,
                    shape = RoundedCornerShape(
                        topStart = KptTheme.spacing.lg,
                        topEnd = KptTheme.spacing.lg,
                    ),
                )
                .clip(
                    RoundedCornerShape(
                        topStart = KptTheme.spacing.lg,
                        topEnd = KptTheme.spacing.lg,
                    ),
                ),
        ) {
            MutationScreenContent(
                screenState = state.screenState,
                submitState = state.submitState,
                onRetry = { onAction(ScheduleMeetingAction.OnScheduleClick) },
                onSubmitted = {},
                modifier = Modifier.fillMaxSize(),
            ) { _, _ ->
                ScheduleMeetingFormFields(
                    state = state,
                    onAction = onAction,
                    modifier = Modifier.fillMaxSize(),
                )
            }
        }
    }
}

@Composable
private fun ScheduleMeetingFormFields(
    state: ScheduleMeetingState,
    onAction: (ScheduleMeetingAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .verticalScroll(rememberScrollState())
            .padding(KptTheme.spacing.md),
        verticalArrangement = Arrangement.spacedBy(KptTheme.spacing.md),
    ) {
        KptTextField(
            value = state.title,
            onValueChange = { onAction(ScheduleMeetingAction.OnTitleChange(it)) },
            label = stringResource(Res.string.feature_meeting_title_label),
            placeholder = stringResource(Res.string.feature_meeting_title_hint),
            errorText = state.titleError?.let { stringResource(it) },
            modifier = Modifier.fillMaxWidth(),
        )

        ScheduleMeetingDateTimeFields(
            state = state,
            onAction = onAction,
        )

        val repetitionOptions = listOf(
            stringResource(Res.string.feature_meeting_repetition_daily),
            stringResource(Res.string.feature_meeting_repetition_weekly),
            stringResource(Res.string.feature_meeting_repetition_monthly),
            stringResource(Res.string.feature_meeting_repetition_custom),
        )
        val repetitionRawValues = listOf(
            MeetingRepetitionType.DAILY,
            MeetingRepetitionType.WEEKLY,
            MeetingRepetitionType.MONTHLY,
            MeetingRepetitionType.CUSTOM,
        )
        val selectedRepetitionIndex =
            repetitionRawValues.indexOf(state.repetitionType).coerceAtLeast(0)

        KptDropdownTextField(
            value = repetitionOptions[selectedRepetitionIndex],
            onOptionSelected = { index, _ ->
                onAction(
                    ScheduleMeetingAction.OnRepetitionTypeChange(
                        repetitionRawValues[index],
                    ),
                )
            },
            options = repetitionOptions,
            label = stringResource(Res.string.feature_meeting_repetition_label),
            modifier = Modifier.fillMaxWidth(),
        )

        if (state.repetitionType == MeetingRepetitionType.CUSTOM) {
            KptTextField(
                value = state.customInterval,
                onValueChange = {
                    onAction(
                        ScheduleMeetingAction.OnCustomIntervalChange(
                            it,
                        ),
                    )
                },
                label = stringResource(Res.string.feature_meeting_repetition_custom_interval_label),
                placeholder = stringResource(Res.string.feature_meeting_repetition_custom_interval_hint),
                errorText = state.customIntervalError?.let { stringResource(it) },
                modifier = Modifier.fillMaxWidth(),
            )
        }

        KptTextField(
            value = state.location,
            onValueChange = { onAction(ScheduleMeetingAction.OnLocationChange(it)) },
            label = stringResource(Res.string.feature_meeting_location_label),
            placeholder = stringResource(Res.string.feature_meeting_location_hint),
            modifier = Modifier.fillMaxWidth(),
        )

        KptTextField(
            value = state.meetingLink,
            onValueChange = { onAction(ScheduleMeetingAction.OnMeetingLinkChange(it)) },
            label = stringResource(Res.string.feature_meeting_link_label),
            placeholder = stringResource(Res.string.feature_meeting_link_hint),
            modifier = Modifier.fillMaxWidth(),
        )

        KptTextField(
            value = state.description,
            onValueChange = { onAction(ScheduleMeetingAction.OnDescriptionChange(it)) },
            label = stringResource(Res.string.feature_meeting_description_label),
            placeholder = stringResource(Res.string.feature_meeting_description_hint),
            singleLine = false,
            minLines = 3,
            maxLines = 5,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

@Composable
private fun ScheduleMeetingDateTimeFields(
    state: ScheduleMeetingState,
    onAction: (ScheduleMeetingAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(KptTheme.spacing.md),
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            val startDateText = state.startDate?.let {
                val weekday = it.dayOfWeek.name.lowercase()
                    .replaceFirstChar { char -> char.uppercase() }
                "$weekday, ${FormatDate.formatLocalDate(it)}"
            }.orEmpty()
            KptTextField(
                value = startDateText,
                onValueChange = {},
                readOnly = true,
                label = stringResource(Res.string.feature_meeting_start_date_label),
                placeholder = stringResource(Res.string.feature_meeting_start_date_hint),
                trailingIcon = {
                    Icon(
                        imageVector = AppIcons.Calendar,
                        contentDescription = null,
                        tint = KptTheme.colorScheme.onSurfaceVariant,
                    )
                },
                errorText = state.startDateError?.let { stringResource(it) },
                modifier = Modifier.fillMaxWidth(),
            )
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .padding(top = 24.dp)
                    .clickable {
                        onAction(
                            ScheduleMeetingAction.OnStartDatePickerToggle(
                                true,
                            ),
                        )
                    },
            )
        }

        Box(modifier = Modifier.fillMaxWidth()) {
            val endDateText = state.endDate?.let {
                val weekday = it.dayOfWeek.name.lowercase()
                    .replaceFirstChar { char -> char.uppercase() }
                "$weekday, ${FormatDate.formatLocalDate(it)}"
            }.orEmpty()
            KptTextField(
                value = endDateText,
                onValueChange = {},
                readOnly = true,
                label = stringResource(Res.string.feature_meeting_end_date_label),
                placeholder = stringResource(Res.string.feature_meeting_end_date_hint),
                trailingIcon = {
                    Icon(
                        imageVector = AppIcons.Calendar,
                        contentDescription = null,
                        tint = KptTheme.colorScheme.onSurfaceVariant,
                    )
                },
                errorText = state.endDateError?.let { stringResource(it) },
                modifier = Modifier.fillMaxWidth(),
            )
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .padding(top = 24.dp)
                    .clickable {
                        onAction(
                            ScheduleMeetingAction.OnEndDatePickerToggle(
                                true,
                            ),
                        )
                    },
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(KptTheme.spacing.md),
        ) {
            Box(modifier = Modifier.weight(1f)) {
                KptTextField(
                    value = state.startTime,
                    onValueChange = {},
                    readOnly = true,
                    label = stringResource(Res.string.feature_meeting_start_time_label),
                    placeholder = stringResource(Res.string.feature_meeting_start_time_hint),
                    trailingIcon = {
                        Icon(
                            imageVector = AppIcons.Time,
                            contentDescription = null,
                            tint = KptTheme.colorScheme.onSurfaceVariant,
                        )
                    },
                    errorText = state.startTimeError?.let { stringResource(it) },
                    modifier = Modifier.fillMaxWidth(),
                )
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .padding(top = 24.dp)
                        .clickable {
                            onAction(
                                ScheduleMeetingAction.OnStartTimePickerToggle(
                                    true,
                                ),
                            )
                        },
                )
            }

            Box(modifier = Modifier.weight(1f)) {
                KptTextField(
                    value = state.endTime,
                    onValueChange = {},
                    readOnly = true,
                    label = stringResource(Res.string.feature_meeting_end_time_label),
                    placeholder = stringResource(Res.string.feature_meeting_end_time_hint),
                    trailingIcon = {
                        Icon(
                            imageVector = AppIcons.Time,
                            contentDescription = null,
                            tint = KptTheme.colorScheme.onSurfaceVariant,
                        )
                    },
                    errorText = state.endTimeError?.let { stringResource(it) },
                    modifier = Modifier.fillMaxWidth(),
                )
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .padding(top = 24.dp)
                        .clickable {
                            onAction(
                                ScheduleMeetingAction.OnEndTimePickerToggle(
                                    true,
                                ),
                            )
                        },
                )
            }
        }
    }
}
