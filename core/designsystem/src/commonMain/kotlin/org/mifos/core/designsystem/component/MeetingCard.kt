/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See See https://github.com/openMF/kmp-project-template/blob/main/LICENSE
 */
package org.mifos.core.designsystem.component

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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.mifos.core.base.designsystem.component.KptCard
import org.mifos.core.base.designsystem.theme.KptTheme
import org.mifos.core.common.FormatDate
import org.mifos.core.designsystem.icon.AppIcons
import org.mifos.core.designsystem.theme.KptTheme
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
                // Title and Status
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

                // Description
                meeting.description?.let {
                    Text(
                        text = it,
                        style = KptTheme.typography.bodyMedium,
                        color = KptTheme.colorScheme.onSurfaceVariant,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                    )
                }

                // Metadata Details (Date, Time, Location)
                Column(
                    verticalArrangement = Arrangement.spacedBy(KptTheme.spacing.xs),
                ) {
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
                            FormatDate.formatLocalDate(it)
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

                    // Location Row
                    if (!meeting.location.isNullOrEmpty()) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(KptTheme.spacing.xs),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Icon(
                                imageVector = AppIcons.Location,
                                contentDescription = null,
                                tint = KptTheme.colorScheme.primary,
                                modifier = Modifier.size(16.dp),
                            )
                            Text(
                                text = meeting.location.orEmpty(),
                                style = KptTheme.typography.bodySmall,
                                color = KptTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                    }
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

@Preview
@Composable
fun MeetingCardPreview() {
    val mockMeeting1 = Meeting(
        id = 1,
        groupId = 101,
        meetingDate = kotlinx.datetime.LocalDate(2026, 8, 20),
        startTime = "10:00 AM",
        endTime = "11:00 AM",
        title = "Weekly Standup Meeting",
        meetingStatusCdStatus = 1,
        statusName = "Scheduled",
        location = "Conference Room A",
        meetingLink = "https://meet.google.com/abc-defg-hij",
        description = "Discuss weekly goals, update status of pending tasks, and plan the roadmap.",
        createdAt = null,
        updatedAt = null,
    )

    val mockMeeting2 = Meeting(
        id = 2,
        groupId = 101,
        meetingDate = kotlinx.datetime.LocalDate(2026, 8, 19),
        startTime = "02:00 PM",
        endTime = "03:00 PM",
        title = "Sprint Retrospective",
        meetingStatusCdStatus = 2,
        statusName = "Completed",
        location = null,
        meetingLink = null,
        description = null,
        createdAt = null,
        updatedAt = null,
    )

    KptTheme {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            MeetingCard(
                meeting = mockMeeting1,
                onClick = {},
            )
            MeetingCard(
                meeting = mockMeeting2,
                onClick = {},
            )
        }
    }
}
