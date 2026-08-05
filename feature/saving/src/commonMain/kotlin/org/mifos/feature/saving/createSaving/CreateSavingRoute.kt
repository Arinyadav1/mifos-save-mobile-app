/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See See https://github.com/openMF/kmp-project-template/blob/main/LICENSE
 */
package org.mifos.feature.saving.createSaving

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import kotlinx.serialization.Serializable
import org.mifos.core.base.ui.nav.composableWithStayTransitions

@Serializable
data class CreateSavingRoute(
    val groupId: Long? = null,
    val clientId: Long? = null,
)

fun NavGraphBuilder.createSavingDestination(
    onBackClick: () -> Unit,
    onBackWithUpdateData: () -> Unit,
) {
    composableWithStayTransitions<CreateSavingRoute> {
        CreateSavingScreen(
            onBackClick = onBackClick,
            onBackWithUpdateData = onBackWithUpdateData,
        )
    }
}

fun NavController.navigateToCreateSaving(
    groupId: Long? = null,
    clientId: Long? = null,
    navOptions: NavOptions? = null,
) {
    this.navigate(
        route = CreateSavingRoute(groupId = groupId, clientId = clientId),
        navOptions = navOptions,
    )
}
