/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See See https://github.com/openMF/kmp-project-template/blob/main/LICENSE
 */
@file:Suppress("MatchingDeclarationName")

package cmp.navigation.authenticated

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.navigation
import cmp.navigation.authenticatedAdminNavBar.AdminNavbarRoute
import cmp.navigation.authenticatedAdminNavBar.adminAuthenticatedNavbarGraph
import cmp.navigation.authenticatedMemberNavBar.MemberNavbarRoute
import cmp.navigation.authenticatedMemberNavBar.memberAuthenticatedNavbarGraph
import kotlinx.serialization.Serializable

@Serializable
data class MemberAuthenticatedGraphRoute(
    val userId: String,
)

@Serializable
data class AdminAuthenticatedGraphRoute(
    val userId: String,
)

internal fun NavController.navigateToMemberAuthenticatedGraph(navOptions: NavOptions? = null, userId: String) {
    navigate(route = MemberAuthenticatedGraphRoute(userId), navOptions = navOptions)
}

internal fun NavController.navigateToAdminAuthenticatedGraph(navOptions: NavOptions? = null, userId: String) {
    navigate(route = AdminAuthenticatedGraphRoute(userId), navOptions = navOptions)
}

internal fun NavGraphBuilder.memberAuthenticatedGraph() {
    navigation<MemberAuthenticatedGraphRoute>(
        startDestination = MemberNavbarRoute,
    ) {
        memberAuthenticatedNavbarGraph()
    }
}

internal fun NavGraphBuilder.adminAuthenticatedGraph() {
    navigation<AdminAuthenticatedGraphRoute>(
        startDestination = AdminNavbarRoute,
    ) {
        adminAuthenticatedNavbarGraph()
    }
}
