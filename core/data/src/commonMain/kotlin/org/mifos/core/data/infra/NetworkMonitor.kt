/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See See https://github.com/openMF/kmp-project-template/blob/main/LICENSE
 */
package org.mifos.core.data.infra

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import org.mifos.core.base.store.screen.ScreenState

/**
 * Backward-compatible typealias — existing consumers keep their import.
 * Delegates to cmp-network-monitor's full-featured NetworkMonitor interface.
 */
typealias NetworkMonitor = io.github.mobilebytelabs.kmptoolkit.networkmonitor.NetworkMonitor

/**
 * Wraps an [upstream] [ScreenState] [Flow] with a reactive network guard.
 *
 * Uses [combine] internally so the merge function re-runs whenever *either*
 * [isOnline] **or** [upstream] emits a new value.
 *
 * Emission priority (evaluated top-to-bottom on every pair of values):
 *  1. **[DataState.Success]**  — always forwarded; cached data survives going offline.
 *  2. **[DataState.Loading]**  — always forwarded; lets the UI render a spinner
 *                                before any network error is surfaced.
 *  3. **offline**              — emits [NetworkUnavailableException].
 *  4. **otherwise**            — forwards whatever error the upstream emitted.
 *
 * ```kotlin
 * override fun getLoans(): Flow<DataState<List<Loan>>> =
 *     networkMonitor.withNetworkCheck(
 *         dataManager.getLoans().asDataStateFlow()
 *     ).flowOn(ioDispatcher)
 * ```
 */
fun <T> NetworkMonitor.withNetworkCheck(
    upstream: Flow<ScreenState<T>>,
): Flow<ScreenState<T>> = combine(isOnline, upstream) { isOnline, screenState ->
    when {
        screenState is ScreenState.Content -> screenState
        screenState is ScreenState.Loading -> screenState
        !isOnline -> ScreenState.NoNetwork(true)
        else -> screenState
    }
}
