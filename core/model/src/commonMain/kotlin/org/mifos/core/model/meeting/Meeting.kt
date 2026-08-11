/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See See https://github.com/openMF/kmp-project-template/blob/main/LICENSE
 */
package org.mifos.core.model.meeting

import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime

data class Meeting(
    val id: Long,
    val groupId: Long,
    val meetingDate: LocalDate?,
    val startTime: String?,
    val endTime: String?,
    val title: String?,
    val meetingStatusCdStatus: Long,
    val statusName: String?,
    val location: String?,
    val meetingLink: String?,
    val description: String?,
    val createdAt: LocalDateTime?,
    val updatedAt: LocalDateTime?,
)
