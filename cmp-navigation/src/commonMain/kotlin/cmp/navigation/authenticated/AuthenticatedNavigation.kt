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
import cmp.navigation.authenticatednavbar.AuthenticatedNavbarRoute
import cmp.navigation.authenticatednavbar.authenticatedNavbarGraph
import kotlinx.serialization.Serializable

@Serializable
internal data object AuthenticatedGraphRoute

internal fun NavController.navigateToAuthenticatedGraph(navOptions: NavOptions? = null) {
    navigate(route = AuthenticatedGraphRoute, navOptions = navOptions)
}

internal fun NavGraphBuilder.authenticatedGraph() {
    navigation<AuthenticatedGraphRoute>(
        startDestination = AuthenticatedNavbarRoute,
    ) {
        authenticatedNavbarGraph()

        // Dev-only entry points to the showcase galleries (only wired in non-release builds).
        // Released builds receive null → SettingsScreen hides the dev menu entirely.
        // See feature/showcase for the gallery destinations.
//        val onTransitionGalleryClick: (() -> Unit)? = if (!isReleaseBuild()) {
//            { navController.navigate(TransitionGalleryRoute) }
//        } else {
//            null
//        }
//        val onStateGalleryClick: (() -> Unit)? = if (!isReleaseBuild()) {
//            { navController.navigate(StateGalleryRoute) }
//        } else {
//            null
//        }
//        settingsDestination(
//            onBackClick = { navController.popBackStackSafely() },
//            onTransitionGalleryClick = onTransitionGalleryClick,
//            onStateGalleryClick = onStateGalleryClick,
//        )
//
//        // Money Toolkit feature graphs — generic personal-finance utilities.
//        currencyRatesGraph(navController)
//        emiCalculatorDestination(onBackClick = { navController.popBackStackSafely() })
//
//        // Banking utility toolkit — local-only personal tools.
//        loansGraph(navController) // B1 — multi-formKey draft showcase
//        billsGraph(navController) // B4 — multi-formKey + platform notification scheduler
//        calculatorsGraph(navController) // B2/B3/B5/B6 — affordability + amortization + comparison + wizard
//        ratesGraph(navController) // B7 — NETWORK_WITH_CACHE rate tracker
//        macroGraph(navController) // B8 — multi-source combine (GDP / CPI / Unemployment)
//
//        // Dev-only transition gallery (Phase 08 Task 14 — Task 12-13 ground work).
//        transitionGalleryGraph(navController)
//
//        // Dev-only state gallery (Phase 02 Task 17 — ScreenState variants + component-scale primitives).
//        stateGalleryGraph(navController)
    }
}
