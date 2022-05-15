package com.github.akhpkn.pdp.db

import com.github.akhpkn.pdp.domain.notification.dao.NotificationSettingsDao
import com.github.akhpkn.pdp.domain.notification.model.NotificationSettings
import org.springframework.r2dbc.core.DatabaseClient
import org.springframework.r2dbc.core.await
import org.springframework.r2dbc.core.awaitSingleOrNull
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
class NotificationSettingsDaoImp(private val databaseClient: DatabaseClient) : NotificationSettingsDao {

    override suspend fun find(userId: UUID): NotificationSettings? = run {
        databaseClient
            .sql("select * from notification_settings where user_id=:userId")
            .bind("userId", userId)
            .map { row, _ ->
                NotificationSettings(
                    userId = row.get("user_id") as UUID,
                    enabled = row.get("enabled") as Boolean,
                    daysBeforeDeadline = row.get("days_before_deadline") as Int,
                    daysBeforeReport = row.get("days_before_report") as Int
                )
            }
            .awaitSingleOrNull()
    }

    override suspend fun insert(settings: NotificationSettings) {
        databaseClient
            .sql(
                """
                    insert into notification_settings(user_id, enabled, days_before_deadline, days_before_report) 
                    values(:userId, :enabled, :daysBeforeDeadline, :daysBeforeReport)
                """.trimIndent()
            )
            .bind("userId", settings.userId)
            .bind("enabled", settings.enabled)
            .bind("daysBeforeDeadline", settings.daysBeforeDeadline)
            .bind("daysBeforeReport", settings.daysBeforeReport)
            .await()
    }

    override suspend fun enable(userId: UUID) = updateEnabled(userId, true)

    override suspend fun disable(userId: UUID) = updateEnabled(userId, false)

    private suspend fun updateEnabled(userId: UUID, enabled: Boolean) {
        databaseClient
            .sql("update notification_settings set enabled=:enabled where user_id=:userId")
            .bind("userId", userId)
            .bind("enabled", enabled)
            .await()
    }

    override suspend fun updateDaysBeforeDeadline(userId: UUID, daysBeforeDeadline: Int) {
        databaseClient
            .sql("update notification_settings set days_before_deadline=:daysBeforeDeadline where user_id=:userId")
            .bind("userId", userId)
            .bind("daysBeforeDeadline", daysBeforeDeadline)
            .await()
    }

    override suspend fun updateDaysBeforeReport(userId: UUID, daysBeforeReport: Int) {
        databaseClient
            .sql(
                """
                    update notification_settings set days_before_report=:daysBeforeReport
                    where user_id=:userId
                """.trimIndent()
            )
            .bind("userId", userId)
            .bind("daysBeforeReport", daysBeforeReport)
            .await()
    }
}
