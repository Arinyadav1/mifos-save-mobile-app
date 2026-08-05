/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See See https://github.com/openMF/kmp-project-template/blob/main/LICENSE
 */
package org.mifos.feature.saving.savingTransaction

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import kotlinx.serialization.Serializable
import org.mifos.core.base.ui.nav.composableWithStayTransitions

@Serializable
data class SavingTransactionRoute(val accountId: Long, val isWithdrawal: Boolean = false)

fun NavGraphBuilder.savingTransactionDestination(
    onBackClick: () -> Unit,
    onBackWithUpdateData: (Long) -> Unit,
) {
    composableWithStayTransitions<SavingTransactionRoute> {
        SavingTransactionScreen(
            onBackClick = onBackClick,
            onBackWithUpdateData = onBackWithUpdateData,
        )
    }
}

fun NavController.navigateToSavingTransaction(
    accountId: Long,
    isWithdrawal: Boolean = false,
    navOptions: NavOptions? = null,
) {
    this.navigate(
        route = SavingTransactionRoute(accountId = accountId, isWithdrawal = isWithdrawal),
        navOptions = navOptions,
    )
}
