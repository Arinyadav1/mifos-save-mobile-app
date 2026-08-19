/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See See https://github.com/openMF/kmp-project-template/blob/main/LICENSE
 */
package cmp.navigation.authenticatedAdminNavBar

import androidx.compose.ui.graphics.vector.ImageVector
import cmp.navigation.generated.resources.Res
import cmp.navigation.generated.resources.groups
import cmp.navigation.generated.resources.home
import cmp.navigation.generated.resources.meetings
import cmp.navigation.utils.toObjectNavigationRoute
import org.jetbrains.compose.resources.StringResource
import org.mifos.core.designsystem.icon.AppIcons
import org.mifos.core.ui.NavigationItem
import org.mifos.feature.groups.groupDashboard.GroupDashboardRoute
import org.mifos.feature.groups.navigation.GroupsGraphRoute
import org.mifos.feature.home.HomeDestination
import org.mifos.feature.home.HomeRoute
import org.mifos.feature.meeting.meetingDashboard.MeetingDashboardRoute
import org.mifos.feature.meeting.navigation.MeetingGraphRoute

sealed class AdminNavBarTabItem : NavigationItem {

    data object HomeTab : AdminNavBarTabItem() {
        override val selectedIcon: ImageVector
            get() = AppIcons.HomeBoarder
        override val icon: ImageVector
            get() = AppIcons.Home
        override val labelRes: StringResource
            get() = Res.string.home
        override val contentDescriptionRes: StringResource
            get() = Res.string.home
        override val graphRoute: String
            get() = HomeDestination.toObjectNavigationRoute()
        override val startDestinationRoute: String
            get() = HomeRoute.toObjectNavigationRoute()
        override val testTag: String
            get() = "AdminHomeTab"
    }

    data object GroupsTab : AdminNavBarTabItem() {
        override val selectedIcon: ImageVector
            get() = AppIcons.GroupsBoarder
        override val icon: ImageVector
            get() = AppIcons.Group
        override val labelRes: StringResource
            get() = Res.string.groups
        override val contentDescriptionRes: StringResource
            get() = Res.string.groups
        override val graphRoute: String
            get() = GroupsGraphRoute.toObjectNavigationRoute()
        override val startDestinationRoute: String
            get() = GroupDashboardRoute.toObjectNavigationRoute()
        override val testTag: String
            get() = "AdminGroupsTab"
    }

    data object MeetingsTab : AdminNavBarTabItem() {
        override val selectedIcon: ImageVector
            get() = AppIcons.MeetingsBoarder
        override val icon: ImageVector
            get() = AppIcons.Meetings
        override val labelRes: StringResource
            get() = Res.string.meetings
        override val contentDescriptionRes: StringResource
            get() = Res.string.meetings
        override val graphRoute: String
            get() = MeetingGraphRoute.toObjectNavigationRoute()
        override val startDestinationRoute: String
            get() = MeetingDashboardRoute.toObjectNavigationRoute()
        override val testTag: String
            get() = "AdminMeetingsTab"
    }
}
