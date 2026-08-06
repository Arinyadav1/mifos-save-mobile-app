/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See See https://github.com/openMF/kmp-project-template/blob/main/LICENSE
 */
package org.mifos.feature.saving.transactionDetails

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.mifos.core.base.store.screen.ScreenState
import org.mifos.core.base.ui.viewmodel.BaseViewModel
import org.mifos.core.data.savings.SavingsRepository
import org.mifos.core.model.savings.SavingDetailTransaction

class TransactionDetailsViewModel(
    savedStateHandle: SavedStateHandle,
    private val savingsRepository: SavingsRepository,
) : BaseViewModel<TransactionDetailsState, TransactionDetailsEvent, TransactionDetailsAction>(
    TransactionDetailsState(),
) {
    private val route = savedStateHandle.toRoute<TransactionDetailsRoute>()
    val accountId = route.accountId
    val transactionId = route.transactionId

    init {
        loadTransactionDetails()
    }

    private fun loadTransactionDetails() {
        mutableStateFlow.update {
            it.copy(screenState = ScreenState.Loading)
        }
        viewModelScope.launch {
            savingsRepository.getSavingTransaction(accountId, transactionId)
                .collect { screenState ->
                    mutableStateFlow.update {
                        it.copy(screenState = screenState)
                    }
                }
        }
    }

    override fun handleAction(action: TransactionDetailsAction) {
        when (action) {
            TransactionDetailsAction.OnBackClick -> {
                sendEvent(TransactionDetailsEvent.NavigateBack)
            }
            TransactionDetailsAction.Retry -> {
                loadTransactionDetails()
            }
        }
    }
}

data class TransactionDetailsState(
    val screenState: ScreenState<SavingDetailTransaction> = ScreenState.Loading,
)

sealed interface TransactionDetailsEvent {
    data object NavigateBack : TransactionDetailsEvent
}

sealed interface TransactionDetailsAction {
    data object OnBackClick : TransactionDetailsAction
    data object Retry : TransactionDetailsAction
}
