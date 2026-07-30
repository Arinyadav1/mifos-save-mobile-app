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

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.text.font.FontWeight
import org.mifos.core.base.designsystem.component.KptButton
import org.mifos.core.base.designsystem.component.KptTextButton
import org.mifos.core.base.designsystem.theme.KptTheme
import org.mifos.core.designsystem.icon.AppIcons

/**
 * A highly reusable bottom sheet component for sorting and filtering list screens.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KptFilterBottomSheet(
    onDismissRequest: () -> Unit,
    sheetState: SheetState,
    sortOptions: List<String>,
    selectedSortOption: String?,
    onSortOptionSelected: (String?) -> Unit,
    filterSections: List<FilterSection>,
    clearFilters: () -> Unit,
    modifier: Modifier = Modifier,
    title: String = "Filters",
    sortSectionTitle: String = "Sort by",
    clearAllText: String = "Clear All",
    applyText: String = "Apply",
) {
    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        sheetState = sheetState,
        dragHandle = null,
        containerColor = KptTheme.colorScheme.background,
        modifier = modifier,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(KptTheme.spacing.md),
        ) {
            FilterHeader(
                title = title,
                clearAllText = clearAllText,
                applyText = applyText,
                clearFilters = clearFilters,
                onDismissRequest = onDismissRequest,
            )
            HorizontalDivider(
                modifier = Modifier.fillMaxWidth(),
                thickness = KptTheme.spacing.xs / 4,
                color = KptTheme.colorScheme.outlineVariant,
            )

            // Sort Section
            if (sortOptions.isNotEmpty()) {
                FilterSortSection(
                    sortSectionTitle = sortSectionTitle,
                    sortOptions = sortOptions,
                    selectedSortOption = selectedSortOption,
                    onSortOptionSelected = onSortOptionSelected,
                )
                HorizontalDivider(
                    modifier = Modifier.fillMaxWidth(),
                    thickness = KptTheme.spacing.xs / 4,
                    color = KptTheme.colorScheme.outlineVariant,
                )
            }

            // Filter Sections
            filterSections.forEach { section ->
                if (section.availableOptions.isNotEmpty()) {
                    FilterListSection(section = section)
                    HorizontalDivider(
                        modifier = Modifier.fillMaxWidth(),
                        thickness = KptTheme.spacing.xs / 4,
                        color = KptTheme.colorScheme.outlineVariant,
                    )
                }
            }
            Spacer(modifier = Modifier.height(KptTheme.spacing.lg))
        }
    }
}

@Composable
private fun FilterHeader(
    title: String,
    clearAllText: String,
    applyText: String,
    clearFilters: () -> Unit,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = modifier
            .fillMaxWidth()
            .padding(bottom = KptTheme.spacing.md),
    ) {
        Text(
            text = title,
            style = KptTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
            color = KptTheme.colorScheme.primary,
        )
        Row(
            horizontalArrangement = Arrangement.spacedBy(KptTheme.spacing.sm),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            KptTextButton(
                onClick = {
                    clearFilters()
                    onDismissRequest()
                },
            ) {
                Text(
                    text = clearAllText,
                    color = KptTheme.colorScheme.error,
                    style = KptTheme.typography.labelLarge,
                )
            }
            KptButton(
                onClick = onDismissRequest,
            ) {
                Text(
                    text = applyText,
                    style = KptTheme.typography.labelLarge,
                )
            }
        }
    }
}

@Composable
private fun FilterSortSection(
    sortSectionTitle: String,
    sortOptions: List<String>,
    selectedSortOption: String?,
    onSortOptionSelected: (String?) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = KptTheme.spacing.sm),
    ) {
        var isExpanded by remember { mutableStateOf(false) }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { isExpanded = !isExpanded }
                .padding(vertical = KptTheme.spacing.xs),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = sortSectionTitle,
                style = KptTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                color = KptTheme.colorScheme.onSurface,
            )
            Icon(
                imageVector = AppIcons.KeyboardArrowDown,
                contentDescription = if (isExpanded) "Collapse" else "Expand",
                modifier = Modifier.rotate(if (isExpanded) 180f else 0f),
                tint = KptTheme.colorScheme.onSurfaceVariant,
            )
        }
        AnimatedVisibility(visible = isExpanded) {
            Column(
                modifier = Modifier.padding(top = KptTheme.spacing.xs),
                verticalArrangement = Arrangement.spacedBy(KptTheme.spacing.xs),
            ) {
                sortOptions.forEach { sort ->
                    val isSelected = sort == selectedSortOption
                    KptRadioToggle(
                        selected = isSelected,
                        onClick = {
                            if (isSelected) {
                                onSortOptionSelected(null)
                            } else {
                                onSortOptionSelected(sort)
                            }
                        },
                        text = sort,
                    )
                }
            }
        }
    }
}

@Composable
private fun FilterListSection(
    section: FilterSection,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = KptTheme.spacing.sm),
    ) {
        var isExpanded by remember { mutableStateOf(false) }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { isExpanded = !isExpanded }
                .padding(vertical = KptTheme.spacing.xs),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = section.title,
                style = KptTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                color = KptTheme.colorScheme.onSurface,
            )
            Icon(
                imageVector = AppIcons.KeyboardArrowDown,
                contentDescription = if (isExpanded) "Collapse" else "Expand",
                modifier = Modifier.rotate(if (isExpanded) 180f else 0f),
                tint = KptTheme.colorScheme.onSurfaceVariant,
            )
        }
        AnimatedVisibility(visible = isExpanded) {
            Column(
                modifier = Modifier.padding(top = KptTheme.spacing.xs),
                verticalArrangement = Arrangement.spacedBy(KptTheme.spacing.xs),
            ) {
                section.availableOptions.forEach { option ->
                    val isChecked = option in section.selectedOptions
                    KptCheckboxToggle(
                        checked = isChecked,
                        onCheckedChange = {
                            section.onOptionToggle(option)
                        },
                        text = option,
                    )
                }
            }
        }
    }
}

data class FilterSection(
    val title: String,
    val availableOptions: List<String>,
    val selectedOptions: List<String>,
    val onOptionToggle: (String) -> Unit,
)
