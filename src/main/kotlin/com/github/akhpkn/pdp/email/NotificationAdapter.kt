package com.github.akhpkn.pdp.email

import com.github.akhpkn.pdp.domain.plan.model.Plan
import com.github.akhpkn.pdp.domain.task.model.Task
import com.github.akhpkn.pdp.domain.user.model.User
import org.springframework.stereotype.Component
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneOffset

@Component
class NotificationAdapter(private val emailService: EmailService) {

    suspend fun notifyFeedbackRequested(requester: User, assignee: User, task: Task) {
        val emailData = EmailData(
            receiver = assignee.email,
            subject = "Запрошен фидбек",
            text = "${requester.name} ${requester.surname} запросил у Вас фидбек по задаче ${task.title}."
        )
        emailService.send(emailData)
    }

    suspend fun notifyFeedbackReceived(requester: User, assignee: User, task: Task) {
        val emailData = EmailData(
            receiver = requester.email,
            subject = "Оставлен фидбек",
            text = "${assignee.name} ${assignee.surname} оставил фидбек по задаче ${task.title}."
        )
        emailService.send(emailData)
    }

    suspend fun notifyPlanDeadline(user: User, plan: Plan) {
        val prettyDate = userFriendlyDateTime(plan.dueTo)
        val emailData = EmailData(
            receiver = user.email,
            subject = "Приближающийся срок индивидуального плана развития",
            text = "Приближается срок индивидуального плана развития \"${plan.title}\": $prettyDate."
        )
        emailService.send(emailData)
    }

    suspend fun notifyTaskDeadline(user: User, task: Task) {
        val prettyDate = userFriendlyDateTime(task.dueTo)
        val emailData = EmailData(
            receiver = user.email,
            subject = "Приближающийся срок задачи",
            text = "Приближается срок задачи ${task.title}: $prettyDate."
        )
        emailService.send(emailData)
    }

    suspend fun notifyTaskReport(user: User, task: Task) {
        val emailData = EmailData(
            receiver = user.email,
            subject = "Отчетность по задачам",
            text = "Оставьте отчет по задаче ${task.title}"
        )
        emailService.send(emailData)
    }

    private fun userFriendlyDateTime(dateTime: Instant): String {
        val localDateTime = LocalDateTime.ofInstant(dateTime, ZoneOffset.UTC)
        val date = localDateTime.toLocalDate()
        return "$date"
    }
}
