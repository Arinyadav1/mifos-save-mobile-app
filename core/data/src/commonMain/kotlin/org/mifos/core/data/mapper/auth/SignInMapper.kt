/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See See https://github.com/openMF/kmp-project-template/blob/main/LICENSE
 */
package org.mifos.core.data.mapper.auth

import org.mifos.core.model.auth.SignInRequest
import org.mifos.core.network.commonDto.CredentialsRequestDto

fun SignInRequest.toDto(): CredentialsRequestDto =
    CredentialsRequestDto(
        username = username,
        password = password,
    )
