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

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.mifos.core.base.store.screen.ScreenState
import org.mifos.core.base.store.submit.SubmitState
import org.mifos.core.base.ui.viewmodel.BaseViewModel
import org.mifos.core.data.group.GroupRepository
import org.mifos.core.model.group.ClientMember

class GroupMembersListViewModel(
    savedStateHandle: SavedStateHandle,
    private val groupRepository: GroupRepository,
) : BaseViewModel<GroupMembersListState, GroupMembersListEvent, GroupMembersListAction>(
    GroupMembersListState(),
) {
    private val route = savedStateHandle.toRoute<GroupMembersListRoute>()
    val groupId = route.groupId

    private val removedMemberIds = MutableStateFlow<Set<Long>>(emptySet())
    private val searchQuery = MutableStateFlow("")

    init {
        loadGroupMembers()
    }

    private fun loadGroupMembers() {
        mutableStateFlow.update {
            it.copy(screenState = ScreenState.Loading)
        }
        viewModelScope.launch {
            combine(
                groupRepository.getGroupDetails(groupId),
                removedMemberIds,
                searchQuery,
            ) { groupState, removedIds, query ->
                when (groupState) {
                    is ScreenState.Content -> {
                        mutableStateFlow.update {
                            it.copy(
                                officeId = groupState.data.officeId,
                            )
                        }
                        val allMembers = groupState.data.clientMembers.orEmpty()
                            .filter { it.id !in removedIds }

                        val filteredMembers = if (query.isBlank()) {
                            allMembers
                        } else {
                            allMembers.filter {
                                val fullName =
                                    "${it.firstname.orEmpty()} ${it.lastname.orEmpty()}".trim()
                                fullName.contains(query, ignoreCase = true) ||
                                    it.displayName.orEmpty()
                                        .contains(query, ignoreCase = true) ||
                                    it.accountNo.orEmpty().contains(query, ignoreCase = true)
                            }
                        }

                        if (filteredMembers.isEmpty()) {
                            ScreenState.Empty
                        } else {
                            ScreenState.Content(
                                data = filteredMembers,
                                freshness = groupState.freshness,
                                fetchedAt = groupState.fetchedAt,
                                freshnessSignal = groupState.freshnessSignal,
                            )
                        }
                    }

                    is ScreenState.Loading -> ScreenState.Loading
                    is ScreenState.Empty -> ScreenState.Empty
                    is ScreenState.NoNetwork -> ScreenState.NoNetwork(groupState.isCaptivePortal)
                    is ScreenState.Error -> ScreenState.Error(
                        groupState.error,
                        groupState.isNetworkError,
                    )

                    is ScreenState.Unauthenticated -> ScreenState.Unauthenticated
                }
            }.collect { mappedScreenState ->
                mutableStateFlow.update {
                    it.copy(screenState = mappedScreenState)
                }
            }
        }
    }

    override fun handleAction(action: GroupMembersListAction) {
        when (action) {
            GroupMembersListAction.OnBackClick -> sendEvent(GroupMembersListEvent.NavigateBack)
            GroupMembersListAction.Retry -> {
                mutableStateFlow.update {
                    it.copy(
                        submitState = SubmitState.Idle,
                    )
                }
                loadGroupMembers()
            }
            is GroupMembersListAction.SearchQueryChanged -> handleSearchQueryChanged(action.query)
            is GroupMembersListAction.OnMemberLongClick -> handleMemberLongClick(action.memberId)
            is GroupMembersListAction.OnMemberClick -> handleMemberClick(action.memberId)
            GroupMembersListAction.ClearSelection -> clearSelection()
            GroupMembersListAction.ShowRemoveConfirmation -> sendEvent(GroupMembersListEvent.ShowConfirmationDialog)
            GroupMembersListAction.ConfirmRemoveMembers -> confirmRemoveMembers()
            GroupMembersListAction.DismissSuccessDialog -> dismissSuccessDialog()
            GroupMembersListAction.OnAddMembersClick -> sendEvent(
                GroupMembersListEvent.NavigateToAddMembers(groupId, state.officeId),
            )
            GroupMembersListAction.EmptyStateCancelClick -> {
                mutableStateFlow.update {
                    it.copy(
                        searchQuery = "",
                    )
                }
                loadGroupMembers()
            }
        }
    }

    private fun handleSearchQueryChanged(query: String) {
        searchQuery.value = query
        mutableStateFlow.update {
            it.copy(searchQuery = query)
        }
    }

    private fun handleMemberLongClick(memberId: Long) {
        mutableStateFlow.update { state ->
            val newSelected = state.selectedMemberIds + memberId
            state.copy(
                isSelectionMode = true,
                selectedMemberIds = newSelected,
            )
        }
    }

    private fun handleMemberClick(memberId: Long) {
        mutableStateFlow.update { state ->
            if (state.isSelectionMode) {
                val newSelected = if (state.selectedMemberIds.contains(memberId)) {
                    state.selectedMemberIds - memberId
                } else {
                    state.selectedMemberIds + memberId
                }
                state.copy(
                    selectedMemberIds = newSelected,
                    isSelectionMode = newSelected.isNotEmpty(),
                )
            } else {
                state
            }
        }
    }

    private fun clearSelection() {
        mutableStateFlow.update {
            it.copy(
                isSelectionMode = false,
                selectedMemberIds = emptySet(),
            )
        }
    }

    private fun confirmRemoveMembers() {
        val toRemove = state.selectedMemberIds
        if (toRemove.isEmpty()) return

        mutableStateFlow.update { it.copy(submitState = SubmitState.Submitting()) }
        viewModelScope.launch {
            when (val result = groupRepository.disassociateClients(groupId, toRemove.toList())) {
                is ScreenState.Content -> {
                    removedMemberIds.update { it + toRemove }
                    mutableStateFlow.update {
                        it.copy(
                            submitState = SubmitState.Submitted(Unit),
                            isSelectionMode = false,
                            selectedMemberIds = emptySet(),
                            showSuccessDialog = true,
                        )
                    }
                }

                is ScreenState.Error -> {
                    mutableStateFlow.update {
                        it.copy(
                            screenState = ScreenState.Error(result.error, result.isNetworkError),
                            submitState = SubmitState.Failed(),
                        )
                    }
                }

                is ScreenState.NoNetwork -> {
                    mutableStateFlow.update {
                        it.copy(
                            screenState = ScreenState.NoNetwork(result.isCaptivePortal),
                            submitState = SubmitState.Failed(),
                        )
                    }
                }

                is ScreenState.Unauthenticated -> {
                    mutableStateFlow.update {
                        it.copy(
                            screenState = ScreenState.Unauthenticated,
                            submitState = SubmitState.Failed(),
                        )
                    }
                }

                is ScreenState.Empty -> {
                    mutableStateFlow.update {
                        it.copy(
                            screenState = ScreenState.Empty,
                            submitState = SubmitState.Failed(),
                        )
                    }
                }

                is ScreenState.Loading -> Unit
            }
        }
    }

    private fun dismissSuccessDialog() {
        mutableStateFlow.update {
            it.copy(
                showSuccessDialog = false,
                submitState = SubmitState.Idle,
            )
        }
        sendEvent(GroupMembersListEvent.NavigateBack)
    }
}

data class GroupMembersListState(
    val screenState: ScreenState<List<ClientMember>> = ScreenState.Loading,
    val submitState: SubmitState<Unit> = SubmitState.Idle,
    val selectedMemberIds: Set<Long> = emptySet(),
    val isSelectionMode: Boolean = false,
    val searchQuery: String = "",
    val showSuccessDialog: Boolean = false,
    val officeId: Long = 0,
)

sealed interface GroupMembersListEvent {
    data object NavigateBack : GroupMembersListEvent
    data object ShowConfirmationDialog : GroupMembersListEvent
    data class NavigateToAddMembers(val groupId: Long, val officeId: Long) : GroupMembersListEvent
}

sealed interface GroupMembersListAction {
    data object OnBackClick : GroupMembersListAction
    data object Retry : GroupMembersListAction
    data class SearchQueryChanged(val query: String) : GroupMembersListAction
    data class OnMemberLongClick(val memberId: Long) : GroupMembersListAction
    data class OnMemberClick(val memberId: Long) : GroupMembersListAction
    data object ClearSelection : GroupMembersListAction
    data object ShowRemoveConfirmation : GroupMembersListAction
    data object ConfirmRemoveMembers : GroupMembersListAction
    data object DismissSuccessDialog : GroupMembersListAction
    data object OnAddMembersClick : GroupMembersListAction
    data object EmptyStateCancelClick : GroupMembersListAction
}
