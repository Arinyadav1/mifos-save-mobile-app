/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See See https://github.com/openMF/kmp-project-template/blob/main/LICENSE
 */
package org.mifos.feature.loan.transactionDetails

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.mifos.core.base.store.screen.ScreenState
import org.mifos.core.base.ui.viewmodel.BaseViewModel
import org.mifos.core.data.loans.LoansRepository
import org.mifos.core.model.loans.LoanTransaction

class LoanTransactionDetailsViewModel(
    savedStateHandle: SavedStateHandle,
    private val loansRepository: LoansRepository,
) : BaseViewModel<LoanTransactionDetailsState, LoanTransactionDetailsEvent, LoanTransactionDetailsAction>(
    LoanTransactionDetailsState(),
) {
    private val route = savedStateHandle.toRoute<LoanTransactionDetailsRoute>()
    val loanId = route.loanId
    val transactionId = route.transactionId

    init {
        loadTransactionDetails()
    }

    private fun loadTransactionDetails() {
        mutableStateFlow.update {
            it.copy(screenState = ScreenState.Loading)
        }
        viewModelScope.launch {
            loansRepository.getLoanTransactionDetails(loanId, transactionId)
                .collect { screenState ->
                    mutableStateFlow.update {
                        it.copy(screenState = screenState)
                    }
                }
        }
    }

    override fun handleAction(action: LoanTransactionDetailsAction) {
        when (action) {
            LoanTransactionDetailsAction.OnBackClick -> {
                sendEvent(LoanTransactionDetailsEvent.NavigateBack)
            }
            LoanTransactionDetailsAction.Retry -> {
                loadTransactionDetails()
            }
        }
    }
}

data class LoanTransactionDetailsState(
    val screenState: ScreenState<LoanTransaction> = ScreenState.Loading,
)

sealed interface LoanTransactionDetailsEvent {
    data object NavigateBack : LoanTransactionDetailsEvent
}

sealed interface LoanTransactionDetailsAction {
    data object OnBackClick : LoanTransactionDetailsAction
    data object Retry : LoanTransactionDetailsAction
}
