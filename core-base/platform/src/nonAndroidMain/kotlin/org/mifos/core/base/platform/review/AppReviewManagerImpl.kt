/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See See https://github.com/openMF/kmp-project-template/blob/main/LICENSE
 */
package org.mifos.core.base.platform.review

import org.mifos.core.base.platform.review.AppReviewManager

/**
 * Default implementation of [org.mifos.core.base.platform.review.AppReviewManager].
 */
class AppReviewManagerImpl : org.mifos.core.base.platform.review.AppReviewManager {
    override fun promptForReview() {
    }

    override fun promptForCustomReview() {
        // TODO:: Implement custom review flow
    }
}
