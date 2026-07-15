/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See See https://github.com/openMF/kmp-project-template/blob/main/LICENSE
 */
package org.mifos.core.data.mapper.group

import kotlinx.datetime.LocalDate
import org.mifos.core.model.group.Group
import org.mifos.core.model.group.GroupStatus
import org.mifos.core.model.group.GroupTimeline
import org.mifos.core.network.fineract.group.dto.GroupDto
import org.mifos.core.network.fineract.group.dto.GroupStatusDto
import org.mifos.core.network.fineract.group.dto.GroupTimelineDto

fun GroupDto.toModel(): Group =
    Group(
        id = id,
        accountNo = accountNo,
        name = name,
        status = status?.toModel(),
        active = active,
        activationDate = activationDate.toLocalDateOrNull(),
        officeId = officeId,
        officeName = officeName,
        timeline = timeline?.toModel(),
    )

fun GroupStatusDto.toModel(): GroupStatus =
    GroupStatus(
        id = id,
        code = code,
        value = value,
    )

fun GroupTimelineDto.toModel(): GroupTimeline =
    GroupTimeline(
        submittedOnDate = submittedOnDate.toLocalDateOrNull(),
        submittedByUsername = submittedByUsername,
        submittedByFirstname = submittedByFirstname,
        submittedByLastname = submittedByLastname,
        activatedOnDate = activatedOnDate.toLocalDateOrNull(),
        activatedByUsername = activatedByUsername,
        activatedByFirstname = activatedByFirstname,
        activatedByLastname = activatedByLastname,
        closedOnDate = closedOnDate.toLocalDateOrNull(),
        closedByUsername = closedByUsername,
        closedByFirstname = closedByFirstname,
        closedByLastname = closedByLastname,
    )

private fun List<Int>?.toLocalDateOrNull(): LocalDate? {
    if (this == null || this.size < 3) return null
    return try {
        LocalDate(this[0], this[1], this[2])
    } catch (e: Exception) {
        null
    }
}
