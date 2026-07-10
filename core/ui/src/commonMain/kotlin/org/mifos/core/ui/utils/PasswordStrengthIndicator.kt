/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See See https://github.com/openMF/kmp-project-template/blob/main/LICENSE
 */
package org.mifos.core.ui.utils

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import org.mifos.core.base.designsystem.theme.KptTheme
import org.mifos.core.designsystem.icon.AppIcons

@Suppress("LongMethod", "CyclomaticComplexMethod", "MagicNumber")
@Composable
fun PasswordStrengthIndicator(
    state: PasswordStrength,
    currentCharacterCount: Int,
    modifier: Modifier = Modifier,
    minimumCharacterCount: Int? = null,
) {
    val widthPercent by animateFloatAsState(
        targetValue = when (state) {
            PasswordStrength.LEVEL_0 -> 0f
            PasswordStrength.LEVEL_1 -> .25f
            PasswordStrength.LEVEL_2 -> .5f
            PasswordStrength.LEVEL_3 -> .66f
            PasswordStrength.LEVEL_4 -> .82f
            PasswordStrength.LEVEL_5 -> 1f
            PasswordStrength.LEVEL_6 -> 1f
        },
        label = "Width Percent State",
    )
    val indicatorColor = when (state) {
        PasswordStrength.LEVEL_0 -> KptTheme.colorScheme.error
        PasswordStrength.LEVEL_1 -> KptTheme.colorScheme.error
        PasswordStrength.LEVEL_2 -> KptTheme.colorScheme.error
        PasswordStrength.LEVEL_3 -> weakColor
        PasswordStrength.LEVEL_4 -> KptTheme.colorScheme.primary
        PasswordStrength.LEVEL_5 -> strongColor
        PasswordStrength.LEVEL_6 -> Color.Magenta
    }
    val animatedIndicatorColor by animateColorAsState(
        targetValue = indicatorColor,
        label = "Indicator Color State",
    )
    val label = when (state) {
        PasswordStrength.LEVEL_0 -> ""
        PasswordStrength.LEVEL_1 -> "Weak"
        PasswordStrength.LEVEL_2 -> "Weak"
        PasswordStrength.LEVEL_3 -> "Weak"
        PasswordStrength.LEVEL_4 -> "Good"
        PasswordStrength.LEVEL_5 -> "Strong"
        PasswordStrength.LEVEL_6 -> "Very Strong"
    }
    Column(
        modifier = modifier,
    ) {
        Box(
            Modifier
                .fillMaxWidth()
                .height(KptTheme.spacing.xs)
                .background(KptTheme.colorScheme.surfaceContainerHigh),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(2.dp))
                    .graphicsLayer {
                        transformOrigin = TransformOrigin(pivotFractionX = 0f, pivotFractionY = 0f)
                        scaleX = widthPercent
                    }
                    .drawBehind {
                        drawRect(animatedIndicatorColor)
                    },
            )
        }
        Spacer(Modifier.height(KptTheme.spacing.xs))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            minimumCharacterCount?.let { minCount ->
                MinimumCharacterCount(
                    minimumRequirementMet = currentCharacterCount >= minCount,
                    minimumCharacterCount = minCount,
                )
            }
            Text(
                text = label,
                style = KptTheme.typography.labelSmall,
                color = indicatorColor,
            )
        }
    }
}

@Composable
private fun MinimumCharacterCount(
    minimumRequirementMet: Boolean,
    minimumCharacterCount: Int,
    modifier: Modifier = Modifier,
) {
    val characterCountColor by animateColorAsState(
        targetValue = if (minimumRequirementMet) {
            strongColor
        } else {
            KptTheme.colorScheme.onSurfaceVariant
        },
        label = "minimumCharacterCountColor",
    )
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        AnimatedContent(
            targetState = if (minimumRequirementMet) {
                AppIcons.CheckCircle
            } else {
                AppIcons.Close
            },
            label = "iconForMinimumCharacterCount",
        ) {
            Icon(
                imageVector = it,
                contentDescription = null,
                tint = characterCountColor,
                modifier = Modifier.size(12.dp),
            )
        }
        Spacer(modifier = Modifier.width(2.dp))
        Text(
            text = "$minimumCharacterCount characters",
            color = characterCountColor,
            style = KptTheme.typography.labelSmall,
        )
    }
}

@Suppress("LongMethod", "CyclomaticComplexMethod")
@Composable
fun CombinedPasswordErrorCard(
    passwordStrengthState: PasswordStrength,
    currentCharacterCount: Int,
    modifier: Modifier = Modifier,
    errorText: String? = null,
    errors: List<String> = emptyList(),
    minimumCharacterCount: Int? = null,
) {
    val hasErrors = errorText != null || errors.isNotEmpty()

    val widthPercent by animateFloatAsState(
        targetValue = when (passwordStrengthState) {
            PasswordStrength.LEVEL_0 -> 0f
            PasswordStrength.LEVEL_1 -> .25f
            PasswordStrength.LEVEL_2 -> .5f
            PasswordStrength.LEVEL_3 -> .66f
            PasswordStrength.LEVEL_4 -> .82f
            PasswordStrength.LEVEL_5 -> 1f
            PasswordStrength.LEVEL_6 -> 1f
        },
        label = "Width Percent State",
    )

    val indicatorColor = when (passwordStrengthState) {
        PasswordStrength.LEVEL_0 -> KptTheme.colorScheme.error
        PasswordStrength.LEVEL_1 -> KptTheme.colorScheme.error
        PasswordStrength.LEVEL_2 -> KptTheme.colorScheme.error
        PasswordStrength.LEVEL_3 -> weakColor
        PasswordStrength.LEVEL_4 -> Color.Magenta
        PasswordStrength.LEVEL_5 -> strongColor
        PasswordStrength.LEVEL_6 -> KptTheme.colorScheme.primary
    }

    val animatedIndicatorColor by animateColorAsState(
        targetValue = indicatorColor,
        label = "Indicator Color State",
    )

    val strengthLabel = when (passwordStrengthState) {
        PasswordStrength.LEVEL_0 -> ""
        PasswordStrength.LEVEL_1 -> "Weak"
        PasswordStrength.LEVEL_2 -> "Weak"
        PasswordStrength.LEVEL_3 -> "Weak"
        PasswordStrength.LEVEL_4 -> "Good"
        PasswordStrength.LEVEL_5 -> "Strong"
        PasswordStrength.LEVEL_6 -> "Very Strong"
    }

    AnimatedVisibility(visible = hasErrors) {
        Card(
            modifier = modifier
                .fillMaxWidth()
                .testTag("passwordErrorCard"),
            shape = KptTheme.shapes.medium,
            colors = CardDefaults.cardColors(
                containerColor = KptTheme.colorScheme.error.copy(alpha = 0.05f),
            ),
            border = BorderStroke(
                width = 1.dp,
                color = KptTheme.colorScheme.error.copy(alpha = 0.2f),
            ),
        ) {
            Column {
                // Top border strength indicator
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(KptTheme.spacing.xs)
                        .background(KptTheme.colorScheme.surfaceContainerHigh),
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp))
                            .graphicsLayer {
                                transformOrigin = TransformOrigin(pivotFractionX = 0f, pivotFractionY = 0f)
                                scaleX = widthPercent
                            }
                            .drawBehind {
                                drawRect(animatedIndicatorColor)
                            },
                    )
                }

                Column(
                    modifier = Modifier.padding(KptTheme.spacing.md),
                    verticalArrangement = Arrangement.spacedBy(KptTheme.spacing.xs),
                ) {
                    // Header row with "Password Requirements" and strength label
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                        ) {
                            Icon(
                                imageVector = AppIcons.OutlinedInfo,
                                contentDescription = "Error",
                                tint = KptTheme.colorScheme.error,
                                modifier = Modifier.size(16.dp),
                            )
                            Text(
                                text = "Password Requirements",
                                style = KptTheme.typography.labelMedium,
                                color = KptTheme.colorScheme.error,
                                fontWeight = FontWeight.Medium,
                            )
                        }

                        if (strengthLabel.isNotEmpty()) {
                            Box(
                                modifier = Modifier
                                    .background(
                                        color = animatedIndicatorColor,
                                        shape = KptTheme.shapes.extraSmall,
                                    )
                                    .padding(horizontal = KptTheme.spacing.sm, vertical = KptTheme.spacing.xs),
                            ) {
                                Text(
                                    text = strengthLabel,
                                    style = KptTheme.typography.labelMedium,
                                    color = Color.White,
                                    fontWeight = FontWeight.Medium,
                                )
                            }
                        }
                    }

                    // Minimum character count indicator if provided
                    minimumCharacterCount?.let { minCount ->
                        MinimumCharacterCount(
                            minimumRequirementMet = currentCharacterCount >= minCount,
                            minimumCharacterCount = minCount,
                        )
                    }

                    // Error text if provided
                    errorText?.let {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("passwordError"),
                            verticalAlignment = Alignment.Top,
                            horizontalArrangement = Arrangement.spacedBy(KptTheme.spacing.sm),
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(KptTheme.spacing.xs)
                                    .background(
                                        color = KptTheme.colorScheme.error,
                                        shape = CircleShape,
                                    )
                                    .padding(top = 6.dp),
                            )
                            Text(
                                text = it,
                                style = KptTheme.typography.labelSmall,
                                color = KptTheme.colorScheme.error,
                                modifier = Modifier.weight(1f),
                            )
                        }
                    }

                    // Error list
                    errors.forEachIndexed { index, error ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("passwordError_$index"),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(
                                space = KptTheme.spacing.xs,
                                alignment = Alignment.CenterHorizontally,
                            ),
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(KptTheme.spacing.xs)
                                    .background(
                                        color = KptTheme.colorScheme.error,
                                        shape = CircleShape,
                                    )
                                    .padding(top = 6.dp),
                            )
                            Text(
                                text = error,
                                style = KptTheme.typography.labelSmall,
                                color = KptTheme.colorScheme.error,
                                modifier = Modifier.weight(1f),
                            )
                        }
                    }
                }
            }
        }
    }
}

private val strongColor = Color(0xFF41B06D)
private val weakColor = Color(0xFF8B6609)
