package com.github.akhpkn.pdp.domain.notification.dao

import com.github.akhpkn.pdp.domain.notification.model.PlanNotificationDto
import com.github.akhpkn.pdp.domain.notification.model.TaskNotificationDto
import kotlinx.coroutines.flow.Flow
import java.time.Instant

interface NotificationDao {

    fun listActualPlansWithEnabledNotifications(pointInTime: Instant): Flow<PlanNotificationDto>

    fun listActualTasksWithEnabledNotifications(pointInTime: Instant): Flow<TaskNotificationDto>

    fun listTasksForReminders(pointInTime: Instant): Flow<Pair<TaskNotificationDto, Instant>>
}
