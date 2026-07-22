/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/kmp-project-template/blob/main/LICENSE
 */
package org.mifos.core.base.designsystem.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import org.mifos.core.base.designsystem.theme.KptTheme


@Composable
fun KptDoubleButton(
    leftButtonText: String,
    rightButtonText: String,
    onLeftButtonClick: () -> Unit,
    onRightButtonClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabledRight: Boolean = true,
    enableLeft: Boolean = true,
) {
    Surface(
        color = KptTheme.colorScheme.surface,
        modifier = modifier.fillMaxWidth(),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(KptTheme.spacing.md),
            horizontalArrangement = Arrangement.spacedBy(KptTheme.spacing.md),
        ) {
            KptOutlinedButton(
                onClick = onLeftButtonClick,
                modifier = Modifier.weight(1f),
                enabled = enableLeft,
            ) {
                Text(leftButtonText)
            }

            KptButton(
                onClick = onRightButtonClick,
                modifier = Modifier.weight(1f),
                enabled = enabledRight,
            ) {
                Text(rightButtonText)
            }
        }
    }
}
/**
 * Primary filled button following the KPT design system.
 *
 * Wraps [Button] with project-standard defaults. Prefer this over importing
 * Material3 [Button] directly so design-system tokens can be applied in one place.
 *
 * ```kotlin
 * KptButton(onClick = viewModel::onSubmit, enabled = uiState.canInteract) {
 *     Text("Save")
 * }
 * ```
 */
@Composable
fun KptButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    colors: ButtonColors = ButtonDefaults.buttonColors(
        containerColor = KptTheme.colorScheme.primary,
    ),
    content: @Composable () -> Unit,
) {
    Button(
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
        colors = colors,
        content = { content() },
    )
}

/**
 * Outlined variant of [KptButton].
 */
@Composable
fun KptOutlinedButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    colors: ButtonColors = ButtonDefaults.outlinedButtonColors(),
    content: @Composable () -> Unit,
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
        colors = colors,
        content = { content() },
    )
}

/**
 * Text (flat) variant of [KptButton].
 */
@Composable
fun KptTextButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    colors: ButtonColors = ButtonDefaults.textButtonColors(),
    content: @Composable () -> Unit,
) {
    TextButton(
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
        colors = colors,
        content = { content() },
    )
}
