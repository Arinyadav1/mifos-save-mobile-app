/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See See https://github.com/openMF/kmp-project-template/blob/main/LICENSE
 */
package org.mifos.feature.meeting.meetingDashboard

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable

@Serializable
data object MeetingDashboardRoute

fun NavController.navigateToMeetingDashboard(navOptions: NavOptions? = null) {
    navigate(route = MeetingDashboardRoute, navOptions = navOptions)
}

fun NavGraphBuilder.meetingDashboardDestination(
    onBackClick: () -> Unit,
    onScheduleMeetingClick: (Long) -> Unit,
    onMeetingClick: (Long, Long) -> Unit,
) {
    composable<MeetingDashboardRoute> {
        MeetingDashboardScreen(
            onBackClick = onBackClick,
            onScheduleMeetingClick = onScheduleMeetingClick,
            onMeetingClick = onMeetingClick,
        )
    }
}
