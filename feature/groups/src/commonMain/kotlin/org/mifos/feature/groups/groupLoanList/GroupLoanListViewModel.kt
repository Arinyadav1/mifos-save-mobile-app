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
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
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
            val loanAccountsFlow = flow {
                emit(ScreenState.Loading)
                groupRepository.getGroupDetails(groupId).collect { groupState ->
                    emit(processGroupDetails(groupState))
                }
            }

            val searchQueryFlow = mutableStateFlow
                .map { it.searchQuery }
                .distinctUntilChanged()

            combine(
                loanAccountsFlow,
                searchQueryFlow,
            ) { loanAccountsState, query ->
                filterLoanAccounts(loanAccountsState, query)
            }.collect { mappedScreenState ->
                mutableStateFlow.update {
                    it.copy(screenState = mappedScreenState)
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

    private fun filterLoanAccounts(
        loanAccountsState: ScreenState<List<LoanAccount>>,
        query: String,
    ): ScreenState<List<LoanAccount>> {
        return when (loanAccountsState) {
            is ScreenState.Content -> {
                val loans = loanAccountsState.data
                val filteredLoans = if (query.isBlank()) {
                    loans
                } else {
                    loans.filter {
                        it.productName.orEmpty().contains(query, ignoreCase = true) ||
                            it.accountNo.orEmpty().contains(query, ignoreCase = true) ||
                            it.status?.value.orEmpty().contains(query, ignoreCase = true)
                    }
                }

                if (filteredLoans.isEmpty()) {
                    ScreenState.Empty
                } else {
                    ScreenState.Content(
                        data = filteredLoans,
                        freshness = loanAccountsState.freshness,
                        fetchedAt = loanAccountsState.fetchedAt,
                        freshnessSignal = loanAccountsState.freshnessSignal,
                    )
                }
            }
            else -> loanAccountsState
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
            }
            is GroupLoanListAction.OnLoanAccountClick -> {
                // Loan account clicked
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
)

sealed interface GroupLoanListEvent {
    data object NavigateBack : GroupLoanListEvent
}

sealed interface GroupLoanListAction {
    data object OnBackClick : GroupLoanListAction
    data object Retry : GroupLoanListAction
    data class SearchQueryChanged(val query: String) : GroupLoanListAction
    data object OnFilterClick : GroupLoanListAction
    data class OnLoanAccountClick(val accountId: Long) : GroupLoanListAction
}
