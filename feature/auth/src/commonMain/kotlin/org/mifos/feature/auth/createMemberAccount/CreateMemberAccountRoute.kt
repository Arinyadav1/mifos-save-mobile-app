/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See See https://github.com/openMF/kmp-project-template/blob/main/LICENSE
 */
package org.mifos.feature.auth.createMemberAccount

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import kotlinx.serialization.Serializable
import org.mifos.core.base.ui.nav.composableWithStayTransitions

@Serializable
data object CreateMemberAccountRoute

fun NavGraphBuilder.createMemberAccountDestination(
    onBackClick: () -> Unit,
    onNavigateToOtpVerification: (isEmail: Boolean) -> Unit,
    onNavigateToSignIn: () -> Unit,
) {
    composableWithStayTransitions<CreateMemberAccountRoute> {
        CreateMemberAccountScreen(
            onBackClick = onBackClick,
            onNavigateToOtpVerification = onNavigateToOtpVerification,
            onNavigateToSignIn = onNavigateToSignIn,
        )
    }
}

fun NavController.navigateToCreateMemberAccountScreen() {
    this.navigate(route = CreateMemberAccountRoute)
}
