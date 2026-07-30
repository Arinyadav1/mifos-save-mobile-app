/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See See https://github.com/openMF/kmp-project-template/blob/main/LICENSE
 */
package org.mifos.core.network.fineract.group.apis

import de.jensklingenberg.ktorfit.http.Body
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.POST
import de.jensklingenberg.ktorfit.http.PUT
import de.jensklingenberg.ktorfit.http.Path
import de.jensklingenberg.ktorfit.http.Query
import io.ktor.client.statement.HttpResponse
import kotlinx.coroutines.flow.Flow
import org.mifos.core.network.commonDto.Page
import org.mifos.core.network.fineract.group.dto.ActivateGroupRequestDto
import org.mifos.core.network.fineract.group.dto.AssociateClientsRequestDto
import org.mifos.core.network.fineract.group.dto.CreateGroupRequestDto
import org.mifos.core.network.fineract.group.dto.DisassociateClientsRequestDto
import org.mifos.core.network.fineract.group.dto.GroupAccountsDto
import org.mifos.core.network.fineract.group.dto.GroupDto
import org.mifos.core.network.fineract.group.dto.GroupTemplateResponseDto
import org.mifos.core.network.fineract.group.dto.UpdateGroupRequestDto
import org.mifos.core.network.utils.ApiEndPoints

interface GroupApi {
    @GET(ApiEndPoints.GROUPS)
    fun getGroups(
        @Query("paged") paged: Boolean = true,
        @Query("offset") offset: Int,
        @Query("limit") limit: Int,
    ): Flow<Page<GroupDto>>

    @GET(ApiEndPoints.GROUPS)
    fun getListGroups(): Flow<List<GroupDto>>

    @GET("${ApiEndPoints.GROUPS}/{groupId}")
    fun getGroupDetails(
        @Path("groupId") groupId: Long,
        @Query("associations") associations: String = "clientMembers",
    ): Flow<GroupDto>

    @GET("${ApiEndPoints.GROUPS}/{groupId}/${ApiEndPoints.ACCOUNTS}")
    fun getGroupAccounts(
        @Path("groupId") groupId: Long,
        @Query("fields") fields: String,
    ): Flow<GroupAccountsDto>

    @POST("${ApiEndPoints.GROUPS}/{groupId}")
    suspend fun disassociateClients(
        @Path("groupId") groupId: Long,
        @Query("command") command: String = "disassociateClients",
        @Body request: DisassociateClientsRequestDto,
    ): HttpResponse

    @POST("${ApiEndPoints.GROUPS}/{groupId}")
    suspend fun associateClients(
        @Path("groupId") groupId: Long,
        @Query("command") command: String = "associateClients",
        @Body request: AssociateClientsRequestDto,
    ): HttpResponse

    @POST("${ApiEndPoints.GROUPS}/{groupId}")
    suspend fun activateGroup(
        @Path("groupId") groupId: Long,
        @Query("command") command: String = "activate",
        @Body request: ActivateGroupRequestDto,
    ): HttpResponse

    @GET("${ApiEndPoints.GROUPS}/template")
    fun getGroupTemplate(): Flow<GroupTemplateResponseDto>

    @POST(ApiEndPoints.GROUPS)
    suspend fun createGroup(
        @Body request: CreateGroupRequestDto,
    ): HttpResponse

    @PUT("${ApiEndPoints.GROUPS}/{groupId}")
    suspend fun updateGroup(
        @Path("groupId") groupId: Long,
        @Body request: UpdateGroupRequestDto,
    ): HttpResponse
}
