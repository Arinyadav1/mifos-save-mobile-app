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

data class CreateMeetingRequest(
    val meetingDate: LocalDate,
    val startTime: String,
    val endTime: String,
    val title: String,
    val location: String?,
    val meetingLink: String?,
    val description: String?,
)
