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

package org.mifos.feature.auth.accountType

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import kotlinx.serialization.Serializable
import org.mifos.core.base.ui.nav.composableWithStayTransitions

@Serializable
data object AccountTypeRoute

fun NavGraphBuilder.accountTypeDestination(
    onBackClick: () -> Unit,
    onAdminCreateAccountScreen: () -> Unit,
    onMemberCreateAccountScreen: () -> Unit,
    onLoginClick: () -> Unit,
) {
    composableWithStayTransitions<AccountTypeRoute> {
        AccountTypeScreen(
            onBackClick = onBackClick,
            onAdminCreateAccountScreen = onAdminCreateAccountScreen,
            onMemberCreateAccountScreen = onMemberCreateAccountScreen,
            onLoginClick = onLoginClick,
        )
    }
}

fun NavController.navigateToAccountTypeScreen(navOptions: NavOptions? = null) {
    this.navigate(route = AccountTypeRoute, navOptions = navOptions)
}
