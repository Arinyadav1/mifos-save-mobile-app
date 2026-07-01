/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/kmp-project-template/blob/main/LICENSE
 */
package org.mifos.core.base.store.freshness

import kotlin.time.Duration
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

/**
 * Pure computation of [org.mifos.core.base.store.freshness.FreshnessBand] from time + last-error inputs only.
 *
 * Decision table (first match wins):
 *  1. `lastSyncedAt == null && lastError == null` → [org.mifos.core.base.store.freshness.FreshnessBand.Initial]
 *  2. `lastError != null`                         → [org.mifos.core.base.store.freshness.FreshnessBand.VeryStale]
 *  3. `age <= ttl`                                → [org.mifos.core.base.store.freshness.FreshnessBand.Fresh]
 *  4. `age <= ttl * 3`                            → [org.mifos.core.base.store.freshness.FreshnessBand.Stale]
 *  5. else                                        → [org.mifos.core.base.store.freshness.FreshnessBand.VeryStale]
 *
 * **No NetworkMonitor input.** Network state is rendered separately by
 * `ConnectivityBanner`; freshness is purely "how old is the data".
 */
@OptIn(ExperimentalTime::class)
object FreshnessBands {
    fun bandFor(
        now: Instant,
        lastSyncedAt: Instant?,
        ttl: Duration,
        lastError: Throwable?,
    ): org.mifos.core.base.store.freshness.FreshnessBand = when {
        lastError != null -> _root_ide_package_.org.mifos.core.base.store.freshness.FreshnessBand.VeryStale
        lastSyncedAt == null -> _root_ide_package_.org.mifos.core.base.store.freshness.FreshnessBand.Initial
        else -> {
            val age = now - lastSyncedAt
            when {
                age <= ttl -> _root_ide_package_.org.mifos.core.base.store.freshness.FreshnessBand.Fresh
                age <= ttl * 3 -> _root_ide_package_.org.mifos.core.base.store.freshness.FreshnessBand.Stale
                else -> _root_ide_package_.org.mifos.core.base.store.freshness.FreshnessBand.VeryStale
            }
        }
    }
}
