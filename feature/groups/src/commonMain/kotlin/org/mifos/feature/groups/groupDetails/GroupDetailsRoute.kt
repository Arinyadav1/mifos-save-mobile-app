/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See See https://github.com/openMF/kmp-project-template/blob/main/LICENSE
 */
package org.mifos.feature.groups.groupDetails

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import kotlinx.serialization.Serializable
import org.mifos.core.base.ui.nav.composableWithStayTransitions

@Serializable
data class GroupDetailsRoute(val groupId: Long)

fun NavGraphBuilder.groupDetailsDestination(
    onBackClick: () -> Unit,
    onMembersClick: (Long) -> Unit,
    onSavingsClick: (Long) -> Unit,
    onLoansClick: (Long) -> Unit,
    onActivateGroupClick: (Long) -> Unit = {},
    onUpdateGroupClick: (Long) -> Unit = {},
) {
    composableWithStayTransitions<GroupDetailsRoute> {
        GroupDetailsScreen(
            onBackClick = onBackClick,
            onMembersClick = onMembersClick,
            onSavingsClick = onSavingsClick,
            onLoansClick = onLoansClick,
            onActivateGroupClick = onActivateGroupClick,
            onUpdateGroupClick = onUpdateGroupClick,
        )
    }
}

fun NavController.navigateToGroupDetails(groupId: Long, navOptions: NavOptions? = null) {
    this.navigate(route = GroupDetailsRoute(groupId), navOptions = navOptions)
}

fun NavController.navigateToGroupDetailWithUpdateData(groupId: Long) {
    this.navigate(route = GroupDetailsRoute(groupId)) {
        popUpTo(GroupDetailsRoute(groupId)) { inclusive = true }
        launchSingleTop = true
    }
}
