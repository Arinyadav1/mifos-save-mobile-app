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

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import org.mifos.core.base.designsystem.component.HorizontalSpacer
import org.mifos.core.base.designsystem.component.KptButton
import org.mifos.core.base.designsystem.component.KptCard
import org.mifos.core.base.designsystem.component.KptOutlinedButton
import org.mifos.core.base.designsystem.component.KptSuccessDialog
import org.mifos.core.base.designsystem.component.VerticalSpacer
import org.mifos.core.base.designsystem.theme.KptTheme
import org.mifos.core.base.store.screen.ScreenState
import org.mifos.core.base.ui.effects.EventsEffect
import org.mifos.core.base.ui.screen.DefaultLoadingContent
import org.mifos.core.base.ui.screen.ScreenContent
import org.mifos.core.base.ui.screen.ScreenStateLoading
import org.mifos.core.common.FormatDate
import org.mifos.core.designsystem.component.KptHeader
import org.mifos.core.designsystem.component.KptHeaderActionButton
import org.mifos.core.designsystem.component.KptHeaderBackButton
import org.mifos.core.designsystem.component.KptHeaderTitle
import org.mifos.core.designsystem.component.KptStatsCard
import org.mifos.core.designsystem.component.StatusChip
import org.mifos.core.designsystem.component.StatusChipIntent
import org.mifos.core.designsystem.component.state.CardLoadingSkeleton
import org.mifos.core.designsystem.icon.AppIcons
import org.mifos.core.model.group.Group
import org.mifos.core.model.meeting.Meeting
import org.mifos.core.model.meeting.MeetingStatus
import org.mifos.core.ui.input.KptTextField
import org.mifos.core.ui.scaffold.KptScaffold
import org.mifos.feature.meeting.component.MeetingCard
import org.mifos.feature.meeting.generated.resources.Res
import org.mifos.feature.meeting.generated.resources.feature_meeting_cancel_meeting
import org.mifos.feature.meeting.generated.resources.feature_meeting_cancelled_meetings
import org.mifos.feature.meeting.generated.resources.feature_meeting_complete_meeting
import org.mifos.feature.meeting.generated.resources.feature_meeting_completed_meetings
import org.mifos.feature.meeting.generated.resources.feature_meeting_dashboard_title
import org.mifos.feature.meeting.generated.resources.feature_meeting_date
import org.mifos.feature.meeting.generated.resources.feature_meeting_location
import org.mifos.feature.meeting.generated.resources.feature_meeting_next_upcoming
import org.mifos.feature.meeting.generated.resources.feature_meeting_no_meetings
import org.mifos.feature.meeting.generated.resources.feature_meeting_ok
import org.mifos.feature.meeting.generated.resources.feature_meeting_overview_stats
import org.mifos.feature.meeting.generated.resources.feature_meeting_record_attendance
import org.mifos.feature.meeting.generated.resources.feature_meeting_select_group
import org.mifos.feature.meeting.generated.resources.feature_meeting_status_updated
import org.mifos.feature.meeting.generated.resources.feature_meeting_tab_all
import org.mifos.feature.meeting.generated.resources.feature_meeting_tab_completed
import org.mifos.feature.meeting.generated.resources.feature_meeting_tab_upcoming
import org.mifos.feature.meeting.generated.resources.feature_meeting_time
import org.mifos.feature.meeting.generated.resources.feature_meeting_upcoming_meetings

@Composable
fun MeetingDashboardScreen(
    onBackClick: () -> Unit,
    onScheduleMeetingClick: (Long) -> Unit,
    onMeetingClick: (Long, Long) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: MeetingDashboardViewModel = koinViewModel(),
) {
    val state by viewModel.stateFlow.collectAsStateWithLifecycle()

    EventsEffect(viewModel.eventFlow) { event ->
        when (event) {
            MeetingDashboardEvent.NavigateBack -> onBackClick()
            is MeetingDashboardEvent.NavigateToScheduleMeeting -> onScheduleMeetingClick(event.groupId)
            is MeetingDashboardEvent.NavigateToMeetingAttendance -> onMeetingClick(event.groupId, event.meetingId)
        }
    }

    if (state.statusUpdatedSuccessfully) {
        KptSuccessDialog(
            title = "Success",
            message = stringResource(Res.string.feature_meeting_status_updated),
            buttonText = stringResource(Res.string.feature_meeting_ok),
            onConfirm = { viewModel.trySendAction(MeetingDashboardAction.ClearUpdateStatusSuccess) },
        )
    }

    MeetingDashboardScreenContent(
        state = state,
        onBackClick = onBackClick,
        onScheduleMeetingClick = onScheduleMeetingClick,
        onMeetingClick = onMeetingClick,
        onAction = viewModel::trySendAction,
        modifier = modifier,
    )
}

@Composable
fun MeetingDashboardScreenContent(
    state: MeetingDashboardState,
    onBackClick: () -> Unit,
    onScheduleMeetingClick: (Long) -> Unit,
    onMeetingClick: (Long, Long) -> Unit,
    onAction: (MeetingDashboardAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    var showGroupDialog by remember { mutableStateOf(false) }
    var selectedTabIndex by remember { mutableStateOf(0) }

    val tabs = listOf(
        stringResource(Res.string.feature_meeting_tab_upcoming),
        stringResource(Res.string.feature_meeting_tab_completed),
        stringResource(Res.string.feature_meeting_tab_all),
    )

    KptScaffold(
        topBar = {
            KptHeader(
                navigationIcon = {
                    KptHeaderBackButton(onClick = onBackClick)
                },
                title = {
                    KptHeaderTitle(
                        title = stringResource(Res.string.feature_meeting_dashboard_title),
                    )
                },
                actions = {
                    val currentSelectedGroupId = (state.screenState as? ScreenState.Content)
                        ?.data?.selectedGroup?.id
                    KptHeaderActionButton(
                        icon = AppIcons.Add,
                        onClick = {
                            currentSelectedGroupId?.let { onScheduleMeetingClick(it) }
                        },
                    )
                },
            )
        },
        containerColor = KptTheme.colorScheme.primary,
        modifier = modifier,
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clip(
                    RoundedCornerShape(
                        topStart = 28.dp,
                        topEnd = 28.dp,
                    ),
                )
                .background(color = KptTheme.colorScheme.surface),
        ) {
            ScreenContent(
                state = state.screenState,
                onRetry = { onAction(MeetingDashboardAction.LoadGroupsAndStatuses) },
                loading = {
                    DefaultLoadingContent(
                        config = ScreenStateLoading.Spinner,
                    )
                },
                modifier = Modifier.fillMaxSize(),
            ) { data, freshness ->
                if (state.isUpdatingStatus) {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(KptTheme.spacing.md),
                        verticalArrangement = Arrangement.spacedBy(KptTheme.spacing.md),
                    ) {
                        items(3) {
                            CardLoadingSkeleton()
                        }
                    }
                } else {
                    MeetingDashboardList(
                        data = data,
                        selectedTabIndex = selectedTabIndex,
                        tabs = tabs,
                        onTabSelected = { selectedTabIndex = it },
                        onMeetingClick = onMeetingClick,
                        onShowGroupDialog = { showGroupDialog = true },
                        onAction = onAction,
                    )
                }

                if (showGroupDialog) {
                    GroupSelectionDialog(
                        groups = data.groups,
                        onGroupSelected = { onAction(MeetingDashboardAction.SelectGroup(it)) },
                        onDismissRequest = { showGroupDialog = false },
                    )
                }
            }
        }
    }
}

@Composable
private fun MeetingDashboardList(
    data: MeetingDashboardData,
    selectedTabIndex: Int,
    tabs: List<String>,
    onTabSelected: (Int) -> Unit,
    onMeetingClick: (Long, Long) -> Unit,
    onShowGroupDialog: () -> Unit,
    onAction: (MeetingDashboardAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(KptTheme.spacing.md),
        verticalArrangement = Arrangement.spacedBy(KptTheme.spacing.md),
    ) {
        item {
            GroupSelectorRow(
                groupName = data.selectedGroup?.name,
                onClick = onShowGroupDialog,
            )
        }

        item {
            MeetingDashboardStats(
                upcomingCount = data.upcomingCount,
                completedCount = data.completedCount,
                cancelledCount = data.cancelledCount,
            )
        }

        item {
            Text(
                text = stringResource(Res.string.feature_meeting_next_upcoming),
                style = KptTheme.typography.titleMedium,
                color = KptTheme.colorScheme.onSurfaceVariant,
            )
        }

        item {
            val nextUpcomingMeeting = data.nextUpcomingMeeting
            if (nextUpcomingMeeting != null) {
                HighlightMeetingCard(
                    meeting = nextUpcomingMeeting,
                    statuses = data.statuses,
                    onMeetingClick = onMeetingClick,
                    onAction = onAction,
                )
            } else {
                EmptyUpcomingMeetingsCard()
            }
        }

        item {
            MeetingDashboardTabs(
                selectedTabIndex = selectedTabIndex,
                tabs = tabs,
                onTabSelected = onTabSelected,
            )
        }

        val filteredMeetings = when (selectedTabIndex) {
            0 -> data.upcomingMeetings
            1 -> data.completedMeetings
            else -> data.meetings
        }

        if (filteredMeetings.isEmpty()) {
            item {
                NoMeetingsEmptyState()
            }
        } else {
            items(filteredMeetings) { meeting ->
                MeetingCard(
                    meeting = meeting,
                    onClick = { onMeetingClick(meeting.groupId, meeting.id) },
                )
            }
        }
    }
}

@Composable
private fun EmptyUpcomingMeetingsCard(
    modifier: Modifier = Modifier,
) {
    KptCard(
        colors = CardDefaults.cardColors(
            containerColor = KptTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f),
        ),
        shape = KptTheme.shapes.medium,
        modifier = modifier,
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(KptTheme.spacing.lg),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = "No upcoming meetings scheduled",
                style = KptTheme.typography.bodyMedium,
                color = KptTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun NoMeetingsEmptyState(
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = KptTheme.spacing.xl),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = stringResource(Res.string.feature_meeting_no_meetings),
            style = KptTheme.typography.bodyMedium,
            color = KptTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
private fun GroupSelectorRow(
    groupName: String?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(KptTheme.shapes.medium)
            .background(KptTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
            .clickable(onClick = onClick)
            .padding(KptTheme.spacing.md),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(KptTheme.spacing.sm),
        ) {
            Icon(
                imageVector = AppIcons.Group,
                contentDescription = null,
                tint = KptTheme.colorScheme.primary,
            )
            Text(
                text = groupName
                    ?: stringResource(Res.string.feature_meeting_select_group),
                style = KptTheme.typography.titleMedium,
                color = KptTheme.colorScheme.onSurface,
            )
        }
        Icon(
            imageVector = AppIcons.KeyboardArrowDown,
            contentDescription = null,
            tint = KptTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
private fun MeetingDashboardStats(
    upcomingCount: Int,
    completedCount: Int,
    cancelledCount: Int,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(KptTheme.spacing.md),
    ) {
        Text(
            text = stringResource(Res.string.feature_meeting_overview_stats),
            style = KptTheme.typography.titleMedium,
            color = KptTheme.colorScheme.onSurfaceVariant,
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(KptTheme.spacing.sm),
        ) {
            KptStatsCard(
                value = upcomingCount.toString(),
                label = stringResource(Res.string.feature_meeting_upcoming_meetings),
                icon = AppIcons.Meetings,
                iconContainerColor = KptTheme.colorScheme.primaryContainer,
                iconColor = KptTheme.colorScheme.primary,
                modifier = Modifier.weight(1f),
            )
            KptStatsCard(
                value = completedCount.toString(),
                label = stringResource(Res.string.feature_meeting_completed_meetings),
                icon = AppIcons.CheckCircle,
                iconContainerColor = KptTheme.colorScheme.primaryContainer,
                iconColor = KptTheme.colorScheme.secondary,
                modifier = Modifier.weight(1f),
            )
            KptStatsCard(
                value = cancelledCount.toString(),
                label = stringResource(Res.string.feature_meeting_cancelled_meetings),
                icon = AppIcons.Cancel,
                iconContainerColor = KptTheme.colorScheme.errorContainer,
                iconColor = KptTheme.colorScheme.error,
                modifier = Modifier.weight(1f),
            )
        }
    }
}

@Composable
private fun MeetingDashboardTabs(
    selectedTabIndex: Int,
    tabs: List<String>,
    onTabSelected: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    TabRow(
        selectedTabIndex = selectedTabIndex,
        containerColor = KptTheme.colorScheme.surface,
        contentColor = KptTheme.colorScheme.primary,
        modifier = modifier,
    ) {
        tabs.forEachIndexed { index, title ->
            Tab(
                selected = selectedTabIndex == index,
                onClick = { onTabSelected(index) },
                text = {
                    Text(
                        text = title,
                        style = KptTheme.typography.titleSmall,
                    )
                },
            )
        }
    }
}

@Composable
fun HighlightMeetingCard(
    meeting: Meeting,
    statuses: List<MeetingStatus>,
    onMeetingClick: (Long, Long) -> Unit,
    onAction: (MeetingDashboardAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    KptCard(
        modifier = modifier.fillMaxWidth(),
        shape = KptTheme.shapes.medium,
        colors = CardDefaults.cardColors(
            containerColor = KptTheme.colorScheme.primaryContainer.copy(alpha = 0.5f),
        ),
    ) {
        Column(
            modifier = Modifier.padding(KptTheme.spacing.md),
            verticalArrangement = Arrangement.spacedBy(KptTheme.spacing.sm),
        ) {
            HighlightMeetingCardHeader(meeting = meeting)
            HighlightMeetingCardDetails(meeting = meeting)
            HighlightMeetingCardActions(
                meeting = meeting,
                statuses = statuses,
                onMeetingClick = onMeetingClick,
                onAction = onAction,
            )
        }
    }
}

@Composable
private fun HighlightMeetingCardHeader(
    meeting: Meeting,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = meeting.title.orEmpty(),
            style = KptTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
            color = KptTheme.colorScheme.onPrimaryContainer,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f),
        )
        meeting.statusName?.let { status ->
            StatusChip(
                text = status,
                intent = when (status.uppercase()) {
                    "SCHEDULED" -> StatusChipIntent.Info
                    "COMPLETED" -> StatusChipIntent.Success
                    "CANCELLED" -> StatusChipIntent.Danger
                    else -> StatusChipIntent.Neutral
                },
            )
        }
    }
}

@Composable
private fun HighlightMeetingCardDetails(
    meeting: Meeting,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(KptTheme.spacing.sm),
    ) {
        meeting.description?.let {
            Text(
                text = it,
                style = KptTheme.typography.bodyMedium,
                color = KptTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
        }

        VerticalSpacer(height = KptTheme.spacing.xs)

        meeting.meetingDate?.let { date ->
            val dayOfWeek = date.dayOfWeek.name.lowercase()
                .replaceFirstChar { it.uppercase() }
            val formattedDate = FormatDate.formatLocalDate(date)
            val dateText = "$dayOfWeek, $formattedDate"
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = AppIcons.Meetings,
                    contentDescription = null,
                    tint = KptTheme.colorScheme.primary,
                    modifier = Modifier.size(16.dp),
                )
                HorizontalSpacer(width = KptTheme.spacing.xs)
                Text(
                    text = stringResource(Res.string.feature_meeting_date) +
                        ": $dateText",
                    style = KptTheme.typography.bodyMedium,
                    color = KptTheme.colorScheme.onPrimaryContainer,
                )
            }
        }

        if (meeting.startTime != null && meeting.endTime != null) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = AppIcons.Time,
                    contentDescription = null,
                    tint = KptTheme.colorScheme.primary,
                    modifier = Modifier.size(16.dp),
                )
                HorizontalSpacer(width = KptTheme.spacing.xs)
                Text(
                    text = stringResource(Res.string.feature_meeting_time) +
                        ": ${meeting.startTime} - ${meeting.endTime}",
                    style = KptTheme.typography.bodyMedium,
                    color = KptTheme.colorScheme.onPrimaryContainer,
                )
            }
        }

        meeting.location?.let { loc ->
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = AppIcons.Location,
                    contentDescription = null,
                    tint = KptTheme.colorScheme.primary,
                    modifier = Modifier.size(16.dp),
                )
                HorizontalSpacer(width = KptTheme.spacing.xs)
                Text(
                    text = stringResource(Res.string.feature_meeting_location) + ": $loc",
                    style = KptTheme.typography.bodyMedium,
                    color = KptTheme.colorScheme.onPrimaryContainer,
                )
            }
        }
    }
}

@Composable
private fun HighlightMeetingCardActions(
    meeting: Meeting,
    statuses: List<MeetingStatus>,
    onMeetingClick: (Long, Long) -> Unit,
    onAction: (MeetingDashboardAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(KptTheme.spacing.sm),
    ) {
        KptButton(
            onClick = { onMeetingClick(meeting.groupId, meeting.id) },
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(
                text = stringResource(Res.string.feature_meeting_record_attendance),
                style = KptTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(KptTheme.spacing.sm),
        ) {
            KptOutlinedButton(
                onClick = {
                    val statusId = statuses.find {
                        it.name.equals("Completed", ignoreCase = true)
                    }?.id ?: 799L
                    onAction(MeetingDashboardAction.UpdateMeetingStatus(meeting.id, statusId))
                },
                modifier = Modifier.weight(1f),
            ) {
                Text(
                    text = stringResource(Res.string.feature_meeting_complete_meeting),
                    style = KptTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                )
            }

            KptOutlinedButton(
                onClick = {
                    val statusId = statuses.find {
                        it.name.equals("Cancelled", ignoreCase = true)
                    }?.id ?: 800L
                    onAction(MeetingDashboardAction.UpdateMeetingStatus(meeting.id, statusId))
                },
                modifier = Modifier.weight(1f),
            ) {
                Text(
                    text = stringResource(Res.string.feature_meeting_cancel_meeting),
                    style = KptTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                )
            }
        }
    }
}

@Composable
fun GroupSelectionDialog(
    groups: List<Group>,
    onGroupSelected: (Group) -> Unit,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var searchQuery by remember { mutableStateOf("") }
    val filteredGroups = groups.filter {
        it.name?.contains(searchQuery, ignoreCase = true) == true ||
            it.accountNo?.contains(searchQuery, ignoreCase = true) == true
    }

    Dialog(onDismissRequest = onDismissRequest) {
        KptCard(
            modifier = modifier
                .fillMaxWidth()
                .padding(KptTheme.spacing.md),
            shape = KptTheme.shapes.medium,
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(KptTheme.spacing.md),
            ) {
                Text(
                    text = stringResource(Res.string.feature_meeting_select_group),
                    style = KptTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    color = KptTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(bottom = KptTheme.spacing.md),
                )

                KptTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = "Search groups...",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = KptTheme.spacing.md),
                )

                LazyColumn(
                    modifier = Modifier.heightIn(max = 300.dp),
                    verticalArrangement = Arrangement.spacedBy(KptTheme.spacing.xs),
                ) {
                    items(filteredGroups) { group ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(KptTheme.shapes.medium)
                                .clickable {
                                    onGroupSelected(group)
                                    onDismissRequest()
                                }
                                .padding(KptTheme.spacing.md),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Icon(
                                imageVector = AppIcons.Group,
                                contentDescription = null,
                                tint = KptTheme.colorScheme.primary,
                                modifier = Modifier.padding(end = KptTheme.spacing.sm),
                            )
                            Column {
                                Text(
                                    text = group.name.orEmpty(),
                                    style = KptTheme.typography.bodyLarge.copy(
                                        fontWeight = FontWeight.Medium,
                                    ),
                                    color = KptTheme.colorScheme.onSurface,
                                )
                                group.accountNo?.let {
                                    Text(
                                        text = it,
                                        style = KptTheme.typography.bodySmall,
                                        color = KptTheme.colorScheme.onSurfaceVariant,
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
