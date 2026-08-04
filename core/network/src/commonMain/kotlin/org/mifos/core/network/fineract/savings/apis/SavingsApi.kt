/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See See https://github.com/openMF/kmp-project-template/blob/main/LICENSE
 */
package org.mifos.core.network.fineract.savings.apis

import de.jensklingenberg.ktorfit.http.Body
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.POST
import de.jensklingenberg.ktorfit.http.Path
import de.jensklingenberg.ktorfit.http.Query
import io.ktor.client.statement.HttpResponse
import kotlinx.coroutines.flow.Flow
import org.mifos.core.network.fineract.savings.dto.ApproveSavingRequestDto
import org.mifos.core.network.fineract.savings.dto.SavingDetailDto
import org.mifos.core.network.utils.ApiEndPoints

interface SavingsApi {
    @GET("${ApiEndPoints.SAVINGS_ACCOUNTS}/{accountId}")
    fun getSavingDetails(
        @Path("accountId") accountId: Long,
    ): Flow<SavingDetailDto>

    @POST("${ApiEndPoints.SAVINGS_ACCOUNTS}/{savingsId}")
    suspend fun approveSaving(
        @Path("savingsId") savingsId: Long,
        @Query("command") command: String = "approve",
        @Body request: ApproveSavingRequestDto,
    ): HttpResponse
}
