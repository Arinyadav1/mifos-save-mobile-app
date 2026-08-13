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
import org.mifos.feature.meeting.meetingAttandance.meetingAttendanceDestination
import org.mifos.feature.meeting.meetingAttandance.navigateToMeetingAttendance
import org.mifos.feature.meeting.meetingList.MeetingListRoute
import org.mifos.feature.meeting.meetingList.meetingListDestination
import org.mifos.feature.meeting.meetingList.navigateToMeetingListWithUpdateData
import org.mifos.feature.meeting.scheduleMeeting.navigateToScheduleMeeting
import org.mifos.feature.meeting.scheduleMeeting.scheduleMeetingDestination

@Serializable
data object MeetingGraphRoute

fun NavGraphBuilder.meetingNavigationGraph(
    navController: NavController,
) {
    navigation<MeetingGraphRoute>(
        startDestination = MeetingListRoute::class,
    ) {
        meetingListDestination(
            onBackClick = navController::popBackStack,
            onScheduleMeetingClick = navController::navigateToScheduleMeeting,
            onMeetingClick = navController::navigateToMeetingAttendance,

        )
        meetingAttendanceDestination(
            onBackClick = navController::popBackStack,
        )
        scheduleMeetingDestination(
            onBackClick = navController::popBackStack,
            onBackWithUpdateData = navController::navigateToMeetingListWithUpdateData,
        )
    }
}
