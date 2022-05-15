package com.github.akhpkn.pdp.domain.notification.service

import com.github.akhpkn.pdp.domain.notification.dao.NotificationDao
import com.github.akhpkn.pdp.domain.plan.model.Plan
import com.github.akhpkn.pdp.domain.task.model.Task
import com.github.akhpkn.pdp.domain.user.model.User
import com.github.akhpkn.pdp.email.NotificationAdapter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import mu.KLogging
import org.springframework.stereotype.Service
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset

@Service
class NotificationService(
    private val notificationAdapter: NotificationAdapter,
    private val notificationDao: NotificationDao
) {

    suspend fun executePlanDeadlineNotifications(pointInTime: Instant) = coroutineScope {
        loadUsersAndPlansWithDeadline(pointInTime).collect {
            launch(Dispatchers.IO) {
                notificationAdapter.notifyPlanDeadline(it.first, it.second)
            }
        }
    }

    suspend fun executeTaskDeadlineNotifications(pointInTime: Instant) = coroutineScope {
        loadUsersAndTasksWithDeadline(pointInTime).collect {
            launch(Dispatchers.IO) {
                notificationAdapter.notifyTaskDeadline(it.first, it.second)
            }
        }
    }

    suspend fun executeTaskReportReminderNotifications(pointInTime: Instant) = coroutineScope {
        loadUsersAndTasksForReminder(pointInTime).collect {
            launch(Dispatchers.IO) {
                notificationAdapter.notifyTaskReport(it.first, it.second)
            }
        }
    }

    private fun loadUsersAndPlansWithDeadline(pointInTime: Instant): Flow<Pair<User, Plan>> = run {
        notificationDao.listActualPlansWithEnabledNotifications(pointInTime)
            .filter {
                val endDate = it.plan.dueTo.toLocalDate()
                val givenDate = pointInTime.toLocalDate()

                givenDate.plusDays(it.notificationSettings.daysBeforeDeadline.toLong()) == endDate
            }
            .map { it.user to it.plan }
    }

    private fun loadUsersAndTasksWithDeadline(pointInTime: Instant): Flow<Pair<User, Task>> = run {
        notificationDao.listActualTasksWithEnabledNotifications(pointInTime)
            .filter {
                val endDate = it.task.dueTo.toLocalDate()
                val givenDate = pointInTime.toLocalDate()
                val result = givenDate.plusDays(it.notificationSettings.daysBeforeDeadline.toLong()) == endDate
                result
            }
            .map { it.user to it.task }
    }

    private fun loadUsersAndTasksForReminder(pointInTime: Instant): Flow<Pair<User, Task>> = run {
        notificationDao.listActualTasksWithEnabledNotifications(pointInTime)
            .filter { dto ->
                with(dto) {
                    isDatePresentInProgression(
                        start = task.createDt.toLocalDate(),
                        stepInDays = notificationSettings.daysBeforeReport,
                        date = pointInTime.toLocalDate()
                    )
                }
            }
            .map { it.user to it.task }
    }

    private fun isDatePresentInProgression(start: LocalDate, stepInDays: Int, date: LocalDate): Boolean =
        isPresentInProgression(
            start = start.toEpochDay(),
            step = stepInDays.toLong(),
            element = date.toEpochDay()
        )

    private fun isPresentInProgression(start: Long, step: Long, element: Long): Boolean = (element - start) % step == 0L

    private fun Instant.toLocalDate(): LocalDate = LocalDate.ofInstant(this, ZoneOffset.systemDefault())

    companion object : KLogging()
}
