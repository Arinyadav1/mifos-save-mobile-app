/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See See https://github.com/openMF/kmp-project-template/blob/main/LICENSE
 */
package org.mifos.feature.saving.savingTransaction

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import org.mifos.core.base.designsystem.component.KptDoubleButton
import org.mifos.core.base.designsystem.component.KptSuccessDialog
import org.mifos.core.base.designsystem.theme.KptTheme
import org.mifos.core.base.store.submit.SubmitState
import org.mifos.core.base.ui.effects.EventsEffect
import org.mifos.core.base.ui.screen.DefaultLoadingContent
import org.mifos.core.base.ui.screen.ScreenStateLoading
import org.mifos.core.base.ui.submit.MutationScreenContent
import org.mifos.core.designsystem.component.KptDatePickerDialog
import org.mifos.core.designsystem.component.KptHeader
import org.mifos.core.designsystem.component.KptHeaderBackButton
import org.mifos.core.designsystem.component.KptHeaderTitle
import org.mifos.core.ui.input.KptDropdownTextField
import org.mifos.core.ui.input.KptTextField
import org.mifos.core.ui.scaffold.KptScaffold
import org.mifos.feature.saving.generated.resources.Res
import org.mifos.feature.saving.generated.resources.feature_saving_approval_date_hint
import org.mifos.feature.saving.generated.resources.feature_saving_cancel
import org.mifos.feature.saving.generated.resources.feature_saving_deposit_account_number
import org.mifos.feature.saving.generated.resources.feature_saving_deposit_account_number_hint
import org.mifos.feature.saving.generated.resources.feature_saving_deposit_amount
import org.mifos.feature.saving.generated.resources.feature_saving_deposit_amount_hint
import org.mifos.feature.saving.generated.resources.feature_saving_deposit_bank_number
import org.mifos.feature.saving.generated.resources.feature_saving_deposit_bank_number_hint
import org.mifos.feature.saving.generated.resources.feature_saving_deposit_check_number
import org.mifos.feature.saving.generated.resources.feature_saving_deposit_check_number_hint
import org.mifos.feature.saving.generated.resources.feature_saving_deposit_date
import org.mifos.feature.saving.generated.resources.feature_saving_deposit_payment_type
import org.mifos.feature.saving.generated.resources.feature_saving_deposit_receipt_number
import org.mifos.feature.saving.generated.resources.feature_saving_deposit_receipt_number_hint
import org.mifos.feature.saving.generated.resources.feature_saving_deposit_routing_code
import org.mifos.feature.saving.generated.resources.feature_saving_deposit_routing_code_hint
import org.mifos.feature.saving.generated.resources.feature_saving_deposit_select_payment_type
import org.mifos.feature.saving.generated.resources.feature_saving_deposit_submit
import org.mifos.feature.saving.generated.resources.feature_saving_deposit_success_message
import org.mifos.feature.saving.generated.resources.feature_saving_deposit_success_title
import org.mifos.feature.saving.generated.resources.feature_saving_deposit_title
import org.mifos.feature.saving.generated.resources.feature_saving_mifos_save
import org.mifos.feature.saving.generated.resources.feature_saving_ok
import org.mifos.feature.saving.generated.resources.feature_saving_withdraw_submit
import org.mifos.feature.saving.generated.resources.feature_saving_withdraw_success_message
import org.mifos.feature.saving.generated.resources.feature_saving_withdraw_success_title
import org.mifos.feature.saving.generated.resources.feature_saving_withdraw_title

@Composable
fun SavingTransactionScreen(
    onBackClick: () -> Unit,
    onBackWithUpdateData: (Long) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SavingTransactionViewModel = koinViewModel(),
) {
    val state by viewModel.stateFlow.collectAsStateWithLifecycle()

    EventsEffect(viewModel.eventFlow) { event ->
        when (event) {
            SavingTransactionEvent.NavigateBack -> onBackClick()
            is SavingTransactionEvent.NavigateBackWithUpdateData -> onBackWithUpdateData(event.accountId)
        }
    }

    if (state.showSuccessDialog) {
        val successTitle = if (state.isWithdrawal) {
            stringResource(Res.string.feature_saving_withdraw_success_title)
        } else {
            stringResource(Res.string.feature_saving_deposit_success_title)
        }
        val successMessage = if (state.isWithdrawal) {
            stringResource(Res.string.feature_saving_withdraw_success_message)
        } else {
            stringResource(Res.string.feature_saving_deposit_success_message)
        }
        KptSuccessDialog(
            title = successTitle,
            message = successMessage,
            buttonText = stringResource(Res.string.feature_saving_ok),
            onConfirm = { viewModel.trySendAction(SavingTransactionAction.DismissSuccessDialog) },
        )
    }

    if (state.isDatePickerVisible) {
        KptDatePickerDialog(
            onDateSelected = { millis ->
                viewModel.trySendAction(SavingTransactionAction.OnDateSelected(millis))
            },
            onDismiss = {
                viewModel.trySendAction(SavingTransactionAction.OnDatePickerToggle(false))
            },
            initialSelectedDateMillis = state.selectedDateMillis,
        )
    }

    SavingTransactionScreenContent(
        state = state,
        onAction = viewModel::trySendAction,
        modifier = modifier,
    )
}

@Composable
internal fun SavingTransactionScreenContent(
    state: SavingTransactionState,
    onAction: (SavingTransactionAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    KptScaffold(
        modifier = modifier,
        containerColor = KptTheme.colorScheme.primary,
        topBar = {
            KptHeader(
                navigationIcon = {
                    KptHeaderBackButton(onClick = { onAction(SavingTransactionAction.OnBackClick) })
                },
                title = {
                    val headerTitle = if (state.isWithdrawal) {
                        stringResource(Res.string.feature_saving_withdraw_title)
                    } else {
                        stringResource(Res.string.feature_saving_deposit_title)
                    }
                    KptHeaderTitle(
                        title = headerTitle,
                        subtitle = stringResource(Res.string.feature_saving_mifos_save),
                    )
                },
            )
        },
        bottomBar = {
            if (state.submitState is SubmitState.Idle || state.submitState is SubmitState.Submitting) {
                val submitText = if (state.isWithdrawal) {
                    stringResource(Res.string.feature_saving_withdraw_submit)
                } else {
                    stringResource(Res.string.feature_saving_deposit_submit)
                }
                KptDoubleButton(
                    onLeftButtonClick = { onAction(SavingTransactionAction.OnBackClick) },
                    onRightButtonClick = { onAction(SavingTransactionAction.SubmitTransaction) },
                    leftButtonText = stringResource(Res.string.feature_saving_cancel),
                    rightButtonText = submitText,
                )
            }
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
            MutationScreenContent(
                screenState = state.screenState,
                submitState = state.submitState,
                onRetry = { onAction(SavingTransactionAction.Retry) },
                onSubmitted = {},
                loading = {
                    DefaultLoadingContent(
                        config = ScreenStateLoading.Spinner,
                    )
                },
                modifier = Modifier.fillMaxSize(),
            ) { _, _ ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = KptTheme.spacing.md, vertical = KptTheme.spacing.lg),
                    verticalArrangement = Arrangement.spacedBy(KptTheme.spacing.md),
                ) {
                    KptTextField(
                        value = state.dateText,
                        onValueChange = {},
                        readOnly = true,
                        enabled = true,
                        label = stringResource(Res.string.feature_saving_deposit_date),
                        placeholder = stringResource(Res.string.feature_saving_approval_date_hint),
                        onCalenderClick = { onAction(SavingTransactionAction.OnDatePickerToggle(true)) },
                        modifier = Modifier.fillMaxWidth(),
                    )

                    KptTextField(
                        value = state.amount,
                        onValueChange = { onAction(SavingTransactionAction.OnAmountChanged(it)) },
                        label = stringResource(Res.string.feature_saving_deposit_amount),
                        placeholder = stringResource(Res.string.feature_saving_deposit_amount_hint),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        errorText = state.amountErrorRes?.let { stringResource(it) },
                        modifier = Modifier.fillMaxWidth(),
                    )

                    val paymentTypeNames = state.paymentTypeOptions.map { it.name.orEmpty() }
                    KptDropdownTextField(
                        value = state.selectedPaymentType?.name.orEmpty(),
                        onOptionSelected = { index, _ ->
                            val option = state.paymentTypeOptions[index]
                            onAction(SavingTransactionAction.OnPaymentTypeSelected(option))
                        },
                        options = paymentTypeNames,
                        label = stringResource(Res.string.feature_saving_deposit_payment_type),
                        placeholder = stringResource(Res.string.feature_saving_deposit_select_payment_type),
                        errorText = state.paymentTypeErrorRes?.let { stringResource(it) },
                        modifier = Modifier.fillMaxWidth(),
                    )

                    KptTextField(
                        value = state.accountNumber,
                        onValueChange = { onAction(SavingTransactionAction.OnAccountNumberChanged(it)) },
                        label = stringResource(Res.string.feature_saving_deposit_account_number),
                        placeholder = stringResource(Res.string.feature_saving_deposit_account_number_hint),
                        modifier = Modifier.fillMaxWidth(),
                    )

                    KptTextField(
                        value = state.checkNumber,
                        onValueChange = { onAction(SavingTransactionAction.OnCheckNumberChanged(it)) },
                        label = stringResource(Res.string.feature_saving_deposit_check_number),
                        placeholder = stringResource(Res.string.feature_saving_deposit_check_number_hint),
                        modifier = Modifier.fillMaxWidth(),
                    )

                    KptTextField(
                        value = state.routingCode,
                        onValueChange = { onAction(SavingTransactionAction.OnRoutingCodeChanged(it)) },
                        label = stringResource(Res.string.feature_saving_deposit_routing_code),
                        placeholder = stringResource(Res.string.feature_saving_deposit_routing_code_hint),
                        modifier = Modifier.fillMaxWidth(),
                    )

                    KptTextField(
                        value = state.receiptNumber,
                        onValueChange = { onAction(SavingTransactionAction.OnReceiptNumberChanged(it)) },
                        label = stringResource(Res.string.feature_saving_deposit_receipt_number),
                        placeholder = stringResource(Res.string.feature_saving_deposit_receipt_number_hint),
                        modifier = Modifier.fillMaxWidth(),
                    )

                    KptTextField(
                        value = state.bankNumber,
                        onValueChange = { onAction(SavingTransactionAction.OnBankNumberChanged(it)) },
                        label = stringResource(Res.string.feature_saving_deposit_bank_number),
                        placeholder = stringResource(Res.string.feature_saving_deposit_bank_number_hint),
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
            }
        }
    }
}
