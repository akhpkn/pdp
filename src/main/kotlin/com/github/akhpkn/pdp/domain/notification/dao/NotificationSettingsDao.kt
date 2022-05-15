package com.github.akhpkn.pdp.domain.notification.dao

import com.github.akhpkn.pdp.domain.notification.model.NotificationSettings
import java.util.UUID

interface NotificationSettingsDao {

    suspend fun find(userId: UUID): NotificationSettings?

    suspend fun insert(settings: NotificationSettings)

    suspend fun enable(userId: UUID)

    suspend fun disable(userId: UUID)

    suspend fun updateDaysBeforeDeadline(userId: UUID, daysBeforeDeadline: Int)

    suspend fun updateDaysBeforeReport(userId: UUID, daysBeforeReport: Int)
}
