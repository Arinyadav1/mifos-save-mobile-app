/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See See https://github.com/openMF/kmp-project-template/blob/main/LICENSE
 */
package org.mifos.feature.saving.transactionDetails

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
import org.mifos.core.model.savings.SavingDetailTransaction
import org.mifos.core.model.savings.isCredit
import org.mifos.core.model.savings.isDebit
import org.mifos.core.ui.scaffold.KptScaffold
import org.mifos.feature.saving.generated.resources.Res
import org.mifos.feature.saving.generated.resources.feature_saving_deposit_account_number
import org.mifos.feature.saving.generated.resources.feature_saving_deposit_bank_number
import org.mifos.feature.saving.generated.resources.feature_saving_deposit_check_number
import org.mifos.feature.saving.generated.resources.feature_saving_deposit_receipt_number
import org.mifos.feature.saving.generated.resources.feature_saving_deposit_routing_code
import org.mifos.feature.saving.generated.resources.feature_saving_details_title
import org.mifos.feature.saving.generated.resources.feature_saving_overview_title
import org.mifos.feature.saving.generated.resources.feature_saving_transaction_amount
import org.mifos.feature.saving.generated.resources.feature_saving_transaction_date
import org.mifos.feature.saving.generated.resources.feature_saving_transaction_details
import org.mifos.feature.saving.generated.resources.feature_saving_transaction_external_id
import org.mifos.feature.saving.generated.resources.feature_saving_transaction_id
import org.mifos.feature.saving.generated.resources.feature_saving_transaction_payment_type
import org.mifos.feature.saving.generated.resources.feature_saving_transaction_reversed
import org.mifos.feature.saving.generated.resources.feature_saving_transaction_running_balance
import org.mifos.feature.saving.generated.resources.feature_saving_transaction_submitted_by
import org.mifos.feature.saving.generated.resources.feature_saving_transaction_submitted_on
import org.mifos.feature.saving.generated.resources.feature_saving_transaction_type

@Composable
fun TransactionDetailsScreen(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: TransactionDetailsViewModel = koinViewModel(),
) {
    val state by viewModel.stateFlow.collectAsStateWithLifecycle()

    EventsEffect(viewModel.eventFlow) { event ->
        when (event) {
            TransactionDetailsEvent.NavigateBack -> onBackClick()
        }
    }

    TransactionDetailsScreenContent(
        state = state,
        onAction = viewModel::trySendAction,
        modifier = modifier,
    )
}

@Composable
internal fun TransactionDetailsScreenContent(
    state: TransactionDetailsState,
    onAction: (TransactionDetailsAction) -> Unit,
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
                    KptHeaderBackButton(onClick = { onAction(TransactionDetailsAction.OnBackClick) })
                },
                title = {
                    KptHeaderTitle(
                        title = stringResource(Res.string.feature_saving_transaction_details),
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
                onRetry = { onAction(TransactionDetailsAction.Retry) },
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
                        title = stringResource(Res.string.feature_saving_overview_title),
                    )

                    KptHorizontalStatsCard(
                        value = amountText,
                        label = transaction.transactionType?.value.orEmpty(),
                        icon = icon,
                        iconContainerColor = iconBgColor,
                        iconColor = iconColor,
                        modifier = Modifier.fillMaxWidth(),
                    )

                    SectionHeader(
                        title = stringResource(Res.string.feature_saving_details_title),
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
    transaction: SavingDetailTransaction,
    currencySymbol: String,
): Map<String, String> {
    return buildMap {
        put(stringResource(Res.string.feature_saving_transaction_id), transaction.id.toString())
        put(
            stringResource(Res.string.feature_saving_transaction_date),
            FormatDate.formatLocalDate(transaction.date),
        )
        put(
            stringResource(Res.string.feature_saving_transaction_type),
            transaction.transactionType?.value.orEmpty(),
        )
        put(
            stringResource(Res.string.feature_saving_transaction_amount),
            formatCurrency(transaction.amount, currencySymbol),
        )
        put(
            stringResource(Res.string.feature_saving_transaction_running_balance),
            formatCurrency(transaction.runningBalance, currencySymbol),
        )

        val externalId = transaction.externalId
        if (!externalId.isNullOrBlank()) {
            put(stringResource(Res.string.feature_saving_transaction_external_id), externalId)
        }

        put(stringResource(Res.string.feature_saving_transaction_reversed), transaction.reversed.toString())

        val payData = transaction.paymentDetailData
        if (payData != null) {
            val paymentType = payData.paymentType
            if (paymentType != null) {
                put(
                    stringResource(Res.string.feature_saving_transaction_payment_type),
                    paymentType.value.orEmpty(),
                )
            }
            val accountNumber = payData.accountNumber
            if (!accountNumber.isNullOrBlank()) {
                put(stringResource(Res.string.feature_saving_deposit_account_number), accountNumber)
            }
            val checkNumber = payData.checkNumber
            if (!checkNumber.isNullOrBlank()) {
                put(stringResource(Res.string.feature_saving_deposit_check_number), checkNumber)
            }
            val routingCode = payData.routingCode
            if (!routingCode.isNullOrBlank()) {
                put(stringResource(Res.string.feature_saving_deposit_routing_code), routingCode)
            }
            val receiptNumber = payData.receiptNumber
            if (!receiptNumber.isNullOrBlank()) {
                put(stringResource(Res.string.feature_saving_deposit_receipt_number), receiptNumber)
            }
            val bankNumber = payData.bankNumber
            if (!bankNumber.isNullOrBlank()) {
                put(stringResource(Res.string.feature_saving_deposit_bank_number), bankNumber)
            }
        }

        if (transaction.submittedOnDate != null) {
            put(
                stringResource(Res.string.feature_saving_transaction_submitted_on),
                FormatDate.formatLocalDate(transaction.submittedOnDate),
            )
        }

        val submittedByUsername = transaction.submittedByUsername
        if (!submittedByUsername.isNullOrBlank()) {
            put(stringResource(Res.string.feature_saving_transaction_submitted_by), submittedByUsername)
        }
    }
}
