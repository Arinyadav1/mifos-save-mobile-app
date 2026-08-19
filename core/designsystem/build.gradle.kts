/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/kmp-project-template/blob/main/LICENSE
 */
plugins {
    alias(libs.plugins.kmp.library.convention)
    alias(libs.plugins.jetbrainsCompose)
    alias(libs.plugins.compose.compiler)
}


kotlin {
    sourceSets {
        androidInstrumentedTest.dependencies {
            implementation(libs.androidx.compose.ui.test)
        }
        androidUnitTest.dependencies {
            implementation(libs.androidx.compose.ui.test)
        }
        commonMain.dependencies {
            api(projects.coreBase.designsystem)
            // Theme wires LocalScreenStateDefaults from core/store so every screen
            // wrapped by MifosTheme picks up the app's branded ScreenState defaults.
            implementation(projects.core.store)
            implementation(projects.core.model)
            implementation(projects.core.common)

            implementation(libs.jb.composeUi)
            implementation(libs.jb.composeUi.util)
            implementation(libs.jb.composeRuntime)
            implementation(libs.jb.foundation)
            implementation(libs.jb.material3)
            implementation(libs.jb.material.icons.extended)
            implementation(libs.jb.componentsResources)
            implementation(libs.jb.ui.tooling.preview)

            implementation(libs.coil.kt.compose)
        }
    }
}

compose.resources {
    publicResClass = true
    generateResClass = always
    packageOfResClass = "org.mifos.core.designsystem.generated.resources"
}