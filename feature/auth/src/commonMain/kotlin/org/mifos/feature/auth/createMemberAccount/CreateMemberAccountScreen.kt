/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See See https://github.com/openMF/kmp-project-template/blob/main/LICENSE
 */
package org.mifos.feature.auth.createMemberAccount

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import org.mifos.core.base.designsystem.component.KptButton
import org.mifos.core.base.designsystem.component.KptTopAppBar
import org.mifos.core.base.designsystem.theme.KptTheme
import org.mifos.core.base.ui.effects.EventsEffect
import org.mifos.core.base.ui.submit.MutationScreenContent
import org.mifos.core.ui.HorizontalSpacer
import org.mifos.core.ui.VerticalSpacer
import org.mifos.core.ui.input.KptTextField
import org.mifos.core.ui.scaffold.KptScaffold
import org.mifos.core.ui.utils.CombinedPasswordErrorCard
import org.mifos.core.ui.utils.PasswordChecker
import org.mifos.core.ui.utils.PasswordStrengthIndicator
import org.mifos.feature.auth.generated.resources.Res
import org.mifos.feature.auth.generated.resources.feature_auth_already_have_account
import org.mifos.feature.auth.generated.resources.feature_auth_confirm_password_label
import org.mifos.feature.auth.generated.resources.feature_auth_confirm_password_placeholder
import org.mifos.feature.auth.generated.resources.feature_auth_create_account_subtitle
import org.mifos.feature.auth.generated.resources.feature_auth_create_account_title
import org.mifos.feature.auth.generated.resources.feature_auth_email
import org.mifos.feature.auth.generated.resources.feature_auth_email_label
import org.mifos.feature.auth.generated.resources.feature_auth_email_placeholder
import org.mifos.feature.auth.generated.resources.feature_auth_external_id_label
import org.mifos.feature.auth.generated.resources.feature_auth_external_id_placeholder
import org.mifos.feature.auth.generated.resources.feature_auth_first_name_label
import org.mifos.feature.auth.generated.resources.feature_auth_first_name_placeholder
import org.mifos.feature.auth.generated.resources.feature_auth_last_name_label
import org.mifos.feature.auth.generated.resources.feature_auth_last_name_placeholder
import org.mifos.feature.auth.generated.resources.feature_auth_middle_name_label
import org.mifos.feature.auth.generated.resources.feature_auth_middle_name_placeholder
import org.mifos.feature.auth.generated.resources.feature_auth_mifos_save_app_title
import org.mifos.feature.auth.generated.resources.feature_auth_mobile_number_label
import org.mifos.feature.auth.generated.resources.feature_auth_mobile_number_placeholder
import org.mifos.feature.auth.generated.resources.feature_auth_password_label
import org.mifos.feature.auth.generated.resources.feature_auth_password_placeholder
import org.mifos.feature.auth.generated.resources.feature_auth_phone_number
import org.mifos.feature.auth.generated.resources.feature_auth_sign_in
import org.mifos.feature.auth.generated.resources.feature_auth_username_label
import org.mifos.feature.auth.generated.resources.feature_auth_username_placeholder
import org.mifos.feature.auth.generated.resources.feature_auth_verification_method

@Composable
fun CreateMemberAccountScreen(
    onBackClick: () -> Unit,
    onNavigateToOtpVerification: (isEmail: Boolean) -> Unit,
    onNavigateToSignIn: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: CreateMemberAccountViewModel = koinViewModel(),
) {
    val state by viewModel.stateFlow.collectAsStateWithLifecycle()

    EventsEffect(viewModel.eventFlow) { event ->
        when (event) {
            is CreateMemberAccountEvent.NavigateToOtpVerification -> onNavigateToOtpVerification(event.isEmail)
            CreateMemberAccountEvent.NavigateToSignIn -> onNavigateToSignIn()
            CreateMemberAccountEvent.NavigateToBack -> onBackClick()
        }
    }

    CreateMemberAccountScreenContent(
        state = state,
        onAction = viewModel::trySendAction,
        modifier = modifier,
    )
}

@Composable
internal fun CreateMemberAccountScreenContent(
    state: CreateMemberAccountState,
    onAction: (CreateMemberAccountAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    MutationScreenContent(
        screenState = state.screenState,
        submitState = state.submitState,
        onRetry = {
            onAction(CreateMemberAccountAction.Retry)
        },
        onSubmitted = {},
        modifier = modifier.fillMaxSize(),
    ) { _, _ ->
        KptScaffold(
            containerColor = KptTheme.colorScheme.surface,
            topBar = {
                KptTopAppBar(
                    title = "",
                    onNavigationIconClick = { onAction(CreateMemberAccountAction.NavigateToBack) },
                )
            },
            modifier = Modifier.fillMaxSize(),
        ) {
            CreateMemberAccountContent(
                state = state,
                onAction = onAction,
                modifier = Modifier,
            )
        }
    }
}

@Composable
fun CreateMemberAccountContent(
    state: CreateMemberAccountState,
    onAction: (CreateMemberAccountAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = KptTheme.spacing.lg)
            .verticalScroll(rememberScrollState()),
    ) {
        VerticalSpacer(KptTheme.spacing.sm)

        Text(
            text = stringResource(Res.string.feature_auth_mifos_save_app_title),
            style = KptTheme.typography.labelLarge.copy(
                fontWeight = FontWeight.Bold,
                color = KptTheme.colorScheme.onSurfaceVariant,
                letterSpacing = 1.5.sp,
            ),
        )

        VerticalSpacer(KptTheme.spacing.xs)

        Text(
            text = stringResource(Res.string.feature_auth_create_account_title),
            style = KptTheme.typography.headlineLarge.copy(
                fontWeight = FontWeight.ExtraBold,
                color = KptTheme.colorScheme.onSurface,
            ),
        )

        VerticalSpacer(KptTheme.spacing.xs)

        Text(
            text = stringResource(Res.string.feature_auth_create_account_subtitle),
            style = KptTheme.typography.bodyLarge.copy(
                color = KptTheme.colorScheme.onSurfaceVariant,
            ),
        )

        VerticalSpacer(KptTheme.spacing.xl)

        MemberRegistrationForm(
            state = state,
            onAction = onAction,
            modifier = Modifier.fillMaxWidth(),
        )

        VerticalSpacer(KptTheme.spacing.lg)

        // Verification Method selection
        Text(
            text = stringResource(Res.string.feature_auth_verification_method),
            style = KptTheme.typography.bodyMedium.copy(
                fontWeight = FontWeight.SemiBold,
                color = KptTheme.colorScheme.onSurfaceVariant,
            ),
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.clickable {
                    onAction(
                        CreateMemberAccountAction.ChangeAuthenticationMode(
                            AuthenticationMode.EMAIL,
                        ),
                    )
                },
            ) {
                RadioButton(
                    selected = state.authenticationMode == AuthenticationMode.EMAIL,
                    onClick = {
                        onAction(
                            CreateMemberAccountAction.ChangeAuthenticationMode(
                                AuthenticationMode.EMAIL,
                            ),
                        )
                    },
                    colors = RadioButtonDefaults.colors(
                        selectedColor = KptTheme.colorScheme.primary,
                    ),
                )
                Text(
                    text = stringResource(Res.string.feature_auth_email),
                    style = KptTheme.typography.bodyMedium,
                )
            }

            HorizontalSpacer(KptTheme.spacing.lg)

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.clickable {
                    onAction(
                        CreateMemberAccountAction.ChangeAuthenticationMode(
                            AuthenticationMode.PHONE,
                        ),
                    )
                },
            ) {
                RadioButton(
                    selected = state.authenticationMode == AuthenticationMode.PHONE,
                    onClick = {
                        onAction(
                            CreateMemberAccountAction.ChangeAuthenticationMode(
                                AuthenticationMode.PHONE,
                            ),
                        )
                    },
                    colors = RadioButtonDefaults.colors(
                        selectedColor = KptTheme.colorScheme.primary,
                    ),
                )
                Text(
                    text = stringResource(Res.string.feature_auth_phone_number),
                    style = KptTheme.typography.bodyMedium,
                )
            }
        }

        VerticalSpacer(KptTheme.spacing.xl)

        // Create Account button
        KptButton(
            onClick = { onAction(CreateMemberAccountAction.CreateAccount) },
            enabled = state.isButtonEnabled,
            colors = ButtonDefaults.buttonColors(
                containerColor = KptTheme.colorScheme.primary,
            ),
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
        ) {
            Text(text = stringResource(Res.string.feature_auth_create_account_title))
        }

        VerticalSpacer(KptTheme.spacing.md)

        // Bottom Prompt: Already have an account? Sign In
        Row(
            modifier = Modifier.fillMaxWidth().padding(bottom = KptTheme.spacing.xl),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = stringResource(Res.string.feature_auth_already_have_account),
                color = KptTheme.colorScheme.onSurfaceVariant,
                style = KptTheme.typography.bodyLarge,
            )
            Text(
                text = stringResource(Res.string.feature_auth_sign_in),
                color = KptTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold,
                style = KptTheme.typography.bodyLarge,
                modifier = Modifier.clickable {
                    onAction(CreateMemberAccountAction.NavigateToSignIn)
                },
            )
        }
    }
}

@Composable
internal fun MemberRegistrationForm(
    state: CreateMemberAccountState,
    onAction: (CreateMemberAccountAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        NameAndUsernameFields(state = state, onAction = onAction)
        ContactFields(state = state, onAction = onAction)
        PasswordFields(state = state, onAction = onAction)
    }
}

@Composable
private fun NameAndUsernameFields(
    state: CreateMemberAccountState,
    onAction: (CreateMemberAccountAction) -> Unit,
) {
    KptTextField(
        value = state.firstName,
        onValueChange = { onAction(CreateMemberAccountAction.ChangeFirstName(it)) },
        label = stringResource(Res.string.feature_auth_first_name_label),
        placeholder = stringResource(Res.string.feature_auth_first_name_placeholder),
        errorText = state.errorFirstname?.let { stringResource(it) },
    )
    VerticalSpacer(KptTheme.spacing.md)

    // Middle Name (optional)
    KptTextField(
        value = state.middleName,
        onValueChange = { onAction(CreateMemberAccountAction.ChangeMiddleName(it)) },
        label = stringResource(Res.string.feature_auth_middle_name_label),
        placeholder = stringResource(Res.string.feature_auth_middle_name_placeholder),
    )

    VerticalSpacer(KptTheme.spacing.md)

    KptTextField(
        value = state.lastName,
        onValueChange = { onAction(CreateMemberAccountAction.ChangeLastName(it)) },
        label = stringResource(Res.string.feature_auth_last_name_label),
        placeholder = stringResource(Res.string.feature_auth_last_name_placeholder),
        errorText = state.errorLastname?.let { stringResource(it) },
    )

    VerticalSpacer(KptTheme.spacing.md)

    // Username
    KptTextField(
        value = state.username,
        onValueChange = { onAction(CreateMemberAccountAction.ChangeUsername(it)) },
        label = stringResource(Res.string.feature_auth_username_label),
        placeholder = stringResource(Res.string.feature_auth_username_placeholder),
        errorText = state.errorUsername?.let { stringResource(it) },
    )

    VerticalSpacer(KptTheme.spacing.md)
}

@Composable
private fun ContactFields(
    state: CreateMemberAccountState,
    onAction: (CreateMemberAccountAction) -> Unit,
) {
    // Email Address
    KptTextField(
        value = state.email,
        onValueChange = { onAction(CreateMemberAccountAction.ChangeEmail(it)) },
        label = stringResource(Res.string.feature_auth_email_label),
        placeholder = stringResource(Res.string.feature_auth_email_placeholder),
        errorText = state.errorEmail?.let { stringResource(it) },
    )

    VerticalSpacer(KptTheme.spacing.md)

    // Mobile Number
    KptTextField(
        value = state.mobileNumber,
        onValueChange = { onAction(CreateMemberAccountAction.ChangeMobileNumber(it)) },
        label = stringResource(Res.string.feature_auth_mobile_number_label),
        placeholder = stringResource(Res.string.feature_auth_mobile_number_placeholder),
        errorText = state.errorMobileNumber?.let { stringResource(it) },
    )

    VerticalSpacer(KptTheme.spacing.md)

    // External ID
    KptTextField(
        value = state.externalId,
        onValueChange = { onAction(CreateMemberAccountAction.ChangeExternalId(it)) },
        label = stringResource(Res.string.feature_auth_external_id_label),
        placeholder = stringResource(Res.string.feature_auth_external_id_placeholder),
    )

    VerticalSpacer(KptTheme.spacing.md)
}

@Composable
private fun PasswordFields(
    state: CreateMemberAccountState,
    onAction: (CreateMemberAccountAction) -> Unit,
) {
    // Password
    KptTextField(
        value = state.password,
        onValueChange = { onAction(CreateMemberAccountAction.ChangePassword(it)) },
        label = stringResource(Res.string.feature_auth_password_label),
        placeholder = stringResource(Res.string.feature_auth_password_placeholder),
        isPassword = true,
        isPasswordVisible = state.isPasswordVisible,
        onTogglePasswordVisibility = {
            onAction(CreateMemberAccountAction.TogglePasswordVisibility)
        },
        errorText = state.errorPassword,
    )

    val passwordStrength = PasswordChecker.getPasswordStrength(state.password)

    val hasError = state.errorPassword != null || state.passwordFeedback.isNotEmpty()

    if (state.password.isNotEmpty() && !hasError) {
        PasswordStrengthIndicator(
            state = passwordStrength,
            currentCharacterCount = state.password.length,
            minimumCharacterCount = 8,
            modifier = Modifier.fillMaxWidth().padding(vertical = KptTheme.spacing.xs),
        )
    }

    if (hasError && state.password.isNotEmpty()) {
        CombinedPasswordErrorCard(
            passwordStrengthState = passwordStrength,
            currentCharacterCount = state.password.length,
            minimumCharacterCount = 8,
            errorText = state.errorPassword,
            errors = state.passwordFeedback,
            modifier = Modifier.fillMaxWidth().padding(vertical = KptTheme.spacing.xs),
        )
    }

    VerticalSpacer(KptTheme.spacing.md)

    // Confirm Password
    KptTextField(
        value = state.confirmPassword,
        onValueChange = { onAction(CreateMemberAccountAction.ChangeConfirmPassword(it)) },
        label = stringResource(Res.string.feature_auth_confirm_password_label),
        placeholder = stringResource(Res.string.feature_auth_confirm_password_placeholder),
        isPassword = true,
        isPasswordVisible = state.isConfirmPasswordVisible,
        onTogglePasswordVisibility = {
            onAction(CreateMemberAccountAction.ToggleConfirmPasswordVisibility)
        },
        errorText = state.errorConfirmPassword?.let { stringResource(it) },
        modifier = Modifier.fillMaxWidth(),
    )
}
