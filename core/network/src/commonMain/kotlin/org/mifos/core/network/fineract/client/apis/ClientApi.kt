/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See See https://github.com/openMF/kmp-project-template/blob/main/LICENSE
 */
package org.mifos.core.network.fineract.client.apis

import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.Path
import de.jensklingenberg.ktorfit.http.Query
import kotlinx.coroutines.flow.Flow
import org.mifos.core.network.commonDto.Page
import org.mifos.core.network.fineract.client.dto.ClientAccountsDto
import org.mifos.core.network.fineract.client.dto.ClientMemberDto
import org.mifos.core.network.fineract.client.dto.ClientTemplateDto
import org.mifos.core.network.utils.ApiEndPoints

interface ClientApi {
    @GET("${ApiEndPoints.CLIENTS}/template")
    fun getClientTemplate(): Flow<ClientTemplateDto>

    @GET("${ApiEndPoints.CLIENTS}/{clientId}/accounts")
    fun getClientAccounts(
        @Path("clientId") clientId: Long,
    ): Flow<ClientAccountsDto>

    @GET(ApiEndPoints.CLIENTS)
    fun searchClients(
        @Query("displayName") displayName: String,
        @Query("orphansOnly") orphansOnly: Boolean = true,
        @Query("sortOrder") sortOrder: String = "ASC",
        @Query("orderBy") orderBy: String = "displayName",
        @Query("officeId") officeId: Long,
    ): Flow<Page<ClientMemberDto>>
}
