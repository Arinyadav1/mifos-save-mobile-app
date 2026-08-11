/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See See https://github.com/openMF/kmp-project-template/blob/main/LICENSE
 */
package org.mifos.core.network.fineract.meeting.apis

import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.Path
import kotlinx.coroutines.flow.Flow
import org.mifos.core.network.fineract.meeting.dto.MeetingDto
import org.mifos.core.network.fineract.meeting.dto.MeetingStatusDto

interface MeetingApi {
    @GET("datatables/group_meeting/{groupId}")
    fun getGroupMeetings(
        @Path("groupId") groupId: Long,
    ): Flow<List<MeetingDto>>

    @GET("codes/138/codevalues")
    fun getMeetingStatuses(): Flow<List<MeetingStatusDto>>
}
