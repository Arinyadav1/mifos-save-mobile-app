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

data class GroupTimeline(
    val submittedOnDate: LocalDate?,
    val submittedByUsername: String?,
    val submittedByFirstname: String?,
    val submittedByLastname: String?,
    val activatedOnDate: LocalDate?,
    val activatedByUsername: String?,
    val activatedByFirstname: String?,
    val activatedByLastname: String?,
    val closedOnDate: LocalDate?,
    val closedByUsername: String?,
    val closedByFirstname: String?,
    val closedByLastname: String?,
)
