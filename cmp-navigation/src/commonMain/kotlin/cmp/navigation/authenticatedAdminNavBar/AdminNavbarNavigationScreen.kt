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

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavOptions
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.navOptions
import cmp.navigation.ui.KptRootScaffold
import cmp.navigation.ui.ScaffoldNavigationData
import cmp.navigation.ui.logDestinationChanged
import cmp.navigation.ui.rememberKptNavController
import kotlinx.collections.immutable.persistentListOf
import org.koin.compose.viewmodel.koinViewModel
import org.mifos.core.base.analytics.rememberAnalyticsHelper
import org.mifos.core.base.designsystem.theme.motion
import org.mifos.core.base.ui.effects.EventsEffect
import org.mifos.core.base.ui.util.RootTransitionProviders
import org.mifos.core.ui.NavigationItem
import org.mifos.feature.groups.groupDashboard.navigateToGroupDashboard
import org.mifos.feature.groups.navigation.groupsNavigationGraph
import org.mifos.feature.home.HomeDestination
import org.mifos.feature.home.homeGraph
import org.mifos.feature.home.navigateToHome
import org.mifos.feature.loan.loanDetails.navigateToLoanDetails
import org.mifos.feature.saving.savingDetails.navigateToSavingDetails

@Composable
internal fun AdminNavbarNavigationScreen(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberKptNavController(
        name = "AdminNavbarScreen",
    ),
    viewModel: AdminNavbarNavigationViewModel = koinViewModel(),
) {
    val analyticsHelper = rememberAnalyticsHelper()

    EventsEffect(eventFlow = viewModel.eventFlow) { event ->
        navController.apply {
            when (event) {
                AdminNavBarEvent.NavigateToHomeScreen -> {
                    analyticsHelper.logDestinationChanged(event.tab.startDestinationRoute)
                    navigateToTabOrRoot(tabToNavigateTo = event.tab) {
                        navigateToHome(navOptions = it)
                    }
                }

                AdminNavBarEvent.NavigateToGroupsScreen -> {
                    analyticsHelper.logDestinationChanged(event.tab.startDestinationRoute)
                    navigateToTabOrRoot(tabToNavigateTo = event.tab) {
                        navigateToGroupDashboard(navOptions = it)
                    }
                }

                AdminNavBarEvent.NavigateToMeetingsScreen -> {
                    analyticsHelper.logDestinationChanged(event.tab.startDestinationRoute)
                    navigateToTabOrRoot(tabToNavigateTo = event.tab) {
                    }
                }
            }
        }
    }

    AdminNavbarNavigationScreenContent(
        navController = navController,
        modifier = modifier,
        onAction = remember(viewModel) {
            { viewModel.trySendAction(it) }
        },
    )
}

@Composable
internal fun AdminNavbarNavigationScreenContent(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
    onAction: (AdminNavBarAction) -> Unit,
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val navigationItems = persistentListOf<NavigationItem>(
        AdminNavBarTabItem.HomeTab,
        AdminNavBarTabItem.GroupsTab,
        AdminNavBarTabItem.MeetingsTab,
    )

    KptRootScaffold(
        contentWindowInsets = WindowInsets(0.dp),
        navigationData = ScaffoldNavigationData(
            navigationItems = navigationItems,
            selectedNavigationItem = navigationItems.find {
                navBackStackEntry.isCurrentRoute(route = it.graphRoute)
            },
            onNavigationClick = { navigationItem ->
                when (navigationItem) {
                    is AdminNavBarTabItem.HomeTab -> {
                        onAction(AdminNavBarAction.HomeTabClick)
                    }

                    is AdminNavBarTabItem.GroupsTab -> {
                        onAction(AdminNavBarAction.GroupsTabClick)
                    }

                    is AdminNavBarTabItem.MeetingsTab -> {
                        onAction(AdminNavBarAction.MeetingsTabClick)
                    }
                }
            },
            shouldShowNavigation = navigationItems.any {
                navBackStackEntry.isCurrentRoute(route = it.graphRoute)
            },
        ),
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        },
        modifier = modifier,
    ) {
        val motion = MaterialTheme.motion
        NavHost(
            navController = navController,
            startDestination = HomeDestination,
            enterTransition = RootTransitionProviders.Kpt.Enter.fadeThrough(motion),
            exitTransition = RootTransitionProviders.Kpt.Exit.fadeThrough(motion),
            popEnterTransition = RootTransitionProviders.Kpt.Enter.fadeThrough(motion),
            popExitTransition = RootTransitionProviders.Kpt.Exit.fadeThrough(motion),
        ) {
            homeGraph()
            groupsNavigationGraph(
                navController = navController,
                onSavingClick = { accountId ->
                    navController.navigateToSavingDetails(accountId)
                },
                onLoanClick = { loanId ->
                    navController.navigateToLoanDetails(loanId)
                },
            )
        }
    }
}

private fun NavController.navigateToTabOrRoot(
    tabToNavigateTo: AdminNavBarTabItem,
    navigate: (NavOptions) -> Unit,
) {
    if (tabToNavigateTo.startDestinationRoute == currentDestination?.route) {
        return
    } else if (currentDestination?.parent?.route == tabToNavigateTo.graphRoute) {
        popBackStack(route = tabToNavigateTo.startDestinationRoute, inclusive = false)
    } else {
        navigate(
            navOptions {
                popUpTo(graph.findStartDestination().id) {
                    saveState = true
                }
                launchSingleTop = true
                restoreState = true
            },
        )
    }
}

private fun NavBackStackEntry?.isCurrentRoute(route: String): Boolean = this
    ?.destination
    ?.hierarchy
    ?.any { it.route == route } == true
