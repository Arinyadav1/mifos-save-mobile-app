/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See See https://github.com/openMF/kmp-project-template/blob/main/LICENSE
 */
package org.mifos.feature.auth.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.navigation
import kotlinx.serialization.Serializable
import org.mifos.core.base.ui.nav.composableWithStayTransitions
import org.mifos.feature.auth.signIn.SignInScreen

@Serializable
data object AuthGraphRoute

@Serializable
data object LoginRoute

fun NavController.navigateToAuthGraph(navOptions: NavOptions? = null) {
    navigate(route = AuthGraphRoute, navOptions = navOptions)
}

fun NavGraphBuilder.authGraph(
    onForgetPasswordScreen: () -> Unit,
    onSignUpTypeScreen: () -> Unit,
) {
    navigation<AuthGraphRoute>(
        startDestination = LoginRoute,
    ) {
        composableWithStayTransitions<LoginRoute> {
            SignInScreen(
                onForgetPasswordScreen = { onForgetPasswordScreen() },
                onSignUpTypeScreen = { onSignUpTypeScreen() },
            )
        }
    }
}
