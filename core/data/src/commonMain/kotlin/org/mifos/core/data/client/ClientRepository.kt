/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See See https://github.com/openMF/kmp-project-template/blob/main/LICENSE
 */
package org.mifos.core.data.client

import kotlinx.coroutines.flow.Flow
import org.mifos.core.base.store.screen.ScreenState
import org.mifos.core.model.client.ClientAccounts
import org.mifos.core.model.group.ClientMember

interface ClientRepository {
    fun getClientAccounts(clientId: Long): Flow<ScreenState<ClientAccounts>>
    suspend fun searchClients(
        displayName: String,
        officeId: Long,
    ): ScreenState<List<ClientMember>>
}
