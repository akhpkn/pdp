package com.github.akhpkn.pdp.api.task

import com.github.akhpkn.pdp.api.task.protocol.StatusRequest
import com.github.akhpkn.pdp.api.task.protocol.TaskUpdateRequest
import com.github.akhpkn.pdp.auth.withAuthorizedUserId
import com.github.akhpkn.pdp.domain.feedback.service.FeedbackService
import com.github.akhpkn.pdp.domain.task.dto.TaskDto
import com.github.akhpkn.pdp.domain.task.model.TaskInputData
import com.github.akhpkn.pdp.domain.task.service.TaskService
import com.github.akhpkn.pdp.security.AccessService
import com.github.akhpkn.pdp.security.AccessType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
@RequestMapping("/api/v1/task")
class TaskController(
    private val service: TaskService,
    private val feedbackService: FeedbackService,
    private val accessService: AccessService
) {

    @GetMapping("/{id}")
    suspend fun getById(@PathVariable id: UUID): TaskDto = run {
        withAuthorizedUserId {
            val access = accessService.checkTaskAccess(userId = it, taskId = id, AccessType.Read)
            val task = service.getById(id)
            val maybeFeedbackRequest = feedbackService.findActiveFeedbackRequest(task.id, it)
            TaskDto.from(task, access, maybeFeedbackRequest)
        }
    }

    @GetMapping
    suspend fun getAllBy(@RequestParam("planId") planId: UUID): Flow<TaskDto> = run {
        withAuthorizedUserId { userId ->
            val access = accessService.checkPlanAccess(userId, planId, AccessType.Read)
            service.getByPlanId(planId).map {
                val maybeFeedbackRequest = feedbackService.findActiveFeedbackRequest(it.id, userId)
                TaskDto.from(it, access, maybeFeedbackRequest)
            }
        }
    }

    @PostMapping
    suspend fun create(@RequestBody request: TaskInputData) {
        withAuthorizedUserId {
            accessService.checkPlanAccess(userId = it, planId = request.planId, AccessType.Write)
            service.create(request)
        }
    }

    @PutMapping("/{id}/status")
    suspend fun updateStatus(@PathVariable id: UUID, @RequestBody request: StatusRequest) {
        withAuthorizedUserId {
            //todo: Owner or Write?
            accessService.checkTaskAccess(userId = it, taskId = id, AccessType.Owner)
            service.updateStatus(id, request.status)
        }
    }

    @PutMapping("/{id}")
    suspend fun update(@PathVariable id: UUID, @RequestBody request: TaskUpdateRequest) {
        withAuthorizedUserId {
            accessService.checkTaskAccess(userId = it, taskId = id, AccessType.Write)
            service.updateInfo(id, request.title, request.description, request.acceptanceCriteria)
        }
    }

    @DeleteMapping("/{id}")
    suspend fun delete(@PathVariable id: UUID) {
        withAuthorizedUserId {
            accessService.checkTaskAccess(userId = it, taskId = id, AccessType.Write)
            service.delete(id)
        }
    }
}
