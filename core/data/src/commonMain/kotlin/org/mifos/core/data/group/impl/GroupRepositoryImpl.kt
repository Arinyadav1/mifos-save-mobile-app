/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See See https://github.com/openMF/kmp-project-template/blob/main/LICENSE
 */
package org.mifos.core.data.group.impl

import io.ktor.http.isSuccess
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import org.mifos.core.base.common.manager.DispatcherManager
import org.mifos.core.base.store.infra.StoreFactory
import org.mifos.core.base.store.paging.PageKey
import org.mifos.core.base.store.screen.ScreenState
import org.mifos.core.data.group.GroupRepository
import org.mifos.core.data.infra.NetworkMonitor
import org.mifos.core.data.mapper.group.toDto
import org.mifos.core.data.mapper.group.toModel
import org.mifos.core.data.util.asScreenStateFlow
import org.mifos.core.data.util.extractErrorMessage
import org.mifos.core.data.util.runAsDataState
import org.mifos.core.model.group.CreateGroupRequest
import org.mifos.core.model.group.Group
import org.mifos.core.model.group.GroupAccounts
import org.mifos.core.model.group.GroupTemplate
import org.mifos.core.network.DataManager
import org.mifos.core.network.fineract.group.dto.ActivateGroupRequestDto
import org.mifos.core.network.fineract.group.dto.AssociateClientsRequestDto
import org.mifos.core.network.fineract.group.dto.DisassociateClientsRequestDto
import org.mifos.core.network.fineract.group.dto.UpdateGroupRequestDto
import org.mobilenativefoundation.store.store5.Fetcher
import org.mobilenativefoundation.store.store5.Store

class GroupRepositoryImpl(
    private val dataManager: DataManager,
    private val networkMonitor: NetworkMonitor,
    private val dispatcher: DispatcherManager,
) : GroupRepository {

    override fun listOfGroupPaging(): Store<PageKey, List<Group>> = StoreFactory.createMemoryStore(
        fetcher = Fetcher.of { key ->
            val pageResponse = dataManager.fineract.groupApi
                .getGroups(
                    paged = true,
                    offset = key.offset,
                    limit = key.pageSize,
                )
                .first()

            pageResponse.pageItems.map { it.toModel() }
        },
    )

    override fun getGroupDetails(groupId: Long): Flow<ScreenState<Group>> {
        return dataManager.fineract.groupApi
            .getGroupDetails(groupId)
            .asScreenStateFlow(
                networkMonitor = networkMonitor,
                dispatcher = dispatcher.io,
            ) { dto ->
                dto.toModel()
            }
    }

    override fun getGroupAccounts(groupId: Long, fields: String): Flow<ScreenState<GroupAccounts>> {
        return dataManager.fineract.groupApi
            .getGroupAccounts(groupId, fields)
            .asScreenStateFlow(
                networkMonitor = networkMonitor,
                dispatcher = dispatcher.io,
            ) { dto ->
                dto.toModel()
            }
    }

    override suspend fun disassociateClients(
        groupId: Long,
        clientMembers: List<Long>,
    ): ScreenState<Unit> {
        return runAsDataState(
            networkMonitor = networkMonitor,
            context = dispatcher.io,
        ) {
            val response = dataManager.fineract.groupApi.disassociateClients(
                groupId = groupId,
                request = DisassociateClientsRequestDto(clientMembers),
            )
            if (!response.status.isSuccess()) {
                val errorMessage = extractErrorMessage(response)
                throw Exception(errorMessage)
            }
        }
    }

    override suspend fun associateClients(
        groupId: Long,
        clientMembers: List<Long>,
    ): ScreenState<Unit> {
        return runAsDataState(
            networkMonitor = networkMonitor,
            context = dispatcher.io,
        ) {
            val response = dataManager.fineract.groupApi.associateClients(
                groupId = groupId,
                request = AssociateClientsRequestDto(clientMembers),
            )
            if (!response.status.isSuccess()) {
                val errorMessage = extractErrorMessage(response)
                throw Exception(errorMessage)
            }
        }
    }

    override suspend fun activateGroup(
        groupId: Long,
        activationDate: String,
        dateFormat: String,
        locale: String,
    ): ScreenState<Unit> {
        return runAsDataState(
            networkMonitor = networkMonitor,
            context = dispatcher.io,
        ) {
            val response = dataManager.fineract.groupApi.activateGroup(
                groupId = groupId,
                request = ActivateGroupRequestDto(
                    activationDate = activationDate,
                    dateFormat = dateFormat,
                    locale = locale,
                ),
            )
            if (!response.status.isSuccess()) {
                val errorMessage = extractErrorMessage(response)
                throw Exception(errorMessage)
            }
        }
    }

    override fun getGroupTemplate(): Flow<ScreenState<GroupTemplate>> {
        return dataManager.fineract.groupApi
            .getGroupTemplate()
            .asScreenStateFlow(
                networkMonitor = networkMonitor,
                dispatcher = dispatcher.io,
            ) { dto ->
                dto.toModel()
            }
    }

    override suspend fun createGroup(request: CreateGroupRequest): ScreenState<Unit> {
        return runAsDataState(
            networkMonitor = networkMonitor,
            context = dispatcher.io,
        ) {
            val response = dataManager.fineract.groupApi.createGroup(
                request = request.toDto(),
            )
            if (!response.status.isSuccess()) {
                val errorMessage = extractErrorMessage(response)
                throw Exception(errorMessage)
            }
        }
    }

    override suspend fun updateGroup(groupId: Long, name: String): ScreenState<Unit> {
        return runAsDataState(
            networkMonitor = networkMonitor,
            context = dispatcher.io,
        ) {
            val response = dataManager.fineract.groupApi.updateGroup(
                groupId = groupId,
                request = UpdateGroupRequestDto(name = name),
            )
            if (!response.status.isSuccess()) {
                val errorMessage = extractErrorMessage(response)
                throw Exception(errorMessage)
            }
        }
    }

    override fun getGroups(offset: Int, limit: Int): Flow<ScreenState<List<Group>>> {
        return dataManager.fineract.groupApi
            .getGroups(paged = true, offset = offset, limit = limit)
            .asScreenStateFlow(
                networkMonitor = networkMonitor,
                dispatcher = dispatcher.io,
            ) { page ->
                page.pageItems.map { it.toModel() }
            }
    }
}
