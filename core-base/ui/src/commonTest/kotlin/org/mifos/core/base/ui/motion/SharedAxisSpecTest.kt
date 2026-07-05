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
import kotlin.test.assertTrue
import org.mifos.core.base.designsystem.theme.Motion

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
        kotlin.test.assertEquals(
            SharedAxisSpec.enterForwardSlideMs(
                defaultMotion
            ),
            SharedAxisSpec.exitForwardSlideMs(
                defaultMotion
            ),
            "forward enter/exit slide durations must be symmetric — asymmetry produces nav blink",
        )
    }

    @Test
    fun forwardEnterAndExitFadeDurationsMustBeEqual() {
        kotlin.test.assertEquals(
            SharedAxisSpec.enterForwardFadeMs(
                defaultMotion
            ),
            SharedAxisSpec.exitForwardFadeMs(
                defaultMotion
            ),
            "forward enter/exit fade durations must be symmetric — asymmetry produces nav blink",
        )
    }

    @Test
    fun backEnterAndExitSlideDurationsMustBeEqual() {
        kotlin.test.assertEquals(
            SharedAxisSpec.enterBackSlideMs(
                defaultMotion
            ),
            SharedAxisSpec.exitBackSlideMs(
                defaultMotion
            ),
            "back enter/exit slide durations must be symmetric — asymmetry produces nav blink",
        )
    }

    @Test
    fun backEnterAndExitFadeDurationsMustBeEqual() {
        kotlin.test.assertEquals(
            SharedAxisSpec.enterBackFadeMs(
                defaultMotion
            ),
            SharedAxisSpec.exitBackFadeMs(defaultMotion),
            "back enter/exit fade durations must be symmetric — asymmetry produces nav blink",
        )
    }

    @Test
    fun isSymmetricReturnsTrueForDefaultMotion() {
        assertTrue(
            SharedAxisSpec.isSymmetric(defaultMotion),
            "default Motion() must satisfy duration symmetry — " +
                "failure indicates a recurrence of the 2026-05-27 nav-blink bug",
        )
    }
}
