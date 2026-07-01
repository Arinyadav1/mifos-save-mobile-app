/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See See https://github.com/openMF/kmp-project-template/blob/main/LICENSE
 */
package cmp.android.app

import org.mifos.core.model.user.DarkThemeConfig

fun org.mifos.core.model.user.DarkThemeConfig.isDarkMode(isSystemDarkMode: Boolean): Boolean = when (this) {
    _root_ide_package_.org.mifos.core.model.user.DarkThemeConfig.FOLLOW_SYSTEM -> isSystemDarkMode
    _root_ide_package_.org.mifos.core.model.user.DarkThemeConfig.DARK -> true
    _root_ide_package_.org.mifos.core.model.user.DarkThemeConfig.LIGHT -> false
}
