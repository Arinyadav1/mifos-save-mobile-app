/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See See https://github.com/openMF/kmp-project-template/blob/main/LICENSE
 */
package org.mifos.feature.auth.signIn

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import org.mifos.core.base.designsystem.component.KptButton
import org.mifos.core.base.designsystem.theme.KptTheme
import org.mifos.core.base.ui.effects.EventsEffect
import org.mifos.core.base.ui.submit.MutationScreenContent
import org.mifos.core.ui.VerticalSpacer
import org.mifos.core.ui.input.KptTextField
import org.mifos.feature.auth.generated.resources.Res
import org.mifos.feature.auth.generated.resources.feature_auth_create_account
import org.mifos.feature.auth.generated.resources.feature_auth_enter_password
import org.mifos.feature.auth.generated.resources.feature_auth_enter_username
import org.mifos.feature.auth.generated.resources.feature_auth_forgot_password
import org.mifos.feature.auth.generated.resources.feature_auth_mifos_save_app_title
import org.mifos.feature.auth.generated.resources.feature_auth_mifos_save_logo_desc
import org.mifos.feature.auth.generated.resources.feature_auth_mifos_save_version
import org.mifos.feature.auth.generated.resources.feature_auth_new_to_mifos_save
import org.mifos.feature.auth.generated.resources.feature_auth_password
import org.mifos.feature.auth.generated.resources.feature_auth_sign_in
import org.mifos.feature.auth.generated.resources.feature_auth_sign_in_subtitle
import org.mifos.feature.auth.generated.resources.feature_auth_username
import org.mifos.feature.auth.generated.resources.feature_auth_welcome_title
import org.mifos.feature.auth.generated.resources.mifos_save_logo

@Composable
fun SignInScreen(
    onForgetPasswordScreen: () -> Unit,
    onAccountTypeScreen: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SignInViewModel = koinViewModel(),
) {
    val state by viewModel.stateFlow.collectAsStateWithLifecycle()

    EventsEffect(viewModel.eventFlow) { event ->
        when (event) {
            SignInEvent.NavigateToAccountTypeScreen -> onAccountTypeScreen()
            SignInEvent.NavigateToForgetPasswordScreen -> onForgetPasswordScreen()
        }
    }

    SignInScreenContent(
        state = state,
        onAction = viewModel::trySendAction,
        modifier = modifier,
    )
}

@Composable
internal fun SignInScreenContent(
    state: SignInState,
    modifier: Modifier = Modifier,
    onAction: (SignInAction) -> Unit,
) {
    MutationScreenContent(
        screenState = state.screenState,
        submitState = state.submitState,
        onRetry = {
            onAction(SignInAction.SingIn)
        },
        onSubmitted = {},
        modifier = modifier.fillMaxSize(),
    ) { _, _ ->
        Scaffold(
            containerColor = KptTheme.colorScheme.surface,
            modifier = Modifier.fillMaxSize().padding(KptTheme.spacing.md),
        ) { paddingValues ->
            SignInContent(
                state = state,
                onAction = onAction,
                modifier = Modifier.padding(paddingValues),
            )
        }
    }
}

@Composable
fun SignInContent(
    state: SignInState,
    modifier: Modifier = Modifier,
    onAction: (SignInAction) -> Unit,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = KptTheme.spacing.lg)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        VerticalSpacer(KptTheme.spacing.xxl)

        Image(
            painter = painterResource(Res.drawable.mifos_save_logo),
            contentDescription = stringResource(Res.string.feature_auth_mifos_save_logo_desc),
            modifier = Modifier.size(80.dp),
        )

        VerticalSpacer(KptTheme.spacing.lg)

        HeaderSection()

        VerticalSpacer(KptTheme.spacing.xl)

        KptTextField(
            value = state.username,
            onValueChange = {
                onAction(SignInAction.ChangeUsername(it))
            },
            label = stringResource(Res.string.feature_auth_username),
            placeholder = stringResource(Res.string.feature_auth_enter_username),
            errorText = state.errorUsername,
        )

        VerticalSpacer(KptTheme.spacing.lg)

        KptTextField(
            value = state.password,
            onValueChange = {
                onAction(SignInAction.ChangePassword(it))
            },
            isPassword = true,
            isPasswordVisible = state.isPasswordVisible,
            onTogglePasswordVisibility = {
                onAction(SignInAction.TogglePasswordVisibility)
            },
            label = stringResource(Res.string.feature_auth_password),
            placeholder = stringResource(Res.string.feature_auth_enter_password),
            errorText = state.errorPassword,
        )

        VerticalSpacer(KptTheme.spacing.md)

        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.CenterEnd,
        ) {
            Text(
                text = stringResource(Res.string.feature_auth_forgot_password),
                modifier = Modifier.clickable(onClick = { onAction(SignInAction.NavigateToForgetPasswordScreen) }),
                color = KptTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold,
            )
        }

        VerticalSpacer(KptTheme.spacing.xl)

        KptButton(
            onClick = {
                onAction(SignInAction.SingIn)
            },
            enabled = state.isButtonEnabled,
            colors = ButtonDefaults.buttonColors(
                containerColor = KptTheme.colorScheme.primary,
            ),
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
        ) {
            Text(stringResource(Res.string.feature_auth_sign_in))
        }

        VerticalSpacer(KptTheme.spacing.lg)

        SignUpSelection(
            onClick = { onAction(SignInAction.NavigateToAccountTypeScreen) },
        )

        VerticalSpacer(KptTheme.spacing.xxl)

        // the version will be change in dynamically later
        Text(
            text = stringResource(Res.string.feature_auth_mifos_save_version) + "1.0.0",
            color = KptTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.38f),
            style = KptTheme.typography.bodySmall,
            modifier = Modifier.padding(bottom = KptTheme.spacing.md),
        )
    }
}

@Composable
private fun SignUpSelection(
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
    ) {
        Text(
            text = stringResource(Res.string.feature_auth_new_to_mifos_save),
            color = KptTheme.colorScheme.onSurfaceVariant,
        )

        Text(
            text = stringResource(Res.string.feature_auth_create_account),
            color = KptTheme.colorScheme.primary,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.clickable(onClick = onClick),
        )
    }
}

@Composable
private fun HeaderSection(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = stringResource(Res.string.feature_auth_mifos_save_app_title),
            style = KptTheme.typography.labelLarge.copy(
                fontWeight = FontWeight.Bold,
                color = KptTheme.colorScheme.onSurfaceVariant,
                letterSpacing = 1.5.sp,
            ),
        )

        VerticalSpacer(KptTheme.spacing.sm)

        Text(
            text = stringResource(Res.string.feature_auth_welcome_title),
            style = KptTheme.typography.headlineLarge.copy(
                fontWeight = FontWeight.ExtraBold,
                color = KptTheme.colorScheme.onSurface,
            ),
        )

        VerticalSpacer(KptTheme.spacing.sm)

        Text(
            text = stringResource(Res.string.feature_auth_sign_in_subtitle),
            style = KptTheme.typography.bodyLarge.copy(
                color = KptTheme.colorScheme.onSurfaceVariant,
            ),
        )
    }
}
