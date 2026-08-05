/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See See https://github.com/openMF/kmp-project-template/blob/main/LICENSE
 */
package org.mifos.feature.saving.savingDetails

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.mifos.core.base.store.screen.ScreenState
import org.mifos.core.base.ui.viewmodel.BaseViewModel
import org.mifos.core.data.savings.SavingsRepository
import org.mifos.core.model.savings.SavingDetail

class SavingDetailsViewModel(
    savedStateHandle: SavedStateHandle,
    private val savingsRepository: SavingsRepository,
) : BaseViewModel<SavingDetailsState, SavingDetailsEvent, SavingDetailsAction>(
    SavingDetailsState(),
) {
    private val route = savedStateHandle.toRoute<SavingDetailsRoute>()
    val accountId = route.accountId

    init {
        loadSavingDetails()
    }

    private fun loadSavingDetails() {
        mutableStateFlow.update {
            it.copy(screenState = ScreenState.Loading)
        }
        viewModelScope.launch {
            savingsRepository.getSavingDetails(accountId)
                .collect { screenState ->
                    mutableStateFlow.update {
                        it.copy(screenState = screenState)
                    }
                }
        }
    }

    override fun handleAction(action: SavingDetailsAction) {
        when (action) {
            SavingDetailsAction.OnBackClick -> {
                sendEvent(SavingDetailsEvent.NavigateBack)
            }
            SavingDetailsAction.Retry -> {
                loadSavingDetails()
            }
            is SavingDetailsAction.SetMenuVisible -> {
                mutableStateFlow.update {
                    it.copy(showMenu = action.visible)
                }
            }
            SavingDetailsAction.OnCloseSavingsClick -> {
                sendEvent(SavingDetailsEvent.NavigateToCloseSavings)
            }
            SavingDetailsAction.OnActivateSavingsClick -> {
                sendEvent(SavingDetailsEvent.NavigateToActivateSavings)
            }
            SavingDetailsAction.OnApproveSavingsClick -> {
                sendEvent(SavingDetailsEvent.NavigateToApproveSavings)
            }
            SavingDetailsAction.OnUpdateSavingsClick -> {
                sendEvent(SavingDetailsEvent.NavigateToUpdateSavings)
            }
            SavingDetailsAction.OnGeneralClick -> {
                sendEvent(SavingDetailsEvent.NavigateToGeneral)
            }
            SavingDetailsAction.OnTransactionsClick -> {
                sendEvent(SavingDetailsEvent.NavigateToTransactions)
            }
            is SavingDetailsAction.OnSavingTransactionClick -> {
                sendEvent(SavingDetailsEvent.NavigateToSavingTransaction(action.isWithdrawal))
            }
        }
    }
}

data class SavingDetailsState(
    val screenState: ScreenState<SavingDetail> = ScreenState.Loading,
    val showMenu: Boolean = false,
)

sealed interface SavingDetailsEvent {
    data object NavigateBack : SavingDetailsEvent
    data object NavigateToCloseSavings : SavingDetailsEvent
    data object NavigateToActivateSavings : SavingDetailsEvent
    data object NavigateToApproveSavings : SavingDetailsEvent
    data object NavigateToUpdateSavings : SavingDetailsEvent
    data object NavigateToGeneral : SavingDetailsEvent
    data object NavigateToTransactions : SavingDetailsEvent
    data class NavigateToSavingTransaction(val isWithdrawal: Boolean) : SavingDetailsEvent
}

sealed interface SavingDetailsAction {
    data object OnBackClick : SavingDetailsAction
    data object Retry : SavingDetailsAction
    data class SetMenuVisible(val visible: Boolean) : SavingDetailsAction
    data object OnCloseSavingsClick : SavingDetailsAction
    data object OnActivateSavingsClick : SavingDetailsAction
    data object OnApproveSavingsClick : SavingDetailsAction
    data object OnUpdateSavingsClick : SavingDetailsAction
    data object OnGeneralClick : SavingDetailsAction
    data object OnTransactionsClick : SavingDetailsAction
    data class OnSavingTransactionClick(val isWithdrawal: Boolean) : SavingDetailsAction
}
