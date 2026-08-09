/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See See https://github.com/openMF/kmp-project-template/blob/main/LICENSE
 */
package org.mifos.feature.loan.approveLoan

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

class ApproveLoanViewModel(
    savedStateHandle: SavedStateHandle,
    private val loansRepository: LoansRepository,
) : BaseViewModel<ApproveLoanState, ApproveLoanEvent, ApproveLoanAction>(
    ApproveLoanState(
        loanId = savedStateHandle.toRoute<ApproveLoanRoute>().loanId,
    ),
) {
    private val route = savedStateHandle.toRoute<ApproveLoanRoute>()

    override fun handleAction(action: ApproveLoanAction) {
        when (action) {
            ApproveLoanAction.OnBackClick -> sendEvent(ApproveLoanEvent.NavigateBack)
            is ApproveLoanAction.OnApprovedDateSelected -> handleApprovedDateSelected(action.millis)
            is ApproveLoanAction.OnDisbursementDateSelected -> handleDisbursementDateSelected(action.millis)
            is ApproveLoanAction.OnApprovedDatePickerToggle -> {
                mutableStateFlow.update { it.copy(isApprovedDatePickerVisible = action.visible) }
            }
            is ApproveLoanAction.OnDisbursementDatePickerToggle -> {
                mutableStateFlow.update { it.copy(isDisbursementDatePickerVisible = action.visible) }
            }
            is ApproveLoanAction.OnNoteChange -> {
                mutableStateFlow.update { it.copy(noteText = action.note) }
            }
            ApproveLoanAction.ApproveLoan -> approveLoan()
            ApproveLoanAction.DismissSuccessDialog -> {
                mutableStateFlow.update { it.copy(showSuccessDialog = false) }
                sendEvent(ApproveLoanEvent.NavigateBackWithUpdateData(route.loanId))
            }
            ApproveLoanAction.Retry -> {
                mutableStateFlow.update {
                    it.copy(
                        screenState = ScreenState.Content(Unit, DataFreshness.FRESH),
                        submitState = SubmitState.Idle,
                    )
                }
            }
            ApproveLoanAction.DismissErrorState -> {
                mutableStateFlow.update { it.copy(submitState = SubmitState.Idle) }
            }
        }
    }

    private fun handleApprovedDateSelected(millis: Long?) {
        if (millis != null) {
            val formattedDate = FormatDate.formatDateFromLong(millis)
            mutableStateFlow.update {
                it.copy(
                    selectedApprovedDateMillis = millis,
                    approvedDateText = formattedDate,
                    isApprovedDatePickerVisible = false,
                )
            }
        } else {
            mutableStateFlow.update { it.copy(isApprovedDatePickerVisible = false) }
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

    private fun approveLoan() {
        val approvedDate = state.approvedDateText
        if (approvedDate.isBlank()) return

        mutableStateFlow.update { it.copy(submitState = SubmitState.Submitting()) }
        viewModelScope.launch {
            val result = loansRepository.approveLoan(
                loanId = route.loanId,
                approvedOnDate = approvedDate,
                expectedDisbursementDate = state.disbursementDateText.takeIf { it.isNotBlank() },
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

data class ApproveLoanState(
    val loanId: Long,
    val selectedApprovedDateMillis: Long? = null,
    val approvedDateText: String = FormatDate.formatDateFromLong(FormatDate.getCurrentEpochMillis()),
    val isApprovedDatePickerVisible: Boolean = false,
    val selectedDisbursementDateMillis: Long? = null,
    val disbursementDateText: String = "",
    val isDisbursementDatePickerVisible: Boolean = false,
    val noteText: String = "",
    val showSuccessDialog: Boolean = false,
    val screenState: ScreenState<Unit> = ScreenState.Content(Unit, DataFreshness.FRESH),
    val submitState: SubmitState<Unit> = SubmitState.Idle,
)

sealed interface ApproveLoanEvent {
    data class NavigateBackWithUpdateData(val loanId: Long) : ApproveLoanEvent
    data object NavigateBack : ApproveLoanEvent
}

sealed interface ApproveLoanAction {
    data object OnBackClick : ApproveLoanAction
    data class OnApprovedDateSelected(val millis: Long?) : ApproveLoanAction
    data class OnDisbursementDateSelected(val millis: Long?) : ApproveLoanAction
    data class OnApprovedDatePickerToggle(val visible: Boolean) : ApproveLoanAction
    data class OnDisbursementDatePickerToggle(val visible: Boolean) : ApproveLoanAction
    data class OnNoteChange(val note: String) : ApproveLoanAction
    data object ApproveLoan : ApproveLoanAction
    data object DismissSuccessDialog : ApproveLoanAction
    data object Retry : ApproveLoanAction
    data object DismissErrorState : ApproveLoanAction
}
