/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See See https://github.com/openMF/kmp-project-template/blob/main/LICENSE
 */
package org.mifos.feature.saving.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.navigation
import kotlinx.serialization.Serializable
import org.mifos.feature.saving.approveSaving.approveSavingDestination
import org.mifos.feature.saving.approveSaving.navigateToApproveSaving
import org.mifos.feature.saving.depositTransaction.depositTransactionDestination
import org.mifos.feature.saving.depositTransaction.navigateToDepositTransaction
import org.mifos.feature.saving.savingDetails.SavingDetailsRoute
import org.mifos.feature.saving.savingDetails.savingDetailsDestination

@Serializable
data object SavingGraphRoute

fun NavGraphBuilder.savingNavigationGraph(
    navController: NavController,
) {
    navigation<SavingGraphRoute>(
        startDestination = SavingDetailsRoute::class,
    ) {
        savingDetailsDestination(
            onBackClick = navController::popBackStack,
            onApproveSavingsClick = { savingsId ->
                navController.navigateToApproveSaving(savingsId)
            },
            onDepositTransactionClick = { accountId ->
                navController.navigateToDepositTransaction(accountId)
            },
        )
        approveSavingDestination(
            onBackClick = navController::popBackStack,
            onBackWithUpdateData = { _ ->
                navController.popBackStack()
            },
        )
        depositTransactionDestination(
            onBackClick = navController::popBackStack,
            onBackWithUpdateData = { _ ->
                navController.popBackStack()
            },
        )
    }
}
