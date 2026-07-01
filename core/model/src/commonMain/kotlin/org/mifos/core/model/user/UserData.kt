/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See See https://github.com/openMF/kmp-project-template/blob/main/LICENSE
 */
package org.mifos.core.model.user

import kotlinx.serialization.Serializable

@Serializable
data class UserData(
    val activeUserId: String,
    val themeBrand: org.mifos.core.model.user.ThemeBrand,
    val darkThemeConfig: org.mifos.core.model.user.DarkThemeConfig,
    val useDynamicColor: Boolean,
    val appLanguage: org.mifos.core.model.user.LanguageConfig,
    val showOnboarding: Boolean,
    val firstTimeUser: Boolean,
    val isAuthenticated: Boolean,
    val isUnlocked: Boolean,
    val passcode: String,
    val enableScreenCapture: Boolean,
    val isPasscodeEnabled: Boolean,
    val isBiometricsEnabled: Boolean,
) {
    companion object {
        val DEFAULT = UserData(
            activeUserId = "",
            passcode = "1234",
            themeBrand = _root_ide_package_.org.mifos.core.model.user.ThemeBrand.DEFAULT,
            darkThemeConfig = _root_ide_package_.org.mifos.core.model.user.DarkThemeConfig.FOLLOW_SYSTEM,
            useDynamicColor = false,
            appLanguage = _root_ide_package_.org.mifos.core.model.user.LanguageConfig.DEFAULT,
            isAuthenticated = true,
            isUnlocked = true,
            isPasscodeEnabled = false,
            isBiometricsEnabled = false,
            showOnboarding = false,
            firstTimeUser = false,
            enableScreenCapture = false,
        )
    }
}
