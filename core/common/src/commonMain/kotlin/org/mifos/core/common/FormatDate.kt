/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See See https://github.com/openMF/kmp-project-template/blob/main/LICENSE
 */
package org.mifos.core.common

import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.number
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock
import kotlin.time.Instant

fun formatDateFromLong(millis: Long): String {
    val dateTime = Instant
        .fromEpochMilliseconds(millis)
        .toLocalDateTime(TimeZone.currentSystemDefault())

    val day = dateTime.day.toString().padStart(2, '0')
    val month = dateTime.month.number.toString().padStart(2, '0')
    val year = dateTime.year
    return "$day/$month/$year"
}

fun getCurrentEpochMillis(): Long {
    return Clock.System.now().toEpochMilliseconds()
}

fun getGreeting(hour: Int): String = when (hour) {
    in 5..11 -> "Good morning"
    in 12..16 -> "Good afternoon"
    in 17..20 -> "Good evening"
    else -> "Good night"
}

fun formatHomeDate(dateTime: LocalDateTime): String {
    val dayOfWeek = dateTime.dayOfWeek.name.lowercase().replaceFirstChar { it.uppercase() }
    val dayOfMonth = dateTime.day
    val monthName = dateTime.month.name.lowercase().replaceFirstChar { it.uppercase() }
    val year = dateTime.year
    return "$dayOfWeek, $dayOfMonth $monthName $year"
}
