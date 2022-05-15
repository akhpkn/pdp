package com.github.akhpkn.pdp.domain.notification

import com.github.akhpkn.pdp.domain.notification.service.NotificationService
import kotlinx.coroutines.runBlocking
import mu.KLogging
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.time.Instant

@Component
class TaskReportNotificationJob(private val notificationService: NotificationService) {

    @Scheduled(cron = "0 0 12 * * ?")
    fun run() {
        logger.info { "$LOG_TAG: Start" }
        val now = Instant.now()
        runBlocking {
            notificationService.executeTaskReportReminderNotifications(now)
        }
        logger.info { "$LOG_TAG: End" }
    }

    companion object : KLogging() {
        private const val LOG_TAG = "TaskReportNotificationJob"
    }
}
