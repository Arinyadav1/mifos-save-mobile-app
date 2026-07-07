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

import org.mifos.core.base.common.manager.DispatcherManager
import org.mifos.core.base.store.screen.ScreenState
import org.mifos.core.data.auth.Authentication
import org.mifos.core.data.infra.NetworkMonitor
import org.mifos.core.data.mapper.auth.toModel
import org.mifos.core.data.util.runAsDataState
import org.mifos.core.model.auth.User
import org.mifos.core.network.DataManager
import org.mifos.core.network.commonDto.CredentialsRequestDto

class AuthenticationImpl(
    private val dataManager: DataManager,
    private val networkMonitor: NetworkMonitor,
    private val dispatcher: DispatcherManager,
) : Authentication {

    override suspend fun signInSelf(
        username: String,
        password: String,
    ): ScreenState<User> {
        return runAsDataState(
            networkMonitor,
            dispatcher.io,
        ) {
            dataManager.self.authApi.authenticate(
                CredentialsRequestDto(username, password),
            ).toModel()
        }
    }

    override suspend fun signInFineract(
        username: String,
        password: String,
    ): ScreenState<User> {
        return runAsDataState(
            networkMonitor,
            dispatcher.io,
        ) {
            dataManager.fineract.authApi.authenticate(
                CredentialsRequestDto(username, password),
            ).toModel()
        }
    }
}
