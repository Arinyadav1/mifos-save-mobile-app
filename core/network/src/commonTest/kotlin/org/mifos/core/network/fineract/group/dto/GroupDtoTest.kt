/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See See https://github.com/openMF/kmp-project-template/blob/main/LICENSE
 */
package org.mifos.core.network.fineract.group.dto

import kotlinx.serialization.json.Json
import org.mifos.core.network.commonDto.Page
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class GroupDtoTest {

    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
        explicitNulls = false
    }

    @Test
    fun parsesValidGroupPageResponseJson() {
        val payload = """
            {
              "totalFilteredRecords": 2,
              "pageItems": [
                {
                  "id": 1,
                  "accountNo": "000000001",
                  "name": "Group Alpha",
                  "status": {
                    "id": 300,
                    "code": "groupStatusType.active",
                    "value": "Active"
                  },
                  "active": true,
                  "activationDate": [2023, 1, 15],
                  "officeId": 1,
                  "officeName": "Head Office",
                  "timeline": {
                    "submittedOnDate": [2023, 1, 1],
                    "submittedByUsername": "mifos",
                    "submittedByFirstname": "System",
                    "submittedByLastname": "Admin",
                    "activatedOnDate": [2023, 1, 15],
                    "activatedByUsername": "mifos",
                    "activatedByFirstname": "System",
                    "activatedByLastname": "Admin"
                  }
                },
                {
                  "id": 2,
                  "accountNo": "000000002",
                  "name": "Group Beta",
                  "active": false,
                  "officeId": 1,
                  "officeName": "Head Office"
                }
              ]
            }
        """.trimIndent()

        val response = json.decodeFromString<Page<GroupDto>>(payload)

        assertEquals(2, response.totalFilteredRecords)
        assertEquals(2, response.pageItems.size)

        val group1 = response.pageItems[0]
        assertEquals(1L, group1.id)
        assertEquals("000000001", group1.accountNo)
        assertEquals("Group Alpha", group1.name)
        assertTrue(group1.active)
        assertEquals(1L, group1.officeId)
        assertEquals("Head Office", group1.officeName)

        val status = group1.status
        assertNotNull(status)
        assertEquals(300L, status.id)
        assertEquals("groupStatusType.active", status.code)
        assertEquals("Active", status.value)

        assertEquals(listOf(2023, 1, 15), group1.activationDate)

        val timeline = group1.timeline
        assertNotNull(timeline)
        assertEquals(listOf(2023, 1, 1), timeline.submittedOnDate)
        assertEquals("mifos", timeline.submittedByUsername)
        assertEquals("System", timeline.submittedByFirstname)
        assertEquals("Admin", timeline.submittedByLastname)
        assertEquals(listOf(2023, 1, 15), timeline.activatedOnDate)
        assertEquals("mifos", timeline.activatedByUsername)
        assertEquals("System", timeline.activatedByFirstname)
        assertEquals("Admin", timeline.activatedByLastname)

        val group2 = response.pageItems[1]
        assertEquals(2L, group2.id)
        assertEquals("000000002", group2.accountNo)
        assertEquals("Group Beta", group2.name)
        assertEquals(false, group2.active)
    }
}
