/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See See https://github.com/openMF/kmp-project-template/blob/main/LICENSE
 */
package org.mifos.core.network.selfService.auth.apis

import de.jensklingenberg.ktorfit.http.Body
import de.jensklingenberg.ktorfit.http.POST
import io.ktor.client.statement.HttpResponse
import org.mifos.core.network.commonDto.CredentialsRequestDto
import org.mifos.core.network.selfService.auth.dto.ConfirmClientUserRequestDto
import org.mifos.core.network.selfService.auth.dto.PasswordResetRequestDto
import org.mifos.core.network.selfService.auth.dto.RegistrationRequestDto
import org.mifos.core.network.selfService.auth.dto.RenewPasswordRequestDto
import org.mifos.core.network.utils.ApiEndPoints

interface AuthApi {
    @POST(ApiEndPoints.AUTHENTICATION)
    suspend fun authenticate(@Body credentialsRequestDto: CredentialsRequestDto): HttpResponse

    @POST("${ApiEndPoints.REGISTRATION}/${ApiEndPoints.CLIENT_USER}")
    suspend fun register(@Body request: RegistrationRequestDto): HttpResponse

    @POST("${ApiEndPoints.REGISTRATION}/${ApiEndPoints.CLIENT_USER}/${ApiEndPoints.CONFIRM}")
    suspend fun confirmClientUser(@Body request: ConfirmClientUserRequestDto): HttpResponse

    @POST("${ApiEndPoints.PASSWORD}/${ApiEndPoints.REQUEST}")
    suspend fun requestPasswordReset(@Body request: PasswordResetRequestDto): HttpResponse

    @POST("${ApiEndPoints.PASSWORD}/${ApiEndPoints.RENEW}")
    suspend fun renewPassword(@Body request: RenewPasswordRequestDto): HttpResponse
}
