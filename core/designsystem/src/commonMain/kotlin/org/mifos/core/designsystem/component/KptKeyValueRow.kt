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

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import org.mifos.core.base.designsystem.theme.KptTheme
import org.mifos.core.designsystem.theme.elevation

/**
 * Reusable card container that displays a map of key-value pairs as rows.
 */
@Composable
fun KptKeyValueCard(
    items: Map<String, String>,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = KptTheme.shapes.medium,
        elevation = CardDefaults.cardElevation(
            defaultElevation = MaterialTheme.elevation.low,
        ),
        colors = CardDefaults.cardColors(
            containerColor = KptTheme.colorScheme.inverseOnSurface,
        ),
    ) {
        Column {
            val entryList = items.entries.toList()
            entryList.forEachIndexed { index, entry ->
                KptKeyValueRow(
                    key = entry.key,
                    value = entry.value,
                    showDivider = index < entryList.lastIndex,
                )
            }
        }
    }
}

/**
 * Reusable row displaying a key on the left and a value on the right,
 * with an optional bottom divider.
 */
@Composable
fun KptKeyValueRow(
    key: String,
    value: String,
    modifier: Modifier = Modifier,
    showDivider: Boolean = true,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = KptTheme.spacing.lg),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = KptTheme.spacing.md),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                text = key,
                style = KptTheme.typography.bodyMedium,
                color = KptTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.weight(1f),
            )
            Text(
                text = value,
                style = KptTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                color = KptTheme.colorScheme.onSurface,
                textAlign = TextAlign.End,
                modifier = Modifier.weight(1.5f),
            )
        }
        if (showDivider) {
            HorizontalDivider(
                color = KptTheme.colorScheme.outlineVariant,
                thickness = 1.dp,
            )
        }
    }
}
