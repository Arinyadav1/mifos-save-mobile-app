/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See See https://github.com/openMF/kmp-project-template/blob/main/LICENSE
 */
package org.mifos.feature.groups.groupLoanList

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.mifos.core.base.store.screen.ScreenState
import org.mifos.core.base.ui.viewmodel.BaseViewModel
import org.mifos.core.data.client.ClientRepository
import org.mifos.core.data.group.GroupRepository
import org.mifos.core.model.client.LoanAccount
import org.mifos.core.model.group.ClientMember
import org.mifos.core.model.group.Group

class GroupLoanListViewModel(
    savedStateHandle: SavedStateHandle,
    private val groupRepository: GroupRepository,
    private val clientRepository: ClientRepository,
) : BaseViewModel<GroupLoanListState, GroupLoanListEvent, GroupLoanListAction>(
    GroupLoanListState(),
) {
    private val route = savedStateHandle.toRoute<GroupLoanListRoute>()
    val groupId = route.groupId

    init {
        loadGroupLoans()
    }

    private fun loadGroupLoans() {
        mutableStateFlow.update {
            it.copy(screenState = ScreenState.Loading)
        }
        viewModelScope.launch {
            groupRepository.getGroupDetails(groupId).collect { groupState ->
                val loansState = processGroupDetails(groupState)
                if (loansState is ScreenState.Content) {
                    val loans = loansState.data
                    val statuses = loans.mapNotNull { it.status?.value }.distinct().sorted()
                    val products = loans.mapNotNull { it.productName }.distinct().sorted()
                    mutableStateFlow.update {
                        it.copy(
                            screenState = loansState,
                            availableStatuses = statuses,
                            availableProducts = products,
                        )
                    }
                } else {
                    mutableStateFlow.update {
                        it.copy(screenState = loansState)
                    }
                }
            }
        }
    }

    private suspend fun processGroupDetails(
        groupState: ScreenState<Group>,
    ): ScreenState<List<LoanAccount>> {
        return when (groupState) {
            is ScreenState.Content -> {
                val members = groupState.data.clientMembers.orEmpty()
                if (members.isEmpty()) {
                    ScreenState.Empty
                } else {
                    val result = fetchMembersLoans(members)
                    mapMemberLoansResultToScreenState(result, groupState)
                }
            }
            is ScreenState.Loading -> ScreenState.Loading
            is ScreenState.Empty -> ScreenState.Empty
            is ScreenState.NoNetwork -> ScreenState.NoNetwork(groupState.isCaptivePortal)
            is ScreenState.Error -> ScreenState.Error(groupState.error, groupState.isNetworkError)
            is ScreenState.Unauthenticated -> ScreenState.Unauthenticated
        }
    }

    private suspend fun fetchMembersLoans(
        members: List<ClientMember>,
    ): MemberLoansResult = coroutineScope {
        val deferred = members.map { member ->
            async {
                try {
                    clientRepository.getClientAccounts(member.id)
                        .first { it !is ScreenState.Loading }
                } catch (e: Exception) {
                    ScreenState.Error(e)
                }
            }
        }

        val results = deferred.awaitAll()
        val loanAccountsList = mutableListOf<LoanAccount>()
        var hasNetworkError = false
        var isCaptivePortal = false
        var errorThrowable: Throwable? = null

        for (result in results) {
            when (result) {
                is ScreenState.Content -> {
                    loanAccountsList.addAll(result.data.loanAccounts)
                }
                is ScreenState.NoNetwork -> {
                    hasNetworkError = true
                    isCaptivePortal = result.isCaptivePortal
                }
                is ScreenState.Error -> {
                    errorThrowable = result.error
                }
                else -> Unit
            }
        }

        MemberLoansResult(
            loanAccounts = loanAccountsList,
            hasNetworkError = hasNetworkError,
            isCaptivePortal = isCaptivePortal,
            errorThrowable = errorThrowable,
        )
    }

    private fun mapMemberLoansResultToScreenState(
        result: MemberLoansResult,
        groupState: ScreenState.Content<Group>,
    ): ScreenState<List<LoanAccount>> {
        return when {
            result.hasNetworkError -> {
                ScreenState.NoNetwork(result.isCaptivePortal)
            }
            result.errorThrowable != null && result.loanAccounts.isEmpty() -> {
                ScreenState.Error(result.errorThrowable)
            }
            result.loanAccounts.isEmpty() -> {
                ScreenState.Empty
            }
            else -> {
                ScreenState.Content(
                    data = result.loanAccounts,
                    freshness = groupState.freshness,
                    fetchedAt = groupState.fetchedAt,
                    freshnessSignal = groupState.freshnessSignal,
                )
            }
        }
    }

    override fun handleAction(action: GroupLoanListAction) {
        when (action) {
            GroupLoanListAction.OnBackClick -> {
                sendEvent(GroupLoanListEvent.NavigateBack)
            }
            GroupLoanListAction.Retry -> {
                loadGroupLoans()
            }
            is GroupLoanListAction.SearchQueryChanged -> {
                mutableStateFlow.update {
                    it.copy(searchQuery = action.query)
                }
            }
            GroupLoanListAction.OnFilterClick -> {
                mutableStateFlow.update {
                    it.copy(isFilterVisible = !it.isFilterVisible)
                }
            }
            is GroupLoanListAction.OnLoanAccountClick -> {
                sendEvent(GroupLoanListEvent.NavigateToLoanDetail(action.accountId))
            }
            is GroupLoanListAction.HandleFilterClick -> {
                mutableStateFlow.update { state ->
                    val newSelectedStatus = if (action.filterType == LoanFilterType.STATUS) {
                        if (action.filterValue in state.selectedStatuses) {
                            state.selectedStatuses - action.filterValue
                        } else {
                            state.selectedStatuses + action.filterValue
                        }
                    } else {
                        state.selectedStatuses
                    }
                    val newSelectedProducts = if (action.filterType == LoanFilterType.PRODUCT) {
                        if (action.filterValue in state.selectedProducts) {
                            state.selectedProducts - action.filterValue
                        } else {
                            state.selectedProducts + action.filterValue
                        }
                    } else {
                        state.selectedProducts
                    }
                    state.copy(
                        selectedStatuses = newSelectedStatus,
                        selectedProducts = newSelectedProducts,
                    )
                }
            }
            is GroupLoanListAction.HandleSortClick -> {
                mutableStateFlow.update { state ->
                    state.copy(sortType = action.sort)
                }
            }
            GroupLoanListAction.ClearFilters -> {
                mutableStateFlow.update { state ->
                    state.copy(
                        selectedStatuses = emptyList(),
                        selectedProducts = emptyList(),
                        sortType = null,
                    )
                }
            }
        }
    }

    private data class MemberLoansResult(
        val loanAccounts: List<LoanAccount>,
        val hasNetworkError: Boolean,
        val isCaptivePortal: Boolean,
        val errorThrowable: Throwable?,
    )
}

data class GroupLoanListState(
    val screenState: ScreenState<List<LoanAccount>> = ScreenState.Loading,
    val searchQuery: String = "",
    val isFilterVisible: Boolean = false,
    val selectedStatuses: List<String> = emptyList(),
    val selectedProducts: List<String> = emptyList(),
    val availableStatuses: List<String> = emptyList(),
    val availableProducts: List<String> = emptyList(),
    val sortType: LoanSortType? = null,
)

enum class LoanSortType(val value: String) {
    PRODUCT_NAME("Product Name"),
    ACCOUNT_NUMBER("Account Number"),
}

enum class LoanFilterType(val value: String) {
    STATUS("Status"),
    PRODUCT("Product"),
}

sealed interface GroupLoanListEvent {
    data object NavigateBack : GroupLoanListEvent
    data class NavigateToLoanDetail(val accountId: Long) : GroupLoanListEvent
}

sealed interface GroupLoanListAction {
    data object OnBackClick : GroupLoanListAction
    data object Retry : GroupLoanListAction
    data class SearchQueryChanged(val query: String) : GroupLoanListAction
    data object OnFilterClick : GroupLoanListAction
    data class OnLoanAccountClick(val accountId: Long) : GroupLoanListAction
    data class HandleFilterClick(val filterValue: String, val filterType: LoanFilterType) : GroupLoanListAction
    data class HandleSortClick(val sort: LoanSortType?) : GroupLoanListAction
    data object ClearFilters : GroupLoanListAction
}
