/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See See https://github.com/openMF/kmp-project-template/blob/main/LICENSE
 */
package org.mifos.core.network.fineract

import de.jensklingenberg.ktorfit.Ktorfit
import org.mifos.core.network.fineract.auth.apis.createAuthApi
import org.mifos.core.network.fineract.client.apis.createClientApi
import org.mifos.core.network.fineract.group.apis.createGroupApi
import org.mifos.core.network.fineract.loans.apis.createLoansApi
import org.mifos.core.network.fineract.meeting.apis.createMeetingApi
import org.mifos.core.network.fineract.savings.apis.createSavingsApi

class FineractApiManager(
    private val ktorfit: Ktorfit,
) {
    val authApi by lazy { ktorfit.createAuthApi() }
    val groupApi by lazy { ktorfit.createGroupApi() }
    val clientApi by lazy { ktorfit.createClientApi() }
    val savingsApi by lazy { ktorfit.createSavingsApi() }
    val loansApi by lazy { ktorfit.createLoansApi() }
    val meetingApi by lazy { ktorfit.createMeetingApi() }
}
