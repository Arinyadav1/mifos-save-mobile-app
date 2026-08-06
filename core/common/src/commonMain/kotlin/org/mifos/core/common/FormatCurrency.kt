/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See See https://github.com/openMF/kmp-project-template/blob/main/LICENSE
 */
package org.mifos.core.common

fun formatCurrency(amount: Double, symbol: String): String {
    val longVal = (amount * 100).toLong()
    val absVal = kotlin.math.abs(longVal)
    val cents = (absVal % 100).toString().padStart(2, '0')
    val dollars = (absVal / 100).toString()

    val sb = StringBuilder()
    var count = 0
    for (i in dollars.length - 1 downTo 0) {
        sb.append(dollars[i])
        count++
        if (count == 3 && i > 0) {
            sb.append(',')
            count = 0
        }
    }
    val dollarsFormatted = sb.reverse().toString()
    val sign = if (amount < 0) "-" else ""
    return "$sign$symbol$dollarsFormatted.$cents"
}
