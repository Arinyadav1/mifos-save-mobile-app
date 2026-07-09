/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See See https://github.com/openMF/kmp-project-template/blob/main/LICENSE
 */
package org.mifos.core.model.error
import kotlinx.serialization.Serializable

@Serializable
data class MifosError(
    val developerMessage: String? = null,
    val httpStatusCode: String? = null,
    val defaultUserMessage: String? = null,
    val userMessageGlobalisationCode: String? = null,
    val errors: List<Errors> = emptyList(),
)
