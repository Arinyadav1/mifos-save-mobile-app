/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See See https://github.com/openMF/kmp-project-template/blob/main/LICENSE
 */
package org.mifos.feature.groups.groupLoanList

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
import org.mifos.feature.groups.generated.resources.feature_groups_empty_loans_message
import org.mifos.feature.groups.generated.resources.feature_groups_filters
import org.mifos.feature.groups.generated.resources.feature_groups_loans_list_title
import org.mifos.feature.groups.generated.resources.feature_groups_mifos_save
import org.mifos.feature.groups.generated.resources.feature_groups_product_name
import org.mifos.feature.groups.generated.resources.feature_groups_search_loans
import org.mifos.feature.groups.generated.resources.feature_groups_sort_account_number
import org.mifos.feature.groups.generated.resources.feature_groups_sort_by

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GroupLoanListScreen(
    onBackClick: () -> Unit,
    onLoanClick: (Long) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: GroupLoanListViewModel = koinViewModel(),
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val state by viewModel.stateFlow.collectAsStateWithLifecycle()

    if (state.isFilterVisible) {
        val accountStatusTitle = stringResource(Res.string.feature_groups_account_status)
        val productNameTitle = stringResource(Res.string.feature_groups_product_name)
        val sortProductNameStr = stringResource(Res.string.feature_groups_product_name)
        val sortAccountNumberStr = stringResource(Res.string.feature_groups_sort_account_number)

        val filterSections = remember(
            state.selectedStatuses,
            state.selectedProducts,
            state.availableStatuses,
            state.availableProducts,
            accountStatusTitle,
            productNameTitle,
        ) {
            listOf(
                FilterSection(
                    title = accountStatusTitle,
                    availableOptions = state.availableStatuses,
                    selectedOptions = state.selectedStatuses,
                    onOptionToggle = { value ->
                        viewModel.trySendAction(GroupLoanListAction.HandleFilterClick(value, LoanFilterType.STATUS))
                    },
                ),
                FilterSection(
                    title = productNameTitle,
                    availableOptions = state.availableProducts,
                    selectedOptions = state.selectedProducts,
                    onOptionToggle = { value ->
                        viewModel.trySendAction(GroupLoanListAction.HandleFilterClick(value, LoanFilterType.PRODUCT))
                    },
                ),
            )
        }

        KptFilterBottomSheet(
            onDismissRequest = { viewModel.trySendAction(GroupLoanListAction.OnFilterClick) },
            sheetState = sheetState,
            title = stringResource(Res.string.feature_groups_filters),
            clearAllText = stringResource(Res.string.feature_groups_clear_all),
            applyText = stringResource(Res.string.feature_groups_apply),
            sortSectionTitle = stringResource(Res.string.feature_groups_sort_by),
            sortOptions = listOf(sortProductNameStr, sortAccountNumberStr),
            selectedSortOption = when (state.sortType) {
                LoanSortType.PRODUCT_NAME -> sortProductNameStr
                LoanSortType.ACCOUNT_NUMBER -> sortAccountNumberStr
                null -> null
            },
            onSortOptionSelected = { sortValue ->
                val sortType = when (sortValue) {
                    sortProductNameStr -> LoanSortType.PRODUCT_NAME
                    sortAccountNumberStr -> LoanSortType.ACCOUNT_NUMBER
                    else -> null
                }
                viewModel.trySendAction(GroupLoanListAction.HandleSortClick(sortType))
            },
            filterSections = filterSections,
            clearFilters = { viewModel.trySendAction(GroupLoanListAction.ClearFilters) },
        )
    }

    EventsEffect(viewModel.eventFlow) { event ->
        when (event) {
            GroupLoanListEvent.NavigateBack -> onBackClick()
            is GroupLoanListEvent.NavigateToLoanDetail -> onLoanClick(event.accountId)
        }
    }

    GroupLoanListScreenContent(
        state = state,
        onAction = viewModel::trySendAction,
        modifier = modifier,
    )
}

@Composable
internal fun GroupLoanListScreenContent(
    state: GroupLoanListState,
    onAction: (GroupLoanListAction) -> Unit,
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
                            onAction(GroupLoanListAction.OnBackClick)
                        },
                    )
                },
                actions = {
                    KptHeaderActionButton(
                        icon = AppIcons.Tune,
                        onClick = { onAction(GroupLoanListAction.OnFilterClick) },
                        contentDescription = stringResource(Res.string.feature_groups_filters),
                    )
                },
                title = {
                    KptHeaderTitle(
                        title = stringResource(Res.string.feature_groups_loans_list_title),
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
                    onQueryChange = { onAction(GroupLoanListAction.SearchQueryChanged(it)) },
                    placeholder = stringResource(Res.string.feature_groups_search_loans),
                )

                ScreenContent(
                    state = state.screenState,
                    onRetry = { onAction(GroupLoanListAction.Retry) },
                    loading = {
                        DefaultLoadingContent(
                            config = ScreenStateLoading.Spinner,
                        )
                    },
                    empty = {
                        KptEmptyState(
                            message = stringResource(Res.string.feature_groups_empty_loans_message),
                        )
                    },
                    modifier = Modifier.fillMaxSize(),
                ) { loans, freshness ->
                    val filteredAndSortedLoans = remember(
                        loans,
                        state.searchQuery,
                        state.selectedStatuses,
                        state.selectedProducts,
                        state.sortType,
                    ) {
                        loans
                            .filter { loan ->
                                val searchMatch = state.searchQuery.isBlank() ||
                                    loan.productName.orEmpty().contains(state.searchQuery, ignoreCase = true) ||
                                    loan.accountNo.orEmpty().contains(state.searchQuery, ignoreCase = true) ||
                                    loan.status?.value.orEmpty().contains(state.searchQuery, ignoreCase = true)
                                val statusMatch = state.selectedStatuses.isEmpty() ||
                                    loan.status?.value in state.selectedStatuses
                                val productMatch = state.selectedProducts.isEmpty() ||
                                    loan.productName in state.selectedProducts
                                searchMatch && statusMatch && productMatch
                            }
                            .let { list ->
                                when (state.sortType) {
                                    LoanSortType.PRODUCT_NAME -> list.sortedBy { it.productName?.lowercase() }
                                    LoanSortType.ACCOUNT_NUMBER -> list.sortedBy { it.accountNo }
                                    else -> list
                                }
                            }
                    }

                    if (filteredAndSortedLoans.isEmpty()) {
                        KptEmptyState(
                            message = stringResource(Res.string.feature_groups_empty_loans_message),
                        )
                    } else {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(bottom = KptTheme.spacing.md),
                            verticalArrangement = Arrangement.spacedBy(KptTheme.spacing.xs),
                        ) {
                            items(filteredAndSortedLoans, key = { it.id }) { loanAccount ->
                                KptItemCard(
                                    title = loanAccount.productName.orEmpty(),
                                    statusText = loanAccount.status?.value
                                        ?.replace("Submitted and ", "")
                                        ?.replaceFirstChar { it.uppercase() }
                                        .orEmpty(),
                                    statusIntent = loanAccount.statusChipIntent,
                                    leadingIcon = AppIcons.Loans,
                                    subRows = listOf(
                                        SubRowItem(
                                            icon = AppIcons.Badge,
                                            text = loanAccount.accountNo.orEmpty(),
                                        ),
                                    ),
                                    onClick = {
                                        onAction(
                                            GroupLoanListAction.OnLoanAccountClick(
                                                loanAccount.id,
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
}
