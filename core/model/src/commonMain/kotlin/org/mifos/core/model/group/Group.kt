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

data class Group(
    val id: Long,
    val accountNo: String?,
    val name: String?,
    val status: GroupStatus?,
    val active: Boolean,
    val activationDate: LocalDate?,
    val officeId: Long,
    val officeName: String?,
    val timeline: GroupTimeline?,
)
