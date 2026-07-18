/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See See https://github.com/openMF/kmp-project-template/blob/main/LICENSE
 */
package org.mifos.feature.groups.groupDetails

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.mifos.core.base.store.screen.ScreenState
import org.mifos.core.base.ui.viewmodel.BaseViewModel
import org.mifos.core.data.group.GroupRepository
import org.mifos.core.model.group.Group

class GroupDetailsViewModel(
    savedStateHandle: SavedStateHandle,
    private val groupRepository: GroupRepository,
) : BaseViewModel<GroupDetailsState, GroupDetailsEvent, GroupDetailsAction>(
    GroupDetailsState(),
) {
    private val route = savedStateHandle.toRoute<GroupDetailsRoute>()
    val groupId = route.groupId

    init {
        loadGroupDetails()
    }

    private fun loadGroupDetails() {
        viewModelScope.launch {
            groupRepository.getGroupDetails(groupId)
                .collect { screenState ->
                    mutableStateFlow.update {
                        it.copy(
                            screenState = screenState,
                        )
                    }
                }
        }
    }

    override fun handleAction(action: GroupDetailsAction) {
        when (action) {
            GroupDetailsAction.OnBackClick -> {
                sendEvent(GroupDetailsEvent.NavigateBack)
            }
            GroupDetailsAction.Retry -> {
                loadGroupDetails()
            }
            GroupDetailsAction.OnLoansClick -> {
                sendEvent(GroupDetailsEvent.NavigateToLoans)
            }
            GroupDetailsAction.OnSavingsClick -> {
                sendEvent(GroupDetailsEvent.NavigateToSavings)
            }
            GroupDetailsAction.OnMeetingsClick -> {
                sendEvent(GroupDetailsEvent.NavigateToMeetings)
            }
            GroupDetailsAction.OnGlimClick -> {
                sendEvent(GroupDetailsEvent.NavigateToGlim)
            }
            GroupDetailsAction.OnGsimClick -> {
                sendEvent(GroupDetailsEvent.NavigateToGsim)
            }
            GroupDetailsAction.OnMembersClick -> {
                sendEvent(GroupDetailsEvent.NavigateToMembers)
            }
        }
    }
}

data class GroupDetailsState(
    val screenState: ScreenState<Group> = ScreenState.Loading,
)

sealed interface GroupDetailsEvent {
    data object NavigateBack : GroupDetailsEvent
    data object NavigateToLoans : GroupDetailsEvent
    data object NavigateToSavings : GroupDetailsEvent
    data object NavigateToMeetings : GroupDetailsEvent
    data object NavigateToGlim : GroupDetailsEvent
    data object NavigateToGsim : GroupDetailsEvent
    data object NavigateToMembers : GroupDetailsEvent
}

sealed interface GroupDetailsAction {
    data object OnBackClick : GroupDetailsAction
    data object Retry : GroupDetailsAction
    data object OnLoansClick : GroupDetailsAction
    data object OnSavingsClick : GroupDetailsAction
    data object OnMeetingsClick : GroupDetailsAction
    data object OnGlimClick : GroupDetailsAction
    data object OnGsimClick : GroupDetailsAction
    data object OnMembersClick : GroupDetailsAction
}
