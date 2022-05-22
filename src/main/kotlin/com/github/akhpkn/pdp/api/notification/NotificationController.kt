package com.github.akhpkn.pdp.api.notification

import com.github.akhpkn.pdp.domain.notification.dao.NotificationDao
import com.github.akhpkn.pdp.domain.notification.service.NotificationService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.time.Instant

@RestController
@RequestMapping("/api/v1/notification")
class NotificationController(private val dao: NotificationDao, private val service: NotificationService) {

    @GetMapping("/plans")
    fun plans() = dao.listActualPlansWithEnabledNotifications(Instant.now())

    @GetMapping("/tasks")
    fun tasks() = dao.listActualTasksWithEnabledNotifications(Instant.now())

    @PostMapping("/plan-dead")
    suspend fun planDeadlines() {
        service.executePlanDeadlineNotifications(Instant.now())
    }

    @PostMapping("/task-dead")
    suspend fun taskDeadlines() {
        service.executeTaskDeadlineNotifications(Instant.now())
    }

    @PostMapping("/report")
    suspend fun reportReminders() {
        service.executeTaskReportNotifications(Instant.now())
    }
}
