/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See See https://github.com/openMF/kmp-project-template/blob/main/LICENSE
 */
package org.mifos.feature.loan.rejectLoan

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

class RejectLoanViewModel(
    savedStateHandle: SavedStateHandle,
    private val loansRepository: LoansRepository,
) : BaseViewModel<RejectLoanState, RejectLoanEvent, RejectLoanAction>(
    RejectLoanState(
        loanId = savedStateHandle.toRoute<RejectLoanRoute>().loanId,
    ),
) {
    private val route = savedStateHandle.toRoute<RejectLoanRoute>()

    override fun handleAction(action: RejectLoanAction) {
        when (action) {
            RejectLoanAction.OnBackClick -> sendEvent(RejectLoanEvent.NavigateBack)
            is RejectLoanAction.OnRejectedDateSelected -> handleRejectedDateSelected(action.millis)
            is RejectLoanAction.OnRejectedDatePickerToggle -> {
                mutableStateFlow.update { it.copy(isRejectedDatePickerVisible = action.visible) }
            }
            is RejectLoanAction.OnNoteChange -> {
                mutableStateFlow.update { it.copy(noteText = action.note) }
            }
            RejectLoanAction.RejectLoan -> rejectLoan()
            RejectLoanAction.DismissSuccessDialog -> {
                mutableStateFlow.update { it.copy(showSuccessDialog = false) }
                sendEvent(RejectLoanEvent.NavigateBackWithUpdateData(route.loanId))
            }
            RejectLoanAction.Retry -> {
                mutableStateFlow.update {
                    it.copy(
                        screenState = ScreenState.Content(Unit, DataFreshness.FRESH),
                        submitState = SubmitState.Idle,
                    )
                }
            }
            RejectLoanAction.DismissErrorState -> {
                mutableStateFlow.update { it.copy(submitState = SubmitState.Idle) }
            }
        }
    }

    private fun handleRejectedDateSelected(millis: Long?) {
        if (millis != null) {
            val formattedDate = FormatDate.formatDateFromLong(millis)
            mutableStateFlow.update {
                it.copy(
                    selectedRejectedDateMillis = millis,
                    rejectedDateText = formattedDate,
                    isRejectedDatePickerVisible = false,
                )
            }
        } else {
            mutableStateFlow.update { it.copy(isRejectedDatePickerVisible = false) }
        }
    }

    private fun rejectLoan() {
        val rejectedDate = state.rejectedDateText
        if (rejectedDate.isBlank()) return

        mutableStateFlow.update { it.copy(submitState = SubmitState.Submitting()) }
        viewModelScope.launch {
            val result = loansRepository.rejectLoan(
                loanId = route.loanId,
                rejectedOnDate = rejectedDate,
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

data class RejectLoanState(
    val loanId: Long,
    val selectedRejectedDateMillis: Long? = null,
    val rejectedDateText: String = FormatDate.formatDateFromLong(FormatDate.getCurrentEpochMillis()),
    val isRejectedDatePickerVisible: Boolean = false,
    val noteText: String = "",
    val showSuccessDialog: Boolean = false,
    val screenState: ScreenState<Unit> = ScreenState.Content(Unit, DataFreshness.FRESH),
    val submitState: SubmitState<Unit> = SubmitState.Idle,
)

sealed interface RejectLoanEvent {
    data class NavigateBackWithUpdateData(val loanId: Long) : RejectLoanEvent
    data object NavigateBack : RejectLoanEvent
}

sealed interface RejectLoanAction {
    data object OnBackClick : RejectLoanAction
    data class OnRejectedDateSelected(val millis: Long?) : RejectLoanAction
    data class OnRejectedDatePickerToggle(val visible: Boolean) : RejectLoanAction
    data class OnNoteChange(val note: String) : RejectLoanAction
    data object RejectLoan : RejectLoanAction
    data object DismissSuccessDialog : RejectLoanAction
    data object Retry : RejectLoanAction
    data object DismissErrorState : RejectLoanAction
}
