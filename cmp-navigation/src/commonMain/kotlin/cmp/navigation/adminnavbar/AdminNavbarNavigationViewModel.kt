/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See See https://github.com/openMF/kmp-project-template/blob/main/LICENSE
 */
package cmp.navigation.adminnavbar

import org.mifos.core.base.ui.viewmodel.BaseViewModel

internal class AdminNavbarNavigationViewModel :
    BaseViewModel<Unit, AdminNavBarEvent, AdminNavBarAction>(
        initialState = Unit,
    ) {

    override fun handleAction(action: AdminNavBarAction) {
        when (action) {
            AdminNavBarAction.SettingsTabClick -> handleSettingsTabClicked()
            AdminNavBarAction.HomeTabClick -> handleHomeTabClicked()
            is AdminNavBarAction.Internal -> handleInternalAction(action)
        }
    }

    private fun handleInternalAction(action: AdminNavBarAction.Internal) {
        when (action) {
            is AdminNavBarAction.Internal.UserStateUpdateReceive -> {
            }
        }
    }

    private fun handleHomeTabClicked() {
        sendEvent(AdminNavBarEvent.NavigateToHomeScreen)
    }

    private fun handleSettingsTabClicked() {
        sendEvent(AdminNavBarEvent.NavigateToProfileScreen)
    }
}

internal sealed class AdminNavBarAction {
    data object HomeTabClick : AdminNavBarAction()

    data object SettingsTabClick : AdminNavBarAction()

    sealed class Internal : AdminNavBarAction() {
        data class UserStateUpdateReceive(
            val userState: org.mifos.core.model.user.UserData?,
        ) : Internal()
    }
}

internal sealed class AdminNavBarEvent {

    abstract val tab: AdminNavBarTabItem

    data object NavigateToHomeScreen : AdminNavBarEvent() {
        override val tab: AdminNavBarTabItem = AdminNavBarTabItem.HomeTab
    }

    data object NavigateToProfileScreen : AdminNavBarEvent() {
        override val tab: AdminNavBarTabItem = AdminNavBarTabItem.ProfileTab
    }
}
