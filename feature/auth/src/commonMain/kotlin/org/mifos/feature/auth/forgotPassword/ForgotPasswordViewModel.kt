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

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.StringResource
import org.mifos.core.base.store.screen.DataFreshness
import org.mifos.core.base.store.screen.ScreenState
import org.mifos.core.base.store.submit.SubmitState
import org.mifos.core.base.ui.viewmodel.BaseViewModel
import org.mifos.core.data.auth.Authentication
import org.mifos.core.model.auth.PasswordResetRequest
import org.mifos.core.ui.utils.TextFieldsValidator
import org.mifos.feature.auth.createMemberAccount.AuthenticationMode

class ForgotPasswordViewModel(
    private val authentication: Authentication,
) : BaseViewModel<ForgotPasswordState, ForgotPasswordEvent, ForgotPasswordAction>(ForgotPasswordState()) {

    override fun handleAction(action: ForgotPasswordAction) {
        when (action) {
            is ForgotPasswordAction.ChangeUsername -> {
                mutableStateFlow.update {
                    it.copy(
                        username = action.value,
                        errorUsername = TextFieldsValidator.stringValidator(action.value),
                        submitState = SubmitState.Idle,
                    )
                }
            }
            is ForgotPasswordAction.ChangeAuthenticationMode -> {
                mutableStateFlow.update {
                    it.copy(
                        authenticationMode = action.value,
                        submitState = SubmitState.Idle,
                    )
                }
            }
            ForgotPasswordAction.SendOtp -> sendOtp()
            ForgotPasswordAction.BackClicked -> sendEvent(ForgotPasswordEvent.NavigateBack)
            ForgotPasswordAction.SignInClicked -> sendEvent(ForgotPasswordEvent.NavigateToSignIn)
            ForgotPasswordAction.Retry -> {
                mutableStateFlow.update {
                    it.copy(
                        screenState = ScreenState.Content(Unit, DataFreshness.FRESH),
                        submitState = SubmitState.Idle,
                    )
                }
            }
            is ForgotPasswordAction.Internal.ReceiveSendOtpResult -> handleSendOtpResult(action.result)
        }
    }

    private fun sendOtp() {
        mutableStateFlow.update {
            it.copy(submitState = SubmitState.Submitting())
        }
        viewModelScope.launch {
            val result = authentication.requestPasswordReset(
                PasswordResetRequest(
                    username = state.username,
                    authenticationMode = state.authenticationMode.value,
                ),
            )
            sendAction(ForgotPasswordAction.Internal.ReceiveSendOtpResult(result))
        }
    }

    private fun handleSendOtpResult(result: ScreenState<Unit>) {
        when (result) {
            is ScreenState.Content -> {
                mutableStateFlow.update {
                    it.copy(submitState = SubmitState.Submitted(Unit))
                }
                sendEvent(
                    ForgotPasswordEvent.NavigateToOtpVerification(
                        username = state.username,
                        isEmail = state.authenticationMode == AuthenticationMode.EMAIL,
                    ),
                )
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

data class ForgotPasswordState(
    val username: String = "",
    val authenticationMode: AuthenticationMode = AuthenticationMode.EMAIL,
    val errorUsername: StringResource? = null,
    val screenState: ScreenState<Unit> = ScreenState.Content(Unit, DataFreshness.FRESH),
    val submitState: SubmitState<Unit> = SubmitState.Idle,
) {
    val isButtonEnabled: Boolean
        get() = username.isNotEmpty() && errorUsername == null && submitState !is SubmitState.Submitting
}

sealed interface ForgotPasswordEvent {
    data object NavigateBack : ForgotPasswordEvent
    data class NavigateToOtpVerification(val username: String, val isEmail: Boolean) : ForgotPasswordEvent
    data object NavigateToSignIn : ForgotPasswordEvent
}

sealed interface ForgotPasswordAction {
    data class ChangeUsername(val value: String) : ForgotPasswordAction
    data class ChangeAuthenticationMode(val value: AuthenticationMode) : ForgotPasswordAction
    data object SendOtp : ForgotPasswordAction
    data object BackClicked : ForgotPasswordAction
    data object SignInClicked : ForgotPasswordAction
    data object Retry : ForgotPasswordAction

    sealed interface Internal : ForgotPasswordAction {
        data class ReceiveSendOtpResult(val result: ScreenState<Unit>) : Internal
    }
}
