/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See See https://github.com/openMF/kmp-project-template/blob/main/LICENSE
 */
package org.mifos.feature.loan.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.navigation
import kotlinx.serialization.Serializable
import org.mifos.feature.loan.approveLoan.approveLoanDestination
import org.mifos.feature.loan.approveLoan.navigateToApproveLoan
import org.mifos.feature.loan.loanDetails.LoanDetailsRoute
import org.mifos.feature.loan.loanDetails.loanDetailsDestination

@Serializable
data object LoanGraphRoute

fun NavGraphBuilder.loanNavigationGraph(
    navController: NavController,
) {
    navigation<LoanGraphRoute>(
        startDestination = LoanDetailsRoute::class,
    ) {
        loanDetailsDestination(
            onBackClick = navController::popBackStack,
            onApproveLoanClick = { loanId ->
                navController.navigateToApproveLoan(loanId)
            },
        )
        approveLoanDestination(
            onBackClick = navController::popBackStack,
            onBackWithUpdateData = { _ ->
                navController.popBackStack()
            },
        )
    }
}
