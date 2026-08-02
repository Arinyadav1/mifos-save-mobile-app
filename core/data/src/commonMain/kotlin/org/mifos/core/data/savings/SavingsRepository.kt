/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See See https://github.com/openMF/kmp-project-template/blob/main/LICENSE
 */
package org.mifos.core.data.savings

import kotlinx.coroutines.flow.Flow
import org.mifos.core.base.store.screen.ScreenState
import org.mifos.core.model.savings.SavingDetail

interface SavingsRepository {
    fun getSavingDetails(accountId: Long): Flow<ScreenState<SavingDetail>>
}
