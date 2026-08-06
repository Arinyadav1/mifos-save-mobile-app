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

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.mifos.core.base.store.screen.DataFreshness
import org.mifos.core.base.store.screen.ScreenState
import org.mifos.core.base.store.submit.SubmitState
import org.mifos.core.base.ui.viewmodel.BaseViewModel
import org.mifos.core.common.Constants
import org.mifos.core.common.FormatDate.formatDateFromLong
import org.mifos.core.common.FormatDate.getCurrentEpochMillis
import org.mifos.core.data.group.GroupRepository

class ActivateGroupViewModel(
    savedStateHandle: SavedStateHandle,
    private val groupRepository: GroupRepository,
) : BaseViewModel<ActivateGroupState, ActivateGroupEvent, ActivateGroupAction>(
    ActivateGroupState(
        groupId = savedStateHandle.toRoute<ActivateGroupRoute>().groupId,
    ),
) {
    private val route = savedStateHandle.toRoute<ActivateGroupRoute>()

    override fun handleAction(action: ActivateGroupAction) {
        when (action) {
            ActivateGroupAction.OnBackClick -> sendEvent(ActivateGroupEvent.NavigateBack)
            is ActivateGroupAction.OnDateSelected -> handleDateSelected(action.millis)
            is ActivateGroupAction.OnDatePickerToggle -> {
                mutableStateFlow.update { it.copy(isDatePickerVisible = action.visible) }
            }

            ActivateGroupAction.ActivateGroup -> activateGroup()
            ActivateGroupAction.DismissSuccessDialog -> {
                mutableStateFlow.update { it.copy(showSuccessDialog = false) }
                sendEvent(ActivateGroupEvent.NavigateBackWithUpdateData(route.groupId))
            }

            ActivateGroupAction.Retry -> {
                mutableStateFlow.update {
                    it.copy(
                        screenState = ScreenState.Content(Unit, DataFreshness.FRESH),
                        submitState = SubmitState.Idle,
                    )
                }
            }

            ActivateGroupAction.DismissErrorState -> {
                mutableStateFlow.update { it.copy(submitState = SubmitState.Idle) }
            }
        }
    }

    private fun handleDateSelected(millis: Long?) {
        if (millis != null) {
            val formattedDate = formatDateFromLong(millis)
            mutableStateFlow.update {
                it.copy(
                    selectedDateMillis = millis,
                    dateText = formattedDate,
                    isDatePickerVisible = false,
                )
            }
        } else {
            mutableStateFlow.update { it.copy(isDatePickerVisible = false) }
        }
    }

    private fun activateGroup() {
        val dateText = state.dateText
        if (dateText.isBlank()) return

        mutableStateFlow.update { it.copy(submitState = SubmitState.Submitting()) }
        viewModelScope.launch {
            when (
                val result = groupRepository.activateGroup(
                    groupId = route.groupId,
                    activationDate = dateText,
                    dateFormat = Constants.DATE_FORMAT_SHORT_MONTH,
                    locale = Constants.LOCALE_EN,
                )
            ) {
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
                            submitState = SubmitState.Failed(),
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

data class ActivateGroupState(
    val groupId: Long,
    val selectedDateMillis: Long? = null,
    val dateText: String = formatDateFromLong(getCurrentEpochMillis()),
    val isDatePickerVisible: Boolean = false,
    val showSuccessDialog: Boolean = false,
    val screenState: ScreenState<Unit> = ScreenState.Content(Unit, DataFreshness.FRESH),
    val submitState: SubmitState<Unit> = SubmitState.Idle,
)

sealed interface ActivateGroupEvent {
    data class NavigateBackWithUpdateData(val groupId: Long) : ActivateGroupEvent
    data object NavigateBack : ActivateGroupEvent
}

sealed interface ActivateGroupAction {
    data object OnBackClick : ActivateGroupAction
    data class OnDateSelected(val millis: Long?) : ActivateGroupAction
    data class OnDatePickerToggle(val visible: Boolean) : ActivateGroupAction
    data object ActivateGroup : ActivateGroupAction
    data object DismissSuccessDialog : ActivateGroupAction
    data object Retry : ActivateGroupAction
    data object DismissErrorState : ActivateGroupAction
}
