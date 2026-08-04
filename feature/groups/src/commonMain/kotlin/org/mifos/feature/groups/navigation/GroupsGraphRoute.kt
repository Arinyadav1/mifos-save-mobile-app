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
import org.mifos.feature.groups.activateGroup.activateGroupDestination
import org.mifos.feature.groups.activateGroup.navigateToActivateGroup
import org.mifos.feature.groups.addMember.addMemberDestination
import org.mifos.feature.groups.addMember.navigateToAddMember
import org.mifos.feature.groups.createGroup.createGroupDestination
import org.mifos.feature.groups.createGroup.navigateToCreateGroup
import org.mifos.feature.groups.groupDashboard.GroupDashboardRoute
import org.mifos.feature.groups.groupDashboard.groupDashboardDestination
import org.mifos.feature.groups.groupDashboard.navigateToGroupDashboardWithUpdateData
import org.mifos.feature.groups.groupDetails.groupDetailsDestination
import org.mifos.feature.groups.groupDetails.navigateToGroupDetailWithUpdateData
import org.mifos.feature.groups.groupDetails.navigateToGroupDetails
import org.mifos.feature.groups.groupLoanList.groupLoanListDestination
import org.mifos.feature.groups.groupLoanList.navigateToGroupLoanList
import org.mifos.feature.groups.groupMembersList.groupMembersListDestination
import org.mifos.feature.groups.groupMembersList.navigateToGroupMembersList
import org.mifos.feature.groups.groupMembersList.navigateToGroupMembersListWithUpdateData
import org.mifos.feature.groups.groupSavingList.groupSavingListDestination
import org.mifos.feature.groups.groupSavingList.navigateToGroupSavingList
import org.mifos.feature.saving.navigation.savingNavigationGraph

@Serializable
data object GroupsGraphRoute

fun NavGraphBuilder.groupsNavigationGraph(
    navController: NavController,
    onSavingClick: (Long) -> Unit,
) {
    navigation<GroupsGraphRoute>(
        startDestination = GroupDashboardRoute,
    ) {
        groupDashboardDestination(
            onBackClick = navController::popBackStack,
            onNewGroupClick = { navController.navigateToCreateGroup() },
            onGroupClick = { groupId ->
                navController.navigateToGroupDetails(groupId)
            },
        )
        createGroupDestination(
            onBackClick = navController::popBackStack,
            onNavigateToGroupDetailWithUpdateData = navController::navigateToGroupDetailWithUpdateData,
            onNavigateToGroupDashboardWithUpdateData = navController::navigateToGroupDashboardWithUpdateData,
        )
        groupDetailsDestination(
            onBackClick = {
                navController.popBackStack()
            },
            onMembersClick = { groupId ->
                navController.navigateToGroupMembersList(groupId)
            },
            onSavingsClick = { groupId ->
                navController.navigateToGroupSavingList(groupId)
            },
            onLoansClick = { groupId ->
                navController.navigateToGroupLoanList(groupId)
            },
            onActivateGroupClick = { groupId ->
                navController.navigateToActivateGroup(groupId)
            },
            onUpdateGroupClick = { groupId ->
                navController.navigateToCreateGroup(groupId)
            },
        )
        groupMembersListDestination(
            onBackClick = navController::popBackStack,
            onAddMembersClick = navController::navigateToAddMember,
        )

        addMemberDestination(
            onBackClick = navController::popBackStack,
            onBackWithUpdateData = navController::navigateToGroupMembersListWithUpdateData,
        )
        activateGroupDestination(
            onBackClick = navController::popBackStack,
            onBackWithUpdateData = { _ ->
                navController.popBackStack()
            },
        )
        groupSavingListDestination(
            onBackClick = {
                navController.popBackStack()
            },
            onSavingClick = onSavingClick,
        )
        groupLoanListDestination(
            onBackClick = {
                navController.popBackStack()
            },
        )
        savingNavigationGraph(navController = navController)
    }
}
