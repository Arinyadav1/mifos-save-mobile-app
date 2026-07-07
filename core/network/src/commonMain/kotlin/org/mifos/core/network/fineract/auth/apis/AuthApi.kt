/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See See https://github.com/openMF/kmp-project-template/blob/main/LICENSE
 */
package org.mifos.core.network.fineract.auth.apis

import de.jensklingenberg.ktorfit.http.Body
import de.jensklingenberg.ktorfit.http.POST
import org.mifos.core.network.commonDto.CredentialsRequestDto
import org.mifos.core.network.commonDto.UserResponseDto
import org.mifos.core.network.utils.ApiEndPoints

interface AuthApi {
    @POST(ApiEndPoints.AUTHENTICATION)
    suspend fun authenticate(@Body credentialsRequestDto: CredentialsRequestDto): UserResponseDto
}
