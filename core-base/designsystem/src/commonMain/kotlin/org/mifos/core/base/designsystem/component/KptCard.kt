/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See See https://github.com/openMF/kmp-project-template/blob/main/LICENSE
 */
package org.mifos.core.base.designsystem.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CardElevation
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.OutlinedCard
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import org.mifos.core.base.designsystem.theme.KptTheme

/**
 * A standard, high-performance card component wrapper for the KPT design system.
 */
@Composable
fun KptCard(
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(20.dp),
    elevation: Dp = KptTheme.elevation.level0,
    onClick: (() -> Unit)? = null,
    colors: CardColors = CardDefaults.cardColors(),
    border: BorderStroke? = null,
    content: @Composable ColumnScope.() -> Unit,
) {
    Card(
        shape = shape,
        modifier = modifier
            .fillMaxWidth()
            .then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier),
        elevation = CardDefaults.cardElevation(
            defaultElevation = elevation,
        ),
        colors = colors,
        border = border,
        content = content,
    )
}

/**
 * A flexible and theme-aware card component for the KPT design system.
 *
 * This composable abstracts over Material3 [Card], [ElevatedCard], and [OutlinedCard],
 * and selects the appropriate variant based on [CardVariant].
 */
@Composable
fun KptCustomCard(
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
    enabled: Boolean = true,
    variant: CardVariant = CardVariant.FILLED,
    shape: Shape? = null,
    colors: CardColors? = null,
    elevation: CardElevation? = null,
    borderStroke: BorderStroke? = null,
    interactionSource: MutableInteractionSource? = null,
    content: @Composable ColumnScope.() -> Unit,
) {
    when (variant) {
        CardVariant.FILLED -> Card(
            onClick = onClick,
            modifier = modifier,
            enabled = enabled,
            shape = shape ?: CardDefaults.shape,
            colors = colors ?: CardDefaults.cardColors(),
            elevation = elevation ?: CardDefaults.cardElevation(),
            border = borderStroke,
            interactionSource = interactionSource,
            content = content,
        )

        CardVariant.ELEVATED -> ElevatedCard(
            onClick = onClick,
            modifier = modifier,
            enabled = enabled,
            shape = shape ?: CardDefaults.elevatedShape,
            colors = colors ?: CardDefaults.elevatedCardColors(),
            elevation = elevation ?: CardDefaults.elevatedCardElevation(),
            interactionSource = interactionSource,
            content = content,
        )

        CardVariant.OUTLINED -> OutlinedCard(
            onClick = onClick,
            modifier = modifier,
            enabled = enabled,
            shape = shape ?: CardDefaults.outlinedShape,
            colors = colors ?: CardDefaults.outlinedCardColors(),
            elevation = elevation ?: CardDefaults.outlinedCardElevation(),
            border = borderStroke ?: CardDefaults.outlinedCardBorder(enabled),
            interactionSource = interactionSource,
            content = content,
        )
    }
}

enum class CardVariant {
    /**
     * A standard filled card with background color and elevation.
     */
    FILLED,

    /**
     * A card with extra emphasis via elevation and contrast, but no border.
     */
    ELEVATED,

    /**
     * A low-emphasis card with a visible border and no elevation.
     */
    OUTLINED,
}
