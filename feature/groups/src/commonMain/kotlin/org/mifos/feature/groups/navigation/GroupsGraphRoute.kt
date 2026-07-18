/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See See https://github.com/openMF/kmp-project-template/blob/main/LICENSE
 */
package org.mifos.feature.groups.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.navigation
import kotlinx.serialization.Serializable
import org.mifos.feature.groups.groupDashboard.GroupDashboardRoute
import org.mifos.feature.groups.groupDashboard.groupDashboardDestination
import org.mifos.feature.groups.groupDetails.groupDetailsDestination
import org.mifos.feature.groups.groupDetails.navigateToGroupDetails

@Serializable
data object GroupsGraphRoute

fun NavGraphBuilder.groupsNavigationGraph(navController: NavController) {
    navigation<GroupsGraphRoute>(
        startDestination = GroupDashboardRoute,
    ) {
        groupDashboardDestination(
            onGroupClick = { groupId ->
                navController.navigateToGroupDetails(groupId)
            },
        )
        groupDetailsDestination(
            onBackClick = {
                navController.popBackStack()
            },
        )
    }
}
