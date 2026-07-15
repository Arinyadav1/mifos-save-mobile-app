/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See See https://github.com/openMF/kmp-project-template/blob/main/LICENSE
 */
package org.mifos.feature.auth.forgotPassword

import androidx.compose.foundation.background
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
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
import org.mifos.core.base.designsystem.component.KptButton
import org.mifos.core.base.designsystem.component.KptTopAppBar
import org.mifos.core.base.designsystem.theme.KptAppColors
import org.mifos.core.base.designsystem.theme.KptTheme
import org.mifos.core.base.ui.effects.EventsEffect
import org.mifos.core.base.ui.submit.MutationScreenContent
import org.mifos.core.designsystem.icon.AppIcons
import org.mifos.core.base.designsystem.component.HorizontalSpacer
import org.mifos.core.base.designsystem.component.VerticalSpacer
import org.mifos.core.ui.input.KptTextField
import org.mifos.core.ui.scaffold.KptScaffold
import org.mifos.feature.auth.createMemberAccount.AuthenticationMode
import org.mifos.feature.auth.generated.resources.Res
import org.mifos.feature.auth.generated.resources.feature_auth_enter_username
import org.mifos.feature.auth.generated.resources.feature_auth_forgot_password
import org.mifos.feature.auth.generated.resources.feature_auth_forgot_password_desc
import org.mifos.feature.auth.generated.resources.feature_auth_forgot_password_email_option
import org.mifos.feature.auth.generated.resources.feature_auth_forgot_password_phone_option
import org.mifos.feature.auth.generated.resources.feature_auth_receive_otp_via
import org.mifos.feature.auth.generated.resources.feature_auth_remember_password_question
import org.mifos.feature.auth.generated.resources.feature_auth_send_otp
import org.mifos.feature.auth.generated.resources.feature_auth_sign_in
import org.mifos.feature.auth.generated.resources.feature_auth_username

@Composable
fun ForgotPasswordScreen(
    onBackClick: () -> Unit,
    onNavigateToOtpVerification: (username: String, isEmail: Boolean) -> Unit,
    onSignInClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ForgotPasswordViewModel = koinViewModel(),
) {
    val state by viewModel.stateFlow.collectAsStateWithLifecycle()

    EventsEffect(viewModel.eventFlow) { event ->
        when (event) {
            ForgotPasswordEvent.NavigateBack -> onBackClick()
            ForgotPasswordEvent.NavigateToSignIn -> onSignInClick()
            is ForgotPasswordEvent.NavigateToOtpVerification -> {
                onNavigateToOtpVerification(event.username, event.isEmail)
            }
        }
    }

    ForgotPasswordScreenContent(
        state = state,
        onAction = viewModel::trySendAction,
        modifier = modifier,
    )
}

@Composable
internal fun ForgotPasswordScreenContent(
    state: ForgotPasswordState,
    onAction: (ForgotPasswordAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    MutationScreenContent(
        screenState = state.screenState,
        submitState = state.submitState,
        onRetry = { onAction(ForgotPasswordAction.Retry) },
        onSubmitted = {},
        modifier = modifier.fillMaxSize(),
    ) { _, _ ->
        KptScaffold(
            containerColor = KptTheme.colorScheme.surface,
            topBar = {
                KptTopAppBar(
                    title = "",
                    onNavigationIconClick = { onAction(ForgotPasswordAction.BackClicked) },
                )
            },
            modifier = Modifier.fillMaxSize(),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = KptTheme.spacing.lg)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.Start,
            ) {
                ForgotPasswordHeader()

                VerticalSpacer(KptTheme.spacing.xl)

                // Username field
                KptTextField(
                    value = state.username,
                    onValueChange = { onAction(ForgotPasswordAction.ChangeUsername(it)) },
                    label = stringResource(Res.string.feature_auth_username),
                    placeholder = stringResource(Res.string.feature_auth_enter_username),
                    leadingIcon = {
                        Icon(
                            imageVector = AppIcons.Person,
                            contentDescription = null,
                            tint = KptTheme.colorScheme.onSurfaceVariant,
                        )
                    },
                    errorText = state.errorUsername?.let { stringResource(it) },
                )

                VerticalSpacer(KptTheme.spacing.lg)

                AuthenticationModeRadioGroup(
                    selectedMode = state.authenticationMode,
                    onModeChange = { onAction(ForgotPasswordAction.ChangeAuthenticationMode(it)) },
                )

                VerticalSpacer(KptTheme.spacing.xxl)

                // Send OTP Button
                KptButton(
                    onClick = { onAction(ForgotPasswordAction.SendOtp) },
                    enabled = state.isButtonEnabled,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = KptTheme.colorScheme.primary,
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                ) {
                    Text(text = stringResource(Res.string.feature_auth_send_otp))
                }

                VerticalSpacer(KptTheme.spacing.lg)

                ForgotPasswordSignInLink(
                    onClick = { onAction(ForgotPasswordAction.SignInClicked) },
                )

                VerticalSpacer(KptTheme.spacing.xxl)
            }
        }
    }
}

@Composable
private fun ForgotPasswordHeader(
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.Start,
    ) {
        VerticalSpacer(KptTheme.spacing.md)

        // Large mail icon inside rounded container
        Box(
            modifier = Modifier
                .size(KptTheme.spacing.xxl)
                .clip(KptTheme.shapes.large)
                .background(KptAppColors.blueLight),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = Icons.Outlined.Email,
                contentDescription = null,
                tint = KptAppColors.blueDark,
                modifier = Modifier.size(KptTheme.spacing.xl),
            )
        }

        VerticalSpacer(KptTheme.spacing.lg)

        // Title
        Text(
            text = stringResource(Res.string.feature_auth_forgot_password),
            style = KptTheme.typography.headlineLarge.copy(
                fontWeight = FontWeight.ExtraBold,
                color = KptTheme.colorScheme.onSurface,
            ),
        )

        VerticalSpacer(KptTheme.spacing.xs)

        // Description
        Text(
            text = stringResource(Res.string.feature_auth_forgot_password_desc),
            style = KptTheme.typography.bodyLarge.copy(
                color = KptTheme.colorScheme.onSurfaceVariant,
            ),
        )
    }
}

@Composable
private fun AuthenticationModeRadioGroup(
    selectedMode: AuthenticationMode,
    modifier: Modifier = Modifier,
    onModeChange: (AuthenticationMode) -> Unit,
) {
    Column(modifier = modifier) {
        // Verification Mode Label
        Text(
            text = stringResource(Res.string.feature_auth_receive_otp_via),
            style = KptTheme.typography.bodyMedium.copy(
                fontWeight = FontWeight.SemiBold,
                color = KptTheme.colorScheme.onSurfaceVariant,
            ),
        )

        VerticalSpacer(KptTheme.spacing.xs)

        // Verification Mode Radio Group
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            // Email Option
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.clickable {
                    onModeChange(AuthenticationMode.EMAIL)
                },
            ) {
                RadioButton(
                    selected = selectedMode == AuthenticationMode.EMAIL,
                    onClick = {
                        onModeChange(AuthenticationMode.EMAIL)
                    },
                    colors = RadioButtonDefaults.colors(
                        selectedColor = KptTheme.colorScheme.primary,
                    ),
                )
                Text(
                    text = stringResource(Res.string.feature_auth_forgot_password_email_option),
                    style = KptTheme.typography.bodyMedium,
                )
            }

            HorizontalSpacer(KptTheme.spacing.lg)

            // Phone Option
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.clickable {
                    onModeChange(AuthenticationMode.PHONE)
                },
            ) {
                RadioButton(
                    selected = selectedMode == AuthenticationMode.PHONE,
                    onClick = {
                        onModeChange(AuthenticationMode.PHONE)
                    },
                    colors = RadioButtonDefaults.colors(
                        selectedColor = KptTheme.colorScheme.primary,
                    ),
                )
                Text(
                    text = stringResource(Res.string.feature_auth_forgot_password_phone_option),
                    style = KptTheme.typography.bodyMedium,
                )
            }
        }
    }
}

@Composable
private fun ForgotPasswordSignInLink(
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        val rememberPasswordText = stringResource(Res.string.feature_auth_remember_password_question)
        val signInText = stringResource(Res.string.feature_auth_sign_in)
        Text(
            text = rememberPasswordText,
            color = KptTheme.colorScheme.onSurfaceVariant,
        )
        Text(
            text = signInText,
            color = KptTheme.colorScheme.primary,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.clickable(onClick = onClick),
        )
    }
}
