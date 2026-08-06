/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See See https://github.com/openMF/kmp-project-template/blob/main/LICENSE
 */
package org.mifos.feature.saving.activateSaving

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
import org.mifos.core.common.FormatDate
import org.mifos.core.data.savings.SavingsRepository

class ActivateSavingViewModel(
    savedStateHandle: SavedStateHandle,
    private val savingsRepository: SavingsRepository,
) : BaseViewModel<ActivateSavingState, ActivateSavingEvent, ActivateSavingAction>(
    ActivateSavingState(
        savingsId = savedStateHandle.toRoute<ActivateSavingRoute>().savingsId,
    ),
) {
    private val route = savedStateHandle.toRoute<ActivateSavingRoute>()

    override fun handleAction(action: ActivateSavingAction) {
        when (action) {
            ActivateSavingAction.OnBackClick -> sendEvent(ActivateSavingEvent.NavigateBack)
            is ActivateSavingAction.OnDateSelected -> handleDateSelected(action.millis)
            is ActivateSavingAction.OnDatePickerToggle -> {
                mutableStateFlow.update { it.copy(isDatePickerVisible = action.visible) }
            }

            ActivateSavingAction.ActivateSaving -> activateSaving()
            ActivateSavingAction.DismissSuccessDialog -> {
                mutableStateFlow.update { it.copy(showSuccessDialog = false) }
                sendEvent(ActivateSavingEvent.NavigateBackWithUpdateData(route.savingsId))
            }

            ActivateSavingAction.Retry -> {
                mutableStateFlow.update {
                    it.copy(
                        screenState = ScreenState.Content(Unit, DataFreshness.FRESH),
                        submitState = SubmitState.Idle,
                    )
                }
            }

            ActivateSavingAction.DismissErrorState -> {
                mutableStateFlow.update { it.copy(submitState = SubmitState.Idle) }
            }
        }
    }

    private fun handleDateSelected(millis: Long?) {
        if (millis != null) {
            val formattedDate = FormatDate.formatDateFromLong(millis)
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

    private fun activateSaving() {
        val dateText = state.dateText
        if (dateText.isBlank()) return

        mutableStateFlow.update { it.copy(submitState = SubmitState.Submitting()) }
        viewModelScope.launch {
            when (
                val result = savingsRepository.activateSaving(
                    savingsId = route.savingsId,
                    activatedOnDate = dateText,
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

data class ActivateSavingState(
    val savingsId: Long,
    val selectedDateMillis: Long? = null,
    val dateText: String = FormatDate.formatDateFromLong(FormatDate.getCurrentEpochMillis()),
    val isDatePickerVisible: Boolean = false,
    val showSuccessDialog: Boolean = false,
    val screenState: ScreenState<Unit> = ScreenState.Content(Unit, DataFreshness.FRESH),
    val submitState: SubmitState<Unit> = SubmitState.Idle,
)

sealed interface ActivateSavingEvent {
    data class NavigateBackWithUpdateData(val savingsId: Long) : ActivateSavingEvent
    data object NavigateBack : ActivateSavingEvent
}

sealed interface ActivateSavingAction {
    data object OnBackClick : ActivateSavingAction
    data class OnDateSelected(val millis: Long?) : ActivateSavingAction
    data class OnDatePickerToggle(val visible: Boolean) : ActivateSavingAction
    data object ActivateSaving : ActivateSavingAction
    data object DismissSuccessDialog : ActivateSavingAction
    data object Retry : ActivateSavingAction
    data object DismissErrorState : ActivateSavingAction
}
