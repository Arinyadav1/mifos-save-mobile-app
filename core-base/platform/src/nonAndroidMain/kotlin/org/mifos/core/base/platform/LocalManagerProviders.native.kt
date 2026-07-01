/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See See https://github.com/openMF/kmp-project-template/blob/main/LICENSE
 */
package org.mifos.core.base.platform

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import org.mifos.core.base.platform.LocalAppReviewManager
import org.mifos.core.base.platform.LocalAppUpdateManager
import org.mifos.core.base.platform.LocalIntentManager
import org.mifos.core.base.platform.context.AppContext
import org.mifos.core.base.platform.intent.IntentManagerImpl
import org.mifos.core.base.platform.review.AppReviewManagerImpl
import org.mifos.core.base.platform.update.AppUpdateManagerImpl

@Composable
actual fun LocalManagerProvider(
    context: AppContext,
    content: @Composable () -> Unit,
) {
    CompositionLocalProvider(
        LocalAppReviewManager provides AppReviewManagerImpl(),
        LocalIntentManager provides IntentManagerImpl(),
        LocalAppUpdateManager provides AppUpdateManagerImpl(),
    ) {
        content()
    }
}
