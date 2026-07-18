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

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import org.mifos.core.base.designsystem.component.HorizontalSpacer
import org.mifos.core.base.designsystem.theme.KptTheme
import org.mifos.core.designsystem.icon.AppIcons

/**
 * A reusable selectable card component following the project's design system.
 * Designed to show full name and account details with selection mode capability.
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun KptSelectableItemCard(
    title: String,
    subtitle: String,
    leadingIcon: ImageVector,
    isSelected: Boolean,
    isInSelectionMode: Boolean,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(
                horizontal = KptTheme.spacing.md,
                vertical = KptTheme.spacing.xs,
            )
            .clip(KptTheme.shapes.medium)
            .combinedClickable(
                onClick = onClick,
                onLongClick = onLongClick,
            ),
        shape = KptTheme.shapes.medium,
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isSelected) KptTheme.elevation.level0 else KptTheme.elevation.level1,
        ),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) {
                KptTheme.colorScheme.primaryContainer
            } else {
                KptTheme.colorScheme.inverseOnSurface
            },
        ),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(KptTheme.spacing.md),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(KptTheme.spacing.xl + KptTheme.spacing.md)
                    .clip(KptTheme.shapes.medium)
                    .background(color = KptTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = leadingIcon,
                    contentDescription = null,
                    tint = KptTheme.colorScheme.primary,
                    modifier = Modifier.size(KptTheme.spacing.lg),
                )
            }

            HorizontalSpacer(width = KptTheme.spacing.md)

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(KptTheme.spacing.xs),
            ) {
                Text(
                    text = title,
                    style = KptTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.SemiBold,
                    ),
                    color = KptTheme.colorScheme.onSurface,
                )
                Text(
                    text = subtitle,
                    style = KptTheme.typography.bodySmall,
                    color = KptTheme.colorScheme.onSurfaceVariant,
                )
            }

            if (isInSelectionMode) {
                HorizontalSpacer(width = KptTheme.spacing.md)
                Icon(
                    imageVector = if (isSelected) AppIcons.RadioButtonChecked else AppIcons.RadioButtonUnchecked,
                    contentDescription = null,
                    tint = if (isSelected) KptTheme.colorScheme.primary else KptTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(KptTheme.spacing.lg),
                )
            }
        }
    }
}
