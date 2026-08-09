/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See See https://github.com/openMF/kmp-project-template/blob/main/LICENSE
 */
package org.mifos.feature.loan.disburseLoan

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
import org.mifos.core.data.loans.LoansRepository

class DisburseLoanViewModel(
    savedStateHandle: SavedStateHandle,
    private val loansRepository: LoansRepository,
) : BaseViewModel<DisburseLoanState, DisburseLoanEvent, DisburseLoanAction>(
    DisburseLoanState(
        loanId = savedStateHandle.toRoute<DisburseLoanRoute>().loanId,
    ),
) {
    private val route = savedStateHandle.toRoute<DisburseLoanRoute>()

    override fun handleAction(action: DisburseLoanAction) {
        when (action) {
            DisburseLoanAction.OnBackClick -> sendEvent(DisburseLoanEvent.NavigateBack)
            is DisburseLoanAction.OnDisbursementDateSelected -> handleDisbursementDateSelected(action.millis)
            is DisburseLoanAction.OnDisbursementDatePickerToggle -> {
                mutableStateFlow.update { it.copy(isDisbursementDatePickerVisible = action.visible) }
            }
            is DisburseLoanAction.OnNoteChange -> {
                mutableStateFlow.update { it.copy(noteText = action.note) }
            }
            DisburseLoanAction.DisburseLoan -> disburseLoan()
            DisburseLoanAction.DismissSuccessDialog -> {
                mutableStateFlow.update { it.copy(showSuccessDialog = false) }
                sendEvent(DisburseLoanEvent.NavigateBackWithUpdateData(route.loanId))
            }
            DisburseLoanAction.Retry -> {
                mutableStateFlow.update {
                    it.copy(
                        screenState = ScreenState.Content(Unit, DataFreshness.FRESH),
                        submitState = SubmitState.Idle,
                    )
                }
            }
            DisburseLoanAction.DismissErrorState -> {
                mutableStateFlow.update { it.copy(submitState = SubmitState.Idle) }
            }
        }
    }

    private fun handleDisbursementDateSelected(millis: Long?) {
        if (millis != null) {
            val formattedDate = FormatDate.formatDateFromLong(millis)
            mutableStateFlow.update {
                it.copy(
                    selectedDisbursementDateMillis = millis,
                    disbursementDateText = formattedDate,
                    isDisbursementDatePickerVisible = false,
                )
            }
        } else {
            mutableStateFlow.update { it.copy(isDisbursementDatePickerVisible = false) }
        }
    }

    private fun disburseLoan() {
        val disbursementDate = state.disbursementDateText
        if (disbursementDate.isBlank()) return

        mutableStateFlow.update { it.copy(submitState = SubmitState.Submitting()) }
        viewModelScope.launch {
            val result = loansRepository.disburseLoan(
                loanId = route.loanId,
                actualDisbursementDate = disbursementDate,
                note = state.noteText.takeIf { it.isNotBlank() },
                dateFormat = Constants.DATE_FORMAT_SHORT_MONTH,
                locale = Constants.LOCALE_EN,
            )
            when (result) {
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

data class DisburseLoanState(
    val loanId: Long,
    val selectedDisbursementDateMillis: Long? = null,
    val disbursementDateText: String = FormatDate.formatDateFromLong(FormatDate.getCurrentEpochMillis()),
    val isDisbursementDatePickerVisible: Boolean = false,
    val noteText: String = "",
    val showSuccessDialog: Boolean = false,
    val screenState: ScreenState<Unit> = ScreenState.Content(Unit, DataFreshness.FRESH),
    val submitState: SubmitState<Unit> = SubmitState.Idle,
)

sealed interface DisburseLoanEvent {
    data class NavigateBackWithUpdateData(val loanId: Long) : DisburseLoanEvent
    data object NavigateBack : DisburseLoanEvent
}

sealed interface DisburseLoanAction {
    data object OnBackClick : DisburseLoanAction
    data class OnDisbursementDateSelected(val millis: Long?) : DisburseLoanAction
    data class OnDisbursementDatePickerToggle(val visible: Boolean) : DisburseLoanAction
    data class OnNoteChange(val note: String) : DisburseLoanAction
    data object DisburseLoan : DisburseLoanAction
    data object DismissSuccessDialog : DisburseLoanAction
    data object Retry : DisburseLoanAction
    data object DismissErrorState : DisburseLoanAction
}
