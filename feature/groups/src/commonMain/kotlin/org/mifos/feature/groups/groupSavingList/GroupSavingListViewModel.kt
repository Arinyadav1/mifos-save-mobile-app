/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See See https://github.com/openMF/kmp-project-template/blob/main/LICENSE
 */
package org.mifos.feature.groups.groupSavingList

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.mifos.core.base.store.screen.ScreenState
import org.mifos.core.base.ui.viewmodel.BaseViewModel
import org.mifos.core.common.Constants
import org.mifos.core.data.group.GroupRepository
import org.mifos.core.model.group.SavingsAccount

class GroupSavingListViewModel(
    savedStateHandle: SavedStateHandle,
    private val groupRepository: GroupRepository,
) : BaseViewModel<GroupSavingListState, GroupSavingListEvent, GroupSavingListAction>(
    GroupSavingListState(),
) {
    private val route = savedStateHandle.toRoute<GroupSavingListRoute>()
    val groupId = route.groupId

    private val searchQuery = MutableStateFlow("")

    init {
        loadGroupSavings()
    }

    private fun loadGroupSavings() {
        mutableStateFlow.update {
            it.copy(screenState = ScreenState.Loading)
        }
        viewModelScope.launch {
            combine(
                groupRepository.getGroupAccounts(groupId, Constants.SAVINGS_ACCOUNTS),
                searchQuery,
            ) { groupAccountsState, query ->
                when (groupAccountsState) {
                    is ScreenState.Content -> {
                        val savings = groupAccountsState.data.savingsAccounts
                        val filteredSavings = if (query.isBlank()) {
                            savings
                        } else {
                            savings.filter {
                                it.productName.orEmpty().contains(query, ignoreCase = true) ||
                                    it.accountNo.orEmpty().contains(query, ignoreCase = true) ||
                                    it.status?.value.orEmpty().contains(query, ignoreCase = true)
                            }
                        }

                        if (filteredSavings.isEmpty()) {
                            ScreenState.Empty
                        } else {
                            ScreenState.Content(
                                data = filteredSavings,
                                freshness = groupAccountsState.freshness,
                                fetchedAt = groupAccountsState.fetchedAt,
                                freshnessSignal = groupAccountsState.freshnessSignal,
                            )
                        }
                    }
                    is ScreenState.Loading -> ScreenState.Loading
                    is ScreenState.Empty -> ScreenState.Empty
                    is ScreenState.NoNetwork -> ScreenState.NoNetwork(groupAccountsState.isCaptivePortal)
                    is ScreenState.Error -> ScreenState.Error(
                        groupAccountsState.error,
                        groupAccountsState.isNetworkError,
                    )
                    is ScreenState.Unauthenticated -> ScreenState.Unauthenticated
                }
            }.collect { mappedScreenState ->
                mutableStateFlow.update {
                    it.copy(screenState = mappedScreenState)
                }
            }
        }
    }

    override fun handleAction(action: GroupSavingListAction) {
        when (action) {
            GroupSavingListAction.OnBackClick -> {
                sendEvent(GroupSavingListEvent.NavigateBack)
            }
            GroupSavingListAction.Retry -> {
                loadGroupSavings()
            }
            is GroupSavingListAction.SearchQueryChanged -> {
                searchQuery.value = action.query
                mutableStateFlow.update {
                    it.copy(searchQuery = action.query)
                }
            }
            GroupSavingListAction.OnNewSavingsClick -> {
                sendEvent(GroupSavingListEvent.NavigateToNewSavings)
            }
            GroupSavingListAction.OnFilterClick -> {
            }
            is GroupSavingListAction.OnSavingAccountClick -> {
                // Savings account clicked
            }
        }
    }
}

data class GroupSavingListState(
    val screenState: ScreenState<List<SavingsAccount>> = ScreenState.Loading,
    val searchQuery: String = "",
)

sealed interface GroupSavingListEvent {
    data object NavigateBack : GroupSavingListEvent
    data object NavigateToNewSavings : GroupSavingListEvent
}

sealed interface GroupSavingListAction {
    data object OnBackClick : GroupSavingListAction
    data object Retry : GroupSavingListAction
    data class SearchQueryChanged(val query: String) : GroupSavingListAction
    data object OnNewSavingsClick : GroupSavingListAction
    data object OnFilterClick : GroupSavingListAction
    data class OnSavingAccountClick(val accountId: Long) : GroupSavingListAction
}
