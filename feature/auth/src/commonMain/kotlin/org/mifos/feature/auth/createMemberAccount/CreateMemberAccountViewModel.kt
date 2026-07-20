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

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.StringResource
import org.mifos.core.base.store.screen.DataFreshness
import org.mifos.core.base.store.screen.ScreenState
import org.mifos.core.base.store.submit.SubmitState
import org.mifos.core.base.ui.viewmodel.BaseViewModel
import org.mifos.core.common.formatDateFromLong
import org.mifos.core.data.auth.AuthenticationRepository
import org.mifos.core.model.auth.RegistrationRequest
import org.mifos.core.model.auth.RegistrationResult
import org.mifos.core.ui.utils.EmailValidationResult
import org.mifos.core.ui.utils.PasswordChecker
import org.mifos.core.ui.utils.PasswordStrength
import org.mifos.core.ui.utils.PasswordStrengthResult
import org.mifos.core.ui.utils.PhoneValidationResult
import org.mifos.core.ui.utils.TextFieldsValidator
import org.mifos.core.ui.utils.ValidationHelper
import org.mifos.feature.auth.generated.resources.Res
import org.mifos.feature.auth.generated.resources.feature_auth_passwords_do_not_match
import kotlin.time.Clock

class CreateMemberAccountViewModel(
    private val authenticationRepository: AuthenticationRepository,
) : BaseViewModel<CreateMemberAccountState, CreateMemberAccountEvent, CreateMemberAccountAction>(
    CreateMemberAccountState(),
) {

    @Suppress("CyclomaticComplexMethod")
    override fun handleAction(action: CreateMemberAccountAction) {
        when (action) {
            is CreateMemberAccountAction.ChangeFirstName -> handleFirstNameChanged(action.value)
            is CreateMemberAccountAction.ChangeMiddleName -> handleMiddleNameChanged(action.value)
            is CreateMemberAccountAction.ChangeLastName -> handleLastNameChanged(action.value)
            is CreateMemberAccountAction.ChangeUsername -> handleUsernameChanged(action.value)
            is CreateMemberAccountAction.ChangeEmail -> handleEmailChanged(action.value)
            is CreateMemberAccountAction.ChangeMobileNumber -> handleMobileNumberChanged(action.value)
            is CreateMemberAccountAction.ChangeExternalId -> handleExternalIdChanged(action.value)
            is CreateMemberAccountAction.ChangePassword -> handlePasswordChanged(action.value)
            is CreateMemberAccountAction.ChangeConfirmPassword -> handleConfirmPasswordChanged(
                action.value,
            )

            is CreateMemberAccountAction.ChangeAuthenticationMode -> handleAuthenticationModeChanged(
                action.value,
            )

            CreateMemberAccountAction.TogglePasswordVisibility -> togglePasswordVisibility()
            CreateMemberAccountAction.ToggleConfirmPasswordVisibility -> toggleConfirmPasswordVisibility()
            CreateMemberAccountAction.CreateAccount -> submitRegistration()
            is CreateMemberAccountAction.Internal.ReceiveRegistrationResult -> handleRegistrationResult(
                action.result,
            )

            CreateMemberAccountAction.NavigateToBack -> sendEvent(CreateMemberAccountEvent.NavigateToBack)
            CreateMemberAccountAction.NavigateToOtpVerification -> {
                val isEmail = state.authenticationMode == AuthenticationMode.EMAIL
                sendEvent(
                    CreateMemberAccountEvent.NavigateToOtpVerification(isEmail),
                )
            }

            CreateMemberAccountAction.NavigateToSignIn -> sendEvent(CreateMemberAccountEvent.NavigateToSignIn)
            CreateMemberAccountAction.Retry -> {
                mutableStateFlow.update {
                    it.copy(
                        screenState = ScreenState.Content(
                            data = Unit,
                            freshness = DataFreshness.FRESH,
                        ),
                    )
                }
            }
        }
    }

    private fun handleFirstNameChanged(value: String) {
        mutableStateFlow.update {
            it.copy(
                firstName = value,
                errorFirstname = TextFieldsValidator.stringValidator(value),
                submitState = SubmitState.Idle,
            )
        }
    }

    private fun handleMiddleNameChanged(value: String) {
        mutableStateFlow.update {
            it.copy(
                middleName = value,
                submitState = SubmitState.Idle,
            )
        }
    }

    private fun handleLastNameChanged(value: String) {
        mutableStateFlow.update {
            it.copy(
                lastName = value,
                errorLastname = TextFieldsValidator.stringValidator(value),
                submitState = SubmitState.Idle,
            )
        }
    }

    private fun handleUsernameChanged(value: String) {
        mutableStateFlow.update {
            it.copy(
                username = value,
                errorUsername = TextFieldsValidator.stringValidator(value),
                submitState = SubmitState.Idle,
            )
        }
    }

    private fun handleEmailChanged(value: String) {
        mutableStateFlow.update {
            val error = when (val result = ValidationHelper.validateEmailWithDetails(value)) {
                is EmailValidationResult.Invalid -> result.errorResource
                is EmailValidationResult.Valid -> null
            }
            it.copy(
                email = value,
                errorEmail = error,
                submitState = SubmitState.Idle,
            )
        }
    }

    private fun handleMobileNumberChanged(value: String) {
        mutableStateFlow.update {
            val error = when (val result = ValidationHelper.validatePhoneNumberWithDetails(value)) {
                is PhoneValidationResult.Invalid -> result.errorResource
                is PhoneValidationResult.Valid -> null
            }
            it.copy(
                mobileNumber = value,
                errorMobileNumber = error,
                submitState = SubmitState.Idle,
            )
        }
    }

    private fun handleExternalIdChanged(value: String) {
        mutableStateFlow.update {
            it.copy(
                externalId = value,
                submitState = SubmitState.Idle,
            )
        }
    }

    private fun handlePasswordChanged(value: String) {
        if (value.isEmpty()) {
            mutableStateFlow.update {
                it.copy(
                    passwordStrengthState = PasswordStrength.LEVEL_0,
                    passwordFeedback = emptyList(),
                )
            }
        } else {
            viewModelScope.launch {
                val result: PasswordStrengthResult =
                    PasswordChecker.getPasswordStrengthResult(value)
                val feedback = PasswordChecker.getPasswordFeedback(value)

                when (result) {
                    is PasswordStrengthResult.Error -> {
                        mutableStateFlow.update {
                            it.copy(
                                errorPassword = result.message,
                                passwordStrengthState = PasswordStrength.LEVEL_0,
                            )
                        }
                    }

                    is PasswordStrengthResult.Success -> {
                        mutableStateFlow.update {
                            it.copy(
                                passwordFeedback = feedback,
                                passwordStrengthState = result.passwordStrength,
                            )
                        }
                    }
                }
            }
        }
        mutableStateFlow.update {
            val confirmError = if (it.confirmPassword.isNotEmpty() && value != it.confirmPassword) {
                Res.string.feature_auth_passwords_do_not_match
            } else {
                null
            }
            it.copy(
                password = value,
                errorConfirmPassword = confirmError,
                submitState = SubmitState.Idle,
            )
        }
    }

    private fun handleConfirmPasswordChanged(value: String) {
        mutableStateFlow.update {
            val error = when {
                value != it.password -> Res.string.feature_auth_passwords_do_not_match
                else -> null
            }
            it.copy(
                confirmPassword = value,
                errorConfirmPassword = error,
                submitState = SubmitState.Idle,
            )
        }
    }

    private fun handleAuthenticationModeChanged(value: AuthenticationMode) {
        mutableStateFlow.update {
            it.copy(
                authenticationMode = value,
                submitState = SubmitState.Idle,
            )
        }
    }

    private fun togglePasswordVisibility() {
        mutableStateFlow.update {
            it.copy(isPasswordVisible = !it.isPasswordVisible)
        }
    }

    private fun toggleConfirmPasswordVisibility() {
        mutableStateFlow.update {
            it.copy(isConfirmPasswordVisible = !it.isConfirmPasswordVisible)
        }
    }

    private fun submitRegistration() {
        val request = RegistrationRequest(
            username = state.username,
            password = state.password,
            mobileNumber = state.mobileNumber,
            email = state.email,
            authenticationMode = state.authenticationMode.value,
            firstname = state.firstName,
            middlename = state.middleName,
            lastname = state.lastName,
            externalId = state.externalId,
            submittedOnDate = formatDateFromLong(Clock.System.now().toEpochMilliseconds()),
        )

        mutableStateFlow.update {
            it.copy(submitState = SubmitState.Submitting())
        }

        viewModelScope.launch {
            val result = authenticationRepository.registerMember(request)
            sendAction(CreateMemberAccountAction.Internal.ReceiveRegistrationResult(result))
        }
    }

    private fun handleRegistrationResult(result: ScreenState<RegistrationResult>) {
        when (result) {
            is ScreenState.Content -> {
                mutableStateFlow.update {
                    it.copy(submitState = SubmitState.Submitted(result.data))
                }
                val isEmail = state.authenticationMode == AuthenticationMode.EMAIL
                sendEvent(CreateMemberAccountEvent.NavigateToOtpVerification(isEmail))
            }

            is ScreenState.Error -> {
                mutableStateFlow.update {
                    it.copy(
                        screenState = ScreenState.Error(result.error),
                        submitState = SubmitState.Idle,
                    )
                }
            }

            is ScreenState.NoNetwork -> {
                mutableStateFlow.update {
                    it.copy(
                        screenState = ScreenState.NoNetwork(),
                        submitState = SubmitState.Idle,
                    )
                }
            }

            is ScreenState.Unauthenticated -> {
                mutableStateFlow.update {
                    it.copy(
                        screenState = ScreenState.Unauthenticated,
                        submitState = SubmitState.Idle,
                    )
                }
            }

            is ScreenState.Empty -> {
                mutableStateFlow.update {
                    it.copy(
                        screenState = ScreenState.Empty,
                        submitState = SubmitState.Idle,
                    )
                }
            }

            is ScreenState.Loading -> Unit
        }
    }
}

enum class AuthenticationMode(val value: String) {
    EMAIL("email"),
    PHONE("sms"),
}

data class CreateMemberAccountState(
    val firstName: String = "",
    val middleName: String = "",
    val lastName: String = "",
    val username: String = "",
    val email: String = "",
    val mobileNumber: String = "",
    val externalId: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val authenticationMode: AuthenticationMode = AuthenticationMode.EMAIL,
    val passwordFeedback: List<String> = emptyList(),
    val passwordStrengthState: PasswordStrength = PasswordStrength.LEVEL_0,
    val isPasswordVisible: Boolean = false,
    val isConfirmPasswordVisible: Boolean = false,

    val screenState: ScreenState<Unit> = ScreenState.Content(
        data = Unit,
        freshness = DataFreshness.FRESH,
    ),
    val submitState: SubmitState<RegistrationResult> = SubmitState.Idle,

    val errorFirstname: StringResource? = null,
    val errorLastname: StringResource? = null,
    val errorUsername: StringResource? = null,
    val errorEmail: StringResource? = null,
    val errorMobileNumber: StringResource? = null,
    val errorPassword: String? = null,
    val errorConfirmPassword: StringResource? = null,
) {
    val isButtonEnabled: Boolean
        get() = listOf(
            firstName,
            lastName,
            username,
            email,
            password,
            confirmPassword,
        ).all { it.isNotBlank() } &&
            listOf(
                errorFirstname,
                errorLastname,
                errorUsername,
                errorEmail,
                errorMobileNumber,
                errorPassword,
                errorConfirmPassword,
            ).all { it == null }
}

sealed interface CreateMemberAccountAction {
    data class ChangeFirstName(val value: String) : CreateMemberAccountAction
    data class ChangeMiddleName(val value: String) : CreateMemberAccountAction
    data class ChangeLastName(val value: String) : CreateMemberAccountAction
    data class ChangeUsername(val value: String) : CreateMemberAccountAction
    data class ChangeEmail(val value: String) : CreateMemberAccountAction
    data class ChangeMobileNumber(val value: String) : CreateMemberAccountAction
    data class ChangeExternalId(val value: String) : CreateMemberAccountAction
    data class ChangePassword(val value: String) : CreateMemberAccountAction
    data class ChangeConfirmPassword(val value: String) : CreateMemberAccountAction
    data class ChangeAuthenticationMode(val value: AuthenticationMode) : CreateMemberAccountAction

    data object TogglePasswordVisibility : CreateMemberAccountAction
    data object ToggleConfirmPasswordVisibility : CreateMemberAccountAction

    data object NavigateToBack : CreateMemberAccountAction
    data object NavigateToOtpVerification : CreateMemberAccountAction
    data object NavigateToSignIn : CreateMemberAccountAction
    data object Retry : CreateMemberAccountAction

    data object CreateAccount : CreateMemberAccountAction

    sealed interface Internal : CreateMemberAccountAction {
        data class ReceiveRegistrationResult(
            val result: ScreenState<RegistrationResult>,
        ) : Internal
    }
}

sealed interface CreateMemberAccountEvent {
    data class NavigateToOtpVerification(val isEmail: Boolean) : CreateMemberAccountEvent
    data object NavigateToSignIn : CreateMemberAccountEvent
    data object NavigateToBack : CreateMemberAccountEvent
}
