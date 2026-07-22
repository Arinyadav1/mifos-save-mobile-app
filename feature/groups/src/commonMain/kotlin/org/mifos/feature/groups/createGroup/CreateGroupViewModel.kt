/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See See https://github.com/openMF/kmp-project-template/blob/main/LICENSE
 */
package org.mifos.feature.groups.createGroup

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.mifos.core.base.store.screen.DataFreshness
import org.mifos.core.base.store.screen.ScreenState
import org.mifos.core.base.store.submit.SubmitState
import org.mifos.core.base.ui.viewmodel.BaseViewModel
import org.mifos.core.common.Constants
import org.mifos.core.common.formatDateFromLong
import org.mifos.core.common.getCurrentEpochMillis
import org.mifos.core.data.client.ClientRepository
import org.mifos.core.data.group.GroupRepository
import org.mifos.core.model.group.ClientMember
import org.mifos.core.model.group.CreateGroupRequest
import org.mifos.core.model.group.OfficeOption

class CreateGroupViewModel(
    private val groupRepository: GroupRepository,
    private val clientRepository: ClientRepository
) : BaseViewModel<CreateGroupState, CreateGroupEvent, CreateGroupAction>(
    CreateGroupState(),
) {

    init {
        loadGroupTemplate()
    }

    override fun handleAction(action: CreateGroupAction) {
        if (handleFormAction(action)) return
        if (handleMemberAction(action)) return
        handleLifecycleAction(action)
    }

    private fun handleFormAction(action: CreateGroupAction): Boolean {
        when (action) {
            is CreateGroupAction.NameChanged -> {
                mutableStateFlow.update { it.copy(name = action.name) }
            }
            is CreateGroupAction.ExternalIdChanged -> {
                mutableStateFlow.update { it.copy(externalId = action.externalId) }
            }
            is CreateGroupAction.OfficeSelected -> handleOfficeSelected(action.index)
            is CreateGroupAction.SubmittedOnDateSelected -> handleSubmittedOnDateSelected(action.millis)
            is CreateGroupAction.SubmittedOnDatePickerToggle -> {
                mutableStateFlow.update { it.copy(isSubmittedDatePickerVisible = action.visible) }
            }
            is CreateGroupAction.ActivateGroupToggled -> {
                mutableStateFlow.update { it.copy(isActiveGroup = action.active) }
            }
            is CreateGroupAction.ActivationDateSelected -> handleActivationDateSelected(action.millis)
            is CreateGroupAction.ActivationDatePickerToggle -> {
                mutableStateFlow.update { it.copy(isActivationDatePickerVisible = action.visible) }
            }
            else -> return false
        }
        return true
    }

    private fun handleMemberAction(action: CreateGroupAction): Boolean {
        when (action) {
            is CreateGroupAction.SearchQueryChanged -> handleSearchQueryChanged(action.query)
            is CreateGroupAction.SelectMember -> handleSelectMember(action.member)
            is CreateGroupAction.RemoveSelectedMember -> handleRemoveSelectedMember(action.memberId)
            CreateGroupAction.DismissDropdown -> {
                mutableStateFlow.update { it.copy(showDropdown = false) }
            }
            else -> return false
        }
        return true
    }

    private fun handleLifecycleAction(action: CreateGroupAction) {
        when (action) {
            CreateGroupAction.OnBackClick -> sendEvent(CreateGroupEvent.NavigateBack)
            CreateGroupAction.CreateGroup -> createGroup()
            CreateGroupAction.DismissSuccessDialog -> {
                mutableStateFlow.update { it.copy(showSuccessDialog = false) }
                sendEvent(CreateGroupEvent.NavigateBack)
            }
            CreateGroupAction.Retry -> {
                mutableStateFlow.update {
                    it.copy(
                        submitState = SubmitState.Idle,
                        screenState = ScreenState.Content(Unit, DataFreshness.FRESH),
                    )
                }
            }
            else -> Unit
        }
    }

    private fun loadGroupTemplate() {
        mutableStateFlow.update { it.copy(screenState = ScreenState.Loading) }
        viewModelScope.launch {
            groupRepository.getGroupTemplate().collect { screenState ->
                when (screenState) {
                    is ScreenState.Content -> {
                        val offices = screenState.data.officeOptions
                        mutableStateFlow.update {
                            it.copy(
                                officeOptions = offices,
                                screenState = ScreenState.Content(Unit, DataFreshness.FRESH),
                            )
                        }
                    }

                    is ScreenState.Error -> {
                        mutableStateFlow.update {
                            it.copy(
                                screenState = ScreenState.Error(
                                    screenState.error,
                                ),
                            )
                        }
                    }

                    is ScreenState.NoNetwork -> {
                        mutableStateFlow.update { it.copy(screenState = ScreenState.NoNetwork()) }
                    }

                    is ScreenState.Unauthenticated -> {
                        mutableStateFlow.update { it.copy(screenState = ScreenState.Unauthenticated) }
                    }

                    is ScreenState.Empty -> {
                        mutableStateFlow.update { it.copy(screenState = ScreenState.Empty) }
                    }

                    is ScreenState.Loading -> Unit
                }
            }
        }
    }

    private fun handleOfficeSelected(index: Int) {
        val options = state.officeOptions
        if (index in options.indices) {
            mutableStateFlow.update {
                it.copy(
                    selectedOfficeIndex = index,
                    selectedOffice = options[index],
                )
            }
        }
    }

    private fun handleSubmittedOnDateSelected(millis: Long?) {
        if (millis != null) {
            val formattedDate = formatDateFromLong(millis)
            mutableStateFlow.update {
                it.copy(
                    selectedSubmittedOnDateMillis = millis,
                    submittedOnDate = formattedDate,
                    isSubmittedDatePickerVisible = false,
                )
            }
        } else {
            mutableStateFlow.update { it.copy(isSubmittedDatePickerVisible = false) }
        }
    }

    private fun handleActivationDateSelected(millis: Long?) {
        if (millis != null) {
            val formattedDate = formatDateFromLong(millis)
            mutableStateFlow.update {
                it.copy(
                    selectedActivationDateMillis = millis,
                    activationDate = formattedDate,
                    isActivationDatePickerVisible = false,
                )
            }
        } else {
            mutableStateFlow.update { it.copy(isActivationDatePickerVisible = false) }
        }
    }

    private fun handleSearchQueryChanged(query: String) {
        mutableStateFlow.update {
            it.copy(
                searchQuery = query,
            )
        }
        viewModelScope.launch {
            performSearch(query)
        }
    }

    private suspend fun performSearch(query: String) {
        if (query.isBlank()) {
            mutableStateFlow.update { it.copy(searchResults = emptyList(), showDropdown = false) }
            return
        }

        mutableStateFlow.update { it.copy(isSearching = true) }
        val result = state.selectedOffice?.id.let {
            it?.let { officeId ->
                clientRepository.searchClients(
                    displayName = query,
                    officeId = officeId,
                )
            }
        }
        when (result) {
            is ScreenState.Content -> {
                val filtered = result.data.filter {
                    it.id !in state.selectedMembers.map { sel -> sel.id }
                }
                mutableStateFlow.update {
                    it.copy(
                        searchResults = filtered,
                        isSearching = false,
                        showDropdown = filtered.isNotEmpty(),
                    )
                }
            }

            else -> {
                mutableStateFlow.update {
                    it.copy(
                        searchResults = emptyList(),
                        isSearching = false,
                        showDropdown = false,
                    )
                }
            }
        }
    }

    private fun handleSelectMember(member: ClientMember) {
        mutableStateFlow.update {
            if (it.selectedMembers.any { m -> m.id == member.id }) {
                it.copy(
                    searchQuery = "",
                    searchResults = emptyList(),
                    showDropdown = false,
                )
            } else {
                it.copy(
                    selectedMembers = it.selectedMembers + member,
                    searchQuery = "",
                    searchResults = emptyList(),
                    showDropdown = false,
                )
            }
        }
    }

    private fun handleRemoveSelectedMember(memberId: Long) {
        mutableStateFlow.update {
            it.copy(
                selectedMembers = it.selectedMembers.filter { m -> m.id != memberId },
            )
        }
    }

    private fun createGroup() {
        mutableStateFlow.update { it.copy(submitState = SubmitState.Submitting()) }
        viewModelScope.launch {
            val request = state.selectedOffice?.id?.let { officeId ->
                CreateGroupRequest(
                    officeId = officeId,
                    name = state.name.trim(),
                    externalId = state.externalId.trim(),
                    clientMembers = state.selectedMembers.map { it.id },
                    dateFormat = Constants.DATE_FORMAT_SHORT_MONTH,
                    locale = Constants.LOCALE_EN,
                    active = if (state.isActiveGroup) true else null,
                    activationDate = if (state.isActiveGroup) state.activationDate else null,
                    submittedOnDate = state.submittedOnDate,
                )
            }

            when (val result = request?.let { groupRepository.createGroup(it) }) {
                is ScreenState.Content -> {
                    mutableStateFlow.update {
                        it.copy(
                            submitState = SubmitState.Submitted(Unit),
                            showSuccessDialog = true,
                        )
                    }
                }

                is ScreenState.Error -> {
                    mutableStateFlow.update {
                        it.copy(
                            screenState = ScreenState.Error(result.error),
                            submitState = SubmitState.Failed(result.error),
                        )
                    }
                }

                is ScreenState.NoNetwork -> {
                    mutableStateFlow.update {
                        it.copy(
                            submitState = SubmitState.Failed(),
                            screenState = ScreenState.NoNetwork(),
                        )
                    }
                }

                is ScreenState.Unauthenticated -> {
                    mutableStateFlow.update {
                        it.copy(
                            screenState = ScreenState.Unauthenticated,
                            submitState = SubmitState.Failed(),
                        )
                    }
                }

                is ScreenState.Empty -> {
                    mutableStateFlow.update {
                        it.copy(
                            screenState = ScreenState.Empty,
                            submitState = SubmitState.Failed(),
                        )
                    }
                }

                is ScreenState.Loading -> Unit
                null -> Unit
            }
        }
    }
}

data class CreateGroupState(
    val name: String = "",
    val externalId: String = "",
    val officeOptions: List<OfficeOption> = emptyList(),
    val selectedOfficeIndex: Int? = null,
    val selectedOffice: OfficeOption? = null,
    val submittedOnDate: String = formatDateFromLong(getCurrentEpochMillis()),
    val selectedSubmittedOnDateMillis: Long? = getCurrentEpochMillis(),
    val isSubmittedDatePickerVisible: Boolean = false,
    val isActiveGroup: Boolean = false,
    val activationDate: String = formatDateFromLong(getCurrentEpochMillis()),
    val selectedActivationDateMillis: Long? = getCurrentEpochMillis(),
    val isActivationDatePickerVisible: Boolean = false,
    val showSuccessDialog: Boolean = false,
    val screenState: ScreenState<Unit> = ScreenState.Content(Unit, DataFreshness.FRESH),
    val submitState: SubmitState<Unit> = SubmitState.Idle,
    val showDropdown: Boolean = false,
    val searchQuery: String = "",
    val searchResults: List<ClientMember> = emptyList(),
    val selectedMembers: List<ClientMember> = emptyList(),
    val isSearching: Boolean = false,
) {
    val isSubmitButtonEnabled: Boolean
        get() = name.isNotBlank() &&
            selectedOffice != null &&
            submittedOnDate.isNotBlank() &&
            (!isActiveGroup || activationDate.isNotBlank())
}

sealed interface CreateGroupEvent {
    data object NavigateBack : CreateGroupEvent
}

sealed interface CreateGroupAction {
    data object OnBackClick : CreateGroupAction
    data class NameChanged(val name: String) : CreateGroupAction
    data class ExternalIdChanged(val externalId: String) : CreateGroupAction
    data class OfficeSelected(val index: Int) : CreateGroupAction
    data class SubmittedOnDateSelected(val millis: Long?) : CreateGroupAction
    data class SubmittedOnDatePickerToggle(val visible: Boolean) : CreateGroupAction
    data class ActivateGroupToggled(val active: Boolean) : CreateGroupAction
    data class ActivationDateSelected(val millis: Long?) : CreateGroupAction
    data class ActivationDatePickerToggle(val visible: Boolean) : CreateGroupAction
    data object CreateGroup : CreateGroupAction
    data object DismissSuccessDialog : CreateGroupAction
    data object Retry : CreateGroupAction
    data class SearchQueryChanged(val query: String) : CreateGroupAction
    data class SelectMember(val member: ClientMember) : CreateGroupAction
    data class RemoveSelectedMember(val memberId: Long) : CreateGroupAction
    data object DismissDropdown : CreateGroupAction
}
