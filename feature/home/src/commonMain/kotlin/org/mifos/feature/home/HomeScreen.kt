/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See See https://github.com/openMF/kmp-project-template/blob/main/LICENSE
 */
package org.mifos.feature.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import org.mifos.core.base.designsystem.component.HorizontalSpacer
import org.mifos.core.base.designsystem.component.VerticalSpacer
import org.mifos.core.base.designsystem.theme.KptTheme
import org.mifos.core.base.ui.screen.DefaultLoadingContent
import org.mifos.core.base.ui.screen.ScreenContent
import org.mifos.core.base.ui.screen.ScreenStateLoading
import org.mifos.core.designsystem.component.KptHeader
import org.mifos.core.designsystem.component.KptHeaderActionButton
import org.mifos.core.designsystem.component.KptHeaderProfile
import org.mifos.core.designsystem.component.KptStatsCard
import org.mifos.core.designsystem.icon.AppIcons
import org.mifos.core.ui.scaffold.KptScaffold
import org.mifos.core.ui.scaffold.rememberKptPullToRefreshState
import org.mifos.feature.home.generated.resources.Res
import org.mifos.feature.home.generated.resources.feature_home_groups
import org.mifos.feature.home.generated.resources.feature_home_members
import org.mifos.feature.home.generated.resources.feature_home_overview

@Composable
internal fun HomeScreen(
    viewModel: HomeViewModel = koinViewModel(),
) {
    val state by viewModel.stateFlow.collectAsStateWithLifecycle()

    KptScaffold(
        pullToRefreshState = rememberKptPullToRefreshState(
            isRefreshing = state.isRefreshing,
            onRefresh = { viewModel.trySendAction(HomeAction.Refresh) },
        ),
        topBar = {
            KptHeader(
                navigationIcon = {
                    KptHeaderProfile(
                        avatarText = state.avatarText,
                        greeting = state.greeting?.let { stringResource(it) }.orEmpty(),
                        name = state.username,
                        onProfileClick = {},
                    )
                },
                actions = {
                    KptHeaderActionButton(
                        icon = Icons.Default.Notifications,
                        onClick = {},
                    )
                    HorizontalSpacer(width = KptTheme.spacing.sm)
                    KptHeaderActionButton(
                        icon = Icons.Default.Settings,
                        onClick = {},
                    )
                },
                title = {
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(100.dp))
                            .background(KptTheme.colorScheme.onPrimary.copy(alpha = 0.12f))
                            .padding(
                                horizontal = KptTheme.spacing.md,
                                vertical = KptTheme.spacing.sm,
                            ),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Icon(
                            imageVector = AppIcons.Calendar,
                            contentDescription = null,
                            tint = KptTheme.colorScheme.onPrimary,
                            modifier = Modifier.size(16.dp),
                        )
                        HorizontalSpacer(width = KptTheme.spacing.xs)
                        Text(
                            text = state.formattedDate,
                            style = KptTheme.typography.bodyMedium.copy(
                                color = KptTheme.colorScheme.onPrimary,
                                fontWeight = FontWeight.Medium,
                            ),
                        )
                    }
                },
                content = null,
            )
        },
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(KptTheme.colorScheme.primary),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(
                        shape = RoundedCornerShape(
                            topStart = KptTheme.spacing.lg,
                            topEnd = KptTheme.spacing.lg,
                        ),
                    )
                    .background(KptTheme.colorScheme.background)
                    .padding(KptTheme.spacing.lg),
            ) {
                Text(
                    text = stringResource(Res.string.feature_home_overview),
                    style = KptTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = KptTheme.colorScheme.onBackground,
                    ),
                )
                VerticalSpacer(height = KptTheme.spacing.md)

                ScreenContent(
                    state = state.screenState,
                    onRetry = { viewModel.trySendAction(HomeAction.Refresh) },
                    loading = {
                        DefaultLoadingContent(
                            config = ScreenStateLoading.Spinner,
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                ) { _, _ ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(KptTheme.spacing.md),
                    ) {
                        KptStatsCard(
                            value = state.totalGroups.toString(),
                            label = stringResource(Res.string.feature_home_groups),
                            icon = AppIcons.Group,
                            iconContainerColor = KptTheme.colorScheme.primaryContainer.copy(alpha = 0.4f),
                            iconColor = KptTheme.colorScheme.primary,
                            modifier = Modifier.weight(1f),
                        )
                        KptStatsCard(
                            value = state.totalMembers.toString(),
                            label = stringResource(Res.string.feature_home_members),
                            icon = AppIcons.Person,
                            iconContainerColor = KptTheme.colorScheme.primaryContainer.copy(alpha = 0.4f),
                            iconColor = KptTheme.colorScheme.primary,
                            modifier = Modifier.weight(1f),
                        )
                    }
                }
            }
        }
    }
}
