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

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.mifos.core.base.designsystem.theme.KptAppColors
import org.mifos.core.base.designsystem.theme.KptTheme

@Composable
fun KptOtpInputField(
    value: String,
    onValueChange: (String) -> Unit,
    hasError: Boolean,
    enabled: Boolean,
    onVerifyClick: () -> Unit,
    isButtonEnabled: Boolean,
    modifier: Modifier = Modifier,
    length: Int = 6,
) {
    val focusRequester = remember { FocusRequester() }
    var isFieldFocused by remember { mutableStateOf(false) }

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            repeat(length) { index ->
                val char = value.getOrNull(index)?.toString() ?: ""
                val isBoxFocused = isFieldFocused && value.length == index

                OtpBox(
                    char = char,
                    isFocused = isBoxFocused,
                    hasError = hasError,
                    enabled = enabled,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Hidden input overlaid on top
        BasicTextField(
            value = value,
            onValueChange = { newValue ->
                if (newValue.length <= length && newValue.all { it.isDigit() }) {
                    onValueChange(newValue)
                }
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Done,
            ),
            keyboardActions = KeyboardActions(
                onDone = {
                    if (isButtonEnabled) {
                        onVerifyClick()
                    }
                }
            ),
            enabled = enabled,
            modifier = Modifier
                .matchParentSize()
                .alpha(0.01f)
                .focusRequester(focusRequester)
                .onFocusChanged { isFieldFocused = it.isFocused }
        )
    }

    // Auto-focus field on first compose
    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }
}

@Composable
private fun OtpBox(
    char: String,
    isFocused: Boolean,
    hasError: Boolean,
    enabled: Boolean,
    modifier: Modifier = Modifier,
) {
    val backgroundColor = when {
        !enabled -> KptTheme.colorScheme.surfaceContainerLow.copy(alpha = 0.38f)
        char.isNotEmpty() -> KptTheme.colorScheme.primary
        else -> KptTheme.colorScheme.surfaceContainerHigh
    }

    val textColor = when {
        char.isNotEmpty() -> KptTheme.colorScheme.onPrimary
        else -> KptTheme.colorScheme.onSurface
    }

    val borderModifier = when {
        hasError -> Modifier.border(2.dp, KptTheme.colorScheme.error, KptTheme.shapes.medium)
        isFocused -> Modifier.border(2.dp, KptTheme.colorScheme.primary, KptTheme.shapes.medium)
        else -> Modifier
    }

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .aspectRatio(1f)
            .clip(KptTheme.shapes.medium)
            .background(backgroundColor)
            .then(borderModifier)
            .padding(vertical = KptTheme.spacing.sm)
    ) {
        Text(
            text = char,
            style = KptTheme.typography.headlineMedium.copy(
                fontWeight = FontWeight.ExtraBold,
                color = textColor,
                fontSize = 24.sp,
            ),
            textAlign = TextAlign.Center,
        )
    }
}
