/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See See https://github.com/openMF/kmp-project-template/blob/main/LICENSE
 */
package org.mifos.feature.meeting.scheduleMeeting

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable

@Serializable
data class ScheduleMeetingRoute(
    val groupId: Long,
)

fun NavController.navigateToScheduleMeeting(groupId: Long) {
    navigate(ScheduleMeetingRoute(groupId))
}

fun NavGraphBuilder.scheduleMeetingDestination(
    onBackClick: () -> Unit,
    onBackWithUpdateData: (Long) -> Unit,
) {
    composable<ScheduleMeetingRoute> {
        ScheduleMeetingScreen(
            onBackClick = onBackClick,
            onBackWithUpdateData = onBackWithUpdateData,
        )
    }
}
