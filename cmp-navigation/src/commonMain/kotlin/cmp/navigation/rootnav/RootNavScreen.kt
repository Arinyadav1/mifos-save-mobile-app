/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See See https://github.com/openMF/kmp-project-template/blob/main/LICENSE
 */
package cmp.navigation.rootnav

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.navOptions
import cmp.navigation.authenticated.AdminAuthenticatedGraphRoute
import cmp.navigation.authenticated.MemberAuthenticatedGraphRoute
import cmp.navigation.authenticated.adminAuthenticatedGraph
import cmp.navigation.authenticated.memberAuthenticatedGraph
import cmp.navigation.authenticated.navigateToAdminAuthenticatedGraph
import cmp.navigation.authenticated.navigateToMemberAuthenticatedGraph
import cmp.navigation.splash.SplashRoute
import cmp.navigation.splash.navigateToSplash
import cmp.navigation.splash.splashDestination
import cmp.navigation.ui.rememberKptNavController
import cmp.navigation.utils.toObjectKClassNavigationRoute
import cmp.navigation.utils.toObjectNavigationRoute
import org.koin.compose.viewmodel.koinViewModel
import org.mifos.core.base.designsystem.theme.motion
import org.mifos.core.base.ui.KptConnectivityBanner
import org.mifos.core.base.ui.util.NonNullEnterTransitionProvider
import org.mifos.core.base.ui.util.NonNullExitTransitionProvider
import org.mifos.core.base.ui.util.RootTransitionProviders
import org.mifos.feature.auth.navigation.AuthGraphRoute
import org.mifos.feature.auth.navigation.authGraph
import org.mifos.feature.auth.navigation.navigateToAuthGraph
import kotlin.concurrent.atomics.AtomicReference
import kotlin.concurrent.atomics.ExperimentalAtomicApi

@OptIn(ExperimentalAtomicApi::class)
@Suppress("LongMethod", "CyclomaticComplexMethod")
@Composable
fun RootNavScreen(
    modifier: Modifier = Modifier,
    viewModel: RootNavViewModel = koinViewModel(),
    navController: NavHostController = rememberKptNavController(name = "RootNavScreen"),
    onSplashScreenRemoved: () -> Unit = {},
) {
    val state by viewModel.stateFlow.collectAsStateWithLifecycle()
    val previousStateReference = remember { AtomicReference(state) }

    val isNotSplashScreen = state != RootNavState.Splash
    LaunchedEffect(isNotSplashScreen) {
        if (isNotSplashScreen) onSplashScreenRemoved()
    }

    // Snapshot theme tokens once so the non-Composable transition lambdas capture
    // theme-resolved providers. Splash → main handoff suppresses motion; other transitions
    // use the M3 fade-through pattern, both honoring MaterialTheme.motion.
    val motion = MaterialTheme.motion
    val fadeThroughEnter = RootTransitionProviders.Kpt.Enter.fadeThrough(motion)
    val fadeThroughExit = RootTransitionProviders.Kpt.Exit.fadeThrough(motion)
    val noEnter = RootTransitionProviders.Kpt.Enter.none
    val noExit = RootTransitionProviders.Kpt.Exit.none

    // Layout configuration:
    // For unauthenticated screens (Splash and Auth/Login), we draw NavHost in fullscreen
    // so overlays (like SubmitProgressOverlay loading screen) and splash can draw behind the status/top bar.
    // For authenticated screens, we apply statusBarsPadding and consume the insets globally
    // so inner TopAppBars start flush against the stripe without double-padding.
    val isAuthOrSplash = state == RootNavState.Splash || state == RootNavState.Auth

    Box(modifier = modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .then(
                    if (isAuthOrSplash) {
                        Modifier
                    } else {
                        Modifier
                            .padding(WindowInsets.statusBars.asPaddingValues())
                            .consumeWindowInsets(WindowInsets.statusBars)
                    },
                ),
        ) {
            NavHost(
                navController = navController,
                startDestination = SplashRoute,
                modifier = Modifier.fillMaxSize(),
                enterTransition = { pickEnter(fadeThroughEnter, noEnter)(this) },
                exitTransition = { pickExit(fadeThroughExit, noExit)(this) },
                popEnterTransition = { pickEnter(fadeThroughEnter, noEnter)(this) },
                popExitTransition = { pickExit(fadeThroughExit, noExit)(this) },
            ) {
                splashDestination()
//            onboardingDestination()
                authGraph(
                    onSignUpTypeScreen = {},
                    onForgetPasswordScreen = {},
                )
                memberAuthenticatedGraph()
                adminAuthenticatedGraph()
//            userUnlockDestination()
            }
        }

        if (!isAuthOrSplash) {
            KptConnectivityBanner()
        }
    }

    val targetRoute = when (state) {
        // SetLanguageRoute
        RootNavState.ShowOnboarding -> ""
        // AuthGraphRoute
        RootNavState.Auth -> AuthGraphRoute::class.toObjectKClassNavigationRoute()
        RootNavState.Splash -> SplashRoute::class.toObjectKClassNavigationRoute()
        // UserUnlockRoute.Standard
        RootNavState.UserLocked -> ""
        is RootNavState.MemberUnlocked -> MemberAuthenticatedGraphRoute::class.toObjectKClassNavigationRoute()
        is RootNavState.AdminUnlocked -> AdminAuthenticatedGraphRoute::class.toObjectKClassNavigationRoute()
    }
    val currentRoute = navController.currentDestination?.rootLevelRoute()

    // Don't navigate if we are already at the correct root. This notably happens during process
    // death. In this case, the NavHost already restores state, so we don't have to navigate.
    // However, if the route is correct but the underlying state is different, we should still
    // proceed in order to get a fresh version of that route.
    if (currentRoute == targetRoute &&
        previousStateReference.load() == state
    ) {
        previousStateReference.store(state)
        return
    }
    previousStateReference.store(state)

    // In some scenarios on an emulator the Activity can leak when recreated
    // if we don't first clear focus anytime we change the root destination.
    ClearFocus()

    // When state changes, navigate to different root navigation state
    val rootNavOptions = navOptions {
        // When changing root navigation state, pop everything else off the back stack:
        popUpTo(navController.graph.id) {
            inclusive = false
            saveState = false
        }
        launchSingleTop = true
        restoreState = false
    }

    // Use a LaunchedEffect to ensure we don't navigate too soon when the app first opens. This
    // avoids a bug that first appeared in Compose Material3 1.2.0-rc01 that causes the initial
    // transition to appear corrupted.
    LaunchedEffect(state) {
        when (state) {
            RootNavState.Splash -> navController.navigateToSplash(rootNavOptions)
            RootNavState.Auth -> navController.navigateToAuthGraph(rootNavOptions)
            // navController.navigateToSetLanguage(rootNavOptions)
            RootNavState.ShowOnboarding -> {}
            // navController.navigateToUserUnlock(rootNavOptions)
            RootNavState.UserLocked -> {}
            is RootNavState.MemberUnlocked -> navController.navigateToMemberAuthenticatedGraph(
                navOptions = rootNavOptions,
                (state as RootNavState.MemberUnlocked).activeUserId,
            )

            is RootNavState.AdminUnlocked -> navController.navigateToAdminAuthenticatedGraph(
                navOptions = rootNavOptions,
                (state as RootNavState.AdminUnlocked).activeUserId,
            )
        }
    }
}

private fun NavDestination?.rootLevelRoute(): String? = when {
    this == null -> null
    parent?.route == null -> route
    else -> parent.rootLevelRoute()
}

/**
 * Pick which pre-resolved enter provider applies, based on the target route. Splash → main
 * handoff suppresses animation (the splash has its own exit choreography); everything else
 * gets the M3 fade-through pattern.
 */
private fun AnimatedContentTransitionScope<NavBackStackEntry>.pickEnter(
    fadeThrough: NonNullEnterTransitionProvider,
    none: NonNullEnterTransitionProvider,
): NonNullEnterTransitionProvider = when (targetState.destination.rootLevelRoute()) {
    SplashRoute.toObjectNavigationRoute() -> none
    else -> fadeThrough
}

private fun AnimatedContentTransitionScope<NavBackStackEntry>.pickExit(
    fadeThrough: NonNullExitTransitionProvider,
    none: NonNullExitTransitionProvider,
): NonNullExitTransitionProvider = when (initialState.destination.rootLevelRoute()) {
    SplashRoute.toObjectNavigationRoute() -> none
    else -> fadeThrough
}

@Composable
expect fun ClearFocus()
