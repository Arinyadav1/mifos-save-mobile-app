/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See See https://github.com/openMF/kmp-project-template/blob/main/LICENSE
 */
package org.mifos.feature.groups.groupMembersList

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import kotlinx.serialization.Serializable
import org.mifos.core.base.ui.nav.composableWithStayTransitions

@Serializable
data class GroupMembersListRoute(val groupId: Long)

fun NavGraphBuilder.groupMembersListDestination(
    onBackClick: () -> Unit,
    onAddMembersClick: (groupId: Long, officeId: Long) -> Unit,
) {
    composableWithStayTransitions<GroupMembersListRoute> {
        GroupMembersListScreen(
            onBackClick = onBackClick,
            onAddMembersClick = onAddMembersClick,
        )
    }
}

fun NavController.navigateToGroupMembersList(groupId: Long, navOptions: NavOptions? = null) {
    this.navigate(route = GroupMembersListRoute(groupId), navOptions = navOptions)
}

fun NavController.navigateToGroupMembersListWithUpdateData(groupId: Long) {
    this.navigate(route = GroupMembersListRoute(groupId)) {
        popUpTo(GroupMembersListRoute(groupId)) { inclusive = true }
        launchSingleTop = true
    }
}
