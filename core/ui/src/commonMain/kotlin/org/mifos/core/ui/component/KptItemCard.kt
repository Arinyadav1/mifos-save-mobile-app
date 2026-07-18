/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See See https://github.com/openMF/kmp-project-template/blob/main/LICENSE
 */
@file:Suppress("MatchingDeclarationName")

package org.mifos.core.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import org.mifos.core.base.designsystem.component.HorizontalSpacer
import org.mifos.core.base.designsystem.theme.KptTheme
import org.mifos.core.designsystem.component.StatusChip
import org.mifos.core.designsystem.component.StatusChipIntent
import org.mifos.core.designsystem.icon.AppIcons
import org.mifos.core.designsystem.theme.elevation
import org.mifos.core.designsystem.theme.spacing
import org.mifos.core.model.group.Group
import org.mifos.core.model.group.SavingsAccount

data class SubRowItem(
    val icon: ImageVector,
    val text: String,
)

@Composable
fun KptItemCard(
    title: String,
    statusText: String,
    statusIntent: StatusChipIntent,
    leadingIcon: ImageVector,
    subRows: List<SubRowItem>,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    containerColor: Color = KptTheme.colorScheme.inverseOnSurface,
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(
                horizontal = MaterialTheme.spacing.lg,
                vertical = MaterialTheme.spacing.xs,
            )
            .clickable(onClick = onClick),
        shape = KptTheme.shapes.medium,
        elevation = CardDefaults.cardElevation(
            defaultElevation = MaterialTheme.elevation.low,
        ),
        colors = CardDefaults.cardColors(
            containerColor = containerColor,
        ),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(MaterialTheme.spacing.lg),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(MaterialTheme.spacing.touchTargetMin)
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
                verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.xs),
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = title,
                        style = KptTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.SemiBold,
                        ),
                        color = KptTheme.colorScheme.onSurface,
                        modifier = Modifier.weight(1f, fill = false),
                    )

                    HorizontalSpacer(width = KptTheme.spacing.sm)

                    StatusChip(
                        text = statusText,
                        intent = statusIntent,
                    )
                }

                subRows.forEach { item ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.xs),
                    ) {
                        Icon(
                            imageVector = item.icon,
                            contentDescription = null,
                            tint = KptTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(KptTheme.spacing.md),
                        )
                        Text(
                            text = item.text,
                            style = KptTheme.typography.bodySmall,
                            color = KptTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
            }

            HorizontalSpacer(width = KptTheme.spacing.sm)

            Icon(
                imageVector = AppIcons.ArrowRight,
                contentDescription = null,
                tint = KptTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(KptTheme.spacing.lg),
            )
        }
    }
}

/**
 * Maps a [Group]'s status to the appropriate [StatusChipIntent] for visual styling.
 */
val Group.statusChipIntent: StatusChipIntent
    get() = when {
        active -> StatusChipIntent.Success
        status?.code?.contains("pending", ignoreCase = true) == true -> StatusChipIntent.Warning
        status?.code?.contains("closed", ignoreCase = true) == true -> StatusChipIntent.Neutral
        else -> StatusChipIntent.Info
    }

/**
 * Maps a [SavingsAccount]'s status to the appropriate [StatusChipIntent] for visual styling.
 */
val org.mifos.core.model.group.SavingsAccount.statusChipIntent: StatusChipIntent
    get() = when {
        status?.active == true -> StatusChipIntent.Success
        status?.submittedAndPendingApproval == true ||
            status?.approved == true -> StatusChipIntent.Warning
        status?.closed == true ||
            status?.rejected == true ||
            status?.withdrawnByApplicant == true -> StatusChipIntent.Neutral
        else -> StatusChipIntent.Info
    }
