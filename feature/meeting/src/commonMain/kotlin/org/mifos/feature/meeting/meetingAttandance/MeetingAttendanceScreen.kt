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

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import org.mifos.core.base.designsystem.component.KptCard
import org.mifos.core.base.designsystem.component.KptDoubleButton
import org.mifos.core.base.designsystem.component.KptSuccessDialog
import org.mifos.core.base.designsystem.theme.KptTheme
import org.mifos.core.base.store.submit.SubmitState
import org.mifos.core.base.ui.effects.EventsEffect
import org.mifos.core.base.ui.screen.DefaultLoadingContent
import org.mifos.core.base.ui.screen.ScreenStateLoading
import org.mifos.core.base.ui.submit.MutationScreenContent
import org.mifos.core.designsystem.component.KptHeader
import org.mifos.core.designsystem.component.KptHeaderBackButton
import org.mifos.core.designsystem.component.KptHeaderTitle
import org.mifos.core.designsystem.component.StatusChip
import org.mifos.core.designsystem.component.StatusChipIntent
import org.mifos.core.designsystem.icon.AppIcons
import org.mifos.core.model.group.ClientMember
import org.mifos.core.model.meeting.AttendanceStatus
import org.mifos.core.model.meeting.MeetingAttendance
import org.mifos.core.ui.input.KptTextField
import org.mifos.core.ui.scaffold.KptScaffold
import org.mifos.feature.meeting.generated.resources.Res
import org.mifos.feature.meeting.generated.resources.feature_meeting_attendance_cancel
import org.mifos.feature.meeting.generated.resources.feature_meeting_attendance_mark_all_present
import org.mifos.feature.meeting.generated.resources.feature_meeting_attendance_members_list
import org.mifos.feature.meeting.generated.resources.feature_meeting_attendance_ok
import org.mifos.feature.meeting.generated.resources.feature_meeting_attendance_remark
import org.mifos.feature.meeting.generated.resources.feature_meeting_attendance_search_hint
import org.mifos.feature.meeting.generated.resources.feature_meeting_attendance_submit
import org.mifos.feature.meeting.generated.resources.feature_meeting_attendance_success_message
import org.mifos.feature.meeting.generated.resources.feature_meeting_attendance_success_title
import org.mifos.feature.meeting.generated.resources.feature_meeting_attendance_title

@Composable
fun MeetingAttendanceScreen(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: MeetingAttendanceViewModel = koinViewModel(),
) {
    val state by viewModel.stateFlow.collectAsStateWithLifecycle()

    EventsEffect(viewModel.eventFlow) { event ->
        when (event) {
            MeetingAttendanceEvent.NavigateBack -> onBackClick()
        }
    }

    if (state.showSuccessDialog) {
        KptSuccessDialog(
            title = stringResource(Res.string.feature_meeting_attendance_success_title),
            message = stringResource(Res.string.feature_meeting_attendance_success_message),
            buttonText = stringResource(Res.string.feature_meeting_attendance_ok),
            onConfirm = { viewModel.trySendAction(MeetingAttendanceAction.DismissSuccessDialog) },
        )
    }

    MeetingAttendanceScreenContent(
        state = state,
        onAction = viewModel::trySendAction,
        modifier = modifier,
    )
}

@Composable
internal fun MeetingAttendanceScreenContent(
    state: MeetingAttendanceState,
    onAction: (MeetingAttendanceAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    KptScaffold(
        modifier = modifier,
        containerColor = KptTheme.colorScheme.primary,
        topBar = {
            KptHeader(
                navigationIcon = {
                    KptHeaderBackButton(onClick = { onAction(MeetingAttendanceAction.OnBackClick) })
                },
                title = {
                    KptHeaderTitle(
                        title = stringResource(Res.string.feature_meeting_attendance_title),
                    )
                },
            )
        },
        bottomBar = {
            if (state.submitState is SubmitState.Idle || state.submitState is SubmitState.Submitting) {
                val hasNewSelection = state.clientMembers.any { member ->
                    val isUnmarkedInHistory = !state.attendanceHistory.any {
                        it.meetingId == state.meetingId && it.memberId == member.id
                    }
                    isUnmarkedInHistory && state.bulkStatuses[member.id] != null
                }
                val enabledRight = hasNewSelection && state.submitState is SubmitState.Idle

                KptDoubleButton(
                    onLeftButtonClick = { onAction(MeetingAttendanceAction.OnBackClick) },
                    onRightButtonClick = { onAction(MeetingAttendanceAction.SubmitAttendance) },
                    leftButtonText = stringResource(Res.string.feature_meeting_attendance_cancel),
                    rightButtonText = stringResource(Res.string.feature_meeting_attendance_submit),
                    enabledRight = enabledRight,
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
                ),
        ) {
            MutationScreenContent(
                screenState = state.screenState,
                submitState = state.submitState,
                onRetry = { onAction(MeetingAttendanceAction.Retry) },
                onSubmitted = {},
                loading = {
                    DefaultLoadingContent(
                        config = ScreenStateLoading.Spinner,
                    )
                },
                modifier = Modifier.fillMaxSize(),
            ) { _, _ ->
                Column(modifier = Modifier.fillMaxSize()) {
                    KptTextField(
                        value = state.searchQuery,
                        onValueChange = { onAction(MeetingAttendanceAction.OnSearchQueryChange(it)) },
                        placeholder = stringResource(Res.string.feature_meeting_attendance_search_hint),
                        leadingIcon = {
                            Icon(
                                imageVector = AppIcons.Search,
                                contentDescription = null,
                                tint = KptTheme.colorScheme.onSurfaceVariant,
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(
                                horizontal = KptTheme.spacing.md,
                                vertical = KptTheme.spacing.md,
                            ),
                    )

                    val filteredMembers = state.clientMembers.filter {
                        it.displayName?.contains(state.searchQuery, ignoreCase = true) == true
                    }

                    // Mark All Present Button Row
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(
                                horizontal = KptTheme.spacing.md,
                                vertical = KptTheme.spacing.xs,
                            ),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            text = stringResource(Res.string.feature_meeting_attendance_members_list),
                            style = KptTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = KptTheme.colorScheme.onSurface,
                        )

                        val hasAnyUnmarked = state.clientMembers.any { member ->
                            val hasRecord = state.attendanceHistory.any {
                                it.meetingId == state.meetingId && it.memberId == member.id
                            }
                            !hasRecord && state.bulkStatuses[member.id] == null
                        }

                        TextButton(
                            onClick = { onAction(MeetingAttendanceAction.MarkAllPresent) },
                            enabled = hasAnyUnmarked,
                            contentPadding = PaddingValues(horizontal = KptTheme.spacing.xs),
                        ) {
                            Text(
                                text = stringResource(Res.string.feature_meeting_attendance_mark_all_present),
                                style = KptTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                                color = if (hasAnyUnmarked) {
                                    KptTheme.colorScheme.primary
                                } else {
                                    KptTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                                },
                            )
                        }
                    }

                    AttendanceListContent(
                        state = state,
                        filteredMembers = filteredMembers,
                        onAction = onAction,
                        modifier = Modifier.weight(1f),
                    )
                }
            }
        }
    }
}

@Composable
fun AttendanceListContent(
    state: MeetingAttendanceState,
    filteredMembers: List<ClientMember>,
    onAction: (MeetingAttendanceAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(KptTheme.spacing.md),
        contentPadding = PaddingValues(bottom = 16.dp),
    ) {
        items(filteredMembers) { member ->
            val isEditable = !state.attendanceHistory.any {
                it.meetingId == state.meetingId && it.memberId == member.id
            }
            MemberAttendanceCard(
                member = member,
                statuses = state.attendanceStatuses,
                selectedStatusId = state.bulkStatuses[member.id],
                remark = state.bulkRemarks[member.id].orEmpty(),
                isEditable = isEditable,
                onAction = onAction,
            )
        }
    }
}

@Composable
fun MemberAttendanceCard(
    member: ClientMember,
    statuses: List<AttendanceStatus>,
    selectedStatusId: Long?,
    remark: String,
    isEditable: Boolean,
    onAction: (MeetingAttendanceAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    KptCard(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = KptTheme.spacing.md),
        shape = KptTheme.shapes.medium,
        colors = CardDefaults.cardColors(
            containerColor = KptTheme.colorScheme.inverseOnSurface,
        ),
    ) {
        Column(
            modifier = Modifier.padding(KptTheme.spacing.md),
            verticalArrangement = Arrangement.spacedBy(KptTheme.spacing.sm),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column {
                    Text(
                        text = member.displayName.orEmpty(),
                        style = KptTheme.typography.titleMedium,
                        color = KptTheme.colorScheme.onSurface,
                    )
                    member.accountNo?.let { accNo ->
                        Text(
                            text = accNo,
                            style = KptTheme.typography.bodySmall,
                            color = KptTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
            }

            AttendanceSelector(
                statuses = statuses,
                selectedStatusId = selectedStatusId,
                isEditable = isEditable,
                onSelect = { onAction(MeetingAttendanceAction.ChangeStatus(member.id, it)) },
            )

            KptTextField(
                value = remark,
                onValueChange = { onAction(MeetingAttendanceAction.ChangeRemark(member.id, it)) },
                placeholder = stringResource(Res.string.feature_meeting_attendance_remark),
                enabled = isEditable,
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

@Composable
fun AttendanceSelector(
    statuses: List<AttendanceStatus>,
    selectedStatusId: Long?,
    isEditable: Boolean,
    onSelect: (Long) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(KptTheme.spacing.xs),
    ) {
        statuses.forEach { status ->
            val isSelected = selectedStatusId == status.id
            val (bgColor, textColor) = resolveStatusColors(status.name, isSelected)
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .weight(1f)
                    .height(40.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(bgColor)
                    .clickable(enabled = isEditable) { onSelect(status.id) }
                    .padding(horizontal = KptTheme.spacing.xs),
            ) {
                Text(
                    text = status.name,
                    style = KptTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                    color = textColor,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    textAlign = TextAlign.Center,
                )
            }
        }
    }
}

@Composable
private fun resolveStatusColors(statusName: String, isSelected: Boolean): Pair<Color, Color> {
    if (!isSelected) {
        return KptTheme.colorScheme.surfaceContainerHighest to KptTheme.colorScheme.onSurfaceVariant
    }
    return when (statusName.uppercase()) {
        "PRESENT" -> Color(0xFFE8F5E9) to Color(0xFF2E7D32) // Soft Green
        "ABSENT" -> Color(0xFFFFEBEE) to Color(0xFFC62828) // Soft Red
        "LATE" -> Color(0xFFFFF8E1) to Color(0xFFF57F17) // Soft Yellow/Orange
        "EXCUSED" -> Color(0xFFE8F0FE) to Color(0xFF1557B0) // Soft Blue
        else -> KptTheme.colorScheme.primaryContainer to KptTheme.colorScheme.onPrimaryContainer
    }
}

@Composable
fun HistoryRecordRow(
    record: MeetingAttendance,
    meetingTitle: String?,
    meetingDate: String?,
    statusName: String,
    modifier: Modifier = Modifier,
) {
    KptCard(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = KptTheme.spacing.md, vertical = KptTheme.spacing.xs),
        shape = KptTheme.shapes.small,
        colors = CardDefaults.cardColors(
            containerColor = KptTheme.colorScheme.inverseOnSurface,
        ),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(KptTheme.spacing.md),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(KptTheme.spacing.xs),
            ) {
                Text(
                    text = meetingTitle ?: "Meeting #${record.meetingId}",
                    style = KptTheme.typography.titleSmall,
                    color = KptTheme.colorScheme.onSurface,
                )
                meetingDate?.let { date ->
                    Text(
                        text = date,
                        style = KptTheme.typography.bodySmall,
                        color = KptTheme.colorScheme.onSurfaceVariant,
                    )
                }
                val remark = record.remark
                if (!remark.isNullOrEmpty()) {
                    Text(
                        text = remark,
                        style = KptTheme.typography.bodySmall,
                        color = KptTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
            StatusChip(
                text = statusName,
                intent = when (statusName.uppercase()) {
                    "PRESENT" -> StatusChipIntent.Success
                    "ABSENT" -> StatusChipIntent.Danger
                    "LATE" -> StatusChipIntent.Warning
                    "EXCUSED" -> StatusChipIntent.Neutral
                    else -> StatusChipIntent.Neutral
                },
            )
        }
    }
}
