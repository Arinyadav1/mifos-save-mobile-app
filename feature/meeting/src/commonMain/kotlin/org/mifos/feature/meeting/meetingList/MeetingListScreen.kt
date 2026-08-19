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
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import org.mifos.core.base.designsystem.component.HorizontalSpacer
import org.mifos.core.base.designsystem.theme.KptTheme
import org.mifos.core.base.ui.effects.EventsEffect
import org.mifos.core.base.ui.screen.DefaultLoadingContent
import org.mifos.core.base.ui.screen.ScreenContent
import org.mifos.core.base.ui.screen.ScreenStateLoading
import org.mifos.core.designsystem.component.KptHeader
import org.mifos.core.designsystem.component.KptHeaderActionButton
import org.mifos.core.designsystem.component.KptHeaderBackButton
import org.mifos.core.designsystem.component.KptHeaderPillButton
import org.mifos.core.designsystem.component.KptHeaderTitle
import org.mifos.core.designsystem.component.MeetingCard
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
