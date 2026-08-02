/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See See https://github.com/openMF/kmp-project-template/blob/main/LICENSE
 */
package org.mifos.feature.saving.savingDetails

import androidx.compose.foundation.background
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
import org.mifos.feature.saving.generated.resources.Res
import org.mifos.feature.saving.generated.resources.feature_saving_account_number
import org.mifos.feature.saving.generated.resources.feature_saving_activate_savings
import org.mifos.feature.saving.generated.resources.feature_saving_available_balance
import org.mifos.feature.saving.generated.resources.feature_saving_close_savings
import org.mifos.feature.saving.generated.resources.feature_saving_current_balance
import org.mifos.feature.saving.generated.resources.feature_saving_details_title
import org.mifos.feature.saving.generated.resources.feature_saving_explore_deposit
import org.mifos.feature.saving.generated.resources.feature_saving_explore_general
import org.mifos.feature.saving.generated.resources.feature_saving_explore_title
import org.mifos.feature.saving.generated.resources.feature_saving_explore_transactions
import org.mifos.feature.saving.generated.resources.feature_saving_explore_withdraw
import org.mifos.feature.saving.generated.resources.feature_saving_overview_title
import org.mifos.feature.saving.generated.resources.feature_saving_savings_product
import org.mifos.feature.saving.generated.resources.feature_saving_update_savings

@Composable
fun SavingDetailsScreen(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    onGeneralClick: (Long) -> Unit = {},
    onTransactionsClick: (Long) -> Unit = {},
    onDepositTransactionClick: (Long) -> Unit = {},
    onWithdrawTransactionClick: (Long) -> Unit = {},
    onActivateSavingsClick: (Long) -> Unit = {},
    onUpdateSavingsClick: (Long) -> Unit = {},
    viewModel: SavingDetailsViewModel = koinViewModel(),
) {
    val state by viewModel.stateFlow.collectAsStateWithLifecycle()

    EventsEffect(viewModel.eventFlow) { event ->
        when (event) {
            SavingDetailsEvent.NavigateBack -> onBackClick()
            SavingDetailsEvent.NavigateToGeneral -> onGeneralClick(viewModel.accountId)
            SavingDetailsEvent.NavigateToTransactions -> onTransactionsClick(viewModel.accountId)
            SavingDetailsEvent.NavigateToDepositTransaction -> onDepositTransactionClick(viewModel.accountId)
            SavingDetailsEvent.NavigateToWithdrawTransaction -> onWithdrawTransactionClick(viewModel.accountId)
            SavingDetailsEvent.NavigateToActivateSavings -> onActivateSavingsClick(viewModel.accountId)
            SavingDetailsEvent.NavigateToUpdateSavings -> onUpdateSavingsClick(viewModel.accountId)
            SavingDetailsEvent.NavigateToCloseSavings -> {
                /* TODO */
            }
        }
    }

    SavingDetailsScreenContent(
        state = state,
        onAction = viewModel::trySendAction,
        modifier = modifier,
    )
}

@Composable
internal fun SavingDetailsScreenContent(
    state: SavingDetailsState,
    onAction: (SavingDetailsAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    KptScaffold(
        modifier = modifier,
        containerColor = KptTheme.colorScheme.primary,
        bottomBar = {
            Box(
                modifier = Modifier.fillMaxWidth().background(KptTheme.colorScheme.surface)
                    .navigationBarsPadding(),
            )
        },
        topBar = {
            val savingDetail = state.screenState.dataOrNull
            KptHeader(
                navigationIcon = {
                    KptHeaderBackButton(onClick = { onAction(SavingDetailsAction.OnBackClick) })
                },
                actions = {
                    Box {
                        KptHeaderActionButton(
                            icon = AppIcons.MoreVert,
                            onClick = { onAction(SavingDetailsAction.SetMenuVisible(true)) },
                        )
                        KptDropdownMenu(
                            expanded = state.showMenu,
                            onDismissRequest = { onAction(SavingDetailsAction.SetMenuVisible(false)) },
                            items = buildList {
                                if (savingDetail?.status?.code?.contains("pending") == true) {
                                    add(
                                        KptDropdownMenuItem(
                                            text = stringResource(Res.string.feature_saving_close_savings),
                                            onClick = {
                                                onAction(SavingDetailsAction.SetMenuVisible(false))
                                                onAction(SavingDetailsAction.OnCloseSavingsClick)
                                            },
                                        ),
                                    )
                                    add(
                                        KptDropdownMenuItem(
                                            text = stringResource(Res.string.feature_saving_activate_savings),
                                            onClick = {
                                                onAction(SavingDetailsAction.SetMenuVisible(false))
                                                onAction(SavingDetailsAction.OnActivateSavingsClick)
                                            },
                                        ),
                                    )
                                }

                                add(
                                    KptDropdownMenuItem(
                                        text = stringResource(Res.string.feature_saving_update_savings),
                                        onClick = {
                                            onAction(SavingDetailsAction.SetMenuVisible(false))
                                            onAction(SavingDetailsAction.OnUpdateSavingsClick)
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
                            title = savingDetail?.savingsProductName ?: "",
                            subtitle = stringResource(Res.string.feature_saving_details_title),
                            maxLines = 2,
                            modifier = Modifier.weight(1f),
                        )
                        if (savingDetail != null) {
                            HorizontalSpacer(
                                KptTheme.spacing.md,
                            )
                            StatusChip(
                                text = savingDetail.status?.value?.replace("Submitted and ", "")
                                    ?.replaceFirstChar { it.uppercase() }.orEmpty(),
                                intent = savingDetail.statusChipIntent,
                            )
                        }
                    }
                },
            )
        },
    ) {
        Box(
            modifier = Modifier.fillMaxSize().background(
                color = KptTheme.colorScheme.surface,
                shape = RoundedCornerShape(
                    topStart = KptTheme.spacing.lg,
                    topEnd = KptTheme.spacing.lg,
                ),
            ),
        ) {
            ScreenContent(
                state = state.screenState,
                onRetry = { onAction(SavingDetailsAction.Retry) },
                loading = {
                    DefaultLoadingContent(
                        config = ScreenStateLoading.Spinner,
                    )
                },
                modifier = Modifier.fillMaxSize(),
            ) { detail, freshness ->
                Column(
                    modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState())
                        .padding(horizontal = KptTheme.spacing.md)
                        .padding(bottom = KptTheme.spacing.xl, top = KptTheme.spacing.sm),
                    verticalArrangement = Arrangement.spacedBy(KptTheme.spacing.md),
                ) {
                    SectionHeader(
                        title = stringResource(Res.string.feature_saving_overview_title),
                    )

                    KptKeyValueCard(
                        items = mapOf(
                            stringResource(Res.string.feature_saving_savings_product) to
                                (detail.savingsProductName ?: "—"),
                            stringResource(Res.string.feature_saving_account_number) to
                                (detail.accountNo ?: "—"),
                            stringResource(Res.string.feature_saving_current_balance) to
                                "${detail.summary?.accountBalance ?: 0.0}",
                            stringResource(Res.string.feature_saving_available_balance) to
                                "${detail.summary?.availableBalance ?: 0.0}",
                        ),
                    )

                    SectionHeader(
                        title = stringResource(Res.string.feature_saving_explore_title),
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
    onAction: (SavingDetailsAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(KptTheme.spacing.sm),
    ) {
        KptExploreCard(
            title = stringResource(Res.string.feature_saving_explore_general),
            leadingIcon = AppIcons.DataInfo,
            onClick = { onAction(SavingDetailsAction.OnGeneralClick) },
        )

        KptExploreCard(
            title = stringResource(Res.string.feature_saving_explore_transactions),
            leadingIcon = AppIcons.Glim,
            onClick = { onAction(SavingDetailsAction.OnTransactionsClick) },
        )

        KptExploreCard(
            title = stringResource(Res.string.feature_saving_explore_deposit),
            leadingIcon = AppIcons.Add,
            onClick = { onAction(SavingDetailsAction.OnDepositTransactionClick) },
        )

        KptExploreCard(
            title = stringResource(Res.string.feature_saving_explore_withdraw),
            leadingIcon = AppIcons.Payment,
            onClick = { onAction(SavingDetailsAction.OnWithdrawTransactionClick) },
        )
    }
}
