/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See See https://github.com/openMF/kmp-project-template/blob/main/LICENSE
 */
package org.mifos.core.ui.input

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import org.mifos.core.base.designsystem.theme.KptTheme
import org.mifos.core.designsystem.icon.AppIcons
import org.mifos.core.designsystem.theme.spacing

@Composable
fun KptSearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier,
    containerColor: Color = KptTheme.colorScheme.inverseOnSurface,
    shape: Shape = KptTheme.shapes.large,
) {
    KptTextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = modifier
            .fillMaxWidth()
            .padding(
                horizontal = MaterialTheme.spacing.lg,
                vertical = MaterialTheme.spacing.md,
            ),
        placeholder = placeholder,
        leadingIcon = {
            Icon(
                imageVector = AppIcons.Search,
                contentDescription = null,
                tint = KptTheme.colorScheme.onSurfaceVariant,
            )
        },
        shape = shape,
        colors = KptTextFieldDefaults.textFieldColors(
            focusedContainerColor = containerColor,
            unfocusedContainerColor = containerColor,
        ),
        singleLine = true,
    )
}
