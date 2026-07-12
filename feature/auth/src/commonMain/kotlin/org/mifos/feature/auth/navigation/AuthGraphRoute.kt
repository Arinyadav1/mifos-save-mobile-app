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

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.navigation
import kotlinx.serialization.Serializable
import org.mifos.feature.auth.accountType.accountTypeDestination
import org.mifos.feature.auth.accountType.navigateToAccountTypeScreen
import org.mifos.feature.auth.createMemberAccount.createMemberAccountDestination
import org.mifos.feature.auth.createMemberAccount.navigateToCreateMemberAccountScreen
import org.mifos.feature.auth.forgotPassword.forgotPasswordDestination
import org.mifos.feature.auth.forgotPassword.navigateToForgotPasswordScreen
import org.mifos.feature.auth.signIn.SignInRoute
import org.mifos.feature.auth.signIn.navigateToSignInScreen
import org.mifos.feature.auth.signIn.signInDestination
import org.mifos.feature.auth.verifyOtp.VerifyOtpFlow
import org.mifos.feature.auth.verifyOtp.navigateToVerifyOtpScreen
import org.mifos.feature.auth.verifyOtp.verifyOtpDestination

@Serializable
data object AuthGraphRoute

fun NavGraphBuilder.authNavigationGraph(
    navController: NavHostController,
) {
    navigation<AuthGraphRoute>(
        startDestination = SignInRoute,
    ) {
        signInDestination(
            onForgetPasswordScreen = navController::navigateToForgotPasswordScreen,
            onSignUpTypeScreen = navController::navigateToAccountTypeScreen,
        )
        forgotPasswordDestination(
            onBackClick = navController::popBackStack,
            onNavigateToOtpVerification = { username, isEmail ->
                navController.navigateToVerifyOtpScreen(
                    flow = VerifyOtpFlow.RESET_PASSWORD_VERIFY,
                    isEmail = isEmail,
                    username = username,
                )
            },
            onSignInClick = {
                navController.navigateToSignInScreen()
            },
        )

        accountTypeDestination(
            onBackClick = navController::popBackStack,
            onMemberCreateAccountScreen = navController::navigateToCreateMemberAccountScreen,
            onAdminCreateAccountScreen = {},
            onSignInClick = navController::popBackStack,
        )
        createMemberAccountDestination(
            onBackClick = navController::popBackStack,
            onNavigateToOtpVerification = { isEmail ->
                navController.navigateToVerifyOtpScreen(
                    flow = VerifyOtpFlow.MEMBER_ACCOUNT_VERIFY,
                    isEmail = isEmail,
                )
            },
            onNavigateToSignIn = navController::navigateToSignInScreen,
        )
        verifyOtpDestination(
            onBackClick = navController::popBackStack,
            onVerificationSuccess = {
                navController.navigateToSignInScreen()
            },
        )
    }
}
