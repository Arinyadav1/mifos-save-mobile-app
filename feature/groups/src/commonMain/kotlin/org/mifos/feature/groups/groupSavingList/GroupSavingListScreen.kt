/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See See https://github.com/openMF/kmp-project-template/blob/main/LICENSE
 */
package org.mifos.feature.groups.groupSavingList

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
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
import org.mifos.core.base.ui.screen.DefaultLoadingContent
import org.mifos.core.base.ui.screen.ScreenContent
import org.mifos.core.base.ui.screen.ScreenStateLoading
import org.mifos.core.designsystem.component.FilterSection
import org.mifos.core.designsystem.component.KptEmptyState
import org.mifos.core.designsystem.component.KptFilterBottomSheet
import org.mifos.core.designsystem.component.KptHeader
import org.mifos.core.designsystem.component.KptHeaderActionButton
import org.mifos.core.designsystem.component.KptHeaderBackButton
import org.mifos.core.designsystem.component.KptHeaderPillButton
import org.mifos.core.designsystem.component.KptHeaderTitle
import org.mifos.core.designsystem.icon.AppIcons
import org.mifos.core.ui.component.KptItemCard
import org.mifos.core.ui.component.SubRowItem
import org.mifos.core.ui.component.statusChipIntent
import org.mifos.core.ui.input.KptSearchBar
import org.mifos.core.ui.scaffold.KptScaffold
import org.mifos.feature.groups.generated.resources.Res
import org.mifos.feature.groups.generated.resources.feature_groups_account_status
import org.mifos.feature.groups.generated.resources.feature_groups_apply
import org.mifos.feature.groups.generated.resources.feature_groups_clear_all
import org.mifos.feature.groups.generated.resources.feature_groups_empty_savings_message
import org.mifos.feature.groups.generated.resources.feature_groups_filters
import org.mifos.feature.groups.generated.resources.feature_groups_mifos_save
import org.mifos.feature.groups.generated.resources.feature_groups_new_savings
import org.mifos.feature.groups.generated.resources.feature_groups_savings_list_title
import org.mifos.feature.groups.generated.resources.feature_groups_search_savings
import org.mifos.feature.groups.generated.resources.feature_groups_sort_account_number
import org.mifos.feature.groups.generated.resources.feature_groups_sort_by
import org.mifos.feature.groups.generated.resources.feature_groups_sort_name

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GroupSavingListScreen(
    onBackClick: () -> Unit,
    onSavingClick: (Long) -> Unit,
    onNewSavingsClick: (Long) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: GroupSavingListViewModel = koinViewModel(),
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val state by viewModel.stateFlow.collectAsStateWithLifecycle()

    if (state.isFilterVisible) {
        val accountStatusTitle = stringResource(Res.string.feature_groups_account_status)
        val sortNameStr = stringResource(Res.string.feature_groups_sort_name)
        val sortAccountNumberStr = stringResource(Res.string.feature_groups_sort_account_number)

        val filterSections = remember(
            state.selectedStatuses,
            state.availableStatuses,
            accountStatusTitle,
        ) {
            listOf(
                FilterSection(
                    title = accountStatusTitle,
                    availableOptions = state.availableStatuses,
                    selectedOptions = state.selectedStatuses,
                    onOptionToggle = { value ->
                        viewModel.trySendAction(
                            GroupSavingListAction.HandleFilterClick(
                                filterValue = value,
                                filterType = SavingFilterType.STATUS,
                            ),
                        )
                    },
                ),
            )
        }

        KptFilterBottomSheet(
            onDismissRequest = { viewModel.trySendAction(GroupSavingListAction.OnFilterClick) },
            sheetState = sheetState,
            title = stringResource(Res.string.feature_groups_filters),
            clearAllText = stringResource(Res.string.feature_groups_clear_all),
            applyText = stringResource(Res.string.feature_groups_apply),
            sortSectionTitle = stringResource(Res.string.feature_groups_sort_by),
            sortOptions = listOf(sortNameStr, sortAccountNumberStr),
            selectedSortOption = when (state.sortType) {
                SavingSortType.PRODUCT_NAME -> sortNameStr
                SavingSortType.ACCOUNT_NUMBER -> sortAccountNumberStr
                null -> null
            },
            onSortOptionSelected = { sortValue ->
                val sortType = when (sortValue) {
                    sortNameStr -> SavingSortType.PRODUCT_NAME
                    sortAccountNumberStr -> SavingSortType.ACCOUNT_NUMBER
                    else -> null
                }
                viewModel.trySendAction(GroupSavingListAction.HandleSortClick(sortType))
            },
            filterSections = filterSections,
            clearFilters = { viewModel.trySendAction(GroupSavingListAction.ClearFilters) },
        )
    }

    EventsEffect(viewModel.eventFlow) { event ->
        when (event) {
            GroupSavingListEvent.NavigateBack -> onBackClick()
            GroupSavingListEvent.NavigateToNewSavings -> onNewSavingsClick(viewModel.groupId)
            is GroupSavingListEvent.NavigateToSavingDetails -> onSavingClick(event.accountId)
        }
    }

    GroupSavingListScreenContent(
        state = state,
        onAction = viewModel::trySendAction,
        modifier = modifier,
    )
}

@Composable
internal fun GroupSavingListScreenContent(
    state: GroupSavingListState,
    onAction: (GroupSavingListAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    KptScaffold(
        modifier = modifier,
        containerColor = KptTheme.colorScheme.primary,
        topBar = {
            KptHeader(
                navigationIcon = {
                    KptHeaderBackButton(
                        onClick = {
                            onAction(GroupSavingListAction.OnBackClick)
                        },
                    )
                },
                actions = {
                    KptHeaderPillButton(
                        text = stringResource(Res.string.feature_groups_new_savings),
                        icon = AppIcons.AddDefault,
                        onClick = { onAction(GroupSavingListAction.OnNewSavingsClick) },
                    )
                    HorizontalSpacer(width = KptTheme.spacing.sm)
                    KptHeaderActionButton(
                        icon = AppIcons.Tune,
                        onClick = { onAction(GroupSavingListAction.OnFilterClick) },
                        contentDescription = stringResource(Res.string.feature_groups_filters),
                    )
                },
                title = {
                    KptHeaderTitle(
                        title = stringResource(Res.string.feature_groups_savings_list_title),
                        subtitle = stringResource(Res.string.feature_groups_mifos_save),
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
            Column(modifier = Modifier.fillMaxSize().padding(horizontal = KptTheme.spacing.md)) {
                KptSearchBar(
                    query = state.searchQuery,
                    onQueryChange = { onAction(GroupSavingListAction.SearchQueryChanged(it)) },
                    placeholder = stringResource(Res.string.feature_groups_search_savings),
                )

                ScreenContent(
                    state = state.screenState,
                    onRetry = { onAction(GroupSavingListAction.Retry) },
                    loading = {
                        DefaultLoadingContent(
                            config = ScreenStateLoading.Spinner,
                        )
                    },
                    empty = {
                        KptEmptyState(
                            message = stringResource(Res.string.feature_groups_empty_savings_message),
                        )
                    },
                    modifier = Modifier.fillMaxSize(),
                ) { savings, freshness ->
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(bottom = KptTheme.spacing.md),
                        verticalArrangement = Arrangement.spacedBy(KptTheme.spacing.xs),
                    ) {
                        items(savings, key = { it.id }) { savingAccount ->
                            KptItemCard(
                                title = savingAccount.productName.orEmpty(),
                                statusText = savingAccount.status?.value
                                    ?.replace("Submitted and ", "")
                                    ?.replaceFirstChar { it.uppercase() }
                                    .orEmpty(),
                                statusIntent = savingAccount.statusChipIntent,
                                leadingIcon = AppIcons.Savings,
                                subRows = listOf(
                                    SubRowItem(
                                        icon = AppIcons.Badge,
                                        text = savingAccount.accountNo.orEmpty(),
                                    ),
                                ),
                                onClick = {
                                    onAction(
                                        GroupSavingListAction.OnSavingAccountClick(
                                            savingAccount.id,
                                        ),
                                    )
                                },
                            )
                        }
                    }
                }
            }
        }
    }
}
