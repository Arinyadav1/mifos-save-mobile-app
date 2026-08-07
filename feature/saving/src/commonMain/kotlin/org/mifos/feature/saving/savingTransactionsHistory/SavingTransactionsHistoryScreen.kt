/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See See https://github.com/openMF/kmp-project-template/blob/main/LICENSE
 */
package org.mifos.feature.saving.savingTransactionsHistory

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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
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
import org.mifos.core.common.formatCurrency
import org.mifos.core.designsystem.component.KptHeader
import org.mifos.core.designsystem.component.KptHeaderBackButton
import org.mifos.core.designsystem.component.KptHeaderTitle
import org.mifos.core.designsystem.icon.AppIcons
import org.mifos.core.designsystem.theme.finance
import org.mifos.core.designsystem.theme.spacing
import org.mifos.core.model.savings.SavingDetailTransaction
import org.mifos.core.model.savings.isCredit
import org.mifos.core.model.savings.isDebit
import org.mifos.core.ui.scaffold.KptScaffold
import org.mifos.feature.saving.generated.resources.Res
import org.mifos.feature.saving.generated.resources.feature_saving_empty_transactions
import org.mifos.feature.saving.generated.resources.feature_saving_transaction_balance_prefix
import org.mifos.feature.saving.generated.resources.feature_saving_transaction_ext_prefix
import org.mifos.feature.saving.generated.resources.feature_saving_transaction_id_prefix
import org.mifos.feature.saving.generated.resources.feature_saving_transactions_title

@Composable
fun SavingTransactionsHistoryScreen(
    onBackClick: () -> Unit,
    onTransactionClick: (Long, Long) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SavingTransactionsHistoryViewModel = koinViewModel(),
) {
    val state by viewModel.stateFlow.collectAsStateWithLifecycle()

    EventsEffect(viewModel.eventFlow) { event ->
        when (event) {
            SavingTransactionsHistoryEvent.NavigateBack -> onBackClick()
            is SavingTransactionsHistoryEvent.NavigateToTransactionDetails ->
                onTransactionClick(event.accountId, event.transactionId)
        }
    }

    SavingTransactionsHistoryScreenContent(
        state = state,
        onAction = viewModel::trySendAction,
        modifier = modifier,
    )
}

@Composable
internal fun SavingTransactionsHistoryScreenContent(
    state: SavingTransactionsHistoryState,
    onAction: (SavingTransactionsHistoryAction) -> Unit,
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
                    KptHeaderBackButton(onClick = { onAction(SavingTransactionsHistoryAction.OnBackClick) })
                },
                title = {
                    KptHeaderTitle(
                        title = stringResource(Res.string.feature_saving_transactions_title),
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
                onRetry = { onAction(SavingTransactionsHistoryAction.Retry) },
                loading = {
                    DefaultLoadingContent(
                        config = ScreenStateLoading.Spinner,
                    )
                },
                modifier = Modifier.fillMaxSize(),
            ) { detail, _ ->
                TransactionList(
                    transactions = detail.transactions,
                    onTransactionClick = { transaction ->
                        onAction(SavingTransactionsHistoryAction.OnTransactionClick(transaction.id))
                    },
                    modifier = Modifier.fillMaxSize(),
                )
            }
        }
    }
}

@Composable
fun TransactionList(
    transactions: List<SavingDetailTransaction>,
    onTransactionClick: (SavingDetailTransaction) -> Unit,
    modifier: Modifier = Modifier,
) {
    if (transactions.isEmpty()) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(KptTheme.spacing.lg),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = stringResource(Res.string.feature_saving_empty_transactions),
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
            items(transactions) { transaction ->
                TransactionCard(
                    transaction = transaction,
                    onClick = { onTransactionClick(transaction) },
                )
            }
        }
    }
}

@Composable
fun TransactionCard(
    transaction: SavingDetailTransaction,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val currencySymbol = transaction.currency?.displaySymbol ?: "$"
    val isCredit = transaction.isCredit
    val isDebit = transaction.isDebit

    val icon = if (isCredit) AppIcons.ArrowUpward else AppIcons.ArrowDownward
    val iconColor = if (isCredit) MaterialTheme.finance.moneyPositive else MaterialTheme.finance.moneyNegative
    val iconBgColor = iconColor.copy(alpha = 0.15f)

    val amountSign = if (isCredit) "+" else if (isDebit) "-" else ""
    val amountText = "$amountSign${formatCurrency(transaction.amount, currencySymbol)}"

    val balanceText = stringResource(
        Res.string.feature_saving_transaction_balance_prefix,
    ) + formatCurrency(transaction.runningBalance, currencySymbol)

    val paymentType = transaction.paymentDetailData?.paymentType?.value
    val externalId = transaction.externalId

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
                .padding(MaterialTheme.spacing.md),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(color = iconBgColor, shape = RoundedCornerShape(20.dp)),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconColor,
                    modifier = Modifier.size(20.dp),
                )
            }

            HorizontalSpacer(width = KptTheme.spacing.md)

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(2.dp),
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(KptTheme.spacing.xs),
                ) {
                    Text(
                        text = transaction.transactionType?.value.orEmpty(),
                        style = KptTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = KptTheme.colorScheme.onSurface,
                    )
                    Text(
                        text = stringResource(Res.string.feature_saving_transaction_id_prefix) + transaction.id,
                        style = KptTheme.typography.bodySmall,
                        color = KptTheme.colorScheme.onSurfaceVariant,
                    )
                }

                Text(
                    text = FormatDate.formatLocalDate(transaction.date),
                    style = KptTheme.typography.bodyMedium,
                    color = KptTheme.colorScheme.onSurfaceVariant,
                )

                if (!paymentType.isNullOrBlank() || !externalId.isNullOrBlank()) {
                    val extPrefix = stringResource(Res.string.feature_saving_transaction_ext_prefix)
                    val infoText = buildString {
                        if (!paymentType.isNullOrBlank()) {
                            append(paymentType)
                        }
                        if (!externalId.isNullOrBlank()) {
                            if (isNotEmpty()) append(" • ")
                            append(extPrefix + externalId)
                        }
                    }
                    Text(
                        text = infoText,
                        style = KptTheme.typography.bodySmall,
                        color = KptTheme.colorScheme.primary,
                        fontWeight = FontWeight.Medium,
                    )
                }
            }

            HorizontalSpacer(width = KptTheme.spacing.md)

            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(2.dp),
            ) {
                Text(
                    text = amountText,
                    style = KptTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = iconColor,
                )
                Text(
                    text = balanceText,
                    style = KptTheme.typography.bodySmall,
                    color = KptTheme.colorScheme.onSurfaceVariant,
                )
            }

            HorizontalSpacer(width = KptTheme.spacing.xs)

            Icon(
                imageVector = AppIcons.ChevronRight,
                contentDescription = null,
                tint = KptTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                modifier = Modifier.size(20.dp),
            )
        }
    }
}
