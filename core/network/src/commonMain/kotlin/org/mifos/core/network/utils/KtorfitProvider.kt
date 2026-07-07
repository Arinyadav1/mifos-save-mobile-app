/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See See https://github.com/openMF/kmp-project-template/blob/main/LICENSE
 */
package org.mifos.core.network.utils

import de.jensklingenberg.ktorfit.Ktorfit
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import org.mifos.core.base.network.httpClient
import org.mifos.core.base.network.setupDefaultHttpClient
import org.mifos.core.datastore.UserPreferencesRepository

fun ktorfitProvider(
    userPreferencesRepository: UserPreferencesRepository,
    baseUrl: String,
): Ktorfit {
    return Ktorfit.Builder()
        .httpClient(
            client = httpClient(
                setupDefaultHttpClient(
                    baseUrl = baseUrl,
                    defaultHeaders = mapOf(
                        "Fineract-Platform-TenantId" to ApiConfig.SELF_SERVICE.tenant,
                        HttpHeaders.ContentType to ContentType.Application.Json.toString(),
                        HttpHeaders.Accept to ContentType.Application.Json.toString(),
                        HttpHeaders.Authorization to "Basic ${userPreferencesRepository.authToken}",
                    ),
                ),
            ),
        )
        .build()
}
