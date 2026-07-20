/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See See https://github.com/openMF/kmp-project-template/blob/main/LICENSE
 */
package org.mifos.feature.groups.di

import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module
import org.mifos.feature.groups.addMember.AddMemberViewModel
import org.mifos.feature.groups.groupDashboard.GroupDashboardViewModel
import org.mifos.feature.groups.groupDetails.GroupDetailsViewModel
import org.mifos.feature.groups.groupMembersList.GroupMembersListViewModel
import org.mifos.feature.groups.groupSavingList.GroupSavingListViewModel

val GroupsModule = module {
    viewModelOf(::GroupDashboardViewModel)
    viewModelOf(::GroupDetailsViewModel)
    viewModelOf(::GroupMembersListViewModel)
    viewModelOf(::GroupSavingListViewModel)
    viewModelOf(::AddMemberViewModel)
}
