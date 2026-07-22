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

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import org.mifos.core.base.designsystem.theme.KptTheme
import org.mifos.core.designsystem.theme.spacing

/**
 * Data class representing an item inside the dropdown search bar results.
 */
data class KptDropdownSearchBarItem(
    val title: String,
    val subtitle: String,
    val onClick: () -> Unit,
)

/**
 * Default configurations for the Dropdown Search Bar.
 */
object KptDropdownSearchBarDefaults

/**
 * A reusable Search Bar component with an integrated search results Dropdown Menu.
 */
@Composable
fun KptDropdownSearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    placeholder: String,
    showDropdown: Boolean,
    dropdownItems: List<KptDropdownSearchBarItem>,
    onDismissRequest: () -> Unit,
    isSearching: Boolean,
    modifier: Modifier = Modifier,
    label: String? = null,
) {
    val density = LocalDensity.current
    var searchBarWidth by remember { mutableStateOf(0) }
    var searchBarHeight by remember { mutableStateOf(0) }

    val hasResults = dropdownItems.isNotEmpty()
    val showMenu = showDropdown && (hasResults || isSearching || (query.isNotBlank() && !isSearching))

    // Flatten bottom corners if dropdown is active
    val searchBarShape = if (showMenu) {
        RoundedCornerShape(
            topStart = KptTheme.spacing.md,
            topEnd = KptTheme.spacing.md,
            bottomStart = 0.dp,
            bottomEnd = 0.dp,
        )
    } else {
        KptTheme.shapes.large
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .onGloballyPositioned { coordinates ->
                searchBarWidth = coordinates.size.width
                searchBarHeight = coordinates.size.height
            },
        contentAlignment = Alignment.TopStart,
    ) {
        KptSearchBar(
            label = label,
            query = query,
            onQueryChange = onQueryChange,
            placeholder = placeholder,
            shape = searchBarShape,
            trailingIcon = {
                if (isSearching) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(16.dp),
                        strokeWidth = 1.5.dp,
                        color = KptTheme.colorScheme.primary,
                    )
                }
            },
        )

        if (showMenu && searchBarWidth > 0) {
            val horizontalPaddingPx = 0
            val verticalPaddingPx = with(density) { MaterialTheme.spacing.md.roundToPx() }
            val cardWidthDp = with(density) { (searchBarWidth - horizontalPaddingPx * 2).toDp() }

            Popup(
                onDismissRequest = onDismissRequest,
                offset = IntOffset(horizontalPaddingPx, searchBarHeight - verticalPaddingPx),
                properties = PopupProperties(focusable = false),
            ) {
                Surface(
                    modifier = Modifier
                        .width(cardWidthDp)
                        .heightIn(max = 300.dp),
                    shape = RoundedCornerShape(
                        topStart = 0.dp,
                        topEnd = 0.dp,
                        bottomStart = KptTheme.spacing.md,
                        bottomEnd = KptTheme.spacing.md,
                    ),
                    color = KptTheme.colorScheme.surfaceContainer,
                    tonalElevation = 0.dp,
                    shadowElevation = KptTheme.elevation.level2,
                ) {
                    KptDropdownMenuContent(
                        query = query,
                        isSearching = isSearching,
                        dropdownItems = dropdownItems,
                    )
                }
            }
        }
    }
}

@Composable
private fun KptDropdownMenuContent(
    query: String,
    isSearching: Boolean,
    dropdownItems: List<KptDropdownSearchBarItem>,
) {
    if (isSearching && dropdownItems.isEmpty()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(KptTheme.spacing.md),
            contentAlignment = Alignment.Center,
        ) {
            CircularProgressIndicator(
                modifier = Modifier.size(16.dp),
                strokeWidth = 1.5.dp,
                color = KptTheme.colorScheme.primary,
            )
        }
    } else if (dropdownItems.isEmpty() && query.isNotBlank() && !isSearching) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(KptTheme.spacing.md),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = "No members found",
                style = KptTheme.typography.bodyMedium,
                color = KptTheme.colorScheme.onSurfaceVariant,
            )
        }
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
        ) {
            itemsIndexed(dropdownItems) { index, item ->
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            item.onClick()
                        }
                        .padding(KptTheme.spacing.md),
                ) {
                    Text(
                        text = item.title,
                        style = KptTheme.typography.titleMedium,
                        color = KptTheme.colorScheme.onSurface,
                    )
                    Text(
                        text = item.subtitle,
                        style = KptTheme.typography.bodySmall,
                        color = KptTheme.colorScheme.onSurfaceVariant,
                    )
                }
                if (index < dropdownItems.lastIndex) {
                    HorizontalDivider(
                        color = KptTheme.colorScheme.outlineVariant,
                        thickness = 1.dp,
                        modifier = Modifier.padding(horizontal = KptTheme.spacing.md),
                    )
                }
            }
        }
    }
}
