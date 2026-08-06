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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
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
import org.mifos.core.ui.scaffold.KptScaffold
import org.mifos.feature.saving.generated.resources.Res
import org.mifos.feature.saving.generated.resources.feature_saving_deposit_account_number
import org.mifos.feature.saving.generated.resources.feature_saving_deposit_bank_number
import org.mifos.feature.saving.generated.resources.feature_saving_deposit_check_number
import org.mifos.feature.saving.generated.resources.feature_saving_deposit_receipt_number
import org.mifos.feature.saving.generated.resources.feature_saving_deposit_routing_code
import org.mifos.feature.saving.generated.resources.feature_saving_empty_transactions
import org.mifos.feature.saving.generated.resources.feature_saving_ok
import org.mifos.feature.saving.generated.resources.feature_saving_transaction_amount
import org.mifos.feature.saving.generated.resources.feature_saving_transaction_balance_prefix
import org.mifos.feature.saving.generated.resources.feature_saving_transaction_date
import org.mifos.feature.saving.generated.resources.feature_saving_transaction_details
import org.mifos.feature.saving.generated.resources.feature_saving_transaction_ext_prefix
import org.mifos.feature.saving.generated.resources.feature_saving_transaction_external_id
import org.mifos.feature.saving.generated.resources.feature_saving_transaction_id
import org.mifos.feature.saving.generated.resources.feature_saving_transaction_id_prefix
import org.mifos.feature.saving.generated.resources.feature_saving_transaction_payment_type
import org.mifos.feature.saving.generated.resources.feature_saving_transaction_reversed
import org.mifos.feature.saving.generated.resources.feature_saving_transaction_running_balance
import org.mifos.feature.saving.generated.resources.feature_saving_transaction_submitted_by
import org.mifos.feature.saving.generated.resources.feature_saving_transaction_submitted_on
import org.mifos.feature.saving.generated.resources.feature_saving_transaction_type
import org.mifos.feature.saving.generated.resources.feature_saving_transactions_title

@Composable
fun SavingTransactionsHistoryScreen(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SavingTransactionsHistoryViewModel = koinViewModel(),
) {
    val state by viewModel.stateFlow.collectAsStateWithLifecycle()

    EventsEffect(viewModel.eventFlow) { event ->
        when (event) {
            SavingTransactionsHistoryEvent.NavigateBack -> onBackClick()
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
                        onAction(SavingTransactionsHistoryAction.OnTransactionClick(transaction))
                    },
                    modifier = Modifier.fillMaxSize(),
                )
            }
        }
    }

    state.selectedTransaction?.let { transaction ->
        TransactionDetailsDialog(
            transaction = transaction,
            onDismiss = { onAction(SavingTransactionsHistoryAction.DismissDetailsDialog) },
        )
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
    val isCredit = transaction.credit || transaction.transactionType?.credit == true
    val isDebit = transaction.debit || transaction.transactionType?.debit == true

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

@Composable
fun TransactionDetailsRow(
    key: String,
    value: String,
    modifier: Modifier = Modifier,
    showDivider: Boolean = true,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = KptTheme.spacing.sm),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                text = key,
                style = KptTheme.typography.bodyMedium,
                color = KptTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.weight(1.2f),
            )
            Text(
                text = value,
                style = KptTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                color = KptTheme.colorScheme.onSurface,
                textAlign = TextAlign.End,
                modifier = Modifier.weight(1.8f),
            )
        }
        if (showDivider) {
            HorizontalDivider(
                color = KptTheme.colorScheme.outlineVariant,
                thickness = 1.dp,
            )
        }
    }
}

@Composable
fun TransactionDetailsDialog(
    transaction: SavingDetailTransaction,
    onDismiss: () -> Unit,
) {
    val currencySymbol = transaction.currency?.displaySymbol ?: "$"
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(
                    text = stringResource(Res.string.feature_saving_ok),
                    color = KptTheme.colorScheme.primary,
                    fontWeight = FontWeight.SemiBold,
                )
            }
        },
        title = {
            Text(
                text = stringResource(Res.string.feature_saving_transaction_details),
                style = KptTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = KptTheme.colorScheme.onSurface,
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(KptTheme.spacing.xs),
            ) {
                TransactionDetailsRow(
                    key = stringResource(Res.string.feature_saving_transaction_id),
                    value = "${transaction.id}",
                )
                TransactionDetailsRow(
                    key = stringResource(Res.string.feature_saving_transaction_date),
                    value = FormatDate.formatLocalDate(transaction.date),
                )
                TransactionDetailsRow(
                    key = stringResource(Res.string.feature_saving_transaction_type),
                    value = transaction.transactionType?.value.orEmpty(),
                )
                TransactionDetailsRow(
                    key = stringResource(Res.string.feature_saving_transaction_amount),
                    value = formatCurrency(transaction.amount, currencySymbol),
                )
                TransactionDetailsRow(
                    key = stringResource(Res.string.feature_saving_transaction_running_balance),
                    value = formatCurrency(transaction.runningBalance, currencySymbol),
                )
                val externalId = transaction.externalId
                if (!externalId.isNullOrBlank()) {
                    TransactionDetailsRow(
                        key = stringResource(Res.string.feature_saving_transaction_external_id),
                        value = externalId,
                    )
                }
                TransactionDetailsRow(
                    key = stringResource(Res.string.feature_saving_transaction_reversed),
                    value = transaction.reversed.toString(),
                )
                val payData = transaction.paymentDetailData
                if (payData != null) {
                    val paymentType = payData.paymentType
                    if (paymentType != null) {
                        TransactionDetailsRow(
                            key = stringResource(Res.string.feature_saving_transaction_payment_type),
                            value = paymentType.value.orEmpty(),
                        )
                    }
                    val accountNumber = payData.accountNumber
                    if (!accountNumber.isNullOrBlank()) {
                        TransactionDetailsRow(
                            key = stringResource(Res.string.feature_saving_deposit_account_number),
                            value = accountNumber,
                        )
                    }
                    val checkNumber = payData.checkNumber
                    if (!checkNumber.isNullOrBlank()) {
                        TransactionDetailsRow(
                            key = stringResource(Res.string.feature_saving_deposit_check_number),
                            value = checkNumber,
                        )
                    }
                    val routingCode = payData.routingCode
                    if (!routingCode.isNullOrBlank()) {
                        TransactionDetailsRow(
                            key = stringResource(Res.string.feature_saving_deposit_routing_code),
                            value = routingCode,
                        )
                    }
                    val receiptNumber = payData.receiptNumber
                    if (!receiptNumber.isNullOrBlank()) {
                        TransactionDetailsRow(
                            key = stringResource(Res.string.feature_saving_deposit_receipt_number),
                            value = receiptNumber,
                        )
                    }
                    val bankNumber = payData.bankNumber
                    if (!bankNumber.isNullOrBlank()) {
                        TransactionDetailsRow(
                            key = stringResource(Res.string.feature_saving_deposit_bank_number),
                            value = bankNumber,
                        )
                    }
                }
                if (transaction.submittedOnDate != null) {
                    TransactionDetailsRow(
                        key = stringResource(Res.string.feature_saving_transaction_submitted_on),
                        value = FormatDate.formatLocalDate(transaction.submittedOnDate),
                    )
                }
                val submittedByUsername = transaction.submittedByUsername
                if (!submittedByUsername.isNullOrBlank()) {
                    TransactionDetailsRow(
                        key = stringResource(Res.string.feature_saving_transaction_submitted_by),
                        value = submittedByUsername,
                        showDivider = false,
                    )
                }
            }
        },
        containerColor = KptTheme.colorScheme.surface,
        shape = KptTheme.shapes.medium,
    )
}
