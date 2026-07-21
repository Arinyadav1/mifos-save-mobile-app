/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See See https://github.com/openMF/kmp-project-template/blob/main/LICENSE
 */
package org.mifos.feature.groups.addMember

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import org.mifos.core.designsystem.component.KptHeader
import org.mifos.core.designsystem.component.KptHeaderBackButton
import org.mifos.core.designsystem.component.KptHeaderTitle
import org.mifos.core.designsystem.component.KptSelectableItemCard
import org.mifos.core.designsystem.icon.AppIcons
import org.mifos.core.ui.input.KptDropdownSearchBar
import org.mifos.core.ui.input.KptDropdownSearchBarItem
import org.mifos.core.ui.scaffold.KptScaffold
import org.mifos.feature.groups.generated.resources.Res
import org.mifos.feature.groups.generated.resources.feature_groups_add_member_title
import org.mifos.feature.groups.generated.resources.feature_groups_add_members
import org.mifos.feature.groups.generated.resources.feature_groups_associate_success_message
import org.mifos.feature.groups.generated.resources.feature_groups_associate_success_title
import org.mifos.feature.groups.generated.resources.feature_groups_cancel
import org.mifos.feature.groups.generated.resources.feature_groups_mifos_save
import org.mifos.feature.groups.generated.resources.feature_groups_ok
import org.mifos.feature.groups.generated.resources.feature_groups_search_members_hint

@Composable
fun AddMemberScreen(
    onBackClick: () -> Unit,
    onBackWithUpdateData: (Long) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: AddMemberViewModel = koinViewModel(),
) {
    val state by viewModel.stateFlow.collectAsStateWithLifecycle()

    EventsEffect(viewModel.eventFlow) { event ->
        when (event) {
            AddMemberEvent.NavigateBack -> onBackClick()
            is AddMemberEvent.NavigateBackWithUpdateData -> onBackWithUpdateData(event.groupId)
        }
    }

    if (state.showSuccessDialog) {
        KptSuccessDialog(
            title = stringResource(Res.string.feature_groups_associate_success_title),
            message = stringResource(Res.string.feature_groups_associate_success_message),
            buttonText = stringResource(Res.string.feature_groups_ok),
            onConfirm = { viewModel.trySendAction(AddMemberAction.DismissSuccessDialog) },
        )
    }

    AddMemberScreenContent(
        state = state,
        onAction = viewModel::trySendAction,
        modifier = modifier,
    )
}

@Composable
internal fun AddMemberScreenContent(
    state: AddMemberState,
    onAction: (AddMemberAction) -> Unit,
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
                            onAction(AddMemberAction.OnBackClick)
                        },
                    )
                },
                title = {
                    KptHeaderTitle(
                        title = stringResource(Res.string.feature_groups_add_member_title),
                        subtitle = stringResource(Res.string.feature_groups_mifos_save),
                    )
                },
            )
        },
        bottomBar = {
            if (state.submitState is SubmitState.Idle || state.submitState is SubmitState.Submitting) {
                KptDoubleButton(
                    onLeftButtonClick = { onAction(AddMemberAction.OnBackClick) },
                    onRightButtonClick = { onAction(AddMemberAction.AddMembers) },
                    leftButtonText = stringResource(Res.string.feature_groups_cancel),
                    rightButtonText = stringResource(Res.string.feature_groups_add_members),
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
                onRetry = { onAction(AddMemberAction.Retry) },
                onSubmitted = {},
                modifier = Modifier.fillMaxSize(),
            ) { _, _ ->
                Column(
                    modifier = Modifier.fillMaxSize(),
                ) {
                    val dropdownItems = state.searchResults.map { member ->
                        val fullName = "${member.firstname.orEmpty()} ${member.lastname.orEmpty()}"
                            .trim()
                            .ifBlank { member.displayName.orEmpty() }
                        KptDropdownSearchBarItem(
                            title = fullName,
                            subtitle = member.accountNo.orEmpty(),
                            onClick = {
                                onAction(AddMemberAction.SelectMember(member))
                            },
                        )
                    }

                    KptDropdownSearchBar(
                        query = state.searchQuery,
                        onQueryChange = { onAction(AddMemberAction.SearchQueryChanged(it)) },
                        placeholder = stringResource(Res.string.feature_groups_search_members_hint),
                        showDropdown = state.showDropdown,
                        dropdownItems = dropdownItems,
                        onDismissRequest = { onAction(AddMemberAction.DismissDropdown) },
                        isSearching = state.isSearching,
                    )

                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(bottom = KptTheme.spacing.md),
                        verticalArrangement = Arrangement.spacedBy(KptTheme.spacing.xs),
                    ) {
                        items(state.selectedMembers, key = { it.id }) { member ->
                            val fullName =
                                "${member.firstname.orEmpty()} ${member.lastname.orEmpty()}"
                                    .trim()
                                    .ifBlank { member.displayName.orEmpty() }
                            KptSelectableItemCard(
                                title = fullName,
                                subtitle = member.accountNo.orEmpty(),
                                leadingIcon = AppIcons.Person,
                                isSelected = false,
                                isInSelectionMode = false,
                                onClick = {
                                    onAction(AddMemberAction.RemoveSelectedMember(member.id))
                                },
                                onLongClick = {},
                            )
                        }
                    }
                }
            }
        }
    }
}
