/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See See https://github.com/openMF/kmp-project-template/blob/main/LICENSE
 */
package org.mifos.core.network.di

import org.koin.dsl.module
import org.mifos.core.datastore.UserPreferencesRepository
import org.mifos.core.network.fineract.FineractApiManager
import org.mifos.core.network.selfService.SelfServiceApiManager
import org.mifos.core.network.utils.ApiConfig
import org.mifos.core.network.utils.ktorfitProvider

val NetworkModule = module {

    single {
        FineractApiManager(
            ktorfit = ktorfitProvider(
                userPreferencesRepository = get<UserPreferencesRepository>(),
                baseUrl = ApiConfig.FINERACT.baseUrl,
            ),
        )
    }

    single {
        SelfServiceApiManager(
            ktorfit = ktorfitProvider(
                userPreferencesRepository = get<UserPreferencesRepository>(),
                baseUrl = ApiConfig.SELF_SERVICE.baseUrl,
            ),
        )
    }
}
