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
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.mifos.core.base.designsystem.component.HorizontalSpacer
import org.mifos.core.base.designsystem.component.VerticalSpacer
import org.mifos.core.base.designsystem.theme.KptTheme
import org.mifos.core.designsystem.theme.KptTheme

/**
 * A highly reusable and premium screen header component (top app bar layout).
 * Featuring an organic gradient background with faint floating ambient circles
 * drawn directly on a canvas, matching the visual designs.
 *
 * @param modifier The modifier to be applied to this layout.
 * @param navigationIcon Optional slot on the top-left side (e.g. Profile or Back button).
 * @param actions Optional slot on the top-right side (e.g. action buttons or search/filter pills).
 * @param title Optional slot for the main title / header text.
 * @param windowInsets The window insets to apply as padding inside the header
 * (defaults to status bars so background draws behind status bar).
 * @param content Optional body slot placed directly below the top bar section.
 */
@Composable
fun KptHeader(
    modifier: Modifier = Modifier,
    navigationIcon: @Composable (() -> Unit)? = null,
    actions: @Composable (RowScope.() -> Unit)? = null,
    title: @Composable (() -> Unit)? = null,
    windowInsets: WindowInsets = WindowInsets.statusBars,
    content: @Composable (ColumnScope.() -> Unit)? = null,
) {
    val color = KptTheme.colorScheme.onPrimary.copy(alpha = 0.05f)

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(color = KptTheme.colorScheme.primary)
            .drawBehind {
                val circleCenter = Offset(size.width * 0.95f, 0f)
                drawCircle(
                    color = color,
                    radius = 70.dp.toPx(),
                    center = circleCenter,
                )
                drawCircle(
                    color = color,
                    radius = 110.dp.toPx(),
                    center = circleCenter,
                )
                drawCircle(
                    color = color,
                    radius = 150.dp.toPx(),
                    center = circleCenter,
                )
            }
            .windowInsetsPadding(windowInsets)
            .padding(horizontal = KptTheme.spacing.lg)
            .padding(top = KptTheme.spacing.lg, bottom = KptTheme.spacing.xl),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (navigationIcon != null) {
                navigationIcon()
                HorizontalSpacer(width = KptTheme.spacing.md)
            }

            Spacer(modifier = Modifier.weight(1f))

            if (actions != null) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.End,
                ) {
                    actions()
                }
            }
        }

        if (title != null) {
            VerticalSpacer(height = KptTheme.spacing.lg)
            title()
        }

        if (content != null) {
            VerticalSpacer(height = KptTheme.spacing.lg)
            content()
        }
    }
}

@Composable
fun KptHeaderProfile(
    avatarText: String,
    greeting: String,
    name: String,
    modifier: Modifier = Modifier,
    onProfileClick: () -> Unit = {},
) {
    Row(
        modifier = modifier.clickable(onClick = onProfileClick),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(CircleShape)
                .background(KptTheme.colorScheme.onPrimary.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = avatarText,
                style = KptTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = KptTheme.colorScheme.onPrimary,
                ),
            )
        }

        HorizontalSpacer(width = KptTheme.spacing.sm)

        Column {
            Text(
                text = greeting,
                style = KptTheme.typography.bodySmall.copy(
                    color = KptTheme.colorScheme.onPrimary.copy(alpha = 0.7f),
                    fontSize = 12.sp,
                ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                text = name,
                style = KptTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = KptTheme.colorScheme.onPrimary,
                    fontSize = 16.sp,
                ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

@Composable
fun KptHeaderBackButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .size(44.dp)
            .clip(CircleShape)
            .background(KptTheme.colorScheme.onPrimary.copy(alpha = 0.15f))
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
            contentDescription = "Back",
            tint = KptTheme.colorScheme.onPrimary,
            modifier = Modifier.size(KptTheme.spacing.xl),
        )
    }
}

@Composable
fun KptHeaderActionButton(
    icon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    showBadge: Boolean = false,
    contentDescription: String? = null,
) {
    Box(
        modifier = modifier.size(44.dp),
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(CircleShape)
                .background(KptTheme.colorScheme.onPrimary.copy(alpha = 0.15f))
                .clickable(onClick = onClick),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = icon,
                contentDescription = contentDescription,
                tint = KptTheme.colorScheme.onPrimary,
                modifier = Modifier.size(22.dp),
            )
        }

        if (showBadge) {
            Box(
                modifier = Modifier
                    .size(KptTheme.spacing.sm)
                    .clip(CircleShape)
                    .background(KptTheme.colorScheme.error)
                    .align(Alignment.TopEnd)
                    .padding(1.dp),
            )
        }
    }
}

@Composable
fun KptHeaderPillButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
) {
    Row(
        modifier = modifier
            .clip(CircleShape)
            .background(KptTheme.colorScheme.surface)
            .clickable(onClick = onClick)
            .padding(horizontal = KptTheme.spacing.lg, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
    ) {
        if (icon != null) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = KptTheme.colorScheme.primary,
                modifier = Modifier.size(KptTheme.spacing.lg),
            )
            HorizontalSpacer(width = KptTheme.spacing.xs)
        }

        Text(
            text = text,
            style = KptTheme.typography.labelLarge.copy(
                fontWeight = FontWeight.Bold,
                color = KptTheme.colorScheme.primary,
            ),
        )
    }
}

@Composable
fun KptHeaderTitle(
    title: String,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
) {
    Column(modifier = modifier) {
        if (subtitle != null) {
            Text(
                text = subtitle,
                style = KptTheme.typography.bodySmall.copy(
                    color = KptTheme.colorScheme.onPrimary.copy(alpha = 0.6f),
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 11.sp,
                ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }

        Text(
            text = title,
            style = KptTheme.typography.headlineMedium.copy(
                fontWeight = FontWeight.Bold,
                color = KptTheme.colorScheme.onPrimary,
                fontSize = 28.sp,
            ),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

@Composable
fun KptHeaderStatsCard(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
    isValueAbove: Boolean = false,
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(KptTheme.spacing.md))
            .background(KptTheme.colorScheme.onPrimary.copy(alpha = 0.08f))
            .padding(horizontal = KptTheme.spacing.md, vertical = KptTheme.spacing.sm),
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.Center,
        ) {
            if (isValueAbove) {
                Text(
                    text = value,
                    style = KptTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = KptTheme.colorScheme.onPrimary,
                        fontSize = 18.sp,
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    text = label,
                    style = KptTheme.typography.bodySmall.copy(
                        color = KptTheme.colorScheme.onPrimary.copy(alpha = 0.6f),
                        fontSize = 11.sp,
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            } else {
                Text(
                    text = label.uppercase(),
                    style = KptTheme.typography.bodySmall.copy(
                        color = KptTheme.colorScheme.onPrimary.copy(alpha = 0.5f),
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 10.sp,
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    text = value,
                    style = KptTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = KptTheme.colorScheme.onPrimary,
                        fontSize = 18.sp,
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
    }
}

@Preview
@Composable
fun KptDashboardHeaderPreview() {
    KptTheme {
        KptHeader(
            navigationIcon = {
                KptHeaderProfile(
                    avatarText = "S",
                    greeting = "Good morning",
                    name = "Sarah Nair",
                )
            },
            actions = {
                KptHeaderActionButton(
                    icon = Icons.Default.Notifications,
                    onClick = {},
                    showBadge = true,
                    contentDescription = "Notifications",
                )
                HorizontalSpacer(width = KptTheme.spacing.sm)
                KptHeaderActionButton(
                    icon = Icons.Default.Settings,
                    onClick = {},
                    contentDescription = "Settings",
                )
            },
            content = {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    KptHeaderStatsCard(
                        label = "Due Today",
                        value = "₹1.24L",
                        isValueAbove = false,
                        modifier = Modifier.weight(1f),
                    )
                    KptHeaderStatsCard(
                        label = "Meetings",
                        value = "4 Today",
                        isValueAbove = false,
                        modifier = Modifier.weight(1f),
                    )
                    KptHeaderStatsCard(
                        label = "Members",
                        value = "2,340",
                        isValueAbove = false,
                        modifier = Modifier.weight(1f),
                    )
                }
            },
        )
    }
}

@Preview
@Composable
fun KptGroupsHeaderPreview() {
    KptTheme {
        KptHeader(
            navigationIcon = {
                KptHeaderBackButton(onClick = {})
            },
            actions = {
                KptHeaderPillButton(
                    text = "New Group",
                    icon = Icons.Default.Add,
                    onClick = {},
                )
                HorizontalSpacer(width = KptTheme.spacing.sm)
                KptHeaderActionButton(
                    icon = Icons.Default.Tune,
                    onClick = {},
                    contentDescription = "Filters",
                )
            },
            title = {
                KptHeaderTitle(
                    title = "Groups",
                    subtitle = "MIFOS SAVE",
                )
            },
            content = {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    KptHeaderStatsCard(
                        label = "Total",
                        value = "186",
                        isValueAbove = true,
                        modifier = Modifier.weight(1f),
                    )
                    KptHeaderStatsCard(
                        label = "Active",
                        value = "162",
                        isValueAbove = true,
                        modifier = Modifier.weight(1f),
                    )
                    KptHeaderStatsCard(
                        label = "Pending",
                        value = "18",
                        isValueAbove = true,
                        modifier = Modifier.weight(1f),
                    )
                    KptHeaderStatsCard(
                        label = "Closed",
                        value = "6",
                        isValueAbove = true,
                        modifier = Modifier.weight(1f),
                    )
                }
            },
        )
    }
}
