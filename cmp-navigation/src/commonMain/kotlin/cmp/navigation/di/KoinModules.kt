/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See See https://github.com/openMF/kmp-project-template/blob/main/LICENSE
 */
package cmp.navigation.di

import cmp.navigation.AppViewModel
import cmp.navigation.authenticatedAdminNavBar.AdminNavbarNavigationViewModel
import cmp.navigation.authenticatedMemberNavBar.MemberNavbarNavigationViewModel
import cmp.navigation.rootnav.RootNavViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module
import org.mifos.core.analytics.di.coreAnalyticsModule
import org.mifos.core.base.analytics.di.analyticsModule
import org.mifos.core.base.common.di.CommonModule
import org.mifos.core.base.platform.di.platformModule
import org.mifos.core.base.security.di.SecurityModule
import org.mifos.core.data.di.DataModule
import org.mifos.core.database.di.DatabaseModule
import org.mifos.core.datastore.di.DatastoreModule
import org.mifos.core.store.di.appStoreModule
import org.mifos.feature.auth.di.AuthModule
import org.mifos.feature.home.di.HomeModule

object KoinModules {
    private val dataModule = module {
        includes(DataModule, appStoreModule)
    }

    private val dispatcherModule = module {
        includes(CommonModule)
    }

    private val AppModule = module {
        includes(platformModule)

        viewModelOf(::AppViewModel)
        viewModelOf(::MemberNavbarNavigationViewModel)
        viewModelOf(::AdminNavbarNavigationViewModel)
        viewModelOf(::RootNavViewModel)
    }

    private val featureModule = module {
        includes(
            HomeModule,
            AuthModule,
        )
    }

    val allModules = listOf(
        SecurityModule,
        dataModule,
        DatabaseModule,
        dispatcherModule,
        analyticsModule,
        DatastoreModule,
        featureModule,
        AppModule,
        coreAnalyticsModule,
    )
}
