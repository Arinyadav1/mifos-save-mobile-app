/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See See https://github.com/openMF/kmp-project-template/blob/main/LICENSE
 */
package org.mifos.feature.auth.verifyOtp

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import org.mifos.core.base.designsystem.component.KptButton
import org.mifos.core.base.designsystem.component.KptOtpInputField
import org.mifos.core.base.designsystem.component.KptSuccessDialog
import org.mifos.core.base.designsystem.component.KptTopAppBar
import org.mifos.core.base.designsystem.theme.KptAppColors
import org.mifos.core.base.designsystem.theme.KptTheme
import org.mifos.core.base.ui.effects.EventsEffect
import org.mifos.core.base.ui.submit.MutationScreenContent
import org.mifos.core.ui.HorizontalSpacer
import org.mifos.core.ui.VerticalSpacer
import org.mifos.core.ui.scaffold.KptScaffold
import org.mifos.feature.auth.generated.resources.Res
import org.mifos.feature.auth.generated.resources.feature_auth_otp_sent_to_prefix
import org.mifos.feature.auth.generated.resources.feature_auth_otp_success_account_message
import org.mifos.feature.auth.generated.resources.feature_auth_otp_success_account_title
import org.mifos.feature.auth.generated.resources.feature_auth_otp_success_dialog_button
import org.mifos.feature.auth.generated.resources.feature_auth_otp_success_reset_message
import org.mifos.feature.auth.generated.resources.feature_auth_otp_success_reset_title
import org.mifos.feature.auth.generated.resources.feature_auth_registered_email_address
import org.mifos.feature.auth.generated.resources.feature_auth_registered_mobile_number
import org.mifos.feature.auth.generated.resources.feature_auth_resend_otp
import org.mifos.feature.auth.generated.resources.feature_auth_resend_timer
import org.mifos.feature.auth.generated.resources.feature_auth_verify_otp_button
import org.mifos.feature.auth.generated.resources.feature_auth_verify_otp_title

@Composable
fun VerifyOtpScreen(
    onBackClick: () -> Unit,
    onVerificationSuccess: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: VerifyOtpViewModel = koinViewModel(),
) {
    val state by viewModel.stateFlow.collectAsStateWithLifecycle()

    EventsEffect(viewModel.eventFlow) { event ->
        when (event) {
            VerifyOtpEvent.VerificationSuccess -> onVerificationSuccess()
            VerifyOtpEvent.NavigateBack -> onBackClick()
        }
    }

    VerifyOtpScreenContent(
        state = state,
        onAction = viewModel::trySendAction,
        modifier = modifier,
    )
}

@Composable
internal fun VerifyOtpScreenContent(
    state: VerifyOtpState,
    onAction: (VerifyOtpAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(modifier = Modifier.fillMaxSize()) {
        MutationScreenContent(
            screenState = state.screenState,
            submitState = state.submitState,
            onRetry = { onAction(VerifyOtpAction.Retry) },
            onSubmitted = {},
            modifier = modifier.fillMaxSize(),
        ) { _, _ ->
            KptScaffold(
                containerColor = KptTheme.colorScheme.surface,
                topBar = {
                    KptTopAppBar(
                        title = "",
                        onNavigationIconClick = { onAction(VerifyOtpAction.BackClicked) },
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
                    OtpHeaderSection(isEmail = state.isEmail)

                    VerticalSpacer(KptTheme.spacing.xl)

                    // OTP inputs
                    KptOtpInputField(
                        value = state.otpCode,
                        onValueChange = { onAction(VerifyOtpAction.OtpChanged(it)) },
                        hasError = state.errorMessage != null,
                        enabled = true,
                        onVerifyClick = { onAction(VerifyOtpAction.VerifyOtp) },
                        isButtonEnabled = state.isVerifyEnabled,
                    )

                    if (state.errorMessage != null) {
                        VerticalSpacer(KptTheme.spacing.sm)
                        Text(
                            text = stringResource(state.errorMessage),
                            color = KptTheme.colorScheme.error,
                            style = KptTheme.typography.bodySmall,
                            modifier = Modifier.padding(start = KptTheme.spacing.xs),
                        )
                    }

                    // Resend section (Reset Password flow only)
                    if (state.flow == VerifyOtpFlow.RESET_PASSWORD_VERIFY) {
                        VerticalSpacer(KptTheme.spacing.lg)
                        OtpResendSection(
                            timerSeconds = state.timerSeconds,
                            isResendEnabled = state.isResendEnabled,
                            onResendClick = { onAction(VerifyOtpAction.ResendOtp) },
                        )
                    }

                    Spacer(modifier = Modifier.weight(1f))

                    // Verify Button
                    KptButton(
                        onClick = { onAction(VerifyOtpAction.VerifyOtp) },
                        enabled = state.isVerifyEnabled,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = KptTheme.colorScheme.primary,
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                    ) {
                        Text(text = stringResource(Res.string.feature_auth_verify_otp_button))
                    }

                    VerticalSpacer(KptTheme.spacing.xl)
                }
            }
        }

        // Generic Success Dialog Overlay
        if (state.showSuccessDialog) {
            val titleRes = if (state.flow == VerifyOtpFlow.RESET_PASSWORD_VERIFY) {
                Res.string.feature_auth_otp_success_reset_title
            } else {
                Res.string.feature_auth_otp_success_account_title
            }
            val messageRes = if (state.flow == VerifyOtpFlow.RESET_PASSWORD_VERIFY) {
                Res.string.feature_auth_otp_success_reset_message
            } else {
                Res.string.feature_auth_otp_success_account_message
            }

            KptSuccessDialog(
                title = stringResource(titleRes),
                message = stringResource(messageRes),
                buttonText = stringResource(Res.string.feature_auth_otp_success_dialog_button),
                onConfirm = { onAction(VerifyOtpAction.DismissSuccessDialog) },
            )
        }
    }
}

@Composable
private fun OtpHeaderSection(
    isEmail: Boolean,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        VerticalSpacer(KptTheme.spacing.md)

        // Hero Icon container
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
            text = stringResource(Res.string.feature_auth_verify_otp_title),
            style = KptTheme.typography.headlineLarge.copy(
                fontWeight = FontWeight.ExtraBold,
                color = KptTheme.colorScheme.onSurface,
            ),
        )

        VerticalSpacer(KptTheme.spacing.xs)

        // Subtitle
        val emailText = stringResource(Res.string.feature_auth_registered_email_address)
        val phoneText = stringResource(Res.string.feature_auth_registered_mobile_number)
        val descriptionString = buildAnnotatedString {
            append(stringResource(Res.string.feature_auth_otp_sent_to_prefix))
            append(" ")
            withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                if (isEmail) {
                    append(emailText)
                } else {
                    append(phoneText)
                }
            }
        }
        Text(
            text = descriptionString,
            style = KptTheme.typography.bodyLarge.copy(
                color = KptTheme.colorScheme.onSurfaceVariant,
            ),
        )
    }
}

@Composable
private fun OtpResendSection(
    timerSeconds: Int,
    isResendEnabled: Boolean,
    onResendClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = if (timerSeconds > 0) {
                stringResource(Res.string.feature_auth_resend_timer)
                    .replace("%s", formatTime(timerSeconds))
            } else {
                ""
            },
            style = KptTheme.typography.bodyMedium.copy(
                color = KptTheme.colorScheme.onSurfaceVariant,
                fontWeight = FontWeight.Medium,
            ),
        )

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .clip(KptTheme.shapes.small)
                .clickable(enabled = isResendEnabled) {
                    onResendClick()
                }
                .padding(KptTheme.spacing.xs),
        ) {
            Icon(
                imageVector = Icons.Outlined.Refresh,
                contentDescription = null,
                tint = if (isResendEnabled) {
                    KptTheme.colorScheme.primary
                } else {
                    KptTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.38f)
                },
                modifier = Modifier.size(18.dp),
            )
            HorizontalSpacer(4.dp)
            Text(
                text = stringResource(Res.string.feature_auth_resend_otp),
                style = KptTheme.typography.bodyMedium.copy(
                    color = if (isResendEnabled) {
                        KptTheme.colorScheme.primary
                    } else {
                        KptTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.38f)
                    },
                    fontWeight = FontWeight.Bold,
                ),
            )
        }
    }
}

private fun formatTime(seconds: Int): String {
    val mins = seconds / 60
    val secs = seconds % 60
    return "${mins.toString().padStart(2, '0')}:${secs.toString().padStart(2, '0')}"
}
