/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See See https://github.com/openMF/kmp-project-template/blob/main/LICENSE
 */
package org.mifos.core.designsystem.theme

import androidx.compose.ui.graphics.Color

// =============================================================================
//  LIGHT THEME COLOR SCHEME SYSTEM
// =============================================================================

/**
 * Trust Indigo primary theme color for light mode. Used for high-emphasis UI elements,
 * main branding accents, and prominent buttons.
 */
val primaryLight = Color(0xFF3A608F)

/**
 * Color used for content (text, icons) displayed on top of the [primaryLight] color.
 */
val onPrimaryLight = Color(0xFFFFFFFF)

/**
 * Preferred container color for primary branding elements requiring less emphasis than [primaryLight].
 */
val primaryContainerLight = Color(0xFFD3E3FF)

/**
 * Color used for content (text, icons) displayed on top of the [primaryContainerLight] container.
 */
val onPrimaryContainerLight = Color(0xFF1F4876)

/**
 * Secondary color for light theme. Applied to medium-emphasis components, secondary CTAs,
 * and supportive visual highlights.
 */
val secondaryLight = Color(0xFF545F70)

/**
 * Color used for content (text, icons) displayed on top of the [secondaryLight] color.
 */
val onSecondaryLight = Color(0xFFFFFFFF)

/**
 * Preferred container color for secondary elements requiring lower emphasis.
 */
val secondaryContainerLight = Color(0xFFD8E3F8)

/**
 * Color used for content (text, icons) displayed on top of the [secondaryContainerLight] container.
 */
val onSecondaryContainerLight = Color(0xFF3C4758)

/**
 * Tertiary accent color for light theme. Used for badges, custom labels, warning states, and amber highlights.
 */
val tertiaryLight = Color(0xFF6D5677)

/**
 * Color used for content (text, icons) displayed on top of the [tertiaryLight] color.
 */
val onTertiaryLight = Color(0xFFFFFFFF)

/**
 * Preferred container color for tertiary elements requiring lower emphasis.
 */
val tertiaryContainerLight = Color(0xFFF5D9FF)

/**
 * Color used for content (text, icons) displayed on top of the [tertiaryContainerLight] container.
 */
val onTertiaryContainerLight = Color(0xFF543F5E)

/**
 * Semantic error color for light theme. Signals warning states, failures, and destructive inputs.
 */
val errorLight = Color(0xFFBA1A1A)

/**
 * Color used for content (text, icons) displayed on top of the [errorLight] error color.
 */
val onErrorLight = Color(0xFFFFFFFF)

/**
 * Container color for error indicators, banners, and fields with validation issues.
 */
val errorContainerLight = Color(0xFFFFDAD6)

/**
 * Color used for content (text, icons) displayed on top of the [errorContainerLight] container.
 */
val onErrorContainerLight = Color(0xFF93000A)

/**
 * The standard backdrop color for screens and scrollable areas in light mode.
 */
val backgroundLight = Color(0xFFF8F9FF)

/**
 * Color used for content (text, icons) displayed directly on top of [backgroundLight].
 */
val onBackgroundLight = Color(0xFF191C20)

/**
 * Color applied to structural surface elements such as cards, sheets, and menus in light mode.
 */
val surfaceLight = Color(0xFFF8F9FF)

/**
 * Color used for content (text, icons) displayed directly on top of [surfaceLight].
 */
val onSurfaceLight = Color(0xFF191C20)

/**
 * A variant of the surface color used to distinguish supportive background content.
 */
val surfaceVariantLight = Color(0xFFDFE2EB)

/**
 * Color used for content (text, icons) displayed directly on top of [surfaceVariantLight].
 */
val onSurfaceVariantLight = Color(0xFF43474E)

/**
 * Color for boundaries, borders, dividers, and decorative outlines.
 */
val outlineLight = Color(0xFF73777F)

/**
 * A lighter variant of the outline color for sub-dividers and subtle boundaries.
 */
val outlineVariantLight = Color(0xFFC3C6CF)

/**
 * The overlay color used to mask components beneath dialogs or modal bottom sheets.
 */
val scrimLight = Color(0xFF000000)

/**
 * Inverted surface color used for snackbars and banners requiring strong visual contrast against light surfaces.
 */
val inverseSurfaceLight = Color(0xFF2E3035)

/**
 * Color used for content (text, icons) displayed directly on top of [inverseSurfaceLight].
 */
val inverseOnSurfaceLight = Color(0xFFEFF0F7)

/**
 * The inverted primary brand color for light surfaces.
 */
val inversePrimaryLight = Color(0xFFA4C9FE)

// --- Light Surface Tonal Ladder ---

/**
 * Slightly dimmed surface variant for depth variation in light mode.
 */
val surfaceDimLight = Color(0xFFD9DAE0)

/**
 * Brightest surface variant for elevated layouts in light mode.
 */
val surfaceBrightLight = Color(0xFFF8F9FF)

/**
 * The lowest level in the surface container hierarchy.
 */
val surfaceContainerLowestLight = Color(0xFFFFFFFF)

/**
 * Low-level surface container for nested cards or structural panels.
 */
val surfaceContainerLowLight = Color(0xFFF2F3FA)

/**
 * Default surface container color for cards, panels, and input areas.
 */
val surfaceContainerLight = Color(0xFFEDEDF4)

/**
 * Higher level surface container color to emphasize floating structures.
 */
val surfaceContainerHighLight = Color(0xFFE7E8EE)

/**
 * Highest level surface container color for maximum structural separation.
 */
val surfaceContainerHighestLight = Color(0xFFE1E2E9)

// =============================================================================
//  DARK THEME COLOR SCHEME SYSTEM
// =============================================================================

/**
 * Trust Indigo primary theme color for dark mode. Used for high-emphasis UI elements,
 * main branding accents, and prominent buttons.
 */
val primaryDark = Color(0xFFA4C9FE)

/**
 * Color used for content (text, icons) displayed on top of the [primaryDark] color.
 */
val onPrimaryDark = Color(0xFF00315C)

/**
 * Preferred container color for primary branding elements requiring less emphasis than [primaryDark] in dark mode.
 */
val primaryContainerDark = Color(0xFF1F4876)

/**
 * Color used for content (text, icons) displayed on top of the [primaryContainerDark] container.
 */
val onPrimaryContainerDark = Color(0xFFD3E3FF)

/**
 * Secondary color for dark theme. Applied to medium-emphasis components, secondary CTAs,
 * and supportive visual highlights.
 */
val secondaryDark = Color(0xFFBCC7DB)

/**
 * Color used for content (text, icons) displayed on top of the [secondaryDark] color.
 */
val onSecondaryDark = Color(0xFF263141)

/**
 * Preferred container color for secondary elements requiring lower emphasis in dark mode.
 */
val secondaryContainerDark = Color(0xFF3C4758)

/**
 * Color used for content (text, icons) displayed on top of the [secondaryContainerDark] container.
 */
val onSecondaryContainerDark = Color(0xFFD8E3F8)

/**
 * Tertiary accent color for dark theme. Used for badges, custom labels, warning states, and amber highlights.
 */
val tertiaryDark = Color(0xFFD9BDE3)

/**
 * Color used for content (text, icons) displayed on top of the [tertiaryDark] color.
 */
val onTertiaryDark = Color(0xFF3C2946)

/**
 * Preferred container color for tertiary elements requiring lower emphasis in dark mode.
 */
val tertiaryContainerDark = Color(0xFF543F5E)

/**
 * Color used for content (text, icons) displayed on top of the [tertiaryContainerDark] container.
 */
val onTertiaryContainerDark = Color(0xFFF5D9FF)

/**
 * Signals warning states, failures, and destructive inputs in dark mode.
 */
val errorDark = Color(0xFFFFB4AB)

/**
 * Color used for content (text, icons) displayed on top of the [errorDark] error color.
 */
val onErrorDark = Color(0xFF690005)

/**
 * Container color for error indicators, banners, and fields with validation issues in dark mode.
 */
val errorContainerDark = Color(0xFF93000A)

/**
 * Color used for content (text, icons) displayed on top of the [errorContainerDark] container.
 */
val onErrorContainerDark = Color(0xFFFFDAD6)

/**
 * The standard backdrop color for screens and scrollable areas in dark mode.
 */
val backgroundDark = Color(0xFF111318)

/**
 * Color used for content (text, icons) displayed directly on top of [backgroundDark].
 */
val onBackgroundDark = Color(0xFFE1E2E9)

/**
 * Color applied to structural surface elements such as cards, sheets, and menus in dark mode.
 */
val surfaceDark = Color(0xFF111318)

/**
 * Color used for content (text, icons) displayed directly on top of [surfaceDark].
 */
val onSurfaceDark = Color(0xFFE1E2E9)

/**
 * A variant of the surface color used to distinguish supportive background content.
 */
val surfaceVariantDark = Color(0xFF43474E)

/**
 * Color used for content (text, icons) displayed directly on top of [surfaceVariantDark].
 */
val onSurfaceVariantDark = Color(0xFFC3C6CF)

/**
 * Color for boundaries, borders, dividers, and decorative outlines in dark mode.
 */
val outlineDark = Color(0xFF8D9199)

/**
 * A lighter variant of the outline color for sub-dividers and subtle boundaries in dark mode.
 */
val outlineVariantDark = Color(0xFF43474E)

/**
 * The overlay color used to mask components beneath dialogs or modal bottom sheets in dark mode.
 */
val scrimDark = Color(0xFF000000)

/**
 * Inverted surface color used for snackbars and banners requiring strong visual contrast against dark surfaces.
 */
val inverseSurfaceDark = Color(0xFFE1E2E9)

/**
 * Color used for content (text, icons) displayed directly on top of [inverseSurfaceDark].
 */
val inverseOnSurfaceDark = Color(0xFF2E3035)

/**
 * The inverted primary brand color for dark surfaces.
 */
val inversePrimaryDark = Color(0xFF3A608F)

// --- Dark Surface Tonal Ladder ---

/**
 * Slightly dimmed surface variant for depth variation in dark mode.
 */
val surfaceDimDark = Color(0xFF111318)

/**
 * Brightest surface variant for elevated layouts in dark mode.
 */
val surfaceBrightDark = Color(0xFF37393E)

/**
 * The lowest level in the surface container hierarchy.
 */
val surfaceContainerLowestDark = Color(0xFF0C0E13)

/**
 * Low-level surface container for nested cards or structural panels in dark mode.
 */
val surfaceContainerLowDark = Color(0xFF191C20)

/**
 * Default surface container color for cards, panels, and input areas in dark mode.
 */
val surfaceContainerDark = Color(0xFF1D2024)

/**
 * Higher level surface container color to emphasize floating structures in dark mode.
 */
val surfaceContainerHighDark = Color(0xFF272A2F)

/**
 * Highest level surface container color for maximum structural separation in dark mode.
 */
val surfaceContainerHighestDark = Color(0xFF32353A)
