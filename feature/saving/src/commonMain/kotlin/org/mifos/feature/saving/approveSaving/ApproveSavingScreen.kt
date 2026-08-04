/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See See https://github.com/openMF/kmp-project-template/blob/main/LICENSE
 */
package org.mifos.feature.saving.approveSaving

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
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
import org.mifos.feature.saving.generated.resources.Res
import org.mifos.feature.saving.generated.resources.feature_saving_approval_date_hint
import org.mifos.feature.saving.generated.resources.feature_saving_approve
import org.mifos.feature.saving.generated.resources.feature_saving_approve_savings_title
import org.mifos.feature.saving.generated.resources.feature_saving_approve_success_message
import org.mifos.feature.saving.generated.resources.feature_saving_approve_success_title
import org.mifos.feature.saving.generated.resources.feature_saving_cancel
import org.mifos.feature.saving.generated.resources.feature_saving_mifos_save
import org.mifos.feature.saving.generated.resources.feature_saving_ok
import org.mifos.feature.saving.generated.resources.feature_saving_select_approval_date

@Composable
fun ApproveSavingScreen(
    onBackClick: () -> Unit,
    onBackWithUpdateData: (Long) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ApproveSavingViewModel = koinViewModel(),
) {
    val state by viewModel.stateFlow.collectAsStateWithLifecycle()

    EventsEffect(viewModel.eventFlow) { event ->
        when (event) {
            ApproveSavingEvent.NavigateBack -> onBackClick()
            is ApproveSavingEvent.NavigateBackWithUpdateData -> onBackWithUpdateData(event.savingsId)
        }
    }

    if (state.showSuccessDialog) {
        KptSuccessDialog(
            title = stringResource(Res.string.feature_saving_approve_success_title),
            message = stringResource(Res.string.feature_saving_approve_success_message),
            buttonText = stringResource(Res.string.feature_saving_ok),
            onConfirm = { viewModel.trySendAction(ApproveSavingAction.DismissSuccessDialog) },
        )
    }

    if (state.isDatePickerVisible) {
        KptDatePickerDialog(
            onDateSelected = { millis ->
                viewModel.trySendAction(ApproveSavingAction.OnDateSelected(millis))
            },
            onDismiss = {
                viewModel.trySendAction(ApproveSavingAction.OnDatePickerToggle(false))
            },
            initialSelectedDateMillis = state.selectedDateMillis,
        )
    }

    ApproveSavingScreenContent(
        state = state,
        onAction = viewModel::trySendAction,
        modifier = modifier,
    )
}

@Composable
internal fun ApproveSavingScreenContent(
    state: ApproveSavingState,
    onAction: (ApproveSavingAction) -> Unit,
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
                            onAction(ApproveSavingAction.OnBackClick)
                        },
                    )
                },
                title = {
                    KptHeaderTitle(
                        title = stringResource(Res.string.feature_saving_approve_savings_title),
                        subtitle = stringResource(Res.string.feature_saving_mifos_save),
                    )
                },
            )
        },
        bottomBar = {
            if (state.submitState is SubmitState.Idle || state.submitState is SubmitState.Submitting) {
                KptDoubleButton(
                    onLeftButtonClick = { onAction(ApproveSavingAction.OnBackClick) },
                    onRightButtonClick = { onAction(ApproveSavingAction.ApproveSaving) },
                    leftButtonText = stringResource(Res.string.feature_saving_cancel),
                    rightButtonText = stringResource(Res.string.feature_saving_approve),
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
                onRetry = { onAction(ApproveSavingAction.Retry) },
                onSubmitted = {},
                modifier = Modifier
                    .fillMaxSize(),
            ) { _, _ ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = KptTheme.spacing.md, vertical = KptTheme.spacing.lg),
                ) {
                    KptTextField(
                        value = state.dateText,
                        onValueChange = {},
                        readOnly = true,
                        enabled = true,
                        label = stringResource(Res.string.feature_saving_select_approval_date),
                        placeholder = stringResource(Res.string.feature_saving_approval_date_hint),
                        onCalenderClick = { onAction(ApproveSavingAction.OnDatePickerToggle(true)) },
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
            }
        }
    }
}
