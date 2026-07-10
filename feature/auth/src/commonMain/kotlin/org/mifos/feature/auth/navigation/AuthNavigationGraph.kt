/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See See https://github.com/openMF/kmp-project-template/blob/main/LICENSE
 */
@file:Suppress("MatchingDeclarationName")

package org.mifos.feature.auth.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.navigation
import kotlinx.serialization.Serializable
import org.mifos.feature.auth.accountType.accountTypeDestination
import org.mifos.feature.auth.accountType.navigateToAccountTypeScreen
import org.mifos.feature.auth.createMemberAccount.createMemberAccountDestination
import org.mifos.feature.auth.createMemberAccount.navigateToCreateMemberAccountScreen
import org.mifos.feature.auth.signIn.LoginRoute
import org.mifos.feature.auth.signIn.navigateToSignInScreen
import org.mifos.feature.auth.signIn.signInDestination

@Serializable
data object AuthGraphRoute

fun NavGraphBuilder.authNavigationGraph(
    navController: NavHostController,
) {
    navigation<AuthGraphRoute>(
        startDestination = LoginRoute,
    ) {
        signInDestination(
            onForgetPasswordScreen = {},
            onSignUpTypeScreen = navController::navigateToAccountTypeScreen,
        )
        accountTypeDestination(
            onBackClick = navController::popBackStack,
            onMemberCreateAccountScreen = navController::navigateToCreateMemberAccountScreen,
            onAdminCreateAccountScreen = {},
            onSignInClick = navController::popBackStack,
        )
        createMemberAccountDestination(
            onBackClick = navController::popBackStack,
            onNavigateToOtpVerification = {},
            onNavigateToSignIn = navController::navigateToSignInScreen,
        )
    }
}
