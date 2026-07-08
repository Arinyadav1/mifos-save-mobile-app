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

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.mifos.core.base.store.screen.DataFreshness
import org.mifos.core.base.store.screen.ScreenState
import org.mifos.core.base.store.submit.SubmitState
import org.mifos.core.base.ui.viewmodel.BaseViewModel
import org.mifos.core.data.auth.Authentication
import org.mifos.core.data.user.UserDataRepository
import org.mifos.core.domain.validate.passwordValidate
import org.mifos.core.domain.validate.usernameValidate
import org.mifos.core.model.auth.User

class SignInViewModel(
    private val authentication: Authentication,
    private val userDataRepository: UserDataRepository,
) : BaseViewModel<SignInState, SignInEvent, SignInAction>(SignInState()) {

    private fun selfSignIn() {
        mutableStateFlow.update {
            it.copy(submitState = SubmitState.Submitting())
        }
        viewModelScope.launch {
            val result = authentication.signInSelf(
                username = state.username,
                password = state.password,
            )
            sendAction(SignInAction.Internal.ReceiveSelfSignInResult(result))
        }
    }

    private fun fineractSignIn() {
        mutableStateFlow.update {
            it.copy(submitState = SubmitState.Submitting())
        }
        viewModelScope.launch {
            val result = authentication.signInFineract(
                username = state.username,
                password = state.password,
            )
            sendAction(SignInAction.Internal.ReceiveFineractSignInResult(result))
        }
    }

    private fun handleFineractSignInResult(action: SignInAction.Internal.ReceiveFineractSignInResult) {
        when (val result = action.fineractSignInResult) {
            is ScreenState.Content -> {
                val data = result.data
                viewModelScope.launch {
                    data.roles.firstOrNull()?.name?.let { userDataRepository.setRole(it) }
                    data.base64EncodedAuthenticationKey?.let { userDataRepository.setToken(it) }
                    userDataRepository.setIsAuthenticated(true)
                    userDataRepository.setIsUnlocked(true)
                }

                mutableStateFlow.update {
                    it.copy(
                        submitState = SubmitState.Submitted(Unit),
                    )
                }
            }
            else -> {
                selfSignIn()
            }
        }
    }

    private fun handleSelfSignInResult(action: SignInAction.Internal.ReceiveSelfSignInResult) {
        when (val result = action.selfSignInResult) {
            is ScreenState.Content -> {
                val data = result.data
                viewModelScope.launch {
                    data.roles.firstOrNull()?.name?.let { userDataRepository.setRole(it) }
                    data.base64EncodedAuthenticationKey?.let { userDataRepository.setToken(it) }
                    userDataRepository.setIsAuthenticated(true)
                    userDataRepository.setIsUnlocked(true)
                }
                mutableStateFlow.update {
                    it.copy(
                        submitState = SubmitState.Submitted(Unit),
                    )
                }
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

    override fun handleAction(action: SignInAction) {
        when (action) {
            is SignInAction.ChangePassword -> {
                mutableStateFlow.update {
                    it.copy(
                        errorPassword = passwordValidate(action.value),
                        password = action.value,
                        submitState = SubmitState.Idle,
                    )
                }
            }
            is SignInAction.ChangeUsername -> {
                mutableStateFlow.update {
                    it.copy(
                        errorUsername = usernameValidate(action.value),
                        username = action.value,
                        submitState = SubmitState.Idle,
                    )
                }
            }
            is SignInAction.Internal.ReceiveFineractSignInResult -> {
                handleFineractSignInResult(action)
            }
            SignInAction.SingIn -> {
                fineractSignIn()
            }
            SignInAction.TogglePasswordVisibility -> {
                mutableStateFlow.update {
                    it.copy(
                        isPasswordVisible = !state.isPasswordVisible,
                    )
                }
            }
            is SignInAction.Internal.ReceiveSelfSignInResult -> {
                handleSelfSignInResult(action)
            }
            SignInAction.ResetSubmitState -> {
                mutableStateFlow.update {
                    it.copy(submitState = SubmitState.Idle)
                }
            }
        }
    }
}

data class SignInState(
    val username: String = "",
    val password: String = "",
    val isPasswordVisible: Boolean = false,
    val screenState: ScreenState<Unit> = ScreenState.Content(
        data = Unit,
        freshness = DataFreshness.FRESH,
    ),
    val submitState: SubmitState<Unit> = SubmitState.Idle,
    val errorUsername: String? = null,
    val errorPassword: String? = null,
) {
    val isButtonEnabled =
        username.isNotBlank() &&
            password.isNotBlank() &&
            errorUsername == null &&
            errorPassword == null
}

sealed interface SignInEvent {
    object NavigateToSignUpTypeScreen : SignInEvent
    object NavigateToForgetPasswordScreen : SignInEvent
}

sealed interface SignInAction {
    data class ChangeUsername(val value: String) : SignInAction
    data class ChangePassword(val value: String) : SignInAction
    data object TogglePasswordVisibility : SignInAction
    data object SingIn : SignInAction
    data object ResetSubmitState : SignInAction

    sealed interface Internal : SignInAction {
        data class ReceiveSelfSignInResult(
            val selfSignInResult: ScreenState<User>,
        ) : Internal

        data class ReceiveFineractSignInResult(
            val fineractSignInResult: ScreenState<User>,
        ) : Internal
    }
}
