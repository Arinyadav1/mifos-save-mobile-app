/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See See https://github.com/openMF/kmp-project-template/blob/main/LICENSE
 */
package org.mifos.feature.meeting.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import org.mifos.core.base.designsystem.component.KptCard
import org.mifos.core.base.designsystem.theme.KptTheme
import org.mifos.core.designsystem.component.StatusChip
import org.mifos.core.designsystem.component.StatusChipIntent
import org.mifos.core.designsystem.icon.AppIcons
import org.mifos.core.model.meeting.Meeting

@Composable
fun MeetingCard(
    meeting: Meeting,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    KptCard(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = KptTheme.shapes.medium,
        colors = CardDefaults.cardColors(
            containerColor = KptTheme.colorScheme.inverseOnSurface,
        ),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(KptTheme.spacing.md),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(KptTheme.spacing.sm),
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = meeting.title.orEmpty(),
                        style = KptTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.SemiBold,
                        ),
                        color = KptTheme.colorScheme.onSurface,
                        modifier = Modifier.weight(1f),
                    )
                    meeting.statusName?.let { status ->
                        StatusChip(
                            text = status,
                            intent = when (status.uppercase()) {
                                "SCHEDULED" -> StatusChipIntent.Info
                                "COMPLETED" -> StatusChipIntent.Success
                                "CANCELLED" -> StatusChipIntent.Danger
                                else -> StatusChipIntent.Neutral
                            },
                        )
                    }
                }

                // Date & Time Row
                Row(
                    horizontalArrangement = Arrangement.spacedBy(KptTheme.spacing.xs),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(
                        imageVector = AppIcons.Meetings,
                        contentDescription = null,
                        tint = KptTheme.colorScheme.primary,
                        modifier = Modifier.size(16.dp),
                    )
                    val dayOfWeek = meeting.meetingDate?.dayOfWeek?.name?.lowercase()
                        ?.replaceFirstChar { it.uppercase() }
                    val formattedDate = meeting.meetingDate?.let {
                        org.mifos.core.common.FormatDate.formatLocalDate(it)
                    }
                    val dateText = if (dayOfWeek != null && formattedDate != null) {
                        "$dayOfWeek, $formattedDate"
                    } else {
                        "—"
                    }
                    val timeText = if (!meeting.startTime.isNullOrEmpty() &&
                        !meeting.endTime.isNullOrEmpty()
                    ) {
                        " (${meeting.startTime} - ${meeting.endTime})"
                    } else if (!meeting.startTime.isNullOrEmpty()) {
                        " (${meeting.startTime})"
                    } else {
                        ""
                    }
                    Text(
                        text = "$dateText$timeText",
                        style = KptTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                        color = KptTheme.colorScheme.onSurface,
                    )
                }
            }

            Icon(
                imageVector = AppIcons.ChevronRight,
                contentDescription = null,
                tint = KptTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(start = KptTheme.spacing.sm),
            )
        }
    }
}
