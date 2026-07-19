/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See See https://github.com/openMF/kmp-project-template/blob/main/LICENSE
 */
package org.mifos.core.designsystem.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import org.mifos.core.base.designsystem.theme.KptTheme

/**
 * A simple reusable dropdown menu container that displays list items as clickable text labels.
 */
@Composable
fun KptDropdownMenu(
    expanded: Boolean,
    onDismissRequest: () -> Unit,
    items: List<KptDropdownMenuItem>,
    modifier: Modifier = Modifier,
) {
    DropdownMenu(
        expanded = expanded,
        onDismissRequest = onDismissRequest,
        modifier = modifier
            .clip(RoundedCornerShape(KptTheme.spacing.sm))
            .background(KptTheme.colorScheme.surfaceContainer),
        tonalElevation = KptTheme.elevation.level1,
    ) {
        items.forEach { item ->
            DropdownMenuItem(
                text = {
                    Text(
                        text = item.text,
                        style = KptTheme.typography.bodyMedium,
                        color = KptTheme.colorScheme.onSurface,
                    )
                },
                onClick = {
                    onDismissRequest()
                    item.onClick()
                },
                contentPadding = PaddingValues(
                    horizontal = KptTheme.spacing.md,
                    vertical = KptTheme.spacing.xs,
                ),
                modifier = Modifier.heightIn(min = KptTheme.spacing.xl),
            )
        }
    }
}

/**
 * Data class representing a simple dropdown menu item.
 */
data class KptDropdownMenuItem(
    val text: String,
    val onClick: () -> Unit,
)
