/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See See https://github.com/openMF/kmp-project-template/blob/main/LICENSE
 */
package org.mifos.feature.saving.depositTransaction

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import kotlinx.serialization.Serializable
import org.mifos.core.base.ui.nav.composableWithStayTransitions

@Serializable
data class DepositTransactionRoute(val accountId: Long)

fun NavGraphBuilder.depositTransactionDestination(
    onBackClick: () -> Unit,
    onBackWithUpdateData: (Long) -> Unit,
) {
    composableWithStayTransitions<DepositTransactionRoute> {
        DepositTransactionScreen(
            onBackClick = onBackClick,
            onBackWithUpdateData = onBackWithUpdateData,
        )
    }
}

fun NavController.navigateToDepositTransaction(accountId: Long, navOptions: NavOptions? = null) {
    this.navigate(route = DepositTransactionRoute(accountId), navOptions = navOptions)
}
