/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/kmp-project-template/blob/main/LICENSE
 */
package org.mifos.core.base.ui.freshness

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import mifos.core.base.store.error.ErrorCategory
import mifos.core.base.store.freshness.FreshnessBand
import mifos.core.base.ui.freshness.toLongMessage

/**
 * Locks the pure-helper contracts inside `FreshnessIndicator.kt`:
 *  - [mifos.core.base.ui.freshness.freshnessVisualFor]   — band × errorCategory → (icon, tint)
 *  - [mifos.core.base.ui.freshness.freshnessTooltipTitle] — band × ageText × errorCategory → title string
 *  - [mifos.core.base.ui.freshness.freshnessTooltipBody]  — band × errorCategory → body string
 *
 * The Compose render path (TooltipBox + IconButton + Tooltip dismiss timing)
 * is validated manually on device — Compose UI test infra is not wired into
 * core-base/ui's commonTest at this time. Matches the pattern set by
 * `DataFreshnessIndicatorStateTest`.
 */
class FreshnessIndicatorTest {

    // === freshnessVisualFor ===

    @Test
    fun `Fresh band uses Neutral tint`() {
        val v = _root_ide_package_.mifos.core.base.ui.freshness.freshnessVisualFor(
            FreshnessBand.Fresh,
            errorCategory = null
        )
        assertEquals(_root_ide_package_.mifos.core.base.ui.freshness.FreshnessTint.Neutral, v.tint)
    }

    @Test
    fun `Stale band uses Tertiary tint`() {
        val v = _root_ide_package_.mifos.core.base.ui.freshness.freshnessVisualFor(
            FreshnessBand.Stale,
            errorCategory = null
        )
        assertEquals(_root_ide_package_.mifos.core.base.ui.freshness.FreshnessTint.Tertiary, v.tint)
    }

    @Test
    fun `VeryStale + error uses Error tint`() {
        val v = _root_ide_package_.mifos.core.base.ui.freshness.freshnessVisualFor(
            FreshnessBand.VeryStale,
            errorCategory = ErrorCategory.Network
        )
        assertEquals(_root_ide_package_.mifos.core.base.ui.freshness.FreshnessTint.Error, v.tint)
    }

    @Test
    fun `VeryStale without error (pure age-based) uses Tertiary tint`() {
        // Age > 3*ttl with no last error — show the warning icon but stay tertiary;
        // not an error condition, just heavily stale.
        val v = _root_ide_package_.mifos.core.base.ui.freshness.freshnessVisualFor(
            FreshnessBand.VeryStale,
            errorCategory = null
        )
        assertEquals(_root_ide_package_.mifos.core.base.ui.freshness.FreshnessTint.Tertiary, v.tint)
    }

    @Test
    fun `Initial band maps to Neutral but FreshnessIndicator returns early on Initial`() {
        // Defensive mapping — composable returns early before using this value,
        // but the pure helper still resolves it to a sensible neutral default.
        val v = _root_ide_package_.mifos.core.base.ui.freshness.freshnessVisualFor(
            FreshnessBand.Initial,
            errorCategory = null
        )
        assertEquals(_root_ide_package_.mifos.core.base.ui.freshness.FreshnessTint.Neutral, v.tint)
    }

    // === freshnessTooltipTitle ===

    @Test
    fun `Fresh tooltip title shows Updated ageText`() {
        assertEquals(
            "Updated 2m ago",
            _root_ide_package_.mifos.core.base.ui.freshness.freshnessTooltipTitle(
                FreshnessBand.Fresh,
                ageText = "2m ago",
                errorCategory = null
            ),
        )
    }

    @Test
    fun `Stale tooltip title shows Updated ageText`() {
        assertEquals(
            "Updated 8m ago",
            _root_ide_package_.mifos.core.base.ui.freshness.freshnessTooltipTitle(
                FreshnessBand.Stale,
                ageText = "8m ago",
                errorCategory = null
            ),
        )
    }

    @Test
    fun `VeryStale with error tooltip title prefixes error short-message`() {
        assertEquals(
            "No network · 5m ago",
            _root_ide_package_.mifos.core.base.ui.freshness.freshnessTooltipTitle(
                FreshnessBand.VeryStale,
                ageText = "5m ago",
                errorCategory = ErrorCategory.Network,
            ),
        )
    }

    @Test
    fun `VeryStale without error tooltip title falls back to Updated ageText`() {
        assertEquals(
            "Updated 25m ago",
            _root_ide_package_.mifos.core.base.ui.freshness.freshnessTooltipTitle(
                FreshnessBand.VeryStale,
                ageText = "25m ago",
                errorCategory = null
            ),
        )
    }

    // === freshnessTooltipBody ===

    @Test
    fun `Fresh body is empty (PlainTooltip shows title only)`() {
        assertEquals("",
            _root_ide_package_.mifos.core.base.ui.freshness.freshnessTooltipBody(
                FreshnessBand.Fresh,
                errorCategory = null
            )
        )
    }

    @Test
    fun `Stale body suggests refresh action`() {
        val body = _root_ide_package_.mifos.core.base.ui.freshness.freshnessTooltipBody(
            FreshnessBand.Stale,
            errorCategory = null
        )
        assertTrue(body.contains("refresh", ignoreCase = true), "body should hint at refresh: '$body'")
    }

    @Test
    fun `VeryStale with error body uses ErrorCategory toLongMessage`() {
        val body = _root_ide_package_.mifos.core.base.ui.freshness.freshnessTooltipBody(
            FreshnessBand.VeryStale,
            errorCategory = ErrorCategory.Network
        )
        assertEquals(ErrorCategory.Network.toLongMessage(), body)
    }

    @Test
    fun `VeryStale without error body provides generic fallback`() {
        val body = _root_ide_package_.mifos.core.base.ui.freshness.freshnessTooltipBody(
            FreshnessBand.VeryStale,
            errorCategory = null
        )
        assertTrue(body.isNotBlank(), "body should not be blank for VeryStale: '$body'")
    }
}
