package com.github.akhpkn.pdp.api.plan

import com.github.akhpkn.pdp.api.plan.protocol.PlanCreationRequest
import com.github.akhpkn.pdp.api.plan.protocol.PlanDataDto
import com.github.akhpkn.pdp.api.plan.protocol.PlanDto
import com.github.akhpkn.pdp.api.plan.protocol.PlanExt
import com.github.akhpkn.pdp.api.plan.protocol.PlanTitle
import com.github.akhpkn.pdp.auth.withAuthorizedUserId
import com.github.akhpkn.pdp.domain.plan.model.Plan
import com.github.akhpkn.pdp.domain.plan.model.PlanData
import com.github.akhpkn.pdp.domain.plan.service.PlanService
import com.github.akhpkn.pdp.domain.task.model.TaskStatus
import com.github.akhpkn.pdp.domain.task.service.TaskService
import com.github.akhpkn.pdp.security.AccessService
import com.github.akhpkn.pdp.security.AccessType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
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
@RequestMapping("/api/v1/plan")
class PlanController(
    private val service: PlanService,
    private val taskService: TaskService,
    private val accessService: AccessService
) {

    @GetMapping("/{id}")
    suspend fun getById(@PathVariable id: UUID): PlanExt = run {
        withAuthorizedUserId { userId ->
            val access = accessService.checkPlanAccess(userId = userId, planId = id, AccessType.Read)
            val plan = service.getById(id)
            PlanExt.from(plan, access)
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
    suspend fun getSharedPlans(): Flow<PlanDataDto> = run {
        withAuthorizedUserId { userId ->
            service.getSharedPlans(userId)
                .toPlanDataDto()
        }
    }

    @GetMapping("/owned")
    suspend fun getOwnedPlans(): Flow<PlanDto> = run {
        withAuthorizedUserId { userId ->
            service.getByAuthor(userId)
                .toPlanDto()
        }
    }

    private suspend fun Flow<Plan>.toPlanDto(): Flow<PlanDto> {
        return map {
            val stats = getStats(it.id)
            PlanDto(
                id = it.id,
                title = it.title,
                userId = it.userId,
                createDt = it.createDt,
                dueTo = it.dueTo,
                tasksCompleted = stats.completed,
                tasksInProgress = stats.inProgress,
                tasksTotal = stats.total
            )
        }
    }

    private suspend fun Flow<PlanData>.toPlanDataDto(): Flow<PlanDataDto> {
        return map {
            val stats = getStats(it.id)
            PlanDataDto(
                id = it.id,
                title = it.title,
                userId = it.userId,
                userEmail = it.userEmail,
                userName = it.userName,
                userSurname = it.userSurname,
                createDt = it.createDt,
                dueTo = it.dueTo,
                tasksCompleted = stats.completed,
                tasksInProgress = stats.inProgress,
                tasksTotal = stats.total
            )
        }
    }

    private suspend fun getStats(planId: UUID): ProgressStats {
        val tasks = taskService.getByPlanId(planId).toList()
        val total = tasks.count()
        val inProgress = tasks.count { t -> t.status == TaskStatus.InProgress }
        val completed = tasks.count { t -> t.status == TaskStatus.Completed }
        return ProgressStats(total, inProgress, completed)
    }

    private data class ProgressStats(val total: Int, val inProgress: Int, val completed: Int)

    @PostMapping
    suspend fun createPlan(@RequestBody request: PlanCreationRequest) {
        withAuthorizedUserId {
            service.create(request.title, it, request.dueTo)
        }
    }

    @PutMapping("/{id}")
    suspend fun updateTitle(@PathVariable id: UUID, @RequestBody title: PlanTitle) {
        withAuthorizedUserId {
            accessService.checkPlanAccess(userId = it, planId = id, accessType = AccessType.Owner)
            service.updateTitle(id, title.title)
        }
    }

    @DeleteMapping("/{id}")
    suspend fun deletePlan(@PathVariable id: UUID) {
        withAuthorizedUserId {
            accessService.checkPlanAccess(userId = it, planId = id, accessType = AccessType.Owner)
            service.delete(id)
        }
    }
}
