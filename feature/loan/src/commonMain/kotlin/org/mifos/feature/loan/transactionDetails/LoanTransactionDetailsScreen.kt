/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See See https://github.com/openMF/kmp-project-template/blob/main/LICENSE
 */
package org.mifos.feature.loan.transactionDetails

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
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
import org.mifos.core.common.FormatDate
import org.mifos.core.common.formatCurrency
import org.mifos.core.designsystem.component.KptHeader
import org.mifos.core.designsystem.component.KptHeaderBackButton
import org.mifos.core.designsystem.component.KptHeaderTitle
import org.mifos.core.designsystem.component.KptHorizontalStatsCard
import org.mifos.core.designsystem.component.KptKeyValueCard
import org.mifos.core.designsystem.component.SectionHeader
import org.mifos.core.designsystem.icon.AppIcons
import org.mifos.core.designsystem.theme.finance
import org.mifos.core.designsystem.theme.spacing
import org.mifos.core.model.loans.LoanTransaction
import org.mifos.core.model.loans.isCredit
import org.mifos.core.model.loans.isDebit
import org.mifos.core.ui.scaffold.KptScaffold
import org.mifos.feature.loan.generated.resources.Res
import org.mifos.feature.loan.generated.resources.feature_loan_overview_title
import org.mifos.feature.loan.generated.resources.feature_loan_transaction_amount
import org.mifos.feature.loan.generated.resources.feature_loan_transaction_date
import org.mifos.feature.loan.generated.resources.feature_loan_transaction_details
import org.mifos.feature.loan.generated.resources.feature_loan_transaction_details_title
import org.mifos.feature.loan.generated.resources.feature_loan_transaction_id
import org.mifos.feature.loan.generated.resources.feature_loan_transaction_office_name
import org.mifos.feature.loan.generated.resources.feature_loan_transaction_outstanding_balance
import org.mifos.feature.loan.generated.resources.feature_loan_transaction_portion_fees
import org.mifos.feature.loan.generated.resources.feature_loan_transaction_portion_interest
import org.mifos.feature.loan.generated.resources.feature_loan_transaction_portion_overpayment
import org.mifos.feature.loan.generated.resources.feature_loan_transaction_portion_penalties
import org.mifos.feature.loan.generated.resources.feature_loan_transaction_portion_principal
import org.mifos.feature.loan.generated.resources.feature_loan_transaction_portion_unrecognized_income
import org.mifos.feature.loan.generated.resources.feature_loan_transaction_reversed
import org.mifos.feature.loan.generated.resources.feature_loan_transaction_submitted_on
import org.mifos.feature.loan.generated.resources.feature_loan_transaction_type

@Composable
fun LoanTransactionDetailsScreen(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: LoanTransactionDetailsViewModel = koinViewModel(),
) {
    val state by viewModel.stateFlow.collectAsStateWithLifecycle()

    EventsEffect(viewModel.eventFlow) { event ->
        when (event) {
            LoanTransactionDetailsEvent.NavigateBack -> onBackClick()
        }
    }

    LoanTransactionDetailsScreenContent(
        state = state,
        onAction = viewModel::trySendAction,
        modifier = modifier,
    )
}

@Composable
internal fun LoanTransactionDetailsScreenContent(
    state: LoanTransactionDetailsState,
    onAction: (LoanTransactionDetailsAction) -> Unit,
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
                    KptHeaderBackButton(onClick = { onAction(LoanTransactionDetailsAction.OnBackClick) })
                },
                title = {
                    KptHeaderTitle(
                        title = stringResource(Res.string.feature_loan_transaction_details),
                        maxLines = 1,
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
                onRetry = { onAction(LoanTransactionDetailsAction.Retry) },
                loading = {
                    DefaultLoadingContent(
                        config = ScreenStateLoading.Spinner,
                    )
                },
                modifier = Modifier.fillMaxSize(),
            ) { transaction, _ ->
                val currencySymbol = transaction.currency?.displaySymbol ?: "$"
                val isCredit = transaction.isCredit
                val isDebit = transaction.isDebit

                val icon = if (isCredit) {
                    AppIcons.ArrowUpward
                } else {
                    AppIcons.ArrowDownward
                }
                val iconColor = if (isCredit) {
                    MaterialTheme.finance.moneyPositive
                } else {
                    MaterialTheme.finance.moneyNegative
                }
                val iconBgColor = iconColor.copy(alpha = 0.15f)

                val amountSign = if (isCredit) "+" else if (isDebit) "-" else ""
                val amountText = "$amountSign${formatCurrency(transaction.amount, currencySymbol)}"

                val detailsMap = buildDetailsMap(transaction, currencySymbol)

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = KptTheme.spacing.md)
                        .padding(bottom = KptTheme.spacing.xl, top = KptTheme.spacing.sm),
                    verticalArrangement = Arrangement.spacedBy(KptTheme.spacing.md),
                ) {
                    SectionHeader(
                        title = stringResource(Res.string.feature_loan_overview_title),
                    )

                    KptHorizontalStatsCard(
                        value = amountText,
                        label = transaction.type?.value.orEmpty(),
                        icon = icon,
                        iconContainerColor = iconBgColor,
                        iconColor = iconColor,
                        modifier = Modifier.fillMaxWidth(),
                    )

                    SectionHeader(
                        title = stringResource(Res.string.feature_loan_transaction_details_title),
                    )

                    KptKeyValueCard(
                        items = detailsMap,
                    )
                }
            }
        }
    }
}

@Composable
private fun buildDetailsMap(
    transaction: LoanTransaction,
    currencySymbol: String,
): Map<String, String> {
    return buildMap {
        put(stringResource(Res.string.feature_loan_transaction_id), transaction.id.toString())
        put(
            stringResource(Res.string.feature_loan_transaction_date),
            FormatDate.formatLocalDate(transaction.date),
        )
        put(
            stringResource(Res.string.feature_loan_transaction_type),
            transaction.type?.value.orEmpty(),
        )
        put(
            stringResource(Res.string.feature_loan_transaction_amount),
            formatCurrency(transaction.amount, currencySymbol),
        )

        if (transaction.principalPortion > 0) {
            put(
                stringResource(Res.string.feature_loan_transaction_portion_principal),
                formatCurrency(transaction.principalPortion, currencySymbol),
            )
        }
        if (transaction.interestPortion > 0) {
            put(
                stringResource(Res.string.feature_loan_transaction_portion_interest),
                formatCurrency(transaction.interestPortion, currencySymbol),
            )
        }
        if (transaction.feeChargesPortion > 0) {
            put(
                stringResource(Res.string.feature_loan_transaction_portion_fees),
                formatCurrency(transaction.feeChargesPortion, currencySymbol),
            )
        }
        if (transaction.penaltyChargesPortion > 0) {
            put(
                stringResource(Res.string.feature_loan_transaction_portion_penalties),
                formatCurrency(transaction.penaltyChargesPortion, currencySymbol),
            )
        }
        if (transaction.overpaymentPortion > 0) {
            put(
                stringResource(Res.string.feature_loan_transaction_portion_overpayment),
                formatCurrency(transaction.overpaymentPortion, currencySymbol),
            )
        }
        if (transaction.unrecognizedIncomePortion > 0) {
            put(
                stringResource(Res.string.feature_loan_transaction_portion_unrecognized_income),
                formatCurrency(transaction.unrecognizedIncomePortion, currencySymbol),
            )
        }

        put(
            stringResource(Res.string.feature_loan_transaction_outstanding_balance),
            formatCurrency(transaction.outstandingLoanBalance, currencySymbol),
        )

        put(
            stringResource(Res.string.feature_loan_transaction_reversed),
            transaction.manuallyReversed.toString(),
        )

        if (transaction.submittedOnDate != null) {
            put(
                stringResource(Res.string.feature_loan_transaction_submitted_on),
                FormatDate.formatLocalDate(transaction.submittedOnDate),
            )
        }

        val officeName = transaction.officeName
        if (!officeName.isNullOrBlank()) {
            put(stringResource(Res.string.feature_loan_transaction_office_name), officeName)
        }
    }
}
