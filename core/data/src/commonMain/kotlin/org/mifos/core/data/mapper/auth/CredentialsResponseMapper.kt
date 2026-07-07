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

import org.mifos.core.model.auth.Role
import org.mifos.core.model.auth.User
import org.mifos.core.network.commonDto.RoleResponseDto
import org.mifos.core.network.commonDto.UserResponseDto

fun UserResponseDto.toModel(): User =
    User(
        userId = userId,
        isAuthenticated = isAuthenticated,
        username = username,
        officeId = officeId,
        officeName = officeName,
        roles = ArrayList(roles.map { it.toModel() }),
        base64EncodedAuthenticationKey = base64EncodedAuthenticationKey,
        permissions = ArrayList(permissions),
        shouldRenewPassword = shouldRenewPassword,
        isTwoFactorAuthenticationRequired = isTwoFactorAuthenticationRequired,
        clients = ArrayList(clients),
    )

fun RoleResponseDto.toModel(): Role =
    Role(
        id = id,
        name = name,
        description = description,
        disabled = disabled,
    )
