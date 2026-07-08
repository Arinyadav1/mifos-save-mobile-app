/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See See https://github.com/openMF/kmp-project-template/blob/main/LICENSE
 */
package org.mifos.core.data.util

/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mifos-x-field-officer-app/blob/master/LICENSE.md
 */

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import org.mifos.core.base.common.DataState
import org.mifos.core.base.store.screen.DataFreshness
import org.mifos.core.base.store.screen.ScreenState
import org.mifos.core.data.infra.NetworkMonitor

/**
 * Wraps a suspend function in a [DataState] wrapper with optional coroutine dispatcher.
 *
 * This extension function eliminates repetitive try-catch blocks in repository implementations
 * by automatically catching exceptions and converting them to [DataState.Error].
 *
 * @param context Optional [CoroutineDispatcher] to switch context for the block execution.
 *                 If provided, the block is executed with [withContext].
 * @param block The suspend lambda that performs the actual work.
 * @return [DataState.Success] with the result if successful, [DataState.Error] if exception occurs.
 *
 * Example usage:
 * override suspend fun approveCheckerEntry(auditId: Int): DataState<GenericResponse> {
 *     return runAsDataState(ioDispatcher) {
 *         dataManagerCheckerInbox.approveCheckerEntry(auditId)
 *     }
 * }
 */
suspend fun <T> runAsDataState(
    context: CoroutineDispatcher? = null,
    block: suspend () -> T,
): ScreenState<T> =
    try {
        val result = if (context != null) {
            withContext(context) { block() }
        } else {
            block()
        }

        ScreenState.Content(
            data = result,
            freshness = DataFreshness.FRESH,
        )
    } catch (e: Throwable) {
        ScreenState.Error(e)
    }

/**
 * Wraps a suspend function in a [DataState] wrapper with network connectivity check.
 *
 * This overload ensures the device is online before executing the block.
 * If offline, returns [DataState.Error] with [NetworkUnavailableException].
 * If online, delegates to the basic [runAsDataState] for execution.
 *
 * @param networkMonitor The [NetworkMonitor] to check network status.
 * @param context Optional [CoroutineDispatcher] to switch context for the block execution.
 * @param block The suspend lambda that performs the actual work.
 * @return [DataState.Error] with [NetworkUnavailableException] if offline,
 *         otherwise the result from [runAsDataState].
 *
 * Example usage:
 * override suspend fun disburseLoan(
 *     loanId: Int,
 *     loanDisbursement: LoanDisbursement?,
 * ): DataState<GenericResponse> {
 *     return runAsDataState(networkMonitor, ioDispatcher) {
 *         dataManagerLoan.disburseLoan(loanId, loanDisbursement)
 *     }
 * }
 */
suspend fun <T> runAsDataState(
    networkMonitor: NetworkMonitor,
    context: CoroutineDispatcher? = null,
    block: suspend () -> T,
): ScreenState<T> {
    if (!networkMonitor.isOnline.first()) {
        return ScreenState.NoNetwork(true)
    }
    return runAsDataState(context, block)
}
