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
import org.mifos.core.data.auth.Authentication
import org.mifos.core.model.auth.ConfirmClientUserRequest
import org.mifos.feature.auth.generated.resources.Res
import org.mifos.feature.auth.generated.resources.feature_auth_error_resend_failed
import org.mifos.feature.auth.generated.resources.feature_auth_error_verification_failed
import kotlin.time.Duration.Companion.milliseconds

class VerifyOtpViewModel(
    savedStateHandle: SavedStateHandle,
    private val authentication: Authentication,
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
            )
        }

        if (route.flow == VerifyOtpFlow.RESET_PASSWORD_VERIFY) {
            startResendTimer()
        }
    }

    override fun handleAction(action: VerifyOtpAction) {
        when (action) {
            is VerifyOtpAction.OtpChanged -> {
                if (action.value.length <= 6 && action.value.all { it.isDigit() }) {
                    mutableStateFlow.update {
                        it.copy(otpCode = action.value, errorMessage = null)
                    }
                }
            }
            VerifyOtpAction.VerifyOtp -> submitOtp()
            VerifyOtpAction.ResendOtp -> resendOtp()
            VerifyOtpAction.BackClicked -> sendEvent(VerifyOtpEvent.NavigateBack)
            VerifyOtpAction.Retry -> {
                mutableStateFlow.update {
                    it.copy(
                        screenState = ScreenState.Content(Unit, DataFreshness.FRESH),
                        submitState = SubmitState.Idle,
                    )
                }
            }
            VerifyOtpAction.DismissSuccessDialog -> {
                mutableStateFlow.update { it.copy(showSuccessDialog = false) }
                sendEvent(VerifyOtpEvent.VerificationSuccess)
            }
            is VerifyOtpAction.Internal.ReceiveVerificationResult -> handleVerificationResult(action.result)
            is VerifyOtpAction.Internal.ReceiveResendResult -> handleResendResult(action.result)
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
                    authentication.confirmClientUser(
                        ConfirmClientUserRequest(verificationToken = state.otpCode),
                    )
                }
                VerifyOtpFlow.ADMIN_ACCOUNT_VERIFY -> {
                    ScreenState.Content(Unit, DataFreshness.FRESH)
                }
                VerifyOtpFlow.RESET_PASSWORD_VERIFY -> {
                    delay(1000.milliseconds)
                    ScreenState.Content(Unit, DataFreshness.FRESH)
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
            delay(1000.milliseconds)
            val result = ScreenState.Content(Unit, DataFreshness.FRESH)
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
    val otpCode: String = "",
    val timerSeconds: Int = 45,
    val isResendEnabled: Boolean = false,
    val screenState: ScreenState<Unit> = ScreenState.Content(Unit, DataFreshness.FRESH),
    val submitState: SubmitState<Unit> = SubmitState.Idle,
    val errorMessage: StringResource? = null,
    val showSuccessDialog: Boolean = false,
) {
    val isVerifyEnabled: Boolean
        get() = otpCode.length == 6 && submitState !is SubmitState.Submitting
}

sealed interface VerifyOtpAction {
    data class OtpChanged(val value: String) : VerifyOtpAction
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
