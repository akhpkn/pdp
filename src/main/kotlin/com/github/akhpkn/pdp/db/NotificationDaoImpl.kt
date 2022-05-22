package com.github.akhpkn.pdp.db

import com.github.akhpkn.pdp.domain.notification.dao.NotificationDao
import com.github.akhpkn.pdp.domain.notification.model.PlanNotificationDto
import com.github.akhpkn.pdp.domain.notification.model.TaskNotificationDto
import io.r2dbc.spi.Row
import io.r2dbc.spi.RowMetadata
import kotlinx.coroutines.flow.Flow
import org.springframework.r2dbc.core.DatabaseClient
import org.springframework.r2dbc.core.flow
import org.springframework.stereotype.Repository
import java.time.Instant
import java.util.UUID

@Repository
class NotificationDaoImpl(private val databaseClient: DatabaseClient) : NotificationDao {

    private val toPlanNotificationDto = { row: Row, rm: RowMetadata ->
        PlanNotificationDto(
            plan = MappingFunctions.toPlan(row, rm),
            user = MappingFunctions.toUser(row, rm),
            notificationSettings = MappingFunctions.toNotificationSettings(row, rm)
        )
    }

    private val toTaskNotificationDto = { row: Row, rm: RowMetadata ->
        TaskNotificationDto(
            task = MappingFunctions.toTask(row, rm).copy(id = row.get("task_id") as UUID),
            user = MappingFunctions.toUser(row, rm).copy(id = row.get("user_id") as UUID),
            notificationSettings = MappingFunctions.toNotificationSettings(row, rm)
        )
    }

    override fun listActualPlansWithEnabledNotifications(pointInTime: Instant): Flow<PlanNotificationDto> = run {
        databaseClient
            .sql(
                """
                    select u.*, p.*, n.* 
                    from notification_settings n 
                    join "user" u on u.id = n.user_id
                    join plan p on u.id = p.user_id
                    where p.create_dt<=:pointInTime and p.due_to>=:pointInTime and n.enabled=true
                """.trimIndent()
            )
            .bind("pointInTime", pointInTime)
            .map(toPlanNotificationDto)
            .flow()
    }

    override fun listActualTasksWithEnabledNotifications(pointInTime: Instant): Flow<TaskNotificationDto> = run {
        databaseClient
            .sql(
                """
                    select u.*, t.*, n.* , t.id as task_id
                    from notification_settings n
                    join "user" u on u.id = n.user_id
                    join plan p on u.id = p.user_id
                    join task t on p.id = t.plan_id
                    where t.create_dt<=:pointInTime and t.due_to>=:pointInTime and n.enabled=true
                """.trimIndent()
            )
            .bind("pointInTime", pointInTime)
            .map(toTaskNotificationDto)
            .flow()
    }

    override fun listTasksForReminders(pointInTime: Instant): Flow<Pair<TaskNotificationDto, Instant>> = run {
        databaseClient
            .sql(
                """
                    select u.*, t.*, n.*, ta.date_time
                    from pdp.public.notification_settings n
                    join "user" u on u.id = n.user_id
                    join plan p on u.id = p.user_id
                    join task t on p.id = t.plan_id
                    join task_audit ta on t.id = ta.task_id
                    where t.status='InProgress' 
                    and ta.date_time=(select max(date_time) from task_audit where task_id=t.id) 
                    and t.due_to>=:pointInTime 
                    and n.enabled=true
                """.trimIndent()
            )
            .bind("pointInTime", pointInTime)
            .map { row, rowMeta ->
                toTaskNotificationDto(row, rowMeta) to row.get("date_time", Instant::class.java)!!
            }
            .flow()
    }
}
