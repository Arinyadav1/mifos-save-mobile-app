/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See See https://github.com/openMF/kmp-project-template/blob/main/LICENSE
 */
package org.mifos.feature.meeting.meetingList

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.mifos.core.base.store.screen.ScreenState
import org.mifos.core.base.ui.viewmodel.BaseViewModel
import org.mifos.core.data.meeting.MeetingRepository
import org.mifos.core.model.meeting.Meeting

class MeetingListViewModel(
    savedStateHandle: SavedStateHandle,
    private val meetingRepository: MeetingRepository,
) : BaseViewModel<MeetingListState, MeetingListEvent, MeetingListAction>(
    MeetingListState(
        groupId = savedStateHandle.toRoute<MeetingListRoute>().groupId,
    ),
) {
    private val route = savedStateHandle.toRoute<MeetingListRoute>()
    val groupId = route.groupId

    init {
        loadMeetings()
    }

    private fun loadMeetings() {
        mutableStateFlow.update {
            it.copy(screenState = ScreenState.Loading)
        }
        viewModelScope.launch {
            meetingRepository.getGroupMeetings(groupId)
                .collect { result ->
                    mutableStateFlow.update { state ->
                        state.copy(screenState = result)
                    }
                }
        }
    }

    override fun handleAction(action: MeetingListAction) {
        when (action) {
            MeetingListAction.OnBackClick -> sendEvent(MeetingListEvent.NavigateBack)
            MeetingListAction.Retry -> loadMeetings()
        }
    }
}

data class MeetingListState(
    val groupId: Long,
    val screenState: ScreenState<List<Meeting>> = ScreenState.Loading,
)

sealed interface MeetingListAction {
    data object OnBackClick : MeetingListAction
    data object Retry : MeetingListAction
}

sealed interface MeetingListEvent {
    data object NavigateBack : MeetingListEvent
}
