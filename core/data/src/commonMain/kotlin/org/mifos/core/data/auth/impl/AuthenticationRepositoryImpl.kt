/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See See https://github.com/openMF/kmp-project-template/blob/main/LICENSE
 */
package org.mifos.core.data.auth.impl

import io.ktor.client.statement.bodyAsText
import io.ktor.http.isSuccess
import kotlinx.serialization.json.Json
import org.mifos.core.base.common.manager.DispatcherManager
import org.mifos.core.base.store.screen.ScreenState
import org.mifos.core.data.auth.AuthenticationRepository
import org.mifos.core.data.infra.NetworkMonitor
import org.mifos.core.data.mapper.auth.toDto
import org.mifos.core.data.mapper.auth.toModel
import org.mifos.core.data.util.extractErrorMessage
import org.mifos.core.data.util.runAsDataState
import org.mifos.core.model.auth.ConfirmClientUserRequest
import org.mifos.core.model.auth.PasswordResetRequest
import org.mifos.core.model.auth.RegistrationRequest
import org.mifos.core.model.auth.RegistrationResult
import org.mifos.core.model.auth.RenewPasswordRequest
import org.mifos.core.model.auth.SignInRequest
import org.mifos.core.model.auth.User
import org.mifos.core.network.DataManager
import org.mifos.core.network.commonDto.UserResponseDto
import org.mifos.core.network.selfService.auth.dto.RegistrationResponseDto

class AuthenticationRepositoryImpl(
    private val dataManager: DataManager,
    private val networkMonitor: NetworkMonitor,
    private val dispatcher: DispatcherManager,
) : AuthenticationRepository {

    override suspend fun signInSelf(
        signInRequest: SignInRequest,
    ): ScreenState<User> {
        return runAsDataState(
            networkMonitor,
            dispatcher.io,
        ) {
            val response = dataManager.self.authApi.authenticate(
                signInRequest.toDto(),
            )
            if (!response.status.isSuccess()) {
                val errorMessage = extractErrorMessage(response)
                throw Exception(errorMessage)
            }
            val json = Json { ignoreUnknownKeys = true }
            val responseText = response.bodyAsText()
            json.decodeFromString<UserResponseDto>(responseText).toModel()
        }
    }

    override suspend fun signInFineract(
        signInRequest: SignInRequest,
    ): ScreenState<User> {
        return runAsDataState(
            networkMonitor,
            dispatcher.io,
        ) {
            val response = dataManager.fineract.authApi.authenticate(
                signInRequest.toDto(),
            )
            if (!response.status.isSuccess()) {
                val errorMessage = extractErrorMessage(response)
                throw Exception(errorMessage)
            }
            val json = Json { ignoreUnknownKeys = true }
            val responseText = response.bodyAsText()
            json.decodeFromString<UserResponseDto>(responseText).toModel()
        }
    }

    override suspend fun registerMember(
        registrationRequest: RegistrationRequest,
    ): ScreenState<RegistrationResult> {
        return runAsDataState(
            networkMonitor,
            dispatcher.io,
        ) {
            val response = dataManager.self.authApi.register(registrationRequest.toDto())

            if (!response.status.isSuccess()) {
                val errorMessage = extractErrorMessage(response)
                throw Exception(errorMessage)
            }
            val json = Json { ignoreUnknownKeys = true }
            val responseText = response.bodyAsText()
            json.decodeFromString<RegistrationResponseDto>(responseText).toModel()
        }
    }

    override suspend fun confirmClientUser(
        confirmClientUserRequest: ConfirmClientUserRequest,
    ): ScreenState<Unit> {
        return runAsDataState(
            networkMonitor,
            dispatcher.io,
        ) {
            val response = dataManager.self.authApi.confirmClientUser(
                confirmClientUserRequest.toDto(),
            )
            if (!response.status.isSuccess()) {
                val errorMessage = extractErrorMessage(response)
                throw Exception(errorMessage)
            }
        }
    }

    override suspend fun requestPasswordReset(
        passwordResetRequest: PasswordResetRequest,
    ): ScreenState<Unit> {
        return runAsDataState(
            networkMonitor,
            dispatcher.io,
        ) {
            val response = dataManager.self.authApi.requestPasswordReset(
                passwordResetRequest.toDto(),
            )
            if (!response.status.isSuccess()) {
                val errorMessage = extractErrorMessage(response)
                throw Exception(errorMessage)
            }
        }
    }

    override suspend fun renewPassword(
        renewPasswordRequest: RenewPasswordRequest,
    ): ScreenState<Unit> {
        return runAsDataState(
            networkMonitor,
            dispatcher.io,
        ) {
            val response = dataManager.self.authApi.renewPassword(
                renewPasswordRequest.toDto(),
            )
            if (!response.status.isSuccess()) {
                val errorMessage = extractErrorMessage(response)
                throw Exception(errorMessage)
            }
        }
    }
}
