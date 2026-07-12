/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See See https://github.com/openMF/kmp-project-template/blob/main/LICENSE
 */
package org.mifos.feature.auth.signIn

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import kotlinx.serialization.Serializable
import org.mifos.core.base.ui.nav.composableWithStayTransitions
import org.mifos.feature.auth.navigation.AuthGraphRoute

@Serializable
data object SignInRoute

fun NavGraphBuilder.signInDestination(
    onForgetPasswordScreen: () -> Unit,
    onSignUpTypeScreen: () -> Unit,
) {
    composableWithStayTransitions<SignInRoute> {
        SignInScreen(
            onForgetPasswordScreen = { onForgetPasswordScreen() },
            onAccountTypeScreen = { onSignUpTypeScreen() },
        )
    }
}

fun NavController.navigateToSignInScreen() {
    this.navigate(route = AuthGraphRoute) {
        popUpTo(AuthGraphRoute) {
            inclusive = true
        }
    }
}
