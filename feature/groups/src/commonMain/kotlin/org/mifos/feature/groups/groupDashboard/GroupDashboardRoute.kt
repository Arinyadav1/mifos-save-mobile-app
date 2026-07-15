/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See See https://github.com/openMF/kmp-project-template/blob/main/LICENSE
 */
package org.mifos.feature.groups.groupDashboard

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import kotlinx.serialization.Serializable
import org.mifos.core.base.ui.nav.composableWithStayTransitions

@Serializable
data object GroupDashboardRoute

fun NavGraphBuilder.groupDashboardDestination() {
    composableWithStayTransitions<GroupDashboardRoute> {
        GroupDashboardScreen()
    }
}

fun NavController.navigateToGroupDashboard(navOptions: NavOptions? = null) {
    this.navigate(route = GroupDashboardRoute, navOptions = navOptions)
}
