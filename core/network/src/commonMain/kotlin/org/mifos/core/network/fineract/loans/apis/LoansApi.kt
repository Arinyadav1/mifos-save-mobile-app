/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See See https://github.com/openMF/kmp-project-template/blob/main/LICENSE
 */
package org.mifos.core.network.fineract.loans.apis

import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.Path
import de.jensklingenberg.ktorfit.http.Query
import kotlinx.coroutines.flow.Flow
import org.mifos.core.network.fineract.loans.dto.LoanDetailDto
import org.mifos.core.network.utils.ApiEndPoints

interface LoansApi {
    @GET("${ApiEndPoints.LOANS}/{loanId}")
    fun getLoanDetails(
        @Path("loanId") loanId: Long,
        @Query("associations") associations: String = "all",
        @Query("exclude") exclude: String = "guarantors,futureSchedule",
    ): Flow<LoanDetailDto>
}
