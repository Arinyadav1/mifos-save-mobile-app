/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See See https://github.com/openMF/kmp-project-template/blob/main/LICENSE
 */
package org.mifos.feature.saving.savingTransactionsHistory

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.mifos.core.base.store.screen.ScreenState
import org.mifos.core.base.ui.viewmodel.BaseViewModel
import org.mifos.core.data.savings.SavingsRepository
import org.mifos.core.model.savings.SavingDetail

class SavingTransactionsHistoryViewModel(
    savedStateHandle: SavedStateHandle,
    private val savingsRepository: SavingsRepository,
) : BaseViewModel<SavingTransactionsHistoryState, SavingTransactionsHistoryEvent, SavingTransactionsHistoryAction>(
    SavingTransactionsHistoryState(
        accountId = savedStateHandle.toRoute<SavingTransactionsHistoryRoute>().accountId,
    ),
) {
    private val route = savedStateHandle.toRoute<SavingTransactionsHistoryRoute>()

    init {
        loadTransactions()
    }

    private fun loadTransactions() {
        mutableStateFlow.update {
            it.copy(screenState = ScreenState.Loading)
        }
        viewModelScope.launch {
            savingsRepository.getSavingDetails(route.accountId)
                .collect { result ->
                    mutableStateFlow.update { state ->
                        state.copy(screenState = result)
                    }
                }
        }
    }

    override fun handleAction(action: SavingTransactionsHistoryAction) {
        when (action) {
            SavingTransactionsHistoryAction.OnBackClick -> sendEvent(SavingTransactionsHistoryEvent.NavigateBack)
            SavingTransactionsHistoryAction.Retry -> loadTransactions()
            is SavingTransactionsHistoryAction.OnTransactionClick -> {
                sendEvent(
                    SavingTransactionsHistoryEvent.NavigateToTransactionDetails(
                        accountId = route.accountId,
                        transactionId = action.transactionId,
                    ),
                )
            }
        }
    }
}

data class SavingTransactionsHistoryState(
    val accountId: Long,
    val screenState: ScreenState<SavingDetail> = ScreenState.Loading,
)

sealed interface SavingTransactionsHistoryAction {
    data object OnBackClick : SavingTransactionsHistoryAction
    data object Retry : SavingTransactionsHistoryAction
    data class OnTransactionClick(val transactionId: Long) : SavingTransactionsHistoryAction
}

sealed interface SavingTransactionsHistoryEvent {
    data object NavigateBack : SavingTransactionsHistoryEvent
    data class NavigateToTransactionDetails(
        val accountId: Long,
        val transactionId: Long,
    ) : SavingTransactionsHistoryEvent
}
