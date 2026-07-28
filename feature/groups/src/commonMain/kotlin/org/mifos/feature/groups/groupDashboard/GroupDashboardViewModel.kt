/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See See https://github.com/openMF/kmp-project-template/blob/main/LICENSE
 */
package org.mifos.feature.groups.groupDashboard

import androidx.lifecycle.viewModelScope
import io.github.mobilebytelabs.kmptoolkit.networkmonitor.NetworkMonitor
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.mifos.core.base.store.infra.FetchedAtRepository
import org.mifos.core.base.store.paging.PageKey
import org.mifos.core.base.store.paging.PagingScreenStream
import org.mifos.core.base.store.paging.asPagingScreenStream
import org.mifos.core.base.store.screen.ScreenState
import org.mifos.core.base.ui.viewmodel.BaseViewModel
import org.mifos.core.data.group.GroupRepository
import org.mifos.core.model.group.Group

class GroupDashboardViewModel(
    private val groupRepository: GroupRepository,
    private val networkMonitor: NetworkMonitor,
    private val fetchedAtRepository: FetchedAtRepository,
) : BaseViewModel<GroupDashboardState, GroupDashboardEvent, GroupDashboardAction>(
    GroupDashboardState(),
) {

    init {
        listOfGroup()
    }

    fun listOfGroup() {
        val pagingStream =
            groupRepository.listOfGroupPaging().asPagingScreenStream(
                networkMonitor = networkMonitor,
                fetchedAtRepository = fetchedAtRepository,
                cacheKey = "groups:list",
                scope = viewModelScope,
                pageSize = PageKey.DEFAULT_PAGE_SIZE,
            )

        mutableStateFlow.update {
            it.copy(
                pagingStream = pagingStream,
            )
        }

        viewModelScope.launch {
            pagingStream.state.collect { screenState ->
                if (screenState is ScreenState.Content) {
                    val groups = screenState.data
                    val offices = groups.mapNotNull { it.officeName }.distinct().sorted()
                    val statuses = groups.mapNotNull { it.status?.value }.distinct().sorted()
                    mutableStateFlow.update {
                        it.copy(
                            availableOffices = offices,
                            availableStatuses = statuses,
                        )
                    }
                }
            }
        }
    }

    override fun handleAction(action: GroupDashboardAction) {
        when (action) {
            is GroupDashboardAction.SearchQueryChanged -> {
                mutableStateFlow.update {
                    it.copy(searchQuery = action.query)
                }
            }

            GroupDashboardAction.OnBackClick -> {
                sendEvent(GroupDashboardEvent.NavigateBack)
            }

            GroupDashboardAction.OnNewGroupClick -> {
                sendEvent(GroupDashboardEvent.NavigateToNewGroup)
            }

            GroupDashboardAction.OnFilterClick -> {
                mutableStateFlow.update {
                    it.copy(isFilterVisible = !it.isFilterVisible)
                }
            }

            is GroupDashboardAction.OnGroupClick -> {
                sendEvent(GroupDashboardEvent.NavigateToGroupDetail(action.groupId))
            }

            GroupDashboardAction.Retry -> {
                state.pagingStream?.retry()
            }

            is GroupDashboardAction.HandleFilterClick -> {
                mutableStateFlow.update { state ->
                    val newSelectedStatus = if (action.filterType == GroupFilterType.STATUS) {
                        if (action.filterValue in state.selectedStatuses) {
                            state.selectedStatuses - action.filterValue
                        } else {
                            state.selectedStatuses + action.filterValue
                        }
                    } else {
                        state.selectedStatuses
                    }
                    val newSelectedOffices = if (action.filterType == GroupFilterType.OFFICE) {
                        if (action.filterValue in state.selectedOffices) {
                            state.selectedOffices - action.filterValue
                        } else {
                            state.selectedOffices + action.filterValue
                        }
                    } else {
                        state.selectedOffices
                    }
                    state.copy(
                        selectedStatuses = newSelectedStatus,
                        selectedOffices = newSelectedOffices,
                    )
                }
            }

            is GroupDashboardAction.HandleSortClick -> {
                mutableStateFlow.update { state ->
                    state.copy(sortType = action.sort)
                }
            }

            GroupDashboardAction.ClearFilters -> {
                mutableStateFlow.update { state ->
                    state.copy(
                        selectedStatuses = emptyList(),
                        selectedOffices = emptyList(),
                        sortType = null,
                    )
                }
            }
        }
    }
}

data class GroupDashboardState(
    val pagingStream: PagingScreenStream<Group>? = null,
    val searchQuery: String = "",
    val isFilterVisible: Boolean = false,
    val selectedStatuses: List<String> = emptyList(),
    val selectedOffices: List<String> = emptyList(),
    val availableStatuses: List<String> = emptyList(),
    val availableOffices: List<String> = emptyList(),
    val sortType: GroupSortType? = null,
)

enum class GroupSortType(val value: String) {
    NAME("Name"),
    ACCOUNT_NUMBER("Account Number"),
}

enum class GroupFilterType(val value: String) {
    STATUS("Status"),
    OFFICE("Office"),
}

sealed interface GroupDashboardEvent {
    data object NavigateBack : GroupDashboardEvent
    data object NavigateToNewGroup : GroupDashboardEvent
    data class NavigateToGroupDetail(val groupId: Long) : GroupDashboardEvent
}

sealed interface GroupDashboardAction {
    data class SearchQueryChanged(val query: String) : GroupDashboardAction
    data object OnBackClick : GroupDashboardAction
    data object OnNewGroupClick : GroupDashboardAction
    data object OnFilterClick : GroupDashboardAction
    data class OnGroupClick(val groupId: Long) : GroupDashboardAction
    data object Retry : GroupDashboardAction
    data class HandleFilterClick(val filterValue: String, val filterType: GroupFilterType) : GroupDashboardAction
    data class HandleSortClick(val sort: GroupSortType?) : GroupDashboardAction
    data object ClearFilters : GroupDashboardAction
}
