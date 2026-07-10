/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See See https://github.com/openMF/kmp-project-template/blob/main/LICENSE
 */
package org.mifos.core.model.auth

data class RegistrationRequest(
    val username: String,
    val password: String,
    val mobileNumber: String,
    val email: String,
    val authenticationMode: String,
    val legalFormId: Int = 1,
    val active: Boolean = false,
    val submittedOnDate: String,
    val firstname: String,
    val middlename: String = "",
    val lastname: String,
    val externalId: String,
    val familyMembers: List<String> = emptyList(),
    val dateFormat: String = "dd/MM/yyyy",
    val locale: String = "en",
)
