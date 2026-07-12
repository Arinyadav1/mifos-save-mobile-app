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

package org.mifos.feature.auth.verifyOtp

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import kotlinx.serialization.Serializable
import org.mifos.core.base.ui.nav.composableWithStayTransitions

@Serializable
data class VerifyOtpRoute(
    val flow: VerifyOtpFlow,
    val isEmail: Boolean = true,
)

fun NavGraphBuilder.verifyOtpDestination(
    onBackClick: () -> Unit,
    onVerificationSuccess: () -> Unit,
) {
    composableWithStayTransitions<VerifyOtpRoute> {
        VerifyOtpScreen(
            onBackClick = onBackClick,
            onVerificationSuccess = onVerificationSuccess,
        )
    }
}

fun NavController.navigateToVerifyOtpScreen(
    flow: VerifyOtpFlow,
    isEmail: Boolean = true,
) {
    this.navigate(
        route = VerifyOtpRoute(
            flow = flow,
            isEmail = isEmail,
        ),
    )
}
