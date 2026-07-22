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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import org.mifos.core.base.designsystem.theme.KptTheme
import org.mifos.core.base.ui.effects.EventsEffect
import org.mifos.core.base.ui.screen.DefaultLoadingContent
import org.mifos.core.base.ui.screen.ScreenContent
import org.mifos.core.base.ui.screen.ScreenStateLoading
import org.mifos.core.designsystem.component.KptEmptyState
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
import org.mifos.feature.groups.generated.resources.feature_groups_empty_loans_message
import org.mifos.feature.groups.generated.resources.feature_groups_filters
import org.mifos.feature.groups.generated.resources.feature_groups_loans_list_title
import org.mifos.feature.groups.generated.resources.feature_groups_mifos_save
import org.mifos.feature.groups.generated.resources.feature_groups_search_loans

@Composable
fun GroupLoanListScreen(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: GroupLoanListViewModel = koinViewModel(),
) {
    val state by viewModel.stateFlow.collectAsStateWithLifecycle()

    EventsEffect(viewModel.eventFlow) { event ->
        when (event) {
            GroupLoanListEvent.NavigateBack -> onBackClick()
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
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(bottom = KptTheme.spacing.md),
                        verticalArrangement = Arrangement.spacedBy(KptTheme.spacing.xs),
                    ) {
                        items(loans, key = { it.id }) { loanAccount ->
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
