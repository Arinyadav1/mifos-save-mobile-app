/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See See https://github.com/openMF/kmp-project-template/blob/main/LICENSE
 */
package org.mifos.core.base.ui.motion

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import mifos.core.base.designsystem.theme.Motion

/**
 * Asserts the duration-symmetry invariant for shared-axis-X transitions.
 *
 * A blink during navigation is the visible symptom of an asymmetric duration
 * pair — for example enter fades over 450ms while exit fades over 200ms.
 * `SharedAxisSpec.isSymmetric()` returns false when any (enter, exit) slide
 * or fade pair diverges; these tests fail-fast on regression.
 */
class SharedAxisSpecTest {

    private val defaultMotion = Motion()

    @Test
    fun forwardEnterAndExitSlideDurationsMustBeEqual() {
        _root_ide_package_.kotlin.test.assertEquals(
            _root_ide_package_.mifos.core.base.ui.motion.SharedAxisSpec.enterForwardSlideMs(
                defaultMotion
            ),
            _root_ide_package_.mifos.core.base.ui.motion.SharedAxisSpec.exitForwardSlideMs(
                defaultMotion
            ),
            "forward enter/exit slide durations must be symmetric — asymmetry produces nav blink",
        )
    }

    @Test
    fun forwardEnterAndExitFadeDurationsMustBeEqual() {
        _root_ide_package_.kotlin.test.assertEquals(
            _root_ide_package_.mifos.core.base.ui.motion.SharedAxisSpec.enterForwardFadeMs(
                defaultMotion
            ),
            _root_ide_package_.mifos.core.base.ui.motion.SharedAxisSpec.exitForwardFadeMs(
                defaultMotion
            ),
            "forward enter/exit fade durations must be symmetric — asymmetry produces nav blink",
        )
    }

    @Test
    fun backEnterAndExitSlideDurationsMustBeEqual() {
        _root_ide_package_.kotlin.test.assertEquals(
            _root_ide_package_.mifos.core.base.ui.motion.SharedAxisSpec.enterBackSlideMs(
                defaultMotion
            ),
            _root_ide_package_.mifos.core.base.ui.motion.SharedAxisSpec.exitBackSlideMs(
                defaultMotion
            ),
            "back enter/exit slide durations must be symmetric — asymmetry produces nav blink",
        )
    }

    @Test
    fun backEnterAndExitFadeDurationsMustBeEqual() {
        _root_ide_package_.kotlin.test.assertEquals(
            _root_ide_package_.mifos.core.base.ui.motion.SharedAxisSpec.enterBackFadeMs(
                defaultMotion
            ),
            _root_ide_package_.mifos.core.base.ui.motion.SharedAxisSpec.exitBackFadeMs(defaultMotion),
            "back enter/exit fade durations must be symmetric — asymmetry produces nav blink",
        )
    }

    @Test
    fun isSymmetricReturnsTrueForDefaultMotion() {
        assertTrue(
            _root_ide_package_.mifos.core.base.ui.motion.SharedAxisSpec.isSymmetric(defaultMotion),
            "default Motion() must satisfy duration symmetry — " +
                "failure indicates a recurrence of the 2026-05-27 nav-blink bug",
        )
    }
}
