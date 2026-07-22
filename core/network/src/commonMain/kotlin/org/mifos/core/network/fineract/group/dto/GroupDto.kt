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

import kotlinx.serialization.Serializable
import org.mifos.core.network.fineract.client.dto.ClientMemberDto

@Serializable
data class GroupDto(
    val id: Long = 0,
    val accountNo: String? = null,
    val name: String? = null,
    val status: GroupStatusDto? = null,
    val active: Boolean = false,
    val activationDate: List<Int>? = null,
    val officeId: Long = 0,
    val officeName: String? = null,
    val timeline: GroupTimelineDto? = null,
    val clientMembers: List<ClientMemberDto>? = null,
)
