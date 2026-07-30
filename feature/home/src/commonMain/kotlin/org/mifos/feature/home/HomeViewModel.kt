/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See See https://github.com/openMF/kmp-project-template/blob/main/LICENSE
 */
package org.mifos.feature.home

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.mifos.core.base.store.screen.ScreenState
import org.mifos.core.base.ui.viewmodel.BaseViewModel
import org.mifos.core.common.formatHomeDate
import org.mifos.core.common.getGreeting
import org.mifos.core.data.group.GroupRepository
import org.mifos.core.data.user.UserDataRepository
import kotlin.time.Clock
import kotlin.time.Duration.Companion.milliseconds

class HomeViewModel(
    private val userDataRepository: UserDataRepository,
    private val groupRepository: GroupRepository,
) : BaseViewModel<HomeState, Nothing, HomeAction>(HomeState()) {

    init {
        val user = userDataRepository.userName.orEmpty()
        val cleanName = user.ifBlank { "User" }
        val avatar = cleanName.firstOrNull()?.toString()?.uppercase() ?: "U"

        // Initialize greeting & date immediately
        val nowInitial = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
        val greetingInitial = getGreeting(nowInitial.hour)
        val dateInitial = formatHomeDate(nowInitial)

        mutableStateFlow.update {
            it.copy(
                username = user,
                avatarText = avatar,
                greeting = greetingInitial,
                formattedDate = dateInitial,
            )
        }

        // Keep greeting & date updated in real time
        viewModelScope.launch {
            while (true) {
                delay(1000.milliseconds)
                val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
                mutableStateFlow.update {
                    it.copy(
                        greeting = getGreeting(now.hour),
                        formattedDate = formatHomeDate(now),
                    )
                }
            }
        }

        fetchDashboardData()
    }

    private fun fetchDashboardData() {
        mutableStateFlow.update { it.copy(screenState = ScreenState.Loading) }
        viewModelScope.launch {
            groupRepository.getGroups().collect { result ->
                when (result) {
                    is ScreenState.Content -> {
                        val groups = result.data
                        val totalGroups = groups.size
                        var totalMembers = 0
                        for (group in groups) {
                            val detailsState = groupRepository.getGroupDetails(group.id).first { state ->
                                state !is ScreenState.Loading
                            }
                            if (detailsState is ScreenState.Content) {
                                totalMembers += detailsState.data.clientMembers?.size ?: 0
                            }
                        }
                        mutableStateFlow.update {
                            it.copy(
                                screenState = ScreenState.Content(Unit, freshness = result.freshness),
                                totalGroups = totalGroups,
                                totalMembers = totalMembers,
                            )
                        }
                    }

                    is ScreenState.Error -> {
                        mutableStateFlow.update { it.copy(screenState = ScreenState.Error(result.error)) }
                    }

                    is ScreenState.NoNetwork -> {
                        mutableStateFlow.update { it.copy(screenState = ScreenState.NoNetwork()) }
                    }

                    is ScreenState.Unauthenticated -> {
                        mutableStateFlow.update { it.copy(screenState = ScreenState.Unauthenticated) }
                    }

                    is ScreenState.Empty -> {
                        mutableStateFlow.update {
                            it.copy(
                                screenState = ScreenState.Empty,
                            )
                        }
                    }

                    is ScreenState.Loading -> {
                        // Do not clear current content if refreshing
                    }
                }
            }
        }
    }

    override fun handleAction(action: HomeAction) {
        when (action) {
            HomeAction.Refresh -> fetchDashboardData()
        }
    }
}

data class HomeState(
    val username: String = "",
    val screenState: ScreenState<Unit> = ScreenState.Loading,
    val isRefreshing: Boolean = false,
    val greeting: String = "",
    val formattedDate: String = "",
    val avatarText: String = "",
    val totalGroups: Int = 0,
    val totalMembers: Int = 0,
)

sealed interface HomeAction {
    data object Refresh : HomeAction
}
