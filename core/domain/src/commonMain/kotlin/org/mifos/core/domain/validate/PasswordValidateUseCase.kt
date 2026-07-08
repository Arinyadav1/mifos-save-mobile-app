/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See See https://github.com/openMF/kmp-project-template/blob/main/LICENSE
 */
package org.mifos.core.domain.validate

fun passwordValidate(password: String): String? {
    return when {
        password.isBlank() ->
            "Password cannot be empty"
        password.length < 6 ->
            "Password must be at least 6 characters long"
        else -> null
    }
}
