/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See See https://github.com/openMF/kmp-project-template/blob/main/LICENSE
 */
package org.mifos.feature.home.ui

import org.mifos.core.base.store.screen.ScreenState
import org.mifos.core.base.ui.viewmodel.BaseViewModel
import org.mifos.core.data.banking.BillReminderRepository
import org.mifos.core.data.banking.LoanRepository
import org.mifos.core.model.banking.BillReminder
import org.mifos.core.model.banking.Loan
import org.mifos.core.model.currency.ExchangeRates

/**
 * **Money Toolkit home dashboard ViewModel.**
 *
 * Composes four independent reactive sources into a single UI state with
 * per-widget loading/empty/error/content slots. Each widget's state evolves
 * independently — one card can be Loading while another shows Content; pull-
 * to-refresh fans out to the network-backed streams concurrently.
 *
 * Sources split by character:
 *  - [exchangeRateStream] and the two FRED rate streams ([fedFundsStream],
 *    [mortgageStream]) are network-backed `ScreenDataStream`s with their own
 *    Loading/Error/Content + freshness state. The rate streams feed a single
 *    [RatesQuickView] slot via [combine].
 *  - [LoanRepository.observeAll] and [BillReminderRepository.observeUpcoming]
 *    are purely local reactive `Flow`s — mapped here into `ScreenState.Empty` /
 *    `ScreenState.Content(freshness = FRESH)` (same projection used elsewhere
 *    in the toolkit for local-only sources).
 */
class HomeViewModel : BaseViewModel<HomeUiState, Nothing, HomeAction>(HomeUiState()) {
    override fun handleAction(action: HomeAction) {
        TODO("Not yet implemented")
    }
}

/**
 * Aggregate state for the Money Toolkit home dashboard.
 *
 * Each slot is an independent [ScreenState] so the screen can render per-card
 * Loading / Empty / Error / Content states.
 */
data class HomeUiState(
    val loans: ScreenState<LoansSummary> = ScreenState.Loading,
    val bills: ScreenState<List<BillReminder>> = ScreenState.Loading,
    val rates: ScreenState<RatesQuickView> = ScreenState.Loading,
    val exchangeRate: ScreenState<ExchangeRates> = ScreenState.Loading,
)

/**
 * Compact projection of the user's loan portfolio for the home dashboard.
 *
 * Only the dashboard's summary surface needs these aggregates; the full list
 * lives on the Loans screen.
 *
 * @property count Number of tracked loans.
 * @property totalMonthlyEmi Sum of every loan's monthly EMI.
 * @property totalOutstanding Sum of every loan's remaining principal.
 */
data class LoansSummary(
    val count: Int,
    val totalMonthlyEmi: Double,
    val totalOutstanding: Double,
    /**
     * The actual loans, sorted soonest-due first. The home dashboard hero renders these as a
     * horizontally-scrollable carousel below the totals so users can flip through every loan
     * without leaving the dashboard.
     */
    val loans: List<Loan> = emptyList(),
)

/**
 * Compact projection of the two headline rate series for the home dashboard.
 *
 * @property fedFundsPercent Latest Effective Federal Funds Rate, in percent.
 * @property mortgage30YPercent Latest 30-Year Fixed Mortgage Average, in percent.
 */
data class RatesQuickView(
    val fedFundsPercent: Double,
    val mortgage30YPercent: Double,
)

sealed interface HomeAction {
    /** Pull-to-refresh — fans out to every network-backed stream. */
    data object RefreshAll : HomeAction

    /** Retry just the Exchange Rate widget (after an error). */
    data object RetryExchangeRate : HomeAction

    /** Retry the Rates Quick widget (refreshes both FRED-backed streams). */
    data object RetryRates : HomeAction
}
