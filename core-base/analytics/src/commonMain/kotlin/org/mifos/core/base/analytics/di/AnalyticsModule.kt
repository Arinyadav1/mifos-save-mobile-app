/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/kmp-project-template/blob/main/LICENSE
 */
package org.mifos.core.base.analytics.di

import org.mifos.core.base.analytics.AnalyticsHelper
import org.mifos.core.base.analytics.provideAnalyticsHelper
import org.koin.dsl.module

val analyticsModule = module {
    single<AnalyticsHelper> { provideAnalyticsHelper() }
}
