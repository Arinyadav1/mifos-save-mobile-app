/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See See https://github.com/openMF/kmp-project-template/blob/main/LICENSE
 */
package org.mifos.feature.auth.accountType

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.outlined.PersonAdd
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import org.jetbrains.compose.resources.stringResource
import org.mifos.core.base.designsystem.component.KptCard
import org.mifos.core.base.designsystem.component.KptTopAppBar
import org.mifos.core.base.designsystem.theme.KptTheme
import org.mifos.core.designsystem.icon.AppIcons
import org.mifos.core.ui.scaffold.KptScaffold
import org.mifos.feature.auth.generated.resources.Res
import org.mifos.feature.auth.generated.resources.feature_auth_account_type_admin
import org.mifos.feature.auth.generated.resources.feature_auth_account_type_admin_desc
import org.mifos.feature.auth.generated.resources.feature_auth_account_type_choose
import org.mifos.feature.auth.generated.resources.feature_auth_account_type_member
import org.mifos.feature.auth.generated.resources.feature_auth_account_type_member_desc
import org.mifos.feature.auth.generated.resources.feature_auth_already_have_account
import org.mifos.feature.auth.generated.resources.feature_auth_create_account_title
import org.mifos.feature.auth.generated.resources.feature_auth_log_in

@Composable
fun AccountTypeScreen(
    onBackClick: () -> Unit,
    onAdminCreateAccountScreen: () -> Unit,
    onMemberCreateAccountScreen: () -> Unit,
    onLoginClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    KptScaffold(
        containerColor = KptTheme.colorScheme.surface,
        topBar = {
            KptTopAppBar(
                title = "",
                onNavigationIconClick = onBackClick,
            )
        },
        modifier = modifier.fillMaxSize(),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = KptTheme.spacing.lg)
                .verticalScroll(rememberScrollState()),
        ) {
            Spacer(modifier = Modifier.height(KptTheme.spacing.md))

            // Light blue background matching mockup
            Box(
                modifier = Modifier
                    .size(KptTheme.spacing.xxl)
                    .clip(KptTheme.shapes.large)
                    .background(Color(0xFFD3E4FF)),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = AppIcons.PersonAdd,
                    contentDescription = null,
                    tint = Color(0xFF1A437E),
                    modifier = Modifier.size(KptTheme.spacing.xl),
                )
            }

            Spacer(modifier = Modifier.height(KptTheme.spacing.lg))

            // Title and Subtitle
            Text(
                text = stringResource(Res.string.feature_auth_create_account_title),
                style = KptTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.ExtraBold,
                    color = KptTheme.colorScheme.onSurface,
                ),
            )

            Spacer(modifier = Modifier.height(KptTheme.spacing.xs))

            Text(
                text = stringResource(Res.string.feature_auth_account_type_choose),
                style = KptTheme.typography.bodyLarge.copy(
                    color = KptTheme.colorScheme.onSurfaceVariant,
                ),
            )

            Spacer(modifier = Modifier.height(KptTheme.spacing.xl))

            // Admin Account Card
            SelectionCard(
                title = stringResource(Res.string.feature_auth_account_type_admin),
                description = stringResource(Res.string.feature_auth_account_type_admin_desc),
                onClick = onAdminCreateAccountScreen,
            )

            Spacer(modifier = Modifier.height(KptTheme.spacing.md))

            // Member Account Card
            SelectionCard(
                title = stringResource(Res.string.feature_auth_account_type_member),
                description = stringResource(Res.string.feature_auth_account_type_member_desc),
                onClick = onMemberCreateAccountScreen,
            )

            Spacer(modifier = Modifier.height(KptTheme.spacing.lg))

            // Bottom Prompt
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = stringResource(Res.string.feature_auth_already_have_account),
                    color = KptTheme.colorScheme.onSurfaceVariant,
                    style = KptTheme.typography.bodyLarge,
                )
                Text(
                    text = stringResource(Res.string.feature_auth_log_in),
                    color = KptTheme.colorScheme.primary,
                    fontWeight = FontWeight.SemiBold,
                    style = KptTheme.typography.bodyLarge,
                    modifier = Modifier.clickable(onClick = onLoginClick),
                )
            }
        }
    }
}

@Composable
fun SelectionCard(
    title: String,
    description: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    // Admin Account Card
    KptCard(
        colors = CardDefaults.cardColors(
            containerColor = KptTheme.colorScheme.primary,
            contentColor = KptTheme.colorScheme.background,
        ),
        onClick = {
            onClick()
        },
    ) {
        Row(
            modifier = modifier
                .fillMaxWidth()
                .padding(KptTheme.spacing.lg),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Column(
                modifier = Modifier.weight(1f),
            ) {
                Text(
                    text = title,
                    style = KptTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = KptTheme.colorScheme.background,
                    ),
                )
                Spacer(modifier = Modifier.height(KptTheme.spacing.xs))
                Text(
                    text = description,
                    style = KptTheme.typography.bodyMedium.copy(
                        color = KptTheme.colorScheme.background,
                    ),
                )
            }

            Spacer(modifier = Modifier.width(KptTheme.spacing.md))

            Icon(
                imageVector = AppIcons.ArrowRight,
                contentDescription = null,
                tint = KptTheme.colorScheme.background,
                modifier = Modifier.size(KptTheme.spacing.lg),
            )
        }
    }
}
