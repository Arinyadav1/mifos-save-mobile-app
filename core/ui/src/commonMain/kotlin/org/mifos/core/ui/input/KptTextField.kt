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

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.withStyle
import org.mifos.core.base.designsystem.theme.KptTheme
import org.mifos.core.designsystem.icon.AppIcons

/**
 * A standard styled TextField that follows the KPT design system guidelines.
 */
@Suppress("LongParameterList", "CyclomaticComplexMethod")
@Composable
fun KptTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    readOnly: Boolean = false,
    textStyle: TextStyle = LocalTextStyle.current,
    label: String? = null,
    placeholder: String? = null,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    onCalenderClick: (() -> Unit)? = null,
    errorText: String? = null,
    isPassword: Boolean = false,
    isPasswordVisible: Boolean = false,
    onTogglePasswordVisibility: (() -> Unit)? = null,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    singleLine: Boolean = true,
    maxLines: Int = if (singleLine) 1 else Int.MAX_VALUE,
    minLines: Int = 1,
    shape: Shape = KptTheme.shapes.medium,
    colors: TextFieldColors = KptTextFieldDefaults.textFieldColors(),
) {
    var internalPasswordVisible by remember { mutableStateOf(false) }
    val resolvedPasswordVisible =
        if (onTogglePasswordVisibility != null) isPasswordVisible else internalPasswordVisible
    val resolvedToggle =
        onTogglePasswordVisibility ?: { internalPasswordVisible = !internalPasswordVisible }

    val actualVisualTransformation = if (isPassword) {
        if (resolvedPasswordVisible) VisualTransformation.None else PasswordVisualTransformation()
    } else {
        VisualTransformation.None
    }

    val actualKeyboardOptions = if (isPassword && keyboardOptions == KeyboardOptions.Default) {
        KeyboardOptions(keyboardType = KeyboardType.Password)
    } else {
        keyboardOptions
    }

    val actualTrailingIcon: @Composable () -> Unit = {
        when {
            isPassword -> {
                IconButton(
                    onClick = resolvedToggle,
                    enabled = enabled,
                ) {
                    Icon(
                        imageVector = if (resolvedPasswordVisible) {
                            AppIcons.VisibilityOff
                        } else {
                            AppIcons.Visibility
                        },
                        contentDescription = if (resolvedPasswordVisible) "Hide password" else "Show password",
                    )
                }
            }
            onCalenderClick != null -> {
                IconButton(
                    onClick = { onCalenderClick() },
                ) {
                    Icon(
                        imageVector = AppIcons.Calendar,
                        contentDescription = null,
                    )
                }
            }

            else -> {
                trailingIcon?.invoke()
            }
        }
    }

    Column(modifier = modifier) {
        if (label != null) {
            val asteriskColor = KptTheme.colorScheme.error
            val labelText = remember(label, asteriskColor) {
                if (label.endsWith(" *")) {
                    buildAnnotatedString {
                        append(label.substringBeforeLast(" *"))
                        withStyle(SpanStyle(color = asteriskColor)) {
                            append(" *")
                        }
                    }
                } else if (label.endsWith("*")) {
                    buildAnnotatedString {
                        append(label.substringBeforeLast("*"))
                        withStyle(SpanStyle(color = asteriskColor)) {
                            append("*")
                        }
                    }
                } else {
                    AnnotatedString(label)
                }
            }
            Text(
                text = labelText,
                style = KptTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.SemiBold,
                    color = if (enabled) {
                        KptTheme.colorScheme.onSurfaceVariant
                    } else {
                        KptTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.38f)
                    },
                ),
                modifier = Modifier.padding(bottom = KptTheme.spacing.xs),
            )
        }

        TextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            enabled = enabled,
            readOnly = readOnly,
            textStyle = textStyle,
            placeholder = placeholder?.let {
                {
                    Text(
                        text = it,
                        color = KptTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.38f),
                    )
                }
            },
            leadingIcon = leadingIcon,
            trailingIcon = actualTrailingIcon,
            isError = errorText != null,
            visualTransformation = actualVisualTransformation,
            keyboardOptions = actualKeyboardOptions,
            keyboardActions = keyboardActions,
            singleLine = singleLine,
            maxLines = maxLines,
            minLines = minLines,
            shape = shape,
            colors = colors,
        )

        if (errorText != null) {
            Text(
                text = errorText,
                color = KptTheme.colorScheme.error,
                style = KptTheme.typography.bodySmall,
                modifier = Modifier.padding(
                    top = KptTheme.spacing.xs,
                    start = KptTheme.spacing.sm,
                ),
            )
        }
    }
}

object KptTextFieldDefaults {
    @Composable
    fun textFieldColors(
        focusedContainerColor: Color = KptTheme.colorScheme.surfaceContainer,
        unfocusedContainerColor: Color = KptTheme.colorScheme.surfaceContainer,
        disabledContainerColor: Color = KptTheme.colorScheme.surfaceContainerLow,
        errorContainerColor: Color = KptTheme.colorScheme.surfaceContainer,
        focusedIndicatorColor: Color = Color.Transparent,
        unfocusedIndicatorColor: Color = Color.Transparent,
        disabledIndicatorColor: Color = Color.Transparent,
        errorIndicatorColor: Color = Color.Transparent,
    ): TextFieldColors {
        return TextFieldDefaults.colors(
            focusedContainerColor = focusedContainerColor,
            unfocusedContainerColor = unfocusedContainerColor,
            disabledContainerColor = disabledContainerColor,
            errorContainerColor = errorContainerColor,
            focusedIndicatorColor = focusedIndicatorColor,
            unfocusedIndicatorColor = unfocusedIndicatorColor,
            disabledIndicatorColor = disabledIndicatorColor,
            errorIndicatorColor = errorIndicatorColor,
        )
    }
}
