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

enum class ApiConfig(
    val path: String,
) {
    FINERACT(path = "/1.0/field/v1/"),
    SELF_SERVICE(path = "/1.0/field/v1/self/"),
    ;

    companion object {
        const val ENDPOINT = "apis.mifos.community"
        const val TENANT = "mifos-bank-1"
        private const val PROTOCOL = "https://"
    }

    val tenant: String
        get() = TENANT

    val defaultBaseUrl: String
        get() = "$PROTOCOL$ENDPOINT"

    val baseUrl: String
        get() = "$defaultBaseUrl$path"

    fun getUrl(endpoint: String): String {
        return "$endpoint$path"
    }
}
