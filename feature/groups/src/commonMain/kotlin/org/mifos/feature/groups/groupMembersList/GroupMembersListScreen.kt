/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See See https://github.com/openMF/kmp-project-template/blob/main/LICENSE
 */
package org.mifos.feature.groups.groupMembersList

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import org.mifos.core.base.designsystem.component.KptButton
import org.mifos.core.base.designsystem.component.KptOutlinedButton
import org.mifos.core.base.designsystem.component.KptSuccessDialog
import org.mifos.core.base.designsystem.component.KptTextButton
import org.mifos.core.base.designsystem.theme.KptTheme
import org.mifos.core.base.ui.effects.EventsEffect
import org.mifos.core.base.ui.screen.DefaultLoadingContent
import org.mifos.core.base.ui.screen.ScreenContent
import org.mifos.core.base.ui.screen.ScreenStateLoading
import org.mifos.core.designsystem.component.KptEmptyState
import org.mifos.core.designsystem.component.KptHeader
import org.mifos.core.designsystem.component.KptHeaderBackButton
import org.mifos.core.designsystem.component.KptHeaderPillButton
import org.mifos.core.designsystem.component.KptHeaderTitle
import org.mifos.core.designsystem.component.KptSelectableItemCard
import org.mifos.core.designsystem.icon.AppIcons
import org.mifos.core.ui.input.KptSearchBar
import org.mifos.core.ui.scaffold.KptScaffold
import org.mifos.feature.groups.generated.resources.Res
import org.mifos.feature.groups.generated.resources.feature_groups_add_members
import org.mifos.feature.groups.generated.resources.feature_groups_cancel
import org.mifos.feature.groups.generated.resources.feature_groups_disassociate_success_message
import org.mifos.feature.groups.generated.resources.feature_groups_disassociate_success_title
import org.mifos.feature.groups.generated.resources.feature_groups_empty_members_message
import org.mifos.feature.groups.generated.resources.feature_groups_members_list_title
import org.mifos.feature.groups.generated.resources.feature_groups_mifos_save
import org.mifos.feature.groups.generated.resources.feature_groups_ok
import org.mifos.feature.groups.generated.resources.feature_groups_remove_members
import org.mifos.feature.groups.generated.resources.feature_groups_remove_members_confirm_desc
import org.mifos.feature.groups.generated.resources.feature_groups_remove_members_confirm_title
import org.mifos.feature.groups.generated.resources.feature_groups_search_members

@Composable
fun GroupMembersListScreen(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    onAddMembersClick: (groupId: Long, officeId: Long) -> Unit = { _, _ -> },
    viewModel: GroupMembersListViewModel = koinViewModel(),
) {
    val state by viewModel.stateFlow.collectAsStateWithLifecycle()
    var showDialog by remember { mutableStateOf(false) }

    EventsEffect(viewModel.eventFlow) { event ->
        when (event) {
            GroupMembersListEvent.NavigateBack -> onBackClick()
            GroupMembersListEvent.ShowConfirmationDialog -> {
                showDialog = true
            }
            is GroupMembersListEvent.NavigateToAddMembers -> onAddMembersClick(event.groupId, event.officeId)
        }
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = {
                Text(
                    text = stringResource(Res.string.feature_groups_remove_members_confirm_title),
                    style = KptTheme.typography.titleMedium,
                )
            },
            text = {
                Text(
                    text = stringResource(Res.string.feature_groups_remove_members_confirm_desc),
                    style = KptTheme.typography.bodyMedium,
                )
            },
            confirmButton = {
                KptTextButton(
                    onClick = {
                        showDialog = false
                        viewModel.trySendAction(GroupMembersListAction.ConfirmRemoveMembers)
                    },
                ) {
                    Text(stringResource(Res.string.feature_groups_remove_members))
                }
            },
            dismissButton = {
                KptTextButton(
                    onClick = { showDialog = false },
                ) {
                    Text(stringResource(Res.string.feature_groups_cancel))
                }
            },
        )
    }

    if (state.showSuccessDialog) {
        KptSuccessDialog(
            title = stringResource(Res.string.feature_groups_disassociate_success_title),
            message = stringResource(Res.string.feature_groups_disassociate_success_message),
            buttonText = stringResource(Res.string.feature_groups_ok),
            onConfirm = { viewModel.trySendAction(GroupMembersListAction.DismissSuccessDialog) },
        )
    }

    GroupMembersListScreenContent(
        state = state,
        onAction = viewModel::trySendAction,
        modifier = modifier,
    )
}

@Composable
internal fun GroupMembersListScreenContent(
    state: GroupMembersListState,
    onAction: (GroupMembersListAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    KptScaffold(
        modifier = modifier,
        containerColor = KptTheme.colorScheme.primary,
        topBar = {
            KptHeader(
                navigationIcon = {
                    KptHeaderBackButton(onClick = {
                        if (state.isSelectionMode) {
                            onAction(GroupMembersListAction.ClearSelection)
                        } else {
                            onAction(GroupMembersListAction.OnBackClick)
                        }
                    })
                },
                actions = {
                    if (!state.isSelectionMode) {
                        KptHeaderPillButton(
                            text = stringResource(Res.string.feature_groups_add_members),
                            icon = AppIcons.AddDefault,
                            onClick = { onAction(GroupMembersListAction.OnAddMembersClick) },
                        )
                    }
                },
                title = {
                    KptHeaderTitle(
                        title = stringResource(Res.string.feature_groups_members_list_title),
                        subtitle = stringResource(Res.string.feature_groups_mifos_save),
                    )
                },
            )
        },
        bottomBar = {
            if (state.isSelectionMode) {
                Surface(
                    tonalElevation = KptTheme.elevation.level1,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(KptTheme.spacing.md),
                        horizontalArrangement = Arrangement.spacedBy(KptTheme.spacing.md),
                    ) {
                        KptOutlinedButton(
                            onClick = { onAction(GroupMembersListAction.ClearSelection) },
                            modifier = Modifier.weight(1f),
                        ) {
                            Text(stringResource(Res.string.feature_groups_cancel))
                        }
                        KptButton(
                            onClick = { onAction(GroupMembersListAction.ShowRemoveConfirmation) },
                            modifier = Modifier.weight(1f),
                        ) {
                            Text(stringResource(Res.string.feature_groups_remove_members))
                        }
                    }
                }
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
            Column(modifier = Modifier.fillMaxSize()) {
                KptSearchBar(
                    query = state.searchQuery,
                    onQueryChange = { onAction(GroupMembersListAction.SearchQueryChanged(it)) },
                    placeholder = stringResource(Res.string.feature_groups_search_members),
                )

                ScreenContent(
                    state = state.screenState,
                    onRetry = { onAction(GroupMembersListAction.Retry) },
                    loading = {
                        DefaultLoadingContent(
                            config = ScreenStateLoading.Spinner,
                        )
                    },
                    empty = {
                        KptEmptyState(
                            message = stringResource(Res.string.feature_groups_empty_members_message),
                            buttonText = stringResource(Res.string.feature_groups_add_members),
                            onButtonClick = { onAction(GroupMembersListAction.OnAddMembersClick) },
                        )
                    },
                    modifier = Modifier.fillMaxSize(),
                ) { members, freshness ->
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(bottom = KptTheme.spacing.md),
                        verticalArrangement = Arrangement.spacedBy(KptTheme.spacing.xs),
                    ) {
                        items(members, key = { it.id }) { member ->
                            val fullName = "${member.firstname.orEmpty()} ${member.lastname.orEmpty()}"
                                .trim()
                                .ifBlank { member.displayName.orEmpty() }
                            KptSelectableItemCard(
                                title = fullName,
                                subtitle = member.accountNo.orEmpty(),
                                leadingIcon = AppIcons.Person,
                                isSelected = state.selectedMemberIds.contains(member.id),
                                isInSelectionMode = state.isSelectionMode,
                                onClick = {
                                    onAction(GroupMembersListAction.OnMemberClick(member.id))
                                },
                                onLongClick = {
                                    onAction(GroupMembersListAction.OnMemberLongClick(member.id))
                                },
                            )
                        }
                    }
                }
            }
        }
    }
}
