/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See See https://github.com/openMF/kmp-project-template/blob/main/LICENSE
 */
package org.mifos.feature.groups.groupDetails

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import org.mifos.core.base.designsystem.component.HorizontalSpacer
import org.mifos.core.base.designsystem.theme.KptTheme
import org.mifos.core.base.store.screen.dataOrNull
import org.mifos.core.base.ui.effects.EventsEffect
import org.mifos.core.base.ui.screen.DefaultLoadingContent
import org.mifos.core.base.ui.screen.ScreenContent
import org.mifos.core.base.ui.screen.ScreenStateLoading
import org.mifos.core.designsystem.component.KptDropdownMenu
import org.mifos.core.designsystem.component.KptDropdownMenuItem
import org.mifos.core.designsystem.component.KptExploreCard
import org.mifos.core.designsystem.component.KptHeader
import org.mifos.core.designsystem.component.KptHeaderActionButton
import org.mifos.core.designsystem.component.KptHeaderBackButton
import org.mifos.core.designsystem.component.KptHeaderTitle
import org.mifos.core.designsystem.component.KptKeyValueCard
import org.mifos.core.designsystem.component.SectionHeader
import org.mifos.core.designsystem.component.StatusChip
import org.mifos.core.designsystem.icon.AppIcons
import org.mifos.core.ui.component.statusChipIntent
import org.mifos.core.ui.scaffold.KptScaffold
import org.mifos.feature.groups.generated.resources.Res
import org.mifos.feature.groups.generated.resources.feature_groups_account_no
import org.mifos.feature.groups.generated.resources.feature_groups_activate_group
import org.mifos.feature.groups.generated.resources.feature_groups_activation_date
import org.mifos.feature.groups.generated.resources.feature_groups_active_loan
import org.mifos.feature.groups.generated.resources.feature_groups_close_group
import org.mifos.feature.groups.generated.resources.feature_groups_details_title
import org.mifos.feature.groups.generated.resources.feature_groups_explore_title
import org.mifos.feature.groups.generated.resources.feature_groups_glim
import org.mifos.feature.groups.generated.resources.feature_groups_gsim
import org.mifos.feature.groups.generated.resources.feature_groups_loans
import org.mifos.feature.groups.generated.resources.feature_groups_meetings
import org.mifos.feature.groups.generated.resources.feature_groups_members
import org.mifos.feature.groups.generated.resources.feature_groups_office_name
import org.mifos.feature.groups.generated.resources.feature_groups_overview_title
import org.mifos.feature.groups.generated.resources.feature_groups_savings
import org.mifos.feature.groups.generated.resources.feature_groups_submitted_date
import org.mifos.feature.groups.generated.resources.feature_groups_total_saving
import org.mifos.feature.groups.generated.resources.feature_groups_update_group

@Composable
fun GroupDetailsScreen(
    onBackClick: () -> Unit,
    onMembersClick: (Long) -> Unit,
    onSavingsClick: (Long) -> Unit,
    onLoansClick: (Long) -> Unit,
    modifier: Modifier = Modifier,
    onMeetingsClick: (Long) -> Unit = {},
    onActivateGroupClick: (Long) -> Unit = {},
    onUpdateGroupClick: (Long) -> Unit = {},
    viewModel: GroupDetailsViewModel = koinViewModel(),
) {
    val state by viewModel.stateFlow.collectAsStateWithLifecycle()

    EventsEffect(viewModel.eventFlow) { event ->
        when (event) {
            GroupDetailsEvent.NavigateBack -> onBackClick()
            GroupDetailsEvent.NavigateToLoans -> {
                onLoansClick(viewModel.groupId)
            }

            GroupDetailsEvent.NavigateToSavings -> {
                onSavingsClick(viewModel.groupId)
            }

            GroupDetailsEvent.NavigateToMeetings -> {
                onMeetingsClick(viewModel.groupId)
            }

            GroupDetailsEvent.NavigateToGlim -> {
                /* TODO: navigate to glim */
            }

            GroupDetailsEvent.NavigateToGsim -> {
                /* TODO: navigate to gsim */
            }

            GroupDetailsEvent.NavigateToMembers -> {
                onMembersClick(viewModel.groupId)
            }

            is GroupDetailsEvent.NavigateToActivateGroup -> {
                onActivateGroupClick(event.groupId)
            }

            is GroupDetailsEvent.NavigateToUpdateGroup -> {
                onUpdateGroupClick(event.groupId)
            }
        }
    }

    GroupDetailsScreenContent(
        state = state,
        onAction = viewModel::trySendAction,
        modifier = modifier,
    )
}

@Composable
internal fun GroupDetailsScreenContent(
    state: GroupDetailsState,
    onAction: (GroupDetailsAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    KptScaffold(
        modifier = modifier,
        containerColor = KptTheme.colorScheme.primary,
        topBar = {
            val group = state.screenState.dataOrNull
            KptHeader(
                navigationIcon = {
                    KptHeaderBackButton(onClick = { onAction(GroupDetailsAction.OnBackClick) })
                },
                actions = {
                    Box {
                        KptHeaderActionButton(
                            icon = AppIcons.MoreVert,
                            onClick = { onAction(GroupDetailsAction.SetMenuVisible(true)) },
                        )
                        KptDropdownMenu(
                            expanded = state.showMenu,
                            onDismissRequest = { onAction(GroupDetailsAction.SetMenuVisible(false)) },
                            items = buildList {
                                if (group?.status?.code?.contains("pending") == true) {
                                    add(
                                        KptDropdownMenuItem(
                                            text = stringResource(Res.string.feature_groups_close_group),
                                            onClick = {},
                                        ),
                                    )
                                    add(
                                        KptDropdownMenuItem(
                                            text = stringResource(Res.string.feature_groups_activate_group),
                                            onClick = {
                                                onAction(GroupDetailsAction.SetMenuVisible(false))
                                                onAction(GroupDetailsAction.OnActivateGroupClick)
                                            },
                                        ),
                                    )
                                }

                                add(
                                    KptDropdownMenuItem(
                                        text = stringResource(Res.string.feature_groups_update_group),
                                        onClick = {
                                            onAction(GroupDetailsAction.SetMenuVisible(false))
                                            onAction(GroupDetailsAction.OnUpdateGroupClick)
                                        },
                                    ),
                                )
                            },
                        )
                    }
                },

                title = {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        KptHeaderTitle(
                            title = group?.name.orEmpty(),
                            subtitle = stringResource(Res.string.feature_groups_details_title),
                            maxLines = 2,
                            modifier = Modifier.weight(1f),
                        )
                        if (group != null) {
                            HorizontalSpacer(
                                KptTheme.spacing.md,
                            )
                            StatusChip(
                                text = group.status?.value.orEmpty(),
                                intent = group.statusChipIntent,
                            )
                        }
                    }
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
                onRetry = { onAction(GroupDetailsAction.Retry) },
                loading = {
                    DefaultLoadingContent(
                        config = ScreenStateLoading.Spinner,
                    )
                },
                modifier = Modifier.fillMaxSize(),
            ) { group, freshness ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = KptTheme.spacing.md)
                        .padding(bottom = KptTheme.spacing.xl, top = KptTheme.spacing.sm),
                    verticalArrangement = Arrangement.spacedBy(KptTheme.spacing.md),
                ) {
                    SectionHeader(
                        title = stringResource(Res.string.feature_groups_overview_title),
                    )

                    KptKeyValueCard(
                        items = mapOf(
                            stringResource(Res.string.feature_groups_account_no) to (
                                group.accountNo
                                    ?: "—"
                                ),
                            stringResource(Res.string.feature_groups_activation_date) to (
                                group.activationDate?.toString()
                                    ?: "—"
                                ),
                            stringResource(Res.string.feature_groups_submitted_date) to (
                                group.timeline?.submittedOnDate?.toString()
                                    ?: "—"
                                ),
                            stringResource(Res.string.feature_groups_office_name) to (
                                group.officeName
                                    ?: "—"
                                ),
                            stringResource(Res.string.feature_groups_active_loan) to "—",
                            stringResource(Res.string.feature_groups_total_saving) to "—",
                        ),
                    )

                    SectionHeader(
                        title = stringResource(Res.string.feature_groups_explore_title),
                    )

                    ExploreCart(
                        onAction = onAction,
                    )
                }
            }
        }
    }
}

@Composable
fun ExploreCart(
    onAction: (GroupDetailsAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(KptTheme.spacing.sm),
    ) {
        KptExploreCard(
            title = stringResource(Res.string.feature_groups_loans),
            leadingIcon = AppIcons.Loans,
            onClick = { onAction(GroupDetailsAction.OnLoansClick) },
        )

        KptExploreCard(
            title = stringResource(Res.string.feature_groups_savings),
            leadingIcon = AppIcons.Savings,
            onClick = { onAction(GroupDetailsAction.OnSavingsClick) },
        )

        KptExploreCard(
            title = stringResource(Res.string.feature_groups_meetings),
            leadingIcon = AppIcons.Meetings,
            onClick = { onAction(GroupDetailsAction.OnMeetingsClick) },
        )

        KptExploreCard(
            title = stringResource(Res.string.feature_groups_members),
            leadingIcon = AppIcons.Groups,
            onClick = { onAction(GroupDetailsAction.OnMembersClick) },
        )

        KptExploreCard(
            title = stringResource(Res.string.feature_groups_glim),
            leadingIcon = AppIcons.Glim,
            onClick = { onAction(GroupDetailsAction.OnGlimClick) },
        )

        KptExploreCard(
            title = stringResource(Res.string.feature_groups_gsim),
            leadingIcon = AppIcons.Gsim,
            onClick = { onAction(GroupDetailsAction.OnGsimClick) },
        )
    }
}
