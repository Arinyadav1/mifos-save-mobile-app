/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See See https://github.com/openMF/kmp-project-template/blob/main/LICENSE
 */
package org.mifos.feature.groups.createGroup

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import org.mifos.core.base.designsystem.component.KptDoubleButton
import org.mifos.core.base.designsystem.component.KptSuccessDialog
import org.mifos.core.base.designsystem.component.KptTopAppBar
import org.mifos.core.base.designsystem.component.VerticalSpacer
import org.mifos.core.base.designsystem.theme.KptTheme
import org.mifos.core.base.store.submit.SubmitState
import org.mifos.core.base.ui.effects.EventsEffect
import org.mifos.core.base.ui.screen.DefaultLoadingContent
import org.mifos.core.base.ui.screen.ScreenStateLoading
import org.mifos.core.base.ui.submit.MutationScreenContent
import org.mifos.core.designsystem.component.KptDatePickerDialog
import org.mifos.core.designsystem.component.KptRadioToggle
import org.mifos.core.designsystem.component.KptSelectableItemCard
import org.mifos.core.designsystem.icon.AppIcons
import org.mifos.core.ui.input.KptDropdownSearchBar
import org.mifos.core.ui.input.KptDropdownSearchBarItem
import org.mifos.core.ui.input.KptDropdownTextField
import org.mifos.core.ui.input.KptTextField
import org.mifos.core.ui.scaffold.KptScaffold
import org.mifos.feature.groups.generated.resources.Res
import org.mifos.feature.groups.generated.resources.feature_groups_activate_add_members
import org.mifos.feature.groups.generated.resources.feature_groups_activate_group_toggle
import org.mifos.feature.groups.generated.resources.feature_groups_activation_date_hint
import org.mifos.feature.groups.generated.resources.feature_groups_cancel
import org.mifos.feature.groups.generated.resources.feature_groups_create_group_button
import org.mifos.feature.groups.generated.resources.feature_groups_create_group_subtitle
import org.mifos.feature.groups.generated.resources.feature_groups_create_group_success_message
import org.mifos.feature.groups.generated.resources.feature_groups_create_group_success_title
import org.mifos.feature.groups.generated.resources.feature_groups_create_group_title
import org.mifos.feature.groups.generated.resources.feature_groups_external_id_label
import org.mifos.feature.groups.generated.resources.feature_groups_external_id_placeholder
import org.mifos.feature.groups.generated.resources.feature_groups_group_name_label
import org.mifos.feature.groups.generated.resources.feature_groups_group_name_placeholder
import org.mifos.feature.groups.generated.resources.feature_groups_mifos_save_mobile
import org.mifos.feature.groups.generated.resources.feature_groups_office_label
import org.mifos.feature.groups.generated.resources.feature_groups_ok
import org.mifos.feature.groups.generated.resources.feature_groups_save
import org.mifos.feature.groups.generated.resources.feature_groups_search_members_hint
import org.mifos.feature.groups.generated.resources.feature_groups_select_activation_date
import org.mifos.feature.groups.generated.resources.feature_groups_select_office_placeholder
import org.mifos.feature.groups.generated.resources.feature_groups_submitted_on_date_label
import org.mifos.feature.groups.generated.resources.feature_groups_submitted_on_date_placeholder
import org.mifos.feature.groups.generated.resources.feature_groups_update_group
import org.mifos.feature.groups.generated.resources.feature_groups_update_group_subtitle
import org.mifos.feature.groups.generated.resources.feature_groups_update_group_success_message
import org.mifos.feature.groups.generated.resources.feature_groups_update_group_success_title

@Composable
fun CreateGroupScreen(
    onBackClick: () -> Unit,
    onNavigateToGroupDashboardWithUpdateData: () -> Unit,
    onNavigateToGroupDetailWithUpdateData: (Long) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: CreateGroupViewModel = koinViewModel(),
) {
    val state by viewModel.stateFlow.collectAsStateWithLifecycle()

    EventsEffect(viewModel.eventFlow) { event ->
        when (event) {
            CreateGroupEvent.NavigateBack -> onBackClick()
            CreateGroupEvent.NavigateToGroupDashboardWithUpdateData ->
                onNavigateToGroupDashboardWithUpdateData()
            is CreateGroupEvent.NavigateToGroupDetailWithUpdateData ->
                onNavigateToGroupDetailWithUpdateData(event.groupId)
        }
    }

    if (state.showSuccessDialog) {
        val title = if (state.groupId != null) {
            stringResource(Res.string.feature_groups_update_group_success_title)
        } else {
            stringResource(Res.string.feature_groups_create_group_success_title)
        }
        val message = if (state.groupId != null) {
            stringResource(Res.string.feature_groups_update_group_success_message)
        } else {
            stringResource(Res.string.feature_groups_create_group_success_message)
        }
        KptSuccessDialog(
            title = title,
            message = message,
            buttonText = stringResource(Res.string.feature_groups_ok),
            onConfirm = { viewModel.trySendAction(CreateGroupAction.DismissSuccessDialog) },
        )
    }

    if (state.isSubmittedDatePickerVisible) {
        KptDatePickerDialog(
            onDateSelected = { millis ->
                viewModel.trySendAction(CreateGroupAction.SubmittedOnDateSelected(millis))
            },
            onDismiss = {
                viewModel.trySendAction(CreateGroupAction.SubmittedOnDatePickerToggle(false))
            },
            initialSelectedDateMillis = state.selectedSubmittedOnDateMillis,
        )
    }

    if (state.isActivationDatePickerVisible) {
        KptDatePickerDialog(
            onDateSelected = { millis ->
                viewModel.trySendAction(CreateGroupAction.ActivationDateSelected(millis))
            },
            onDismiss = {
                viewModel.trySendAction(CreateGroupAction.ActivationDatePickerToggle(false))
            },
            initialSelectedDateMillis = state.selectedActivationDateMillis,
        )
    }

    CreateGroupScreenContent(
        state = state,
        onAction = viewModel::trySendAction,
        modifier = modifier,
    )
}

@Composable
internal fun CreateGroupScreenContent(
    state: CreateGroupState,
    onAction: (CreateGroupAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    KptScaffold(
        containerColor = KptTheme.colorScheme.surface,
        topBar = {
            KptTopAppBar(
                title = "",
                onNavigationIconClick = { onAction(CreateGroupAction.OnBackClick) },
            )
        },
        bottomBar = {
            if (state.submitState is SubmitState.Idle || state.submitState is SubmitState.Submitting) {
                val rightButtonText = if (state.groupId != null) {
                    stringResource(Res.string.feature_groups_update_group)
                } else {
                    stringResource(Res.string.feature_groups_create_group_title)
                }
                KptDoubleButton(
                    leftButtonText = stringResource(Res.string.feature_groups_cancel),
                    rightButtonText = rightButtonText,
                    onRightButtonClick = { onAction(CreateGroupAction.CreateGroup) },
                    onLeftButtonClick = { onAction(CreateGroupAction.OnBackClick) },
                    enabledRight = state.isSubmitButtonEnabled,
                )
            }
        },
        modifier = Modifier.fillMaxSize(),
    ) {
        MutationScreenContent(
            screenState = state.screenState,
            submitState = state.submitState,
            loading = {
                DefaultLoadingContent(
                    config = ScreenStateLoading.Spinner,
                )
            },
            onRetry = { onAction(CreateGroupAction.Retry) },
            onSubmitted = {},
            shape = RectangleShape,
            modifier = modifier.fillMaxSize(),
        ) { _, _ ->
            CreateGroupFormContent(
                state = state,
                onAction = onAction,
                modifier = Modifier,
            )
        }
    }
}

@Composable
private fun CreateGroupFormContent(
    state: CreateGroupState,
    onAction: (CreateGroupAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = KptTheme.spacing.md)
            .verticalScroll(rememberScrollState()),
    ) {
        VerticalSpacer(KptTheme.spacing.sm)

        Text(
            text = stringResource(Res.string.feature_groups_mifos_save_mobile),
            style = KptTheme.typography.labelLarge.copy(
                fontWeight = FontWeight.Bold,
                color = KptTheme.colorScheme.onSurfaceVariant,
                letterSpacing = 1.5.sp,
            ),
        )

        VerticalSpacer(KptTheme.spacing.xs)

        val title = if (state.groupId != null) {
            stringResource(Res.string.feature_groups_update_group)
        } else {
            stringResource(Res.string.feature_groups_create_group_title)
        }
        Text(
            text = title,
            style = KptTheme.typography.headlineLarge.copy(
                fontWeight = FontWeight.ExtraBold,
                color = KptTheme.colorScheme.onSurface,
            ),
        )

        VerticalSpacer(KptTheme.spacing.xs)

        val subtitle = if (state.groupId != null) {
            stringResource(Res.string.feature_groups_update_group_subtitle)
        } else {
            stringResource(Res.string.feature_groups_create_group_subtitle)
        }
        Text(
            text = subtitle,
            style = KptTheme.typography.bodyLarge.copy(
                color = KptTheme.colorScheme.onSurfaceVariant,
            ),
        )

        VerticalSpacer(KptTheme.spacing.xl)

        // Group Name*
        KptTextField(
            value = state.name,
            onValueChange = { onAction(CreateGroupAction.NameChanged(it)) },
            label = stringResource(Res.string.feature_groups_group_name_label),
            placeholder = stringResource(Res.string.feature_groups_group_name_placeholder),
            modifier = Modifier.fillMaxWidth(),
        )

        VerticalSpacer(KptTheme.spacing.md)

        // External Id
        KptTextField(
            value = state.externalId,
            onValueChange = { onAction(CreateGroupAction.ExternalIdChanged(it)) },
            enabled = (state.groupId == null),
            label = stringResource(Res.string.feature_groups_external_id_label),
            placeholder = stringResource(Res.string.feature_groups_external_id_placeholder),
            modifier = Modifier.fillMaxWidth(),
        )

        VerticalSpacer(KptTheme.spacing.md)

        KptDropdownTextField(
            value = state.selectedOffice?.name.orEmpty(),
            onOptionSelected = { index, _ -> onAction(CreateGroupAction.OfficeSelected(index)) },
            options = state.officeOptions.map { it.name },
            enabled = (state.groupId == null),
            label = stringResource(Res.string.feature_groups_office_label),
            placeholder = stringResource(Res.string.feature_groups_select_office_placeholder),
            modifier = Modifier.fillMaxWidth(),
        )

        VerticalSpacer(KptTheme.spacing.sm)

        if (state.groupId == null) {
            AddMembers(
                state = state,
                onAction = onAction,
            )
        } else if (state.selectedMembers.isNotEmpty()) {
            Column(
                verticalArrangement = Arrangement.spacedBy(KptTheme.spacing.xs),
            ) {
                Text(
                    text = stringResource(Res.string.feature_groups_activate_add_members),
                    style = KptTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = KptTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.38f),
                    ),
                    modifier = Modifier.padding(bottom = KptTheme.spacing.xs),
                )
                state.selectedMembers.forEach { member ->
                    val fullName = "${member.firstname.orEmpty()} ${member.lastname.orEmpty()}"
                        .trim()
                        .ifBlank { member.displayName.orEmpty() }

                    KptSelectableItemCard(
                        title = fullName,
                        subtitle = member.accountNo.orEmpty(),
                        leadingIcon = AppIcons.Person,
                        isSelected = false,
                        isInSelectionMode = false,
                        onClick = {},
                        onLongClick = {},
                    )
                }
            }
        }

        VerticalSpacer(KptTheme.spacing.sm)

        KptTextField(
            value = state.submittedOnDate,
            onValueChange = {},
            readOnly = true,
            enabled = (state.groupId == null),
            label = stringResource(Res.string.feature_groups_submitted_on_date_label),
            placeholder = stringResource(Res.string.feature_groups_submitted_on_date_placeholder),
            onCalenderClick = if (state.groupId == null) {
                { onAction(CreateGroupAction.SubmittedOnDatePickerToggle(true)) }
            } else null,
            modifier = Modifier.fillMaxWidth(),
        )

        VerticalSpacer(KptTheme.spacing.md)

        // Activate Group Radio Toggle
        KptRadioToggle(
            selected = state.isActiveGroup,
            enabled = (state.groupId == null),
            onClick = {
                onAction(CreateGroupAction.ActivateGroupToggled(!state.isActiveGroup))
            },
            text = stringResource(Res.string.feature_groups_activate_group_toggle),
        )

        // Conditional Activation Date* field
        AnimatedVisibility(visible = state.isActiveGroup) {
            Column {
                VerticalSpacer(KptTheme.spacing.md)
                KptTextField(
                    value = state.activationDate,
                    onValueChange = {},
                    readOnly = true,
                    enabled = (state.groupId == null),
                    label = stringResource(Res.string.feature_groups_select_activation_date),
                    placeholder = stringResource(Res.string.feature_groups_activation_date_hint),
                    onCalenderClick = if (state.groupId == null) {
                        { onAction(CreateGroupAction.ActivationDatePickerToggle(true)) }
                    } else null,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }
    }
}

@Composable
fun AddMembers(
    state: CreateGroupState,
    onAction: (CreateGroupAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    val dropdownItems = state.searchResults.map { member ->
        val fullName = "${member.firstname.orEmpty()} ${member.lastname.orEmpty()}"
            .trim()
            .ifBlank { member.displayName.orEmpty() }
        KptDropdownSearchBarItem(
            title = fullName,
            subtitle = member.accountNo.orEmpty(),
            onClick = {
                onAction(CreateGroupAction.SelectMember(member))
            },
        )
    }

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(KptTheme.spacing.xs),
    ) {
        KptDropdownSearchBar(
            query = state.searchQuery,
            onQueryChange = { onAction(CreateGroupAction.SearchQueryChanged(it)) },
            placeholder = stringResource(Res.string.feature_groups_search_members_hint),
            showDropdown = state.showDropdown,
            dropdownItems = dropdownItems,
            label = stringResource(Res.string.feature_groups_activate_add_members),
            onDismissRequest = { onAction(CreateGroupAction.DismissDropdown) },
            isSearching = state.isSearching,
        )

        state.selectedMembers.forEach { member ->
            val fullName = "${member.firstname.orEmpty()} ${member.lastname.orEmpty()}"
                .trim()
                .ifBlank { member.displayName.orEmpty() }

            KptSelectableItemCard(
                title = fullName,
                subtitle = member.accountNo.orEmpty(),
                leadingIcon = AppIcons.Person,
                isSelected = false,
                isInSelectionMode = false,
                onClick = {
                    onAction(CreateGroupAction.RemoveSelectedMember(member.id))
                },
                onLongClick = {},
            )
        }
    }
}
