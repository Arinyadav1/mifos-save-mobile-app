/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See See https://github.com/openMF/kmp-project-template/blob/main/LICENSE
 */
package org.mifos.feature.meeting.meetingList

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import org.mifos.core.base.designsystem.component.HorizontalSpacer
import org.mifos.core.base.designsystem.component.KptCard
import org.mifos.core.base.designsystem.theme.KptTheme
import org.mifos.core.base.ui.effects.EventsEffect
import org.mifos.core.base.ui.screen.DefaultLoadingContent
import org.mifos.core.base.ui.screen.ScreenContent
import org.mifos.core.base.ui.screen.ScreenStateLoading
import org.mifos.core.common.FormatDate
import org.mifos.core.designsystem.component.KptHeader
import org.mifos.core.designsystem.component.KptHeaderActionButton
import org.mifos.core.designsystem.component.KptHeaderBackButton
import org.mifos.core.designsystem.component.KptHeaderPillButton
import org.mifos.core.designsystem.component.KptHeaderTitle
import org.mifos.core.designsystem.component.StatusChip
import org.mifos.core.designsystem.component.StatusChipIntent
import org.mifos.core.designsystem.icon.AppIcons
import org.mifos.core.model.meeting.Meeting
import org.mifos.core.ui.scaffold.KptScaffold
import org.mifos.feature.meeting.generated.resources.Res
import org.mifos.feature.meeting.generated.resources.feature_meeting_empty_list
import org.mifos.feature.meeting.generated.resources.feature_meeting_filters
import org.mifos.feature.meeting.generated.resources.feature_meeting_list_title
import org.mifos.feature.meeting.generated.resources.feature_meeting_schedule_meeting

@Composable
fun MeetingListScreen(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    onMeetingClick: (Long) -> Unit = {},
    onScheduleMeetingClick: (Long) -> Unit = {},
    onFilterClick: () -> Unit = {},
    viewModel: MeetingListViewModel = koinViewModel(),
) {
    val state by viewModel.stateFlow.collectAsStateWithLifecycle()

    EventsEffect(viewModel.eventFlow) { event ->
        when (event) {
            MeetingListEvent.NavigateBack -> onBackClick()
        }
    }

    MeetingListScreenContent(
        state = state,
        onAction = viewModel::trySendAction,
        onMeetingClick = { meeting -> onMeetingClick(meeting.id) },
        onScheduleMeetingClick = onScheduleMeetingClick,
        onFilterClick = onFilterClick,
        modifier = modifier,
    )
}

@Composable
internal fun MeetingListScreenContent(
    state: MeetingListState,
    onAction: (MeetingListAction) -> Unit,
    onMeetingClick: (Meeting) -> Unit,
    onScheduleMeetingClick: (Long) -> Unit,
    onFilterClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    KptScaffold(
        modifier = modifier,
        containerColor = KptTheme.colorScheme.primary,
        bottomBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(KptTheme.colorScheme.surface)
                    .navigationBarsPadding(),
            )
        },
        topBar = {
            KptHeader(
                navigationIcon = {
                    KptHeaderBackButton(onClick = { onAction(MeetingListAction.OnBackClick) })
                },
                actions = {
                    KptHeaderPillButton(
                        text = stringResource(Res.string.feature_meeting_schedule_meeting),
                        icon = AppIcons.AddDefault,
                        onClick = { onScheduleMeetingClick(state.groupId) },
                    )
                    HorizontalSpacer(width = KptTheme.spacing.sm)
                    KptHeaderActionButton(
                        icon = AppIcons.Tune,
                        onClick = onFilterClick,
                        contentDescription = stringResource(Res.string.feature_meeting_filters),
                    )
                },
                title = {
                    KptHeaderTitle(
                        title = stringResource(Res.string.feature_meeting_list_title),
                    )
                },
            )
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
            ScreenContent(
                state = state.screenState,
                onRetry = { onAction(MeetingListAction.Retry) },
                loading = {
                    DefaultLoadingContent(
                        config = ScreenStateLoading.Spinner,
                    )
                },
                modifier = Modifier.fillMaxSize(),
            ) { meetings, freshness ->
                MeetingList(
                    meetings = meetings,
                    onMeetingClick = onMeetingClick,
                    modifier = Modifier.fillMaxSize(),
                )
            }
        }
    }
}

@Composable
fun MeetingList(
    meetings: List<Meeting>,
    onMeetingClick: (Meeting) -> Unit,
    modifier: Modifier = Modifier,
) {
    if (meetings.isEmpty()) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(KptTheme.spacing.lg),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = stringResource(Res.string.feature_meeting_empty_list),
                style = KptTheme.typography.bodyMedium,
                color = KptTheme.colorScheme.onSurfaceVariant,
            )
        }
    } else {
        LazyColumn(
            modifier = modifier.fillMaxSize(),
            contentPadding = PaddingValues(
                horizontal = KptTheme.spacing.md,
                vertical = KptTheme.spacing.md,
            ),
            verticalArrangement = Arrangement.spacedBy(KptTheme.spacing.md),
        ) {
            items(meetings) { meeting ->
                MeetingCard(
                    meeting = meeting,
                    onClick = { onMeetingClick(meeting) },
                )
            }
        }
    }
}

@Composable
fun MeetingCard(
    meeting: Meeting,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    KptCard(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = KptTheme.shapes.medium,
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
                verticalArrangement = Arrangement.spacedBy(KptTheme.spacing.sm),
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = meeting.title.orEmpty(),
                        style = KptTheme.typography.titleMedium,
                        color = KptTheme.colorScheme.onSurface,
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

                meeting.description?.let {
                    Text(
                        text = it,
                        style = KptTheme.typography.bodyMedium,
                        color = KptTheme.colorScheme.onSurfaceVariant,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                    )
                }

                Column(
                    verticalArrangement = Arrangement.spacedBy(KptTheme.spacing.xs),
                ) {
                    // Date & Time Row
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(KptTheme.spacing.xs),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Icon(
                            imageVector = AppIcons.Time,
                            contentDescription = null,
                            tint = KptTheme.colorScheme.primary,
                            modifier = Modifier.size(16.dp),
                        )
                        val dateText = meeting.meetingDate?.let { date ->
                            val dayOfWeek = date.dayOfWeek.name.lowercase().replaceFirstChar { it.uppercase() }
                            "$dayOfWeek, ${FormatDate.formatLocalDate(date)}"
                        } ?: "—"
                        val timeText = if (!meeting.startTime.isNullOrEmpty() && !meeting.endTime.isNullOrEmpty()) {
                            " (${meeting.startTime} - ${meeting.endTime})"
                        } else if (!meeting.startTime.isNullOrEmpty()) {
                            " (${meeting.startTime})"
                        } else {
                            ""
                        }
                        Text(
                            text = "$dateText$timeText",
                            style = KptTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                            color = KptTheme.colorScheme.onSurface,
                        )
                    }

                    // Location Row
                    if (!meeting.location.isNullOrEmpty()) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(KptTheme.spacing.xs),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Icon(
                                imageVector = AppIcons.Location,
                                contentDescription = null,
                                tint = KptTheme.colorScheme.primary,
                                modifier = Modifier.size(16.dp),
                            )
                            Text(
                                text = meeting.location.orEmpty(),
                                style = KptTheme.typography.bodySmall,
                                color = KptTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                    }
                }
            }

            Icon(
                imageVector = AppIcons.ChevronRight,
                contentDescription = null,
                tint = KptTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(start = KptTheme.spacing.sm),
            )
        }
    }
}
