package com.github.akhpkn.pdp.domain.notification.service

import com.github.akhpkn.pdp.domain.notification.dao.NotificationSettingsDao
import com.github.akhpkn.pdp.domain.notification.model.NotificationSettings
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class NotificationSettingsService(private val dao: NotificationSettingsDao) {

    suspend fun createDefaultSettings(userId: UUID) {
        dao.insert(
            NotificationSettings(
                userId = userId,
                enabled = false,
                daysBeforeDeadline = 2,
                daysBeforeReport = 14
            )
        )
    }

    suspend fun get(userId: UUID): NotificationSettings = dao.find(userId) ?: throw RuntimeException("Settings not found")

    suspend fun enable(userId: UUID) = dao.enable(userId)

    suspend fun disable(userId: UUID) = dao.disable(userId)

    suspend fun editDeadlineNotificationSettings(userId: UUID, daysBeforeDeadline: Int) =
        dao.updateDaysBeforeDeadline(userId, daysBeforeDeadline)

    suspend fun editTaskReportReminderSettings(userId: UUID, days: Int) =
        dao.updateDaysBeforeReport(userId, days)
}
