/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See See https://github.com/openMF/kmp-project-template/blob/main/LICENSE
 */
package org.mifos.feature.loan.rejectLoan

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import org.mifos.core.base.designsystem.component.KptDoubleButton
import org.mifos.core.base.designsystem.component.KptSuccessDialog
import org.mifos.core.base.designsystem.theme.KptTheme
import org.mifos.core.base.store.submit.SubmitState
import org.mifos.core.base.ui.effects.EventsEffect
import org.mifos.core.base.ui.submit.MutationScreenContent
import org.mifos.core.designsystem.component.KptDatePickerDialog
import org.mifos.core.designsystem.component.KptHeader
import org.mifos.core.designsystem.component.KptHeaderBackButton
import org.mifos.core.designsystem.component.KptHeaderTitle
import org.mifos.core.ui.input.KptTextField
import org.mifos.core.ui.scaffold.KptScaffold
import org.mifos.feature.loan.generated.resources.Res
import org.mifos.feature.loan.generated.resources.feature_loan_cancel
import org.mifos.feature.loan.generated.resources.feature_loan_mifos_save
import org.mifos.feature.loan.generated.resources.feature_loan_note
import org.mifos.feature.loan.generated.resources.feature_loan_note_hint
import org.mifos.feature.loan.generated.resources.feature_loan_ok
import org.mifos.feature.loan.generated.resources.feature_loan_reject
import org.mifos.feature.loan.generated.resources.feature_loan_reject_loan_title
import org.mifos.feature.loan.generated.resources.feature_loan_reject_success_message
import org.mifos.feature.loan.generated.resources.feature_loan_reject_success_title
import org.mifos.feature.loan.generated.resources.feature_loan_rejection_date_hint
import org.mifos.feature.loan.generated.resources.feature_loan_select_rejection_date

@Composable
fun RejectLoanScreen(
    onBackClick: () -> Unit,
    onBackWithUpdateData: (Long) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: RejectLoanViewModel = koinViewModel(),
) {
    val state by viewModel.stateFlow.collectAsStateWithLifecycle()

    EventsEffect(viewModel.eventFlow) { event ->
        when (event) {
            RejectLoanEvent.NavigateBack -> onBackClick()
            is RejectLoanEvent.NavigateBackWithUpdateData -> onBackWithUpdateData(event.loanId)
        }
    }

    if (state.showSuccessDialog) {
        KptSuccessDialog(
            title = stringResource(Res.string.feature_loan_reject_success_title),
            message = stringResource(Res.string.feature_loan_reject_success_message),
            buttonText = stringResource(Res.string.feature_loan_ok),
            onConfirm = { viewModel.trySendAction(RejectLoanAction.DismissSuccessDialog) },
        )
    }

    if (state.isRejectedDatePickerVisible) {
        KptDatePickerDialog(
            onDateSelected = { millis ->
                viewModel.trySendAction(RejectLoanAction.OnRejectedDateSelected(millis))
            },
            onDismiss = {
                viewModel.trySendAction(RejectLoanAction.OnRejectedDatePickerToggle(false))
            },
            initialSelectedDateMillis = state.selectedRejectedDateMillis,
        )
    }

    RejectLoanScreenContent(
        state = state,
        onAction = viewModel::trySendAction,
        modifier = modifier,
    )
}

@Composable
internal fun RejectLoanScreenContent(
    state: RejectLoanState,
    onAction: (RejectLoanAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    val scrollState = rememberScrollState()

    KptScaffold(
        modifier = modifier,
        containerColor = KptTheme.colorScheme.primary,
        topBar = {
            KptHeader(
                navigationIcon = {
                    KptHeaderBackButton(
                        onClick = {
                            onAction(RejectLoanAction.OnBackClick)
                        },
                    )
                },
                title = {
                    KptHeaderTitle(
                        title = stringResource(Res.string.feature_loan_reject_loan_title),
                        subtitle = stringResource(Res.string.feature_loan_mifos_save),
                    )
                },
            )
        },
        bottomBar = {
            if (state.submitState is SubmitState.Idle || state.submitState is SubmitState.Submitting) {
                KptDoubleButton(
                    onLeftButtonClick = { onAction(RejectLoanAction.OnBackClick) },
                    onRightButtonClick = { onAction(RejectLoanAction.RejectLoan) },
                    leftButtonText = stringResource(Res.string.feature_loan_cancel),
                    rightButtonText = stringResource(Res.string.feature_loan_reject),
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
                onRetry = { onAction(RejectLoanAction.Retry) },
                onSubmitted = {},
                modifier = Modifier.fillMaxSize(),
            ) { _, _ ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(scrollState)
                        .padding(horizontal = KptTheme.spacing.md, vertical = KptTheme.spacing.lg),
                ) {
                    KptTextField(
                        value = state.rejectedDateText,
                        onValueChange = {},
                        readOnly = true,
                        enabled = true,
                        label = stringResource(Res.string.feature_loan_select_rejection_date),
                        placeholder = stringResource(Res.string.feature_loan_rejection_date_hint),
                        onCalenderClick = { onAction(RejectLoanAction.OnRejectedDatePickerToggle(true)) },
                        modifier = Modifier.fillMaxWidth(),
                    )

                    Spacer(modifier = Modifier.height(KptTheme.spacing.md))

                    KptTextField(
                        value = state.noteText,
                        onValueChange = { onAction(RejectLoanAction.OnNoteChange(it)) },
                        readOnly = false,
                        enabled = true,
                        label = stringResource(Res.string.feature_loan_note),
                        placeholder = stringResource(Res.string.feature_loan_note_hint),
                        singleLine = false,
                        maxLines = 5,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
            }
        }
    }
}
