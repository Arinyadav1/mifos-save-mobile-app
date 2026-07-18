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

@Serializable
data class ClientMemberDto(
    val id: Long = 0,
    val accountNo: String? = null,
    val externalId: String? = null,
    val status: ClientStatusDto? = null,
    val active: Boolean = false,
    val activationDate: List<Int>? = null,
    val firstname: String? = null,
    val lastname: String? = null,
    val displayName: String? = null,
    val mobileNo: String? = null,
    val emailAddress: String? = null,
    val isStaff: Boolean = false,
    val officeId: Long = 0,
    val officeName: String? = null,
    val timeline: ClientTimelineDto? = null,
)
