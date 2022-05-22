package com.github.akhpkn.pdp.domain.notification.service

import com.github.akhpkn.pdp.domain.plan.model.Plan
import com.github.akhpkn.pdp.domain.task.model.Task
import com.github.akhpkn.pdp.domain.user.model.User

interface NotificationSender {

    suspend fun notifyFeedbackRequested(requester: User, assignee: User, task: Task)

    suspend fun notifyFeedbackReceived(requester: User, assignee: User, task: Task)

    suspend fun notifyPlanDeadline(user: User, plan: Plan)

    suspend fun notifyTaskDeadline(user: User, task: Task)

    suspend fun notifyTaskReport(user: User, task: Task)
}
