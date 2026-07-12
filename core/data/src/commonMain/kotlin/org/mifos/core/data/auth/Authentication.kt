/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See See https://github.com/openMF/kmp-project-template/blob/main/LICENSE
 */
package org.mifos.core.data.auth

import org.mifos.core.base.store.screen.ScreenState
import org.mifos.core.model.auth.ConfirmClientUserRequest
import org.mifos.core.model.auth.PasswordResetRequest
import org.mifos.core.model.auth.RegistrationRequest
import org.mifos.core.model.auth.RegistrationResult
import org.mifos.core.model.auth.RenewPasswordRequest
import org.mifos.core.model.auth.SignInRequest
import org.mifos.core.model.auth.User

interface Authentication {
    suspend fun signInSelf(signInRequest: SignInRequest): ScreenState<User>
    suspend fun signInFineract(signInRequest: SignInRequest): ScreenState<User>
    suspend fun registerMember(registrationRequest: RegistrationRequest): ScreenState<RegistrationResult>
    suspend fun confirmClientUser(confirmClientUserRequest: ConfirmClientUserRequest): ScreenState<Unit>
    suspend fun requestPasswordReset(passwordResetRequest: PasswordResetRequest): ScreenState<Unit>
    suspend fun renewPassword(renewPasswordRequest: RenewPasswordRequest): ScreenState<Unit>
}
