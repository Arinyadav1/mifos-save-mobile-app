/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See See https://github.com/openMF/kmp-project-template/blob/main/LICENSE
 */
package org.mifos.core.data.loans.impl

import kotlinx.coroutines.flow.Flow
import org.mifos.core.base.common.manager.DispatcherManager
import org.mifos.core.base.store.screen.ScreenState
import org.mifos.core.data.infra.NetworkMonitor
import org.mifos.core.data.loans.LoansRepository
import org.mifos.core.data.mapper.loans.toModel
import org.mifos.core.data.util.asScreenStateFlow
import org.mifos.core.model.loans.LoanDetail
import org.mifos.core.network.DataManager

class LoansRepositoryImpl(
    private val dataManager: DataManager,
    private val networkMonitor: NetworkMonitor,
    private val dispatcher: DispatcherManager,
) : LoansRepository {

    override fun getLoanDetails(loanId: Long): Flow<ScreenState<LoanDetail>> {
        return dataManager.fineract.loansApi.getLoanDetails(loanId).asScreenStateFlow(
            networkMonitor = networkMonitor,
            dispatcher = dispatcher.io,
        ) { dto ->
            dto.toModel()
        }
    }
}
