/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See See https://github.com/openMF/kmp-project-template/blob/main/LICENSE
 */
package org.mifos.core.data.crypto

import kotlinx.coroutines.CoroutineScope
import org.mifos.core.base.store.paging.PagingScreenStream
import org.mifos.core.base.store.screen.ScreenDataStream
import org.mifos.core.model.crypto.CoinDetail
import org.mifos.core.model.crypto.CoinMarket

interface CryptoRepository {
    fun coinMarketsStream(scope: CoroutineScope, pageSize: Int = 20): PagingScreenStream<CoinMarket>

    fun coinDetailStream(coinId: String, scope: CoroutineScope): ScreenDataStream<CoinDetail>
}
