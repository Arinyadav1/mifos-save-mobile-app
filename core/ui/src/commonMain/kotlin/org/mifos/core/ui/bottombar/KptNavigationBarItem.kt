/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See See https://github.com/openMF/kmp-project-template/blob/main/LICENSE
 */
package org.mifos.core.ui.bottombar

import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.offset
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource
import org.mifos.core.base.designsystem.theme.KptTheme

@Composable
fun RowScope.KptNavigationBarItem(
    labelRes: StringResource,
    contentDescriptionRes: StringResource,
    selectedIcon: ImageVector,
    unselectedIcon: ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    NavigationBarItem(
        icon = {
            Icon(
                imageVector = if (isSelected) selectedIcon else unselectedIcon,
                contentDescription = stringResource(contentDescriptionRes),
            )
        },
        label = {
            Text(
                text = stringResource(labelRes),
                style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Medium,
                ),
                modifier = Modifier.offset(y = (-3).dp),
            )
        },
        selected = isSelected,
        alwaysShowLabel = true,
        onClick = onClick,
        modifier = modifier,
        colors = NavigationBarItemDefaults.colors(
            selectedIconColor = KptTheme.colorScheme.primary,
            unselectedIconColor = KptTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
            selectedTextColor = KptTheme.colorScheme.primary,
            unselectedTextColor = KptTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
            indicatorColor = Color.Transparent,
        ),
    )
}
