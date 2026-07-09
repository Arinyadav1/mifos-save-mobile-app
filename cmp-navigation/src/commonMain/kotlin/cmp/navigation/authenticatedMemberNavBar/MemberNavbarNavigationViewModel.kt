/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See See https://github.com/openMF/kmp-project-template/blob/main/LICENSE
 */
package cmp.navigation.authenticatedMemberNavBar

import org.mifos.core.base.ui.viewmodel.BaseViewModel

internal class MemberNavbarNavigationViewModel :
    BaseViewModel<Unit, MemberNavBarEvent, MemberNavBarAction>(
        initialState = Unit,
    ) {

    override fun handleAction(action: MemberNavBarAction) {
        when (action) {
            MemberNavBarAction.SettingsTabClick -> handleSettingsTabClicked()
            MemberNavBarAction.HomeTabClick -> handleHomeTabClicked()
            is MemberNavBarAction.Internal -> handleInternalAction(action)
        }
    }

    private fun handleInternalAction(action: MemberNavBarAction.Internal) {
        when (action) {
            is MemberNavBarAction.Internal.UserStateUpdateReceive -> {
            }
        }
    }

    private fun handleHomeTabClicked() {
        sendEvent(MemberNavBarEvent.NavigateToHomeScreen)
    }

    private fun handleSettingsTabClicked() {
        sendEvent(MemberNavBarEvent.NavigateToProfileScreen)
    }
}

internal sealed class MemberNavBarAction {
    data object HomeTabClick : MemberNavBarAction()

    data object SettingsTabClick : MemberNavBarAction()

    sealed class Internal : MemberNavBarAction() {
        data class UserStateUpdateReceive(
            val userState: org.mifos.core.model.user.UserData?,
        ) : Internal()
    }
}

internal sealed class MemberNavBarEvent {

    abstract val tab: MemberNavBarTabItem

    data object NavigateToHomeScreen : MemberNavBarEvent() {
        override val tab: MemberNavBarTabItem = MemberNavBarTabItem.HomeTab
    }

    data object NavigateToProfileScreen : MemberNavBarEvent() {
        override val tab: MemberNavBarTabItem = MemberNavBarTabItem.ProfileTab
    }
}
