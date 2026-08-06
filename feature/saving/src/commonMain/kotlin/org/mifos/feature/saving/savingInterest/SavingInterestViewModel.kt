/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See See https://github.com/openMF/kmp-project-template/blob/main/LICENSE
 */
package org.mifos.feature.saving.savingInterest

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.mifos.core.base.store.screen.ScreenState
import org.mifos.core.base.ui.viewmodel.BaseViewModel
import org.mifos.core.data.savings.SavingsRepository
import org.mifos.core.model.savings.SavingInterestDetail

class SavingInterestViewModel(
    savedStateHandle: SavedStateHandle,
    private val savingsRepository: SavingsRepository,
) : BaseViewModel<SavingInterestState, SavingInterestEvent, SavingInterestAction>(
    SavingInterestState(),
) {
    private val route = savedStateHandle.toRoute<SavingInterestRoute>()
    val accountId = route.accountId

    init {
        loadSavingInterestDetails()
    }

    private fun loadSavingInterestDetails() {
        mutableStateFlow.update {
            it.copy(screenState = ScreenState.Loading)
        }
        viewModelScope.launch {
            savingsRepository.calculateSavingInterest(accountId)
                .collect { screenState ->
                    mutableStateFlow.update {
                        it.copy(screenState = screenState)
                    }
                }
        }
    }

    override fun handleAction(action: SavingInterestAction) {
        when (action) {
            SavingInterestAction.OnBackClick -> {
                sendEvent(SavingInterestEvent.NavigateBack)
            }
            SavingInterestAction.Retry -> {
                loadSavingInterestDetails()
            }
        }
    }
}

data class SavingInterestState(
    val screenState: ScreenState<SavingInterestDetail> = ScreenState.Loading,
)

sealed interface SavingInterestEvent {
    data object NavigateBack : SavingInterestEvent
}

sealed interface SavingInterestAction {
    data object OnBackClick : SavingInterestAction
    data object Retry : SavingInterestAction
}
