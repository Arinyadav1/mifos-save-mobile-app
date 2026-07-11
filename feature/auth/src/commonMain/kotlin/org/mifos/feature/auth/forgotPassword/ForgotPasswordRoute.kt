/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See See https://github.com/openMF/kmp-project-template/blob/main/LICENSE
 */
package org.mifos.feature.auth.forgotPassword

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import kotlinx.serialization.Serializable
import org.mifos.core.base.ui.nav.composableWithStayTransitions

@Serializable
data object ForgotPasswordRoute

fun NavGraphBuilder.forgotPasswordDestination(
    onBackClick: () -> Unit,
    onNavigateToOtpVerification: (username: String, isEmail: Boolean) -> Unit,
    onSignInClick: () -> Unit,
) {
    composableWithStayTransitions<ForgotPasswordRoute> {
        ForgotPasswordScreen(
            onBackClick = onBackClick,
            onNavigateToOtpVerification = onNavigateToOtpVerification,
            onSignInClick = onSignInClick,
        )
    }
}

fun NavController.navigateToForgotPasswordScreen() {
    this.navigate(route = ForgotPasswordRoute)
}
