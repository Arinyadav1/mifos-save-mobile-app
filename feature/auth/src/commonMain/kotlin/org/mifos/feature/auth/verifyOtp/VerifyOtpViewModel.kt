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

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import org.jetbrains.compose.resources.StringResource
import org.mifos.core.base.store.screen.DataFreshness
import org.mifos.core.base.store.screen.ScreenState
import org.mifos.core.base.store.submit.SubmitState
import org.mifos.core.base.ui.viewmodel.BaseViewModel
import org.mifos.core.data.auth.AuthenticationRepository
import org.mifos.core.model.auth.ConfirmClientUserRequest
import org.mifos.core.model.auth.PasswordResetRequest
import org.mifos.core.model.auth.RenewPasswordRequest
import org.mifos.core.ui.utils.PasswordChecker
import org.mifos.core.ui.utils.PasswordStrength
import org.mifos.core.ui.utils.PasswordStrengthResult
import org.mifos.feature.auth.createMemberAccount.AuthenticationMode
import org.mifos.feature.auth.generated.resources.Res
import org.mifos.feature.auth.generated.resources.feature_auth_error_resend_failed
import org.mifos.feature.auth.generated.resources.feature_auth_error_verification_failed
import org.mifos.feature.auth.generated.resources.feature_auth_passwords_do_not_match
import kotlin.time.Duration.Companion.milliseconds

class VerifyOtpViewModel(
    savedStateHandle: SavedStateHandle,
    private val authenticationRepository: AuthenticationRepository,
) : BaseViewModel<VerifyOtpState, VerifyOtpEvent, VerifyOtpAction>(
    VerifyOtpState(),
) {

    private var timerJob: Job? = null

    init {
        val route = savedStateHandle.toRoute<VerifyOtpRoute>()

        mutableStateFlow.update {
            it.copy(
                flow = route.flow,
                isEmail = route.isEmail,
                username = route.username,
            )
        }

        if (route.flow == VerifyOtpFlow.RESET_PASSWORD_VERIFY) {
            startResendTimer()
        }
    }

    override fun handleAction(action: VerifyOtpAction) {
        when (action) {
            is VerifyOtpAction.OtpChanged -> handleOtpChanged(action.value)
            is VerifyOtpAction.ChangePassword -> handlePasswordChanged(action.value)
            is VerifyOtpAction.ChangeConfirmPassword -> handleConfirmPasswordChanged(action.value)
            VerifyOtpAction.TogglePasswordVisibility -> togglePasswordVisibility()
            VerifyOtpAction.ToggleConfirmPasswordVisibility -> toggleConfirmPasswordVisibility()
            VerifyOtpAction.VerifyOtp -> submitOtp()
            VerifyOtpAction.ResendOtp -> resendOtp()
            VerifyOtpAction.BackClicked -> sendEvent(VerifyOtpEvent.NavigateBack)
            VerifyOtpAction.Retry -> handleRetry()
            VerifyOtpAction.DismissSuccessDialog -> handleDismissSuccessDialog()
            is VerifyOtpAction.Internal.ReceiveVerificationResult -> handleVerificationResult(action.result)
            is VerifyOtpAction.Internal.ReceiveResendResult -> handleResendResult(action.result)
        }
    }

    private fun handleOtpChanged(value: String) {
        if (value.length <= 6 && value.all { it.isDigit() }) {
            mutableStateFlow.update {
                it.copy(otpCode = value, errorMessage = null)
            }
        }
    }

    private fun handleRetry() {
        mutableStateFlow.update {
            it.copy(
                screenState = ScreenState.Content(Unit, DataFreshness.FRESH),
                submitState = SubmitState.Idle,
            )
        }
    }

    private fun handleDismissSuccessDialog() {
        mutableStateFlow.update { it.copy(showSuccessDialog = false) }
        sendEvent(VerifyOtpEvent.VerificationSuccess)
    }

    private fun handlePasswordChanged(value: String) {
        if (value.isEmpty()) {
            mutableStateFlow.update {
                it.copy(
                    password = value,
                    passwordStrengthState = PasswordStrength.LEVEL_0,
                    passwordFeedback = emptyList(),
                    errorPassword = null,
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
                                errorPassword = null,
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

    private fun startResendTimer() {
        timerJob?.cancel()
        mutableStateFlow.update { it.copy(timerSeconds = 45, isResendEnabled = false) }
        timerJob = viewModelScope.launch {
            while (stateFlow.value.timerSeconds > 0) {
                delay(1000.milliseconds)
                mutableStateFlow.update {
                    val nextSec = it.timerSeconds - 1
                    it.copy(
                        timerSeconds = nextSec,
                        isResendEnabled = nextSec <= 0,
                    )
                }
            }
        }
    }

    private fun submitOtp() {
        mutableStateFlow.update { it.copy(submitState = SubmitState.Submitting()) }
        viewModelScope.launch {
            val result = when (state.flow) {
                VerifyOtpFlow.MEMBER_ACCOUNT_VERIFY -> {
                    authenticationRepository.confirmClientUser(
                        ConfirmClientUserRequest(verificationToken = state.otpCode),
                    )
                }
                VerifyOtpFlow.ADMIN_ACCOUNT_VERIFY -> {
                    ScreenState.Content(Unit, DataFreshness.FRESH)
                }
                VerifyOtpFlow.RESET_PASSWORD_VERIFY -> {
                    authenticationRepository.renewPassword(
                        RenewPasswordRequest(
                            externalAuthenticationToken = state.otpCode,
                            password = state.password,
                            repeatPassword = state.confirmPassword,
                        ),
                    )
                }
            }
            sendAction(VerifyOtpAction.Internal.ReceiveVerificationResult(result))
        }
    }

    private fun handleVerificationResult(result: ScreenState<Unit>) {
        when (result) {
            is ScreenState.Content -> {
                mutableStateFlow.update {
                    it.copy(
                        submitState = SubmitState.Submitted(Unit),
                        showSuccessDialog = true,
                    )
                }
            }
            is ScreenState.Error -> {
                mutableStateFlow.update {
                    it.copy(
                        submitState = SubmitState.Idle,
                        errorMessage = Res.string.feature_auth_error_verification_failed,
                    )
                }
            }
            else -> {
                mutableStateFlow.update { it.copy(submitState = SubmitState.Idle) }
            }
        }
    }

    private fun resendOtp() {
        if (!state.isResendEnabled) return
        mutableStateFlow.update { it.copy(submitState = SubmitState.Submitting()) }
        viewModelScope.launch {
            val result = authenticationRepository.requestPasswordReset(
                PasswordResetRequest(
                    username = state.username,
                    authenticationMode = if (state.isEmail) {
                        AuthenticationMode.EMAIL.value
                    } else {
                        AuthenticationMode.PHONE.value
                    },
                ),
            )
            sendAction(VerifyOtpAction.Internal.ReceiveResendResult(result))
        }
    }

    private fun handleResendResult(result: ScreenState<Unit>) {
        when (result) {
            is ScreenState.Content -> {
                mutableStateFlow.update { it.copy(submitState = SubmitState.Idle) }
                startResendTimer()
            }
            is ScreenState.Error -> {
                mutableStateFlow.update {
                    it.copy(
                        submitState = SubmitState.Idle,
                        errorMessage = Res.string.feature_auth_error_resend_failed,
                    )
                }
            }
            else -> {
                mutableStateFlow.update { it.copy(submitState = SubmitState.Idle) }
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        timerJob?.cancel()
    }
}

@Serializable
enum class VerifyOtpFlow {
    MEMBER_ACCOUNT_VERIFY,
    ADMIN_ACCOUNT_VERIFY,
    RESET_PASSWORD_VERIFY,
}

data class VerifyOtpState(
    val flow: VerifyOtpFlow = VerifyOtpFlow.MEMBER_ACCOUNT_VERIFY,
    val isEmail: Boolean = true,
    val username: String = "",
    val otpCode: String = "",
    val timerSeconds: Int = 45,
    val isResendEnabled: Boolean = false,
    val screenState: ScreenState<Unit> = ScreenState.Content(Unit, DataFreshness.FRESH),
    val submitState: SubmitState<Unit> = SubmitState.Idle,
    val errorMessage: StringResource? = null,
    val showSuccessDialog: Boolean = false,
    val password: String = "",
    val confirmPassword: String = "",
    val passwordFeedback: List<String> = emptyList(),
    val passwordStrengthState: PasswordStrength = PasswordStrength.LEVEL_0,
    val isPasswordVisible: Boolean = false,
    val isConfirmPasswordVisible: Boolean = false,
    val errorPassword: String? = null,
    val errorConfirmPassword: StringResource? = null,
) {
    val isVerifyEnabled: Boolean
        get() = if (flow == VerifyOtpFlow.RESET_PASSWORD_VERIFY) {
            otpCode.length == 6 &&
                password.isNotEmpty() &&
                confirmPassword.isNotEmpty() &&
                errorPassword == null &&
                errorConfirmPassword == null &&
                submitState !is SubmitState.Submitting
        } else {
            otpCode.length == 6 && submitState !is SubmitState.Submitting
        }
}

sealed interface VerifyOtpAction {
    data class OtpChanged(val value: String) : VerifyOtpAction
    data class ChangePassword(val value: String) : VerifyOtpAction
    data class ChangeConfirmPassword(val value: String) : VerifyOtpAction
    data object TogglePasswordVisibility : VerifyOtpAction
    data object ToggleConfirmPasswordVisibility : VerifyOtpAction
    data object VerifyOtp : VerifyOtpAction
    data object ResendOtp : VerifyOtpAction
    data object BackClicked : VerifyOtpAction
    data object Retry : VerifyOtpAction
    data object DismissSuccessDialog : VerifyOtpAction

    sealed interface Internal : VerifyOtpAction {
        data class ReceiveVerificationResult(val result: ScreenState<Unit>) : Internal
        data class ReceiveResendResult(val result: ScreenState<Unit>) : Internal
    }
}

sealed interface VerifyOtpEvent {
    data object VerificationSuccess : VerifyOtpEvent
    data object NavigateBack : VerifyOtpEvent
}
