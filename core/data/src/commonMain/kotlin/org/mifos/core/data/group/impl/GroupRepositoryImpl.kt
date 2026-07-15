/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See See https://github.com/openMF/kmp-project-template/blob/main/LICENSE
 */
package org.mifos.core.data.group.impl

import kotlinx.coroutines.flow.first
import org.mifos.core.base.store.infra.StoreFactory
import org.mifos.core.base.store.paging.PageKey
import org.mifos.core.data.group.GroupRepository
import org.mifos.core.data.mapper.group.toModel
import org.mifos.core.model.group.Group
import org.mifos.core.network.DataManager
import org.mobilenativefoundation.store.store5.Fetcher
import org.mobilenativefoundation.store.store5.Store

class GroupRepositoryImpl(
    private val dataManager: DataManager,
) : GroupRepository {

    override fun listOfGroupPaging(): Store<PageKey, List<Group>> = StoreFactory.createMemoryStore(
        fetcher = Fetcher.of { key ->
            val pageResponse = dataManager.fineract.groupApi
                .getGroups(
                    paged = true,
                    offset = key.offset,
                    limit = key.pageSize,
                )
                .first()

            pageResponse.pageItems.map { it.toModel() }
        },
    )
}
