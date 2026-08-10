/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See See https://github.com/openMF/kmp-project-template/blob/main/LICENSE
 */
package org.mifos.feature.meeting.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.navigation
import kotlinx.serialization.Serializable
import org.mifos.feature.meeting.meetingList.MeetingListRoute
import org.mifos.feature.meeting.meetingList.meetingListDestination
import org.mifos.feature.meeting.meetingList.navigateToMeetingListWithUpdateData
import org.mifos.feature.meeting.scheduleMeeting.navigateToScheduleMeeting
import org.mifos.feature.meeting.scheduleMeeting.scheduleMeetingDestination

@Serializable
data object MeetingGraphRoute

fun NavGraphBuilder.meetingNavigationGraph(
    navController: NavController,
    onScheduleMeetingClick: (Long) -> Unit = {},
    onFilterClick: () -> Unit = {},
) {
    navigation<MeetingGraphRoute>(
        startDestination = MeetingListRoute::class,
    ) {
        meetingListDestination(
            onBackClick = navController::popBackStack,
            onScheduleMeetingClick = onScheduleMeetingClick,
            onScheduleMeetingClick = { groupId ->
                navController.navigateToScheduleMeeting(groupId)
            },
            onFilterClick = onFilterClick,
        )
        scheduleMeetingDestination(
            onBackClick = navController::popBackStack,
            onBackWithUpdateData = navController::navigateToMeetingListWithUpdateData,
        )
    }
}
