/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See See https://github.com/openMF/kmp-project-template/blob/main/LICENSE
 */
package org.mifos.feature.saving.di

import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module
import org.mifos.feature.saving.activateSaving.ActivateSavingViewModel
import org.mifos.feature.saving.approveSaving.ApproveSavingViewModel
import org.mifos.feature.saving.createSaving.CreateSavingViewModel
import org.mifos.feature.saving.depositTransaction.DepositTransactionViewModel
import org.mifos.feature.saving.savingDetails.SavingDetailsViewModel
import org.mifos.feature.saving.savingTransaction.SavingTransactionViewModel

val SavingModule = module {
    viewModelOf(::SavingDetailsViewModel)
    viewModelOf(::ApproveSavingViewModel)
    viewModelOf(::SavingTransactionViewModel)
    viewModelOf(::ActivateSavingViewModel)
    viewModelOf(::CreateSavingViewModel)
}
