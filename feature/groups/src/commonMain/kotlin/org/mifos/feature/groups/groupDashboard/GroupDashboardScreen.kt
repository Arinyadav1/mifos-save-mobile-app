/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See See https://github.com/openMF/kmp-project-template/blob/main/LICENSE
 */
package org.mifos.feature.groups.groupDashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import org.mifos.core.base.designsystem.component.HorizontalSpacer
import org.mifos.core.base.designsystem.theme.KptTheme
import org.mifos.core.base.ui.effects.EventsEffect
import org.mifos.core.base.ui.paging.PagingScreenContent
import org.mifos.core.designsystem.component.FilterSection
import org.mifos.core.designsystem.component.KptFilterBottomSheet
import org.mifos.core.designsystem.component.KptHeader
import org.mifos.core.designsystem.component.KptHeaderActionButton
import org.mifos.core.designsystem.component.KptHeaderBackButton
import org.mifos.core.designsystem.component.KptHeaderPillButton
import org.mifos.core.designsystem.component.KptHeaderStatsCard
import org.mifos.core.designsystem.component.KptHeaderTitle
import org.mifos.core.designsystem.icon.AppIcons
import org.mifos.core.ui.component.KptItemCard
import org.mifos.core.ui.component.SubRowItem
import org.mifos.core.ui.component.statusChipIntent
import org.mifos.core.ui.input.KptSearchBar
import org.mifos.core.ui.scaffold.KptScaffold
import org.mifos.core.ui.scaffold.rememberKptPullToRefreshState
import org.mifos.feature.groups.generated.resources.Res
import org.mifos.feature.groups.generated.resources.feature_groups_account_status
import org.mifos.feature.groups.generated.resources.feature_groups_active
import org.mifos.feature.groups.generated.resources.feature_groups_apply
import org.mifos.feature.groups.generated.resources.feature_groups_clear_all
import org.mifos.feature.groups.generated.resources.feature_groups_closed
import org.mifos.feature.groups.generated.resources.feature_groups_filters
import org.mifos.feature.groups.generated.resources.feature_groups_groups_title
import org.mifos.feature.groups.generated.resources.feature_groups_mifos_save
import org.mifos.feature.groups.generated.resources.feature_groups_new_group
import org.mifos.feature.groups.generated.resources.feature_groups_office_name
import org.mifos.feature.groups.generated.resources.feature_groups_pending
import org.mifos.feature.groups.generated.resources.feature_groups_search_groups
import org.mifos.feature.groups.generated.resources.feature_groups_sort_account_number
import org.mifos.feature.groups.generated.resources.feature_groups_sort_by
import org.mifos.feature.groups.generated.resources.feature_groups_sort_name
import org.mifos.feature.groups.generated.resources.feature_groups_total

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GroupDashboardScreen(
    onBackClick: () -> Unit,
    onNewGroupClick: () -> Unit,
    onGroupClick: (Long) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: GroupDashboardViewModel = koinViewModel(),
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val state by viewModel.stateFlow.collectAsStateWithLifecycle()

    if (state.isFilterVisible) {
        val accountStatusTitle = stringResource(Res.string.feature_groups_account_status)
        val officeNameTitle = stringResource(Res.string.feature_groups_office_name)
        val sortNameStr = stringResource(Res.string.feature_groups_sort_name)
        val sortAccountNumberStr = stringResource(Res.string.feature_groups_sort_account_number)

        val filterSections = remember(
            state.selectedStatuses,
            state.selectedOffices,
            state.availableStatuses,
            state.availableOffices,
            accountStatusTitle,
            officeNameTitle,
        ) {
            listOf(
                FilterSection(
                    title = accountStatusTitle,
                    availableOptions = state.availableStatuses,
                    selectedOptions = state.selectedStatuses,
                    onOptionToggle = { value ->
                        viewModel.trySendAction(GroupDashboardAction.HandleFilterClick(value, GroupFilterType.STATUS))
                    },
                ),
                FilterSection(
                    title = officeNameTitle,
                    availableOptions = state.availableOffices,
                    selectedOptions = state.selectedOffices,
                    onOptionToggle = { value ->
                        viewModel.trySendAction(GroupDashboardAction.HandleFilterClick(value, GroupFilterType.OFFICE))
                    },
                ),
            )
        }

        KptFilterBottomSheet(
            onDismissRequest = { viewModel.trySendAction(GroupDashboardAction.OnFilterClick) },
            sheetState = sheetState,
            title = stringResource(Res.string.feature_groups_filters),
            clearAllText = stringResource(Res.string.feature_groups_clear_all),
            applyText = stringResource(Res.string.feature_groups_apply),
            sortSectionTitle = stringResource(Res.string.feature_groups_sort_by),
            sortOptions = listOf(sortNameStr, sortAccountNumberStr),
            selectedSortOption = when (state.sortType) {
                GroupSortType.NAME -> sortNameStr
                GroupSortType.ACCOUNT_NUMBER -> sortAccountNumberStr
                null -> null
            },
            onSortOptionSelected = { sortValue ->
                val sortType = when (sortValue) {
                    sortNameStr -> GroupSortType.NAME
                    sortAccountNumberStr -> GroupSortType.ACCOUNT_NUMBER
                    else -> null
                }
                viewModel.trySendAction(GroupDashboardAction.HandleSortClick(sortType))
            },
            filterSections = filterSections,
            clearFilters = { viewModel.trySendAction(GroupDashboardAction.ClearFilters) },
        )
    }

    EventsEffect(viewModel.eventFlow) { event ->
        when (event) {
            GroupDashboardEvent.NavigateBack -> onBackClick()
            GroupDashboardEvent.NavigateToNewGroup -> onNewGroupClick()
            is GroupDashboardEvent.NavigateToGroupDetail -> onGroupClick(event.groupId)
        }
    }

    GroupDashboardScreenContent(
        state = state,
        onAction = viewModel::trySendAction,
        modifier = modifier,
    )
}

@Composable
internal fun GroupDashboardScreenContent(
    state: GroupDashboardState,
    modifier: Modifier = Modifier,
    onAction: (GroupDashboardAction) -> Unit,
) {
    state.pagingStream?.let { pagingStream ->
        KptScaffold(
            modifier = modifier,
            containerColor = KptTheme.colorScheme.primary,
            topBar = {
                GroupDashboardHeader(
                    onAction = onAction,
                )
            },
            pullToRefreshState = rememberKptPullToRefreshState(
                pagingStream = pagingStream,
            ),
        ) {
            Column(
                modifier = Modifier.fillMaxSize()
                    .background(
                        color = KptTheme.colorScheme.surface,
                        shape = RoundedCornerShape(
                            topStart = KptTheme.spacing.lg,
                            topEnd = KptTheme.spacing.lg,
                        ),
                    ),
            ) {
                KptSearchBar(
                    query = state.searchQuery,
                    modifier = Modifier.padding(horizontal = KptTheme.spacing.md),
                    onQueryChange = { onAction(GroupDashboardAction.SearchQueryChanged(it)) },
                    placeholder = stringResource(Res.string.feature_groups_search_groups),
                )

                PagingScreenContent(
                    pagingStream = pagingStream,
                    onRetry = { onAction(GroupDashboardAction.Retry) },
                    modifier = Modifier.fillMaxSize().padding(horizontal = KptTheme.spacing.md),
                ) { groups ->
                    val filteredAndSortedGroups = groups
                        .filter { group ->
                            val searchMatch = state.searchQuery.isBlank() ||
                                group.name.orEmpty().contains(state.searchQuery, ignoreCase = true)
                            val statusMatch = state.selectedStatuses.isEmpty() ||
                                group.status?.value in state.selectedStatuses
                            val officeMatch = state.selectedOffices.isEmpty() ||
                                group.officeName in state.selectedOffices
                            searchMatch && statusMatch && officeMatch
                        }
                        .let { list ->
                            when (state.sortType) {
                                GroupSortType.NAME -> list.sortedBy { it.name?.lowercase() }
                                GroupSortType.ACCOUNT_NUMBER -> list.sortedBy { it.accountNo }
                                else -> list
                            }
                        }

                    items(filteredAndSortedGroups, key = { it.id }) { group ->
                        KptItemCard(
                            title = group.name.orEmpty(),
                            statusText = group.status?.value.orEmpty(),
                            statusIntent = group.statusChipIntent,
                            leadingIcon = AppIcons.Group,
                            subRows = listOf(
                                SubRowItem(
                                    icon = AppIcons.Bank,
                                    text = group.officeName.orEmpty(),
                                ),
                                SubRowItem(
                                    icon = AppIcons.Badge,
                                    text = group.accountNo.orEmpty(),
                                ),
                            ),
                            onClick = { onAction(GroupDashboardAction.OnGroupClick(group.id)) },
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun GroupDashboardHeader(
    onAction: (GroupDashboardAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    KptHeader(
        modifier = modifier,
        navigationIcon = {
            KptHeaderBackButton(onClick = { onAction(GroupDashboardAction.OnBackClick) })
        },
        actions = {
            KptHeaderPillButton(
                text = stringResource(Res.string.feature_groups_new_group),
                icon = AppIcons.AddDefault,
                onClick = { onAction(GroupDashboardAction.OnNewGroupClick) },
            )
            HorizontalSpacer(width = KptTheme.spacing.sm)
            KptHeaderActionButton(
                icon = AppIcons.Tune,
                onClick = { onAction(GroupDashboardAction.OnFilterClick) },
                contentDescription = stringResource(Res.string.feature_groups_filters),
            )
        },
        title = {
            KptHeaderTitle(
                title = stringResource(Res.string.feature_groups_groups_title),
                subtitle = stringResource(Res.string.feature_groups_mifos_save),
            )
        },
        content = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(KptTheme.spacing.xs),
            ) {
                KptHeaderStatsCard(
                    label = stringResource(Res.string.feature_groups_total),
                    value = "—",
                    isValueAbove = true,
                    modifier = Modifier.weight(1f),
                )
                KptHeaderStatsCard(
                    label = stringResource(Res.string.feature_groups_active),
                    value = "—",
                    isValueAbove = true,
                    modifier = Modifier.weight(1f),
                )
                KptHeaderStatsCard(
                    label = stringResource(Res.string.feature_groups_pending),
                    value = "—",
                    isValueAbove = true,
                    modifier = Modifier.weight(1f),
                )
                KptHeaderStatsCard(
                    label = stringResource(Res.string.feature_groups_closed),
                    value = "—",
                    isValueAbove = true,
                    modifier = Modifier.weight(1f),
                )
            }
        },
    )
}
