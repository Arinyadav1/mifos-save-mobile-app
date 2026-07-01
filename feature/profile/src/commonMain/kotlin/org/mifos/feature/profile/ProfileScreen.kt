/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See See https://github.com/openMF/kmp-project-template/blob/main/LICENSE
 */
package org.mifos.feature.profile

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import org.mifos.core.ui.scaffold.KptScaffold

@Composable
internal fun ProfileScreen(modifier: Modifier = Modifier) {
    ProfileScreenContent(
        modifier = modifier.fillMaxSize(),
    )
}

@Composable
internal fun ProfileScreenContent(modifier: Modifier = Modifier) {
    KptScaffold(modifier = modifier) {
        Box(
            modifier = Modifier
                .fillMaxSize(),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = "Groups",
            )
        }
    }
}
