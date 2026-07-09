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

import org.mifos.core.model.auth.RegistrationRequest
import org.mifos.core.model.auth.RegistrationResult
import org.mifos.core.network.commonDto.RegistrationRequestDto
import org.mifos.core.network.commonDto.RegistrationResponseDto

fun RegistrationRequest.toDto(): RegistrationRequestDto =
    RegistrationRequestDto(
        username = username,
        password = password,
        mobileNumber = mobileNumber,
        email = email,
        authenticationMode = authenticationMode,
        legalFormId = legalFormId,
        active = active,
        submittedOnDate = submittedOnDate,
        firstname = firstname,
        middlename = middlename,
        lastname = lastname,
        externalId = externalId,
        familyMembers = familyMembers,
        dateFormat = dateFormat,
        locale = locale,
    )

fun RegistrationResponseDto.toModel(): RegistrationResult =
    RegistrationResult(
        officeId = officeId,
        clientId = clientId,
        resourceId = resourceId,
    )
