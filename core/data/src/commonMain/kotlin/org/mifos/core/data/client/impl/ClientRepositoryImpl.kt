/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See See https://github.com/openMF/kmp-project-template/blob/main/LICENSE
 */
package org.mifos.core.data.client.impl

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import org.mifos.core.base.common.manager.DispatcherManager
import org.mifos.core.base.store.screen.ScreenState
import org.mifos.core.data.client.ClientRepository
import org.mifos.core.data.infra.NetworkMonitor
import org.mifos.core.data.mapper.client.toModel
import org.mifos.core.data.mapper.group.toModel
import org.mifos.core.data.util.asScreenStateFlow
import org.mifos.core.data.util.runAsDataState
import org.mifos.core.model.client.ClientAccounts
import org.mifos.core.model.client.ClientTemplate
import org.mifos.core.model.group.ClientMember
import org.mifos.core.network.DataManager

class ClientRepositoryImpl(
    private val dataManager: DataManager,
    private val networkMonitor: NetworkMonitor,
    private val dispatcher: DispatcherManager,
) : ClientRepository {

    override fun getClientTemplate(): Flow<ScreenState<ClientTemplate>> {
        return dataManager.fineract.clientApi
            .getClientTemplate()
            .asScreenStateFlow(
                networkMonitor = networkMonitor,
                dispatcher = dispatcher.io,
            ) { dto ->
                dto.toModel()
            }
    }

    override fun getClientAccounts(clientId: Long): Flow<ScreenState<ClientAccounts>> {
        return dataManager.fineract.clientApi
            .getClientAccounts(clientId)
            .asScreenStateFlow(
                networkMonitor = networkMonitor,
                dispatcher = dispatcher.io,
            ) { dto ->
                dto.toModel()
            }
    }

    override suspend fun searchClients(
        displayName: String,
        officeId: Long,
    ): ScreenState<List<ClientMember>> {
        return runAsDataState(
            networkMonitor = networkMonitor,
            context = dispatcher.io,
        ) {
            val response = dataManager.fineract.clientApi
                .searchClients(
                    displayName = displayName,
                    officeId = officeId,
                )
                .first()
            response.pageItems.map { it.toModel() }
        }
    }
}
