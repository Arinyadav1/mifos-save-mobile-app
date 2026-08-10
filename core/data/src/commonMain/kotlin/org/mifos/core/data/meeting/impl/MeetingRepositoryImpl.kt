/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See See https://github.com/openMF/kmp-project-template/blob/main/LICENSE
 */
package org.mifos.core.data.meeting.impl

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import org.mifos.core.base.common.manager.DispatcherManager
import org.mifos.core.base.store.screen.ScreenState
import org.mifos.core.data.infra.NetworkMonitor
import org.mifos.core.data.mapper.meeting.toModel
import org.mifos.core.data.meeting.MeetingRepository
import org.mifos.core.data.util.asScreenStateFlow
import org.mifos.core.model.meeting.Meeting
import org.mifos.core.network.DataManager

class MeetingRepositoryImpl(
    private val dataManager: DataManager,
    private val networkMonitor: NetworkMonitor,
    private val dispatcher: DispatcherManager,
) : MeetingRepository {

    override fun getGroupMeetings(groupId: Long): Flow<ScreenState<List<Meeting>>> {
        return combine(
            dataManager.fineract.meetingApi.getGroupMeetings(groupId),
            dataManager.fineract.meetingApi.getMeetingStatuses(),
        ) { meetingsDto, statusesDto ->
            val statusMap = statusesDto.associate { it.id to it.name }
            meetingsDto.map { it.toModel(statusMap) }
        }.asScreenStateFlow(
            networkMonitor = networkMonitor,
            dispatcher = dispatcher.io,
        )
    }
}
