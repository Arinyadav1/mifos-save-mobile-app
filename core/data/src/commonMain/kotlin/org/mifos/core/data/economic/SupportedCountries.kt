/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See See https://github.com/openMF/kmp-project-template/blob/main/LICENSE
 */
package org.mifos.core.data.economic

import org.mifos.core.model.economic.Country

/**
 * Curated list of countries the Banking Utility Toolkit's macro-snapshot
 * screen ships with out of the box.
 *
 * Selection criteria:
 * - The G20 plus a handful of other large economies the template's adopters
 *   are most likely to demo against. This gives the screen a useful default
 *   experience without bundling the full ~250-country World Bank catalogue.
 * - Codes are ISO 3166-1 alpha-2, the form the toolkit standardises on (see
 *   [org.mifos.core.model.economic.MacroIndicator]). The World Bank API
 *   accepts alpha-2 directly.
 *
 * Future enhancement: dynamic loading via the World Bank `/v2/country`
 * endpoint is out of scope for this template — a hardcoded curated list is
 * sufficient and keeps the picker snappy with no extra request.
 */
object SupportedCountries {

    /** Curated G20-plus list — alphabetised by display name for stable UX. */
    val list: List<org.mifos.core.model.economic.Country> = listOf(
        _root_ide_package_.org.mifos.core.model.economic.Country(
            code = "AR",
            name = "Argentina",
            flagEmoji = "🇦🇷",
        ),
        _root_ide_package_.org.mifos.core.model.economic.Country(
            code = "AU",
            name = "Australia",
            flagEmoji = "🇦🇺",
        ),
        _root_ide_package_.org.mifos.core.model.economic.Country(
            code = "BR",
            name = "Brazil",
            flagEmoji = "🇧🇷",
        ),
        _root_ide_package_.org.mifos.core.model.economic.Country(
            code = "CA",
            name = "Canada",
            flagEmoji = "🇨🇦",
        ),
        _root_ide_package_.org.mifos.core.model.economic.Country(
            code = "CN",
            name = "China",
            flagEmoji = "🇨🇳",
        ),
        _root_ide_package_.org.mifos.core.model.economic.Country(
            code = "EG",
            name = "Egypt",
            flagEmoji = "🇪🇬",
        ),
        _root_ide_package_.org.mifos.core.model.economic.Country(
            code = "FR",
            name = "France",
            flagEmoji = "🇫🇷",
        ),
        _root_ide_package_.org.mifos.core.model.economic.Country(
            code = "DE",
            name = "Germany",
            flagEmoji = "🇩🇪",
        ),
        _root_ide_package_.org.mifos.core.model.economic.Country(
            code = "IN",
            name = "India",
            flagEmoji = "🇮🇳",
        ),
        _root_ide_package_.org.mifos.core.model.economic.Country(
            code = "ID",
            name = "Indonesia",
            flagEmoji = "🇮🇩",
        ),
        _root_ide_package_.org.mifos.core.model.economic.Country(
            code = "IT",
            name = "Italy",
            flagEmoji = "🇮🇹",
        ),
        _root_ide_package_.org.mifos.core.model.economic.Country(
            code = "JP",
            name = "Japan",
            flagEmoji = "🇯🇵",
        ),
        _root_ide_package_.org.mifos.core.model.economic.Country(
            code = "KE",
            name = "Kenya",
            flagEmoji = "🇰🇪",
        ),
        _root_ide_package_.org.mifos.core.model.economic.Country(
            code = "KR",
            name = "Korea, Rep.",
            flagEmoji = "🇰🇷",
        ),
        _root_ide_package_.org.mifos.core.model.economic.Country(
            code = "MX",
            name = "Mexico",
            flagEmoji = "🇲🇽",
        ),
        _root_ide_package_.org.mifos.core.model.economic.Country(
            code = "NG",
            name = "Nigeria",
            flagEmoji = "🇳🇬",
        ),
        _root_ide_package_.org.mifos.core.model.economic.Country(
            code = "PK",
            name = "Pakistan",
            flagEmoji = "🇵🇰",
        ),
        _root_ide_package_.org.mifos.core.model.economic.Country(
            code = "PH",
            name = "Philippines",
            flagEmoji = "🇵🇭",
        ),
        _root_ide_package_.org.mifos.core.model.economic.Country(
            code = "PL",
            name = "Poland",
            flagEmoji = "🇵🇱",
        ),
        _root_ide_package_.org.mifos.core.model.economic.Country(
            code = "RU",
            name = "Russian Federation",
            flagEmoji = "🇷🇺",
        ),
        _root_ide_package_.org.mifos.core.model.economic.Country(
            code = "SA",
            name = "Saudi Arabia",
            flagEmoji = "🇸🇦",
        ),
        _root_ide_package_.org.mifos.core.model.economic.Country(
            code = "SG",
            name = "Singapore",
            flagEmoji = "🇸🇬",
        ),
        _root_ide_package_.org.mifos.core.model.economic.Country(
            code = "ZA",
            name = "South Africa",
            flagEmoji = "🇿🇦",
        ),
        _root_ide_package_.org.mifos.core.model.economic.Country(
            code = "ES",
            name = "Spain",
            flagEmoji = "🇪🇸",
        ),
        _root_ide_package_.org.mifos.core.model.economic.Country(
            code = "SE",
            name = "Sweden",
            flagEmoji = "🇸🇪",
        ),
        _root_ide_package_.org.mifos.core.model.economic.Country(
            code = "CH",
            name = "Switzerland",
            flagEmoji = "🇨🇭",
        ),
        _root_ide_package_.org.mifos.core.model.economic.Country(
            code = "TR",
            name = "Turkiye",
            flagEmoji = "🇹🇷",
        ),
        _root_ide_package_.org.mifos.core.model.economic.Country(
            code = "AE",
            name = "United Arab Emirates",
            flagEmoji = "🇦🇪",
        ),
        _root_ide_package_.org.mifos.core.model.economic.Country(
            code = "GB",
            name = "United Kingdom",
            flagEmoji = "🇬🇧",
        ),
        _root_ide_package_.org.mifos.core.model.economic.Country(
            code = "US",
            name = "United States",
            flagEmoji = "🇺🇸",
        ),
        _root_ide_package_.org.mifos.core.model.economic.Country(
            code = "VN",
            name = "Vietnam",
            flagEmoji = "🇻🇳",
        ),
    )

    /** Backing index keyed on uppercase ISO code for O(1) [findByCode]. */
    private val byCode: Map<String, org.mifos.core.model.economic.Country> = list.associateBy { it.code }

    /**
     * Lookup a country by ISO code. Case-insensitive — the picker, intent
     * deeplinks, or saved navigation args may all carry mixed case.
     */
    fun findByCode(code: String): org.mifos.core.model.economic.Country? = byCode[code.trim().uppercase()]

    /**
     * Filter the list by a free-text query. Matches a country's ISO code as a
     * prefix OR its localised name as a substring, both case-insensitively.
     *
     * An empty (or whitespace-only) query returns the full list — typical
     * picker behavior so the screen renders before the user types anything.
     */
    fun search(query: String): List<org.mifos.core.model.economic.Country> {
        val trimmed = query.trim()
        if (trimmed.isEmpty()) return list
        val upper = trimmed.uppercase()
        return list.filter { c ->
            c.code.startsWith(upper) || c.name.contains(trimmed, ignoreCase = true)
        }
    }
}
