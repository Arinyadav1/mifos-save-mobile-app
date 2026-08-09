/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See See https://github.com/openMF/kmp-project-template/blob/main/LICENSE
 */
package org.mifos.feature.loan.transactionList

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.mifos.core.base.store.screen.ScreenState
import org.mifos.core.base.ui.viewmodel.BaseViewModel
import org.mifos.core.data.loans.LoansRepository
import org.mifos.core.model.loans.LoanDetail

class LoanTransactionListViewModel(
    savedStateHandle: SavedStateHandle,
    private val loansRepository: LoansRepository,
) : BaseViewModel<LoanTransactionListState, LoanTransactionListEvent, LoanTransactionListAction>(
    LoanTransactionListState(
        loanId = savedStateHandle.toRoute<LoanTransactionListRoute>().loanId,
    ),
) {
    private val route = savedStateHandle.toRoute<LoanTransactionListRoute>()

    init {
        loadTransactions()
    }

    private fun loadTransactions() {
        mutableStateFlow.update {
            it.copy(screenState = ScreenState.Loading)
        }
        viewModelScope.launch {
            loansRepository.getLoanDetails(route.loanId)
                .collect { result ->
                    mutableStateFlow.update { state ->
                        state.copy(screenState = result)
                    }
                }
        }
    }

    override fun handleAction(action: LoanTransactionListAction) {
        when (action) {
            LoanTransactionListAction.OnBackClick -> sendEvent(LoanTransactionListEvent.NavigateBack)
            LoanTransactionListAction.Retry -> loadTransactions()
            is LoanTransactionListAction.OnTransactionClick -> {
                sendEvent(LoanTransactionListEvent.NavigateToTransactionDetails(route.loanId, action.transactionId))
            }
        }
    }
}

data class LoanTransactionListState(
    val loanId: Long,
    val screenState: ScreenState<LoanDetail> = ScreenState.Loading,
)

sealed interface LoanTransactionListAction {
    data object OnBackClick : LoanTransactionListAction
    data object Retry : LoanTransactionListAction
    data class OnTransactionClick(val transactionId: Long) : LoanTransactionListAction
}

sealed interface LoanTransactionListEvent {
    data object NavigateBack : LoanTransactionListEvent
    data class NavigateToTransactionDetails(val loanId: Long, val transactionId: Long) : LoanTransactionListEvent
}
