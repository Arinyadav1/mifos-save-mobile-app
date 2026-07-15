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
import org.mifos.core.network.fineract.group.dto.GroupDto
import org.mifos.core.network.fineract.group.dto.GroupStatusDto
import org.mifos.core.network.fineract.group.dto.GroupTimelineDto
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class GroupMapperTest {

    @Test
    fun mapsGroupDtoToGroup() {
        val dto = GroupDto(
            id = 42L,
            accountNo = "G42",
            name = "Test Group",
            active = true,
            activationDate = listOf(2026, 7, 15),
            officeId = 3L,
            officeName = "Region Office",
            status = GroupStatusDto(
                id = 300L,
                code = "groupStatusType.active",
                value = "Active",
            ),
            timeline = GroupTimelineDto(
                submittedOnDate = listOf(2026, 7, 1),
                submittedByUsername = "admin",
                activatedOnDate = listOf(2026, 7, 15),
                activatedByUsername = "supervisor",
            ),
        )

        val domain = dto.toModel()

        assertEquals(42L, domain.id)
        assertEquals("G42", domain.accountNo)
        assertEquals("Test Group", domain.name)
        assertTrue(domain.active)
        assertEquals(LocalDate(2026, 7, 15), domain.activationDate)
        assertEquals(3L, domain.officeId)
        assertEquals("Region Office", domain.officeName)

        val status = domain.status
        assertNotNull(status)
        assertEquals(300L, status.id)
        assertEquals("groupStatusType.active", status.code)
        assertEquals("Active", status.value)

        val timeline = domain.timeline
        assertNotNull(timeline)
        assertEquals(LocalDate(2026, 7, 1), timeline.submittedOnDate)
        assertEquals("admin", timeline.submittedByUsername)
        assertEquals(LocalDate(2026, 7, 15), timeline.activatedOnDate)
        assertEquals("supervisor", timeline.activatedByUsername)
    }

    @Test
    fun mapsEmptyGroupDtoHandlesNullsGracefully() {
        val dto = GroupDto()
        val domain = dto.toModel()

        assertEquals(0L, domain.id)
        assertNull(domain.accountNo)
        assertNull(domain.name)
        assertNull(domain.status)
        assertEquals(false, domain.active)
        assertNull(domain.activationDate)
        assertEquals(0L, domain.officeId)
        assertNull(domain.officeName)
        assertNull(domain.timeline)
    }

    @Test
    fun mapsInvalidDatesToNull() {
        val dto = GroupDto(
            activationDate = listOf(2026),
        )
        val domain = dto.toModel()
        assertNull(domain.activationDate)

        val timelineDto = GroupTimelineDto(
            submittedOnDate = listOf(2026, 13, 40),
        )
        val timelineDomain = timelineDto.toModel()
        assertNull(timelineDomain.submittedOnDate)
    }
}
