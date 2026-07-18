/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See See https://github.com/openMF/kmp-project-template/blob/main/LICENSE
 */
package org.mifos.core.model.group

import kotlinx.datetime.LocalDate

data class ClientMember(
    val id: Long,
    val accountNo: String?,
    val externalId: String?,
    val status: ClientStatus?,
    val active: Boolean,
    val activationDate: LocalDate?,
    val firstname: String?,
    val lastname: String?,
    val displayName: String?,
    val mobileNo: String?,
    val emailAddress: String?,
    val isStaff: Boolean,
    val officeId: Long,
    val officeName: String?,
    val timeline: ClientTimeline?,
)
