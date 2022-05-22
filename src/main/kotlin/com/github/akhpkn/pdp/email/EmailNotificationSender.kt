package com.github.akhpkn.pdp.email

import com.github.akhpkn.pdp.domain.notification.service.NotificationSender
import com.github.akhpkn.pdp.domain.plan.model.Plan
import com.github.akhpkn.pdp.domain.task.model.Task
import com.github.akhpkn.pdp.domain.user.model.User
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneOffset

@Component
class EmailNotificationSender(
    private val emailService: EmailService,
    @Value("\${client-app.base-link}")
    private val baseLink: String
): NotificationSender {

    override suspend fun notifyFeedbackRequested(requester: User, assignee: User, task: Task) {
        val text = """
            Пользователь ${requester.name} ${requester.surname} запросил у Вас обратную связь по задаче ${task.title}.
            Чтобы ее оставить, перейдите по ссылке:
            ${taskLink(task)}
        """.trimIndent()
        val emailData = EmailData(
            receiver = assignee.email,
            subject = "Запрошена обратная связь",
            text = text
        )
        emailService.send(emailData)
    }

    override suspend fun notifyFeedbackReceived(requester: User, assignee: User, task: Task) {
        val text = """
            Пользователь ${assignee.name} ${assignee.surname} оставил обратную связь по задаче ${task.title}.
            Чтобы посмотреть, перейдите по ссылке:
            ${taskLink(task)}
        """.trimIndent()
        val emailData = EmailData(
            receiver = requester.email,
            subject = "Оставлена обратная связь",
            text = text
        )
        emailService.send(emailData)
    }

    override suspend fun notifyPlanDeadline(user: User, plan: Plan) {
        val prettyDate = userFriendlyDateTime(plan.dueTo)
        val emailData = EmailData(
            receiver = user.email,
            subject = "Приближающийся срок индивидуального плана развития",
            text = "Приближается срок индивидуального плана развития \"${plan.title}\": $prettyDate."
        )
        emailService.send(emailData)
    }

    override suspend fun notifyTaskDeadline(user: User, task: Task) {
        val prettyDate = userFriendlyDateTime(task.dueTo)
        val text = """
            Приближается срок задачи ${task.title}: $prettyDate.
            Пожалуйста, поделитесь результатами работы в комментариях к задаче и соберите обратную связь.
            Страница задачи: ${taskLink(task)}
        """.trimIndent()
        val emailData = EmailData(
            receiver = user.email,
            subject = "Приближающийся срок задачи",
            text = text
        )
        emailService.send(emailData)
    }

    override suspend fun notifyTaskReport(user: User, task: Task) {
        val text = """
            Пожалуйста, расскажите о промежуточных результатах работы над задачей ${task.title}.
            Для этого оставьте комментарий на странице задачи: ${taskLink(task)}
        """.trimIndent()
        val emailData = EmailData(
            receiver = user.email,
            subject = "Отчетность по задачам",
            text = text
        )
        emailService.send(emailData)
    }

    private fun userFriendlyDateTime(dateTime: Instant): String {
        val localDateTime = LocalDateTime.ofInstant(dateTime, ZoneOffset.UTC)
        val date = localDateTime.toLocalDate()
        return "$date"
    }

    private fun taskLink(task: Task) = "$baseLink/task/${task.id}"
}
