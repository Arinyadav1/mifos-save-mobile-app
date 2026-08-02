/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See See https://github.com/openMF/kmp-project-template/blob/main/LICENSE
 */
package org.mifos.core.data.savings.impl

import kotlinx.coroutines.flow.Flow
import org.mifos.core.base.common.manager.DispatcherManager
import org.mifos.core.base.store.screen.ScreenState
import org.mifos.core.data.infra.NetworkMonitor
import org.mifos.core.data.mapper.savings.toModel
import org.mifos.core.data.savings.SavingsRepository
import org.mifos.core.data.util.asScreenStateFlow
import org.mifos.core.model.savings.SavingDetail
import org.mifos.core.network.DataManager

class SavingsRepositoryImpl(
    private val dataManager: DataManager,
    private val networkMonitor: NetworkMonitor,
    private val dispatcher: DispatcherManager,
) : SavingsRepository {

    override fun getSavingDetails(accountId: Long): Flow<ScreenState<SavingDetail>> {
        return dataManager.fineract.savingsApi.getSavingDetails(accountId).asScreenStateFlow(
            networkMonitor = networkMonitor,
            dispatcher = dispatcher.io,
        ) { dto ->
            dto.toModel()
        }
    }
}
