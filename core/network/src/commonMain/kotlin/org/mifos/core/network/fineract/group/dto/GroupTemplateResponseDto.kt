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
data class GroupTemplateResponseDto(
    val officeOptions: List<OfficeOptionDto> = emptyList(),
    val staffOptions: List<StaffOptionDto> = emptyList(),
    val availableRoles: List<RoleOptionDto> = emptyList(),
)

@Serializable
data class OfficeOptionDto(
    val id: Long,
    val name: String,
    val nameDecorated: String? = null,
)

@Serializable
data class StaffOptionDto(
    val id: Long,
    val firstname: String? = null,
    val lastname: String? = null,
    val displayName: String? = null,
    val mobileNo: String? = null,
    val officeId: Long? = null,
    val officeName: String? = null,
    val isLoanOfficer: Boolean? = null,
    val isActive: Boolean? = null,
    val joiningDate: List<Int>? = null,
    val dateFormat: String? = null,
)

@Serializable
data class RoleOptionDto(
    val id: Long,
    val name: String,
    val position: Int? = null,
    val description: String? = null,
    val active: Boolean? = null,
    val mandatory: Boolean? = null,
)
