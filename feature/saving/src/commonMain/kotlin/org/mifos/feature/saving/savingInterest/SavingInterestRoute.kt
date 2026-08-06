/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See See https://github.com/openMF/kmp-project-template/blob/main/LICENSE
 */
package org.mifos.feature.saving.savingInterest

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import kotlinx.serialization.Serializable
import org.mifos.core.base.ui.nav.composableWithStayTransitions

@Serializable
data class SavingInterestRoute(val accountId: Long)

fun NavGraphBuilder.savingInterestDestination(
    onBackClick: () -> Unit,
) {
    composableWithStayTransitions<SavingInterestRoute> {
        SavingInterestScreen(
            onBackClick = onBackClick,
        )
    }
}

fun NavController.navigateToSavingInterest(accountId: Long, navOptions: NavOptions? = null) {
    this.navigate(route = SavingInterestRoute(accountId), navOptions = navOptions)
}
