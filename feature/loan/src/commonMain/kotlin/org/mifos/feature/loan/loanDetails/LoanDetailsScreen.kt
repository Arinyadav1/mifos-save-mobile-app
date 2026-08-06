/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See See https://github.com/openMF/kmp-project-template/blob/main/LICENSE
 */
package org.mifos.feature.loan.loanDetails

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
import org.mifos.feature.loan.generated.resources.Res
import org.mifos.feature.loan.generated.resources.feature_loan_account_number
import org.mifos.feature.loan.generated.resources.feature_loan_approve_loan
import org.mifos.feature.loan.generated.resources.feature_loan_details_title
import org.mifos.feature.loan.generated.resources.feature_loan_explore_disbursement
import org.mifos.feature.loan.generated.resources.feature_loan_explore_general
import org.mifos.feature.loan.generated.resources.feature_loan_explore_repayment_schedule
import org.mifos.feature.loan.generated.resources.feature_loan_explore_title
import org.mifos.feature.loan.generated.resources.feature_loan_explore_transactions
import org.mifos.feature.loan.generated.resources.feature_loan_loan_product
import org.mifos.feature.loan.generated.resources.feature_loan_outstanding
import org.mifos.feature.loan.generated.resources.feature_loan_overview_title
import org.mifos.feature.loan.generated.resources.feature_loan_principal
import org.mifos.feature.loan.generated.resources.feature_loan_reject_loan
import org.mifos.feature.loan.generated.resources.feature_loan_undo_approval_loan

@Composable
fun LoanDetailsScreen(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    onGeneralClick: (Long) -> Unit = {},
    onTransactionsClick: (Long) -> Unit = {},
    onRepaymentScheduleClick: (Long) -> Unit = {},
    onLoanDisbursementClick: (Long) -> Unit = {},
    onRejectLoanClick: (Long) -> Unit = {},
    onApproveLoanClick: (Long) -> Unit = {},
    onUndoApprovalLoanClick: (Long) -> Unit = {},
    viewModel: LoanDetailsViewModel = koinViewModel(),
) {
    val state by viewModel.stateFlow.collectAsStateWithLifecycle()

    EventsEffect(viewModel.eventFlow) { event ->
        when (event) {
            LoanDetailsEvent.NavigateBack -> onBackClick()
            LoanDetailsEvent.NavigateToGeneral -> onGeneralClick(viewModel.loanId)
            LoanDetailsEvent.NavigateToTransactions -> onTransactionsClick(viewModel.loanId)
            LoanDetailsEvent.NavigateToRepaymentSchedule -> onRepaymentScheduleClick(viewModel.loanId)
            LoanDetailsEvent.NavigateToLoanDisbursement -> onLoanDisbursementClick(viewModel.loanId)
            LoanDetailsEvent.NavigateToRejectLoan -> onRejectLoanClick(viewModel.loanId)
            LoanDetailsEvent.NavigateToApproveLoan -> onApproveLoanClick(viewModel.loanId)
            LoanDetailsEvent.NavigateToUndoApprovalLoan -> onUndoApprovalLoanClick(viewModel.loanId)
        }
    }

    LoanDetailsScreenContent(
        state = state,
        onAction = viewModel::trySendAction,
        modifier = modifier,
    )
}

@Composable
internal fun LoanDetailsScreenContent(
    state: LoanDetailsState,
    onAction: (LoanDetailsAction) -> Unit,
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
            LoanDetailsHeader(
                state = state,
                onAction = onAction,
            )
        },
    ) {
        LoanDetailsBody(
            state = state,
            onAction = onAction,
        )
    }
}

@Composable
private fun LoanDetailsHeader(
    state: LoanDetailsState,
    onAction: (LoanDetailsAction) -> Unit,
) {
    val loanDetail = state.screenState.dataOrNull
    KptHeader(
        navigationIcon = {
            KptHeaderBackButton(onClick = { onAction(LoanDetailsAction.OnBackClick) })
        },
        actions = {
            Box {
                KptHeaderActionButton(
                    icon = AppIcons.MoreVert,
                    onClick = { onAction(LoanDetailsAction.SetMenuVisible(true)) },
                )
                KptDropdownMenu(
                    expanded = state.showMenu,
                    onDismissRequest = { onAction(LoanDetailsAction.SetMenuVisible(false)) },
                    items = buildList {
                        if (loanDetail?.status?.pendingApproval == true) {
                            add(
                                KptDropdownMenuItem(
                                    text = stringResource(Res.string.feature_loan_approve_loan),
                                    onClick = {
                                        onAction(LoanDetailsAction.SetMenuVisible(false))
                                        onAction(LoanDetailsAction.OnApproveLoanClick)
                                    },
                                ),
                            )
                            add(
                                KptDropdownMenuItem(
                                    text = stringResource(Res.string.feature_loan_reject_loan),
                                    onClick = {
                                        onAction(LoanDetailsAction.SetMenuVisible(false))
                                        onAction(LoanDetailsAction.OnRejectLoanClick)
                                    },
                                ),
                            )
                        } else if (loanDetail?.status?.waitingForDisbursal == true) {
                            add(
                                KptDropdownMenuItem(
                                    text = stringResource(Res.string.feature_loan_undo_approval_loan),
                                    onClick = {
                                        onAction(LoanDetailsAction.SetMenuVisible(false))
                                        onAction(LoanDetailsAction.OnUndoApprovalLoanClick)
                                    },
                                ),
                            )
                        } else {
                            add(
                                KptDropdownMenuItem(
                                    text = stringResource(Res.string.feature_loan_approve_loan),
                                    onClick = {
                                        onAction(LoanDetailsAction.SetMenuVisible(false))
                                        onAction(LoanDetailsAction.OnApproveLoanClick)
                                    },
                                ),
                            )
                            add(
                                KptDropdownMenuItem(
                                    text = stringResource(Res.string.feature_loan_reject_loan),
                                    onClick = {
                                        onAction(LoanDetailsAction.SetMenuVisible(false))
                                        onAction(LoanDetailsAction.OnRejectLoanClick)
                                    },
                                ),
                            )
                            add(
                                KptDropdownMenuItem(
                                    text = stringResource(Res.string.feature_loan_undo_approval_loan),
                                    onClick = {
                                        onAction(LoanDetailsAction.SetMenuVisible(false))
                                        onAction(LoanDetailsAction.OnUndoApprovalLoanClick)
                                    },
                                ),
                            )
                        }
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
                    title = loanDetail?.loanProductName ?: "",
                    subtitle = stringResource(Res.string.feature_loan_details_title),
                    maxLines = 2,
                    modifier = Modifier.weight(1f),
                )
                if (loanDetail != null) {
                    HorizontalSpacer(
                        KptTheme.spacing.md,
                    )
                    StatusChip(
                        text = loanDetail.status?.value?.replace("Submitted and ", "")
                            ?.replaceFirstChar { it.uppercase() }.orEmpty(),
                        intent = loanDetail.statusChipIntent,
                    )
                }
            }
        },
    )
}

@Composable
private fun LoanDetailsBody(
    state: LoanDetailsState,
    onAction: (LoanDetailsAction) -> Unit,
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
            onRetry = { onAction(LoanDetailsAction.Retry) },
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
                    title = stringResource(Res.string.feature_loan_overview_title),
                )

                KptKeyValueCard(
                    items = mapOf(
                        stringResource(Res.string.feature_loan_loan_product) to
                            (detail.loanProductName ?: "—"),
                        stringResource(Res.string.feature_loan_account_number) to
                            (detail.accountNo ?: "—"),
                        stringResource(Res.string.feature_loan_principal) to
                            "${detail.currency?.displaySymbol ?: ""}${detail.principal}",
                        stringResource(Res.string.feature_loan_outstanding) to
                            "${detail.currency?.displaySymbol ?: ""}${detail.summary?.totalOutstanding ?: 0.0}",
                    ),
                )

                SectionHeader(
                    title = stringResource(Res.string.feature_loan_explore_title),
                )

                ExploreCart(
                    onAction = onAction,
                )
            }
        }
    }
}

@Composable
fun ExploreCart(
    onAction: (LoanDetailsAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(KptTheme.spacing.sm),
    ) {
        KptExploreCard(
            title = stringResource(Res.string.feature_loan_explore_general),
            leadingIcon = AppIcons.DataInfo,
            onClick = { onAction(LoanDetailsAction.OnGeneralClick) },
        )

        KptExploreCard(
            title = stringResource(Res.string.feature_loan_explore_transactions),
            leadingIcon = AppIcons.Glim,
            onClick = { onAction(LoanDetailsAction.OnTransactionsClick) },
        )

        KptExploreCard(
            title = stringResource(Res.string.feature_loan_explore_repayment_schedule),
            leadingIcon = AppIcons.Savings,
            onClick = { onAction(LoanDetailsAction.OnRepaymentScheduleClick) },
        )

        KptExploreCard(
            title = stringResource(Res.string.feature_loan_explore_disbursement),
            leadingIcon = AppIcons.Payment,
            onClick = { onAction(LoanDetailsAction.OnLoanDisbursementClick) },
        )
    }
}
