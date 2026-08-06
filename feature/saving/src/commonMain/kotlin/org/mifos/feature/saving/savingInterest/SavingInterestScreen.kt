/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See See https://github.com/openMF/kmp-project-template/blob/main/LICENSE
 */
package org.mifos.feature.saving.savingInterest

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import org.mifos.core.base.designsystem.theme.KptTheme
import org.mifos.core.base.store.screen.dataOrNull
import org.mifos.core.base.ui.effects.EventsEffect
import org.mifos.core.base.ui.screen.DefaultLoadingContent
import org.mifos.core.base.ui.screen.ScreenContent
import org.mifos.core.base.ui.screen.ScreenStateLoading
import org.mifos.core.designsystem.component.KptHeader
import org.mifos.core.designsystem.component.KptHeaderBackButton
import org.mifos.core.designsystem.component.KptHeaderTitle
import org.mifos.core.designsystem.component.KptKeyValueCard
import org.mifos.core.designsystem.component.KptStatsCard
import org.mifos.core.designsystem.component.SectionHeader
import org.mifos.core.designsystem.icon.AppIcons
import org.mifos.core.ui.scaffold.KptScaffold
import org.mifos.feature.saving.generated.resources.Res
import org.mifos.feature.saving.generated.resources.feature_saving_compounding_period
import org.mifos.feature.saving.generated.resources.feature_saving_days_in_year
import org.mifos.feature.saving.generated.resources.feature_saving_interest_calculation
import org.mifos.feature.saving.generated.resources.feature_saving_interest_details_title
import org.mifos.feature.saving.generated.resources.feature_saving_interest_not_posted
import org.mifos.feature.saving.generated.resources.feature_saving_interest_rate
import org.mifos.feature.saving.generated.resources.feature_saving_interest_showcase_title
import org.mifos.feature.saving.generated.resources.feature_saving_interest_summary_title
import org.mifos.feature.saving.generated.resources.feature_saving_posting_period
import org.mifos.feature.saving.generated.resources.feature_saving_running_balance_on_interest_posting_till_date
import org.mifos.feature.saving.generated.resources.feature_saving_running_balance_on_pivot_date
import org.mifos.feature.saving.generated.resources.feature_saving_total_interest_posted
import org.mifos.feature.saving.generated.resources.feature_saving_total_overdraft_interest_derived

@Composable
fun SavingInterestScreen(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SavingInterestViewModel = koinViewModel(),
) {
    val state by viewModel.stateFlow.collectAsStateWithLifecycle()

    EventsEffect(viewModel.eventFlow) { event ->
        when (event) {
            SavingInterestEvent.NavigateBack -> onBackClick()
        }
    }

    SavingInterestScreenContent(
        state = state,
        onAction = viewModel::trySendAction,
        modifier = modifier,
    )
}

@Composable
internal fun SavingInterestScreenContent(
    state: SavingInterestState,
    onAction: (SavingInterestAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    KptScaffold(
        modifier = modifier,
        containerColor = KptTheme.colorScheme.primary,
        bottomBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(KptTheme.colorScheme.surface)
                    .navigationBarsPadding(),
            )
        },
        topBar = {
            val details = state.screenState.dataOrNull
            KptHeader(
                navigationIcon = {
                    KptHeaderBackButton(onClick = { onAction(SavingInterestAction.OnBackClick) })
                },
                title = {
                    KptHeaderTitle(
                        title = details?.savingsProductName ?: "",
                        subtitle = stringResource(Res.string.feature_saving_interest_showcase_title),
                        maxLines = 2,
                    )
                },
            )
        },
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    color = KptTheme.colorScheme.surface,
                    shape = RoundedCornerShape(
                        topStart = KptTheme.spacing.lg,
                        topEnd = KptTheme.spacing.lg,
                    ),
                ),
        ) {
            ScreenContent(
                state = state.screenState,
                onRetry = { onAction(SavingInterestAction.Retry) },
                loading = {
                    DefaultLoadingContent(
                        config = ScreenStateLoading.Spinner,
                    )
                },
                modifier = Modifier.fillMaxSize(),
            ) { detail, _ ->
                val symbol = detail.currency?.displaySymbol ?: "$"
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = KptTheme.spacing.md)
                        .padding(bottom = KptTheme.spacing.xl, top = KptTheme.spacing.sm),
                    verticalArrangement = Arrangement.spacedBy(KptTheme.spacing.md),
                ) {
                    SectionHeader(
                        title = stringResource(Res.string.feature_saving_interest_summary_title),
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(KptTheme.spacing.sm),
                    ) {
                        KptStatsCard(
                            value = "$symbol${detail.summary?.totalInterestPosted ?: 0.0}",
                            label = stringResource(Res.string.feature_saving_total_interest_posted),
                            icon = AppIcons.Savings,
                            iconContainerColor = KptTheme.colorScheme.primaryContainer,
                            iconColor = KptTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.weight(1f),
                        )

                        KptStatsCard(
                            value = "$symbol${detail.summary?.interestNotPosted ?: 0.0}",
                            label = stringResource(Res.string.feature_saving_interest_not_posted),
                            icon = AppIcons.OutlinedInfo,
                            iconContainerColor = KptTheme.colorScheme.secondaryContainer,
                            iconColor = KptTheme.colorScheme.onSecondaryContainer,
                            modifier = Modifier.weight(1f),
                        )
                    }

                    KptStatsCard(
                        value = "$symbol${detail.summary?.totalOverdraftInterestDerived ?: 0.0}",
                        label = stringResource(Res.string.feature_saving_total_overdraft_interest_derived),
                        icon = AppIcons.AttachMoney,
                        iconContainerColor = KptTheme.colorScheme.tertiaryContainer,
                        iconColor = KptTheme.colorScheme.onTertiaryContainer,
                        modifier = Modifier.fillMaxWidth(),
                    )

                    SectionHeader(
                        title = stringResource(Res.string.feature_saving_interest_details_title),
                    )

                    KptKeyValueCard(
                        items = mapOf(
                            stringResource(Res.string.feature_saving_interest_rate) to
                                "${detail.nominalAnnualInterestRate}%",
                            stringResource(Res.string.feature_saving_compounding_period) to
                                (detail.interestCompoundingPeriodType?.value ?: "—"),
                            stringResource(Res.string.feature_saving_posting_period) to
                                (detail.interestPostingPeriodType?.value ?: "—"),
                            stringResource(Res.string.feature_saving_interest_calculation) to
                                (detail.interestCalculationType?.value ?: "—"),
                            stringResource(Res.string.feature_saving_days_in_year) to
                                (detail.interestCalculationDaysInYearType?.value ?: "—"),
                            stringResource(Res.string.feature_saving_running_balance_on_interest_posting_till_date) to
                                "$symbol${detail.summary?.runningBalanceOnInterestPostingTillDate ?: 0.0}",
                            stringResource(Res.string.feature_saving_running_balance_on_pivot_date) to
                                "$symbol${detail.summary?.runningBalanceOnPivotDate ?: 0.0}",
                        ),
                    )
                }
            }
        }
    }
}
