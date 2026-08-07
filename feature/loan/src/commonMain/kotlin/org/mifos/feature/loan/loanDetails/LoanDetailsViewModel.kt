/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See See https://github.com/openMF/kmp-project-template/blob/main/LICENSE
 */
package org.mifos.feature.loan.loanDetails

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.mifos.core.base.store.screen.ScreenState
import org.mifos.core.base.ui.viewmodel.BaseViewModel
import org.mifos.core.data.loans.LoansRepository
import org.mifos.core.model.loans.LoanDetail

class LoanDetailsViewModel(
    savedStateHandle: SavedStateHandle,
    private val loansRepository: LoansRepository,
) : BaseViewModel<LoanDetailsState, LoanDetailsEvent, LoanDetailsAction>(
    LoanDetailsState(),
) {
    private val route = savedStateHandle.toRoute<LoanDetailsRoute>()
    val loanId = route.loanId

    init {
        loadLoanDetails()
    }

    private fun loadLoanDetails() {
        mutableStateFlow.update {
            it.copy(screenState = ScreenState.Loading)
        }
        viewModelScope.launch {
            loansRepository.getLoanDetails(loanId)
                .collect { screenState ->
                    mutableStateFlow.update {
                        it.copy(screenState = screenState)
                    }
                }
        }
    }

    override fun handleAction(action: LoanDetailsAction) {
        when (action) {
            LoanDetailsAction.OnBackClick -> {
                sendEvent(LoanDetailsEvent.NavigateBack)
            }
            LoanDetailsAction.Retry -> {
                loadLoanDetails()
            }
            is LoanDetailsAction.SetMenuVisible -> {
                mutableStateFlow.update {
                    it.copy(showMenu = action.visible)
                }
            }
            LoanDetailsAction.OnGeneralClick -> {
                sendEvent(LoanDetailsEvent.NavigateToGeneral)
            }
            LoanDetailsAction.OnTransactionsClick -> {
                sendEvent(LoanDetailsEvent.NavigateToTransactions)
            }
            LoanDetailsAction.OnRepaymentScheduleClick -> {
                sendEvent(LoanDetailsEvent.NavigateToRepaymentSchedule)
            }
            LoanDetailsAction.OnLoanDisbursementClick -> {
                sendEvent(LoanDetailsEvent.NavigateToLoanDisbursement)
            }
            LoanDetailsAction.OnRejectLoanClick -> {
                sendEvent(LoanDetailsEvent.NavigateToRejectLoan)
            }
            LoanDetailsAction.OnApproveLoanClick -> {
                sendEvent(LoanDetailsEvent.NavigateToApproveLoan)
            }
            LoanDetailsAction.OnUndoApprovalLoanClick -> {
                sendEvent(LoanDetailsEvent.NavigateToUndoApprovalLoan)
            }
        }
    }
}

data class LoanDetailsState(
    val screenState: ScreenState<LoanDetail> = ScreenState.Loading,
    val showMenu: Boolean = false,
)

sealed interface LoanDetailsEvent {
    data object NavigateBack : LoanDetailsEvent
    data object NavigateToGeneral : LoanDetailsEvent
    data object NavigateToTransactions : LoanDetailsEvent
    data object NavigateToRepaymentSchedule : LoanDetailsEvent
    data object NavigateToLoanDisbursement : LoanDetailsEvent
    data object NavigateToRejectLoan : LoanDetailsEvent
    data object NavigateToApproveLoan : LoanDetailsEvent
    data object NavigateToUndoApprovalLoan : LoanDetailsEvent
}

sealed interface LoanDetailsAction {
    data object OnBackClick : LoanDetailsAction
    data object Retry : LoanDetailsAction
    data class SetMenuVisible(val visible: Boolean) : LoanDetailsAction
    data object OnGeneralClick : LoanDetailsAction
    data object OnTransactionsClick : LoanDetailsAction
    data object OnRepaymentScheduleClick : LoanDetailsAction
    data object OnLoanDisbursementClick : LoanDetailsAction
    data object OnRejectLoanClick : LoanDetailsAction
    data object OnApproveLoanClick : LoanDetailsAction
    data object OnUndoApprovalLoanClick : LoanDetailsAction
}
