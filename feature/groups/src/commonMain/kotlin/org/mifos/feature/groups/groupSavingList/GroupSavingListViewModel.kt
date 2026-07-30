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
import org.mifos.core.model.group.GroupAccounts
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
    private val selectedStatuses = MutableStateFlow<List<String>>(emptyList())
    private val sortType = MutableStateFlow<SavingSortType?>(null)

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
                selectedStatuses,
                sortType,
            ) { groupAccountsState, query, statuses, sort ->
                mapGroupAccountsState(groupAccountsState, query, statuses, sort)
            }.collect { (mappedScreenState, availableStatusesList) ->
                mutableStateFlow.update {
                    it.copy(
                        screenState = mappedScreenState,
                        availableStatuses = availableStatusesList,
                    )
                }
            }
        }
    }

    private fun mapGroupAccountsState(
        groupAccountsState: ScreenState<GroupAccounts>,
        query: String,
        statuses: List<String>,
        sort: SavingSortType?,
    ): Pair<ScreenState<List<SavingsAccount>>, List<String>> {
        val availableStatusesList = when (groupAccountsState) {
            is ScreenState.Content -> {
                groupAccountsState.data.savingsAccounts
                    .mapNotNull { it.status?.value }
                    .distinct()
                    .sorted()
            }
            else -> emptyList()
        }

        val mappedScreenState = when (groupAccountsState) {
            is ScreenState.Content -> {
                val savings = groupAccountsState.data.savingsAccounts
                val filteredSavings = getFilteredAndSortedSavings(savings, query, statuses, sort)

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

        return mappedScreenState to availableStatusesList
    }

    private fun getFilteredAndSortedSavings(
        savings: List<SavingsAccount>,
        query: String,
        statuses: List<String>,
        sort: SavingSortType?,
    ): List<SavingsAccount> {
        return savings.filter { saving ->
            val searchMatch = query.isBlank() ||
                saving.productName.orEmpty().contains(query, ignoreCase = true) ||
                saving.accountNo.orEmpty().contains(query, ignoreCase = true) ||
                saving.status?.value.orEmpty().contains(query, ignoreCase = true)
            val statusMatch = statuses.isEmpty() ||
                saving.status?.value in statuses
            searchMatch && statusMatch
        }.let { list ->
            when (sort) {
                SavingSortType.PRODUCT_NAME -> list.sortedBy { it.productName?.lowercase() }
                SavingSortType.ACCOUNT_NUMBER -> list.sortedBy { it.accountNo }
                else -> list
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
                mutableStateFlow.update {
                    it.copy(isFilterVisible = !it.isFilterVisible)
                }
            }
            is GroupSavingListAction.OnSavingAccountClick -> {
                // Savings account clicked
            }
            is GroupSavingListAction.HandleFilterClick -> {
                val current = selectedStatuses.value
                val next = if (action.filterType == SavingFilterType.STATUS) {
                    if (action.filterValue in current) {
                        current - action.filterValue
                    } else {
                        current + action.filterValue
                    }
                } else {
                    current
                }
                selectedStatuses.value = next
                mutableStateFlow.update {
                    it.copy(selectedStatuses = next)
                }
            }
            is GroupSavingListAction.HandleSortClick -> {
                sortType.value = action.sort
                mutableStateFlow.update {
                    it.copy(sortType = action.sort)
                }
            }
            GroupSavingListAction.ClearFilters -> {
                selectedStatuses.value = emptyList()
                sortType.value = null
                mutableStateFlow.update {
                    it.copy(
                        selectedStatuses = emptyList(),
                        sortType = null,
                    )
                }
            }
        }
    }
}

data class GroupSavingListState(
    val screenState: ScreenState<List<SavingsAccount>> = ScreenState.Loading,
    val searchQuery: String = "",
    val isFilterVisible: Boolean = false,
    val selectedStatuses: List<String> = emptyList(),
    val availableStatuses: List<String> = emptyList(),
    val sortType: SavingSortType? = null,
)

enum class SavingSortType(val value: String) {
    PRODUCT_NAME("Product Name"),
    ACCOUNT_NUMBER("Account Number"),
}

enum class SavingFilterType(val value: String) {
    STATUS("Status"),
}

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
    data class HandleFilterClick(val filterValue: String, val filterType: SavingFilterType) : GroupSavingListAction
    data class HandleSortClick(val sort: SavingSortType?) : GroupSavingListAction
    data object ClearFilters : GroupSavingListAction
}
