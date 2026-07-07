/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See See https://github.com/openMF/kmp-project-template/blob/main/LICENSE
 */
package org.mifos.core.network

import org.mifos.core.network.fineract.FineractApiManager
import org.mifos.core.network.selfService.SelfServiceApiManager

class DataManager(
    private val selfServiceApiManager: SelfServiceApiManager,
    private val fineractApiManager: FineractApiManager,
) {
    val self by lazy { selfServiceApiManager }

    val fineract by lazy { fineractApiManager }
}
