package com.github.akhpkn.pdp.api.notification

import com.github.akhpkn.pdp.auth.withAuthorizedUserId
import com.github.akhpkn.pdp.domain.notification.service.NotificationSettingsService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/notification-settings")
class NotificationSettingsController(private val service: NotificationSettingsService) {

    @GetMapping
    suspend fun get() = withAuthorizedUserId { service.get(it) }

    @PutMapping("/enable")
    suspend fun enable() = withAuthorizedUserId { service.enable(it) }

    @PutMapping("/disable")
    suspend fun disable() = withAuthorizedUserId { service.disable(it) }

    @PutMapping("/deadline")
    suspend fun updateDaysBeforeDeadline(@RequestBody request: DaysWrapper) {
        withAuthorizedUserId {
            service.editDeadlineNotificationSettings(it, request.days)
        }
    }

    @PutMapping("/report")
    suspend fun updateDaysBeforeReport(@RequestBody request: DaysWrapper) {
        withAuthorizedUserId {
            service.editTaskReportReminderSettings(it, request.days)
        }
    }
}
