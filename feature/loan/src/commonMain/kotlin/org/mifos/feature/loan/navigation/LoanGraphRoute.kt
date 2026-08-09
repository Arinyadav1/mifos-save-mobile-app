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
import org.mifos.feature.loan.disburseLoan.disburseLoanDestination
import org.mifos.feature.loan.disburseLoan.navigateToDisburseLoan
import org.mifos.feature.loan.loanDetails.LoanDetailsRoute
import org.mifos.feature.loan.loanDetails.loanDetailsDestination
import org.mifos.feature.loan.rejectLoan.navigateToRejectLoan
import org.mifos.feature.loan.rejectLoan.rejectLoanDestination
import org.mifos.feature.loan.transactionDetails.loanTransactionDetailsDestination
import org.mifos.feature.loan.transactionDetails.navigateToLoanTransactionDetails
import org.mifos.feature.loan.transactionList.loanTransactionListDestination
import org.mifos.feature.loan.transactionList.navigateToLoanTransactionList

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
            onTransactionsClick = { loanId ->
                navController.navigateToLoanTransactionList(loanId)
            },
            onApproveLoanClick = { loanId ->
                navController.navigateToApproveLoan(loanId)
            },
            onRejectLoanClick = { loanId ->
                navController.navigateToRejectLoan(loanId)
            },
            onLoanDisbursementClick = { loanId ->
                navController.navigateToDisburseLoan(loanId)
            },
        )
        loanTransactionListDestination(
            onBackClick = navController::popBackStack,
            onTransactionClick = { loanId, transactionId ->
                navController.navigateToLoanTransactionDetails(loanId, transactionId)
            },
        )
        loanTransactionDetailsDestination(
            onBackClick = navController::popBackStack,
        )
        approveLoanDestination(
            onBackClick = navController::popBackStack,
            onBackWithUpdateData = { _ ->
                navController.popBackStack()
            },
        )
        rejectLoanDestination(
            onBackClick = navController::popBackStack,
            onBackWithUpdateData = { _ ->
                navController.popBackStack()
            },
        )
        disburseLoanDestination(
            onBackClick = navController::popBackStack,
            onBackWithUpdateData = { _ ->
                navController.popBackStack()
            },
        )
    }
}
