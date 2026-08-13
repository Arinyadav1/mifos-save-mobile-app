/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See See https://github.com/openMF/kmp-project-template/blob/main/LICENSE
 */
package org.mifos.feature.meeting.meetingList

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import kotlinx.serialization.Serializable

@Serializable
data class MeetingListRoute(val groupId: Long)

fun NavGraphBuilder.meetingListDestination(
    onBackClick: () -> Unit,
    onScheduleMeetingClick: (Long) -> Unit = {},
    onFilterClick: () -> Unit = {},
    onMeetingClick: (Long, Long) -> Unit,
) {
    composable<MeetingListRoute> { backStackEntry ->
        val route = backStackEntry.toRoute<MeetingListRoute>()
        MeetingListScreen(
            onBackClick = onBackClick,
            onScheduleMeetingClick = onScheduleMeetingClick,
            onFilterClick = onFilterClick,
            onMeetingClick = { meetingId ->
                onMeetingClick(route.groupId, meetingId)
            },
        )
    }
}

fun NavController.navigateToMeetingList(groupId: Long) {
    this.navigate(route = MeetingListRoute(groupId))
}

fun NavController.navigateToMeetingListWithUpdateData(
    groupId: Long,
) {
    this.navigate(route = MeetingListRoute(groupId)) {
        popUpTo(MeetingListRoute(groupId)) { inclusive = true }
        launchSingleTop = true
    }
}
