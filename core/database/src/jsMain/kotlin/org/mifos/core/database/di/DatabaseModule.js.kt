/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See See https://github.com/openMF/kmp-project-template/blob/main/LICENSE
 */
package org.mifos.core.database.di

import kotlinx.coroutines.Dispatchers
import org.koin.core.module.Module
import org.koin.dsl.module
import org.mifos.core.base.database.AppDatabaseFactory
import org.mifos.core.database.AppDatabase

actual val platformModule: Module = module {
    single {
        AppDatabaseFactory()
            .createDatabase<
                AppDatabase,
                >(databaseName = "app_database")
            .setQueryCoroutineContext(Dispatchers.Default)
            .build()
    }
}
