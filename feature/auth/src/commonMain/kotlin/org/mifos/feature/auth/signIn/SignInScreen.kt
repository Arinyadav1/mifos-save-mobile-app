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
import androidx.compose.foundation.layout.Spacer
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
import org.koin.compose.viewmodel.koinViewModel
import org.mifos.core.base.designsystem.component.KptButton
import org.mifos.core.base.designsystem.theme.KptTheme
import org.mifos.core.base.ui.effects.EventsEffect
import org.mifos.core.base.ui.submit.MutationScreenContent
import org.mifos.core.ui.input.KptTextField
import org.mifos.feature.auth.generated.resources.Res
import org.mifos.feature.auth.generated.resources.mifos_save_logo

@Composable
fun SignInScreen(
    onForgetPasswordScreen: () -> Unit,
    onSignUpTypeScreen: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SignInViewModel = koinViewModel(),
) {
    val state by viewModel.stateFlow.collectAsStateWithLifecycle()

    EventsEffect(viewModel.eventFlow) { event ->
        when (event) {
            SignInEvent.NavigateToSignUpTypeScreen -> onSignUpTypeScreen()
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
    Scaffold(
        containerColor = KptTheme.colorScheme.surface,
        modifier = modifier.fillMaxSize().padding(KptTheme.spacing.md),
    ) { paddingValues ->
        MutationScreenContent(
            screenState = state.screenState,
            submitState = state.submitState,
            onRetry = {
                onAction(SignInAction.SingIn)
            },
            onSubmitted = {},
            modifier = Modifier.padding(paddingValues).fillMaxSize(),
        ) { _, _ ->
            SignInContent(
                state = state,
                onAction = onAction,
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
    val enabled = state.canInteract
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = KptTheme.spacing.lg)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(modifier = Modifier.height(KptTheme.spacing.xxl))

        Image(
            painter = painterResource(Res.drawable.mifos_save_logo),
            contentDescription = "Mifos Save Logo",
            modifier = Modifier.size(80.dp),
        )

        Spacer(modifier = Modifier.height(KptTheme.spacing.lg))

        HeaderSection()

        Spacer(modifier = Modifier.height(KptTheme.spacing.xl))

        KptTextField(
            value = state.username,
            onValueChange = {
                onAction(SignInAction.ChangeUsername(it))
            },
            label = "Username",
            placeholder = "Enter your username",
            enabled = enabled,
            errorText = state.errorUsername,
        )

        Spacer(modifier = Modifier.height(KptTheme.spacing.lg))

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
            label = "Password",
            placeholder = "Enter your password",
            enabled = enabled,
            errorText = state.errorPassword,
        )

        Spacer(modifier = Modifier.height(KptTheme.spacing.md))

        ForgotPassword(
            enabled = enabled,
            onClick = {
            },
        )

        Spacer(modifier = Modifier.height(KptTheme.spacing.xl))

        KptButton(
            onClick = {
                onAction(SignInAction.SingIn)
            },
            enabled = enabled,
            colors = ButtonDefaults.buttonColors(
                containerColor = KptTheme.colorScheme.primary,
            ),
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
        ) {
            Text("Sign In")
        }

        Spacer(modifier = Modifier.height(KptTheme.spacing.lg))

        SignUpSelection(
            enabled = enabled,
            onClick = {
            },
        )

        Spacer(modifier = Modifier.height(KptTheme.spacing.xxl))

        VersionText()
    }
}

@Composable
private fun SignUpSelection(
    enabled: Boolean,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
    ) {
        Text(
            text = "New to Mifos Save? ",
            color = KptTheme.colorScheme.onSurfaceVariant,
        )

        Text(
            text = "Create account",
            color = if (enabled) {
                KptTheme.colorScheme.primary
            } else {
                KptTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.38f)
            },
            fontWeight = FontWeight.Bold,
            modifier = if (enabled) Modifier.clickable(onClick = onClick) else Modifier,
        )
    }
}

@Composable
private fun ForgotPassword(
    enabled: Boolean,
    onClick: () -> Unit,
) {
    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.CenterEnd,
    ) {
        Text(
            text = "Forgot Password?",
            modifier = if (enabled) Modifier.clickable(onClick = onClick) else Modifier,
            color = if (enabled) {
                KptTheme.colorScheme.primary
            } else {
                KptTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.38f)
            },
            fontWeight = FontWeight.Bold,
        )
    }
}

@Composable
private fun VersionText() {
    Text(
        text = "Mifos Save v1.0.0",
        color = KptTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.38f),
        style = KptTheme.typography.bodySmall,
        modifier = Modifier.padding(bottom = KptTheme.spacing.md),
    )
}

@Composable
private fun HeaderSection(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = "MIFOS SAVE",
            style = KptTheme.typography.labelLarge.copy(
                fontWeight = FontWeight.Bold,
                color = KptTheme.colorScheme.onSurfaceVariant,
                letterSpacing = 1.5.sp,
            ),
        )

        Spacer(modifier = Modifier.height(KptTheme.spacing.sm))

        Text(
            text = "Welcome",
            style = KptTheme.typography.headlineLarge.copy(
                fontWeight = FontWeight.ExtraBold,
                color = KptTheme.colorScheme.onSurface,
            ),
        )

        Spacer(modifier = Modifier.height(KptTheme.spacing.sm))

        Text(
            text = "Sign in to your account",
            style = KptTheme.typography.bodyLarge.copy(
                color = KptTheme.colorScheme.onSurfaceVariant,
            ),
        )
    }
}
