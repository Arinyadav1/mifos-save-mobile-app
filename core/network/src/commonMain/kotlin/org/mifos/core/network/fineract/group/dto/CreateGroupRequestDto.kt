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
data class CreateGroupRequestDto(
    val officeId: String,
    val name: String,
    val externalId: String,
    val clientMembers: List<String>? = null,
    val dateFormat: String,
    val locale: String,
    val active: Boolean? = null,
    val activationDate: String? = null,
    val submittedOnDate: String? = null,
)
