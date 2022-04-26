package com.github.akhpkn.pdp.api.plan

import com.github.akhpkn.pdp.api.plan.protocol.PlanCreationRequest
import com.github.akhpkn.pdp.api.plan.protocol.PlanDto
import com.github.akhpkn.pdp.auth.withAuthorizedUserId
import com.github.akhpkn.pdp.security.AccessType
import com.github.akhpkn.pdp.domain.plan.model.Plan
import com.github.akhpkn.pdp.domain.plan.model.PlanData
import com.github.akhpkn.pdp.domain.plan.service.PlanService
import com.github.akhpkn.pdp.domain.task.model.TaskStatus
import com.github.akhpkn.pdp.domain.task.service.TaskService
import com.github.akhpkn.pdp.security.AccessService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
@RequestMapping("/api/v1/plan")
class PlanController(
    private val service: PlanService,
    private val taskService: TaskService,
    private val accessService: AccessService
) {

    @GetMapping("/{id}")
    suspend fun getById(@PathVariable id: UUID): Plan = run {
        withAuthorizedUserId {
            accessService.checkPlanAccess(userId = it, planId = id, AccessType.Read)
            service.getById(id)
        }
    }

    @GetMapping
    suspend fun getAllBy(@RequestParam("userId") userId: UUID): Flow<Plan> = run {
        withAuthorizedUserId {
            accessService.checkUserAccess(userId = it, targetUserId = userId)
            service.getByAuthor(userId)
        }
    }

    @GetMapping("/shared")
    suspend fun getSharedPlans(): Flow<PlanData> = run {
        withAuthorizedUserId {
            service.getSharedPlans(it)
        }
    }

    @GetMapping("/owned")
    suspend fun getOwnedPlans(): Flow<PlanDto> = run {
        withAuthorizedUserId { userId ->
            service.getByAuthor(userId)
                .map { plan ->
                    val tasks = taskService.getByPlanId(plan.id).toList()
                    val total = tasks.count()
                    val inProgress = tasks.count { it.status == TaskStatus.InProgress }
                    val completed = tasks.count { it.status == TaskStatus.Completed }
                    PlanDto(
                        id = plan.id,
                        title = plan.title,
                        userId = plan.userId,
                        createDt = plan.createDt,
                        dueTo = plan.dueTo,
                        tasksCompleted = completed,
                        tasksInProgress = inProgress,
                        tasksTotal = total
                    )
                }
        }
    }

    @PostMapping
    suspend fun createPlan(@RequestBody request: PlanCreationRequest) {
        withAuthorizedUserId {
            service.create(request.title, it, request.dueTo)
        }
    }
}
