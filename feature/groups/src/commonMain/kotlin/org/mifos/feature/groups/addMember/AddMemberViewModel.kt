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

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.mifos.core.base.store.screen.DataFreshness
import org.mifos.core.base.store.screen.ScreenState
import org.mifos.core.base.store.submit.SubmitState
import org.mifos.core.base.ui.viewmodel.BaseViewModel
import org.mifos.core.data.group.GroupRepository
import org.mifos.core.model.group.ClientMember

class AddMemberViewModel(
    savedStateHandle: SavedStateHandle,
    private val groupRepository: GroupRepository,
) : BaseViewModel<AddMemberState, AddMemberEvent, AddMemberAction>(
    AddMemberState(
        officeId = savedStateHandle.toRoute<AddMemberRoute>().officeId,
    ),
) {
    private val route = savedStateHandle.toRoute<AddMemberRoute>()

    override fun handleAction(action: AddMemberAction) {
        when (action) {
            AddMemberAction.OnBackClick -> sendEvent(AddMemberEvent.NavigateBack)
            is AddMemberAction.SearchQueryChanged -> handleSearchQueryChanged(action.query)
            is AddMemberAction.SelectMember -> handleSelectMember(action.member)
            is AddMemberAction.RemoveSelectedMember -> handleRemoveSelectedMember(action.memberId)
            AddMemberAction.AddMembers -> associateClients()
            AddMemberAction.DismissSuccessDialog -> {
                mutableStateFlow.update { it.copy(showSuccessDialog = false) }
                sendEvent(AddMemberEvent.NavigateBackWithUpdateData(route.groupId))
            }
            AddMemberAction.DismissDropdown -> {
                mutableStateFlow.update { it.copy(showDropdown = false) }
            }
            AddMemberAction.Retry -> {
                mutableStateFlow.update {
                    it.copy(
                        submitState = SubmitState.Idle,
                        screenState = ScreenState.Content(Unit, DataFreshness.FRESH),
                    )
                }
            }
        }
    }

    private fun handleSearchQueryChanged(query: String) {
        mutableStateFlow.update {
            it.copy(
                searchQuery = query,
            )
        }
        viewModelScope.launch {
            performSearch(query)
        }
    }

    private suspend fun performSearch(query: String) {
        val officeId = state.officeId ?: return
        if (query.isBlank()) {
            mutableStateFlow.update { it.copy(searchResults = emptyList(), showDropdown = false) }
            return
        }

        mutableStateFlow.update { it.copy(isSearching = true) }
        val result = groupRepository.searchClients(
            displayName = query,
            officeId = officeId,
        )
        when (result) {
            is ScreenState.Content -> {
                val filtered = result.data.filter {
                    it.id !in state.selectedMembers.map { sel -> sel.id }
                }
                mutableStateFlow.update {
                    it.copy(
                        searchResults = filtered,
                        isSearching = false,
                        showDropdown = filtered.isNotEmpty(),
                    )
                }
            }

            else -> {
                mutableStateFlow.update {
                    it.copy(
                        searchResults = emptyList(),
                        isSearching = false,
                        showDropdown = false,
                    )
                }
            }
        }
    }

    private fun handleSelectMember(member: ClientMember) {
        mutableStateFlow.update {
            if (it.selectedMembers.any { m -> m.id == member.id }) {
                it.copy(
                    searchQuery = "",
                    searchResults = emptyList(),
                    showDropdown = false,
                )
            } else {
                it.copy(
                    selectedMembers = it.selectedMembers + member,
                    searchQuery = "",
                    searchResults = emptyList(),
                    showDropdown = false,
                )
            }
        }
    }

    private fun handleRemoveSelectedMember(memberId: Long) {
        mutableStateFlow.update {
            it.copy(
                selectedMembers = it.selectedMembers.filter { m -> m.id != memberId },
            )
        }
    }

    private fun associateClients() {
        val toAdd = state.selectedMembers.map { it.id }
        if (toAdd.isEmpty()) return

        mutableStateFlow.update { it.copy(submitState = SubmitState.Submitting()) }
        viewModelScope.launch {
            when (val result = groupRepository.associateClients(route.groupId, toAdd)) {
                is ScreenState.Content -> {
                    mutableStateFlow.update {
                        it.copy(
                            submitState = SubmitState.Submitted(Unit),
                            showSuccessDialog = true,
                        )
                    }
                }
                is ScreenState.Error -> {
                    mutableStateFlow.update {
                        it.copy(
                            screenState = ScreenState.Error(result.error),
                            submitState = SubmitState.Failed(result.error),
                        )
                    }
                }
                is ScreenState.NoNetwork -> {
                    mutableStateFlow.update {
                        it.copy(
                            screenState = ScreenState.NoNetwork(),
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
}

data class AddMemberState(
    val searchQuery: String = "",
    val searchResults: List<ClientMember> = emptyList(),
    val selectedMembers: List<ClientMember> = emptyList(),
    val isSearching: Boolean = false,
    val showSuccessDialog: Boolean = false,
    val officeId: Long? = null,
    val showDropdown: Boolean = false,
    val screenState: ScreenState<Unit> = ScreenState.Content(Unit, DataFreshness.FRESH),
    val submitState: SubmitState<Unit> = SubmitState.Idle,
)

sealed interface AddMemberEvent {
    data class NavigateBackWithUpdateData(val groupId: Long) : AddMemberEvent
    data object NavigateBack : AddMemberEvent
}

sealed interface AddMemberAction {
    data object OnBackClick : AddMemberAction
    data class SearchQueryChanged(val query: String) : AddMemberAction
    data class SelectMember(val member: ClientMember) : AddMemberAction
    data class RemoveSelectedMember(val memberId: Long) : AddMemberAction
    data object AddMembers : AddMemberAction
    data object DismissSuccessDialog : AddMemberAction
    data object DismissDropdown : AddMemberAction
    data object Retry : AddMemberAction
}
