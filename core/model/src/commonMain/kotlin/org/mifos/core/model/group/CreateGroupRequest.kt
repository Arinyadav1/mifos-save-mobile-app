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

data class CreateGroupRequest(
    val officeId: Long,
    val name: String,
    val externalId: String,
    val clientMembers: List<Long>? = null,
    val dateFormat: String,
    val locale: String,
    val active: Boolean? = null,
    val activationDate: String? = null,
    val submittedOnDate: String? = null,
)
