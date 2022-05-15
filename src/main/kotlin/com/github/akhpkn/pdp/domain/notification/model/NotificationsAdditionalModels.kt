package com.github.akhpkn.pdp.domain.notification.model

import com.github.akhpkn.pdp.domain.plan.model.Plan
import com.github.akhpkn.pdp.domain.task.model.Task
import com.github.akhpkn.pdp.domain.user.model.User

data class PlanNotificationDto(
    val plan: Plan,
    val user: User,
    val notificationSettings: NotificationSettings
)

data class TaskNotificationDto(
    val task: Task,
    val user: User,
    val notificationSettings: NotificationSettings
)
