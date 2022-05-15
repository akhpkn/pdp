package com.github.akhpkn.pdp.domain.notification.model

import java.util.UUID

data class NotificationSettings(
    val userId: UUID,
    val enabled: Boolean,
    val daysBeforeDeadline: Int,
    val daysBeforeReport: Int
)
