/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See See https://github.com/openMF/kmp-project-template/blob/main/LICENSE
 */
package org.mifos.feature.groups.activateGroup

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
import org.mifos.feature.groups.generated.resources.Res
import org.mifos.feature.groups.generated.resources.feature_groups_activate_group
import org.mifos.feature.groups.generated.resources.feature_groups_activate_group_success_message
import org.mifos.feature.groups.generated.resources.feature_groups_activate_group_success_title
import org.mifos.feature.groups.generated.resources.feature_groups_activate_group_title
import org.mifos.feature.groups.generated.resources.feature_groups_activation_date_hint
import org.mifos.feature.groups.generated.resources.feature_groups_cancel
import org.mifos.feature.groups.generated.resources.feature_groups_mifos_save
import org.mifos.feature.groups.generated.resources.feature_groups_ok
import org.mifos.feature.groups.generated.resources.feature_groups_select_activation_date

@Composable
fun ActivateGroupScreen(
    onBackClick: () -> Unit,
    onBackWithUpdateData: (Long) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ActivateGroupViewModel = koinViewModel(),
) {
    val state by viewModel.stateFlow.collectAsStateWithLifecycle()

    EventsEffect(viewModel.eventFlow) { event ->
        when (event) {
            ActivateGroupEvent.NavigateBack -> onBackClick()
            is ActivateGroupEvent.NavigateBackWithUpdateData -> onBackWithUpdateData(event.groupId)
        }
    }

    if (state.showSuccessDialog) {
        KptSuccessDialog(
            title = stringResource(Res.string.feature_groups_activate_group_success_title),
            message = stringResource(Res.string.feature_groups_activate_group_success_message),
            buttonText = stringResource(Res.string.feature_groups_ok),
            onConfirm = { viewModel.trySendAction(ActivateGroupAction.DismissSuccessDialog) },
        )
    }

    if (state.isDatePickerVisible) {
        KptDatePickerDialog(
            onDateSelected = { millis ->
                viewModel.trySendAction(ActivateGroupAction.OnDateSelected(millis))
            },
            onDismiss = {
                viewModel.trySendAction(ActivateGroupAction.OnDatePickerToggle(false))
            },
            initialSelectedDateMillis = state.selectedDateMillis,
        )
    }

    ActivateGroupScreenContent(
        state = state,
        onAction = viewModel::trySendAction,
        modifier = modifier,
    )
}

@Composable
internal fun ActivateGroupScreenContent(
    state: ActivateGroupState,
    onAction: (ActivateGroupAction) -> Unit,
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
                            onAction(ActivateGroupAction.OnBackClick)
                        },
                    )
                },
                title = {
                    KptHeaderTitle(
                        title = stringResource(Res.string.feature_groups_activate_group_title),
                        subtitle = stringResource(Res.string.feature_groups_mifos_save),
                    )
                },
            )
        },
        bottomBar = {
            if (state.submitState is SubmitState.Idle || state.submitState is SubmitState.Submitting) {
                KptDoubleButton(
                    onLeftButtonClick = { onAction(ActivateGroupAction.OnBackClick) },
                    onRightButtonClick = { onAction(ActivateGroupAction.ActivateGroup) },
                    leftButtonText = stringResource(Res.string.feature_groups_cancel),
                    rightButtonText = stringResource(Res.string.feature_groups_activate_group),
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
                onRetry = { onAction(ActivateGroupAction.Retry) },
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
                        label = stringResource(Res.string.feature_groups_select_activation_date),
                        placeholder = stringResource(Res.string.feature_groups_activation_date_hint),
                        onCalenderClick = { onAction(ActivateGroupAction.OnDatePickerToggle(true)) },
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
            }
        }
    }
}
