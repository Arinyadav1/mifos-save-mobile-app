package org.mifos.core.network.fineract.client.dto

import kotlinx.serialization.Serializable

@Serializable
data class ClientMemberDto(
    val id: Long = 0,
    val accountNo: String? = null,
    val externalId: String? = null,
    val status: ClientStatusDto? = null,
    val active: Boolean = false,
    val activationDate: List<Int>? = null,
    val firstname: String? = null,
    val lastname: String? = null,
    val displayName: String? = null,
    val mobileNo: String? = null,
    val emailAddress: String? = null,
    val isStaff: Boolean = false,
    val officeId: Long = 0,
    val officeName: String? = null,
    val timeline: ClientTimelineDto? = null,
)