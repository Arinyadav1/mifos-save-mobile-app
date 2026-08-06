/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See See https://github.com/openMF/kmp-project-template/blob/main/LICENSE
 */
package org.mifos.feature.saving.savingDetails

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import kotlinx.serialization.Serializable
import org.mifos.core.base.ui.nav.composableWithStayTransitions

@Serializable
data class SavingDetailsRoute(val accountId: Long)

fun NavGraphBuilder.savingDetailsDestination(
    onBackClick: () -> Unit,
    onApproveSavingsClick: (Long) -> Unit,
    onSavingTransactionClick: (Long, Boolean) -> Unit,
    onActivateSavingsClick: (Long) -> Unit,
    onTransactionsClick: (Long) -> Unit,
    onSavingInterestClick: (Long) -> Unit,
) {
    composableWithStayTransitions<SavingDetailsRoute> {
        SavingDetailsScreen(
            onBackClick = onBackClick,
            onApproveSavingsClick = onApproveSavingsClick,
            onSavingTransactionClick = onSavingTransactionClick,
            onActivateSavingsClick = onActivateSavingsClick,
            onTransactionsClick = onTransactionsClick,
            onSavingInterestClick = onSavingInterestClick,
        )
    }
}

fun NavController.navigateToSavingDetails(accountId: Long, navOptions: NavOptions? = null) {
    this.navigate(route = SavingDetailsRoute(accountId), navOptions = navOptions)
}
