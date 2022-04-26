package com.github.akhpkn.pdp.api.task

import com.github.akhpkn.pdp.auth.withAuthorizedUserId
import com.github.akhpkn.pdp.domain.task.model.TaskAuditData
import com.github.akhpkn.pdp.domain.task.service.TaskAuditService
import com.github.akhpkn.pdp.security.AccessService
import com.github.akhpkn.pdp.security.AccessType
import kotlinx.coroutines.flow.Flow
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
@RequestMapping("/api/v1/task-audit")
class TaskAuditController(
    private val service: TaskAuditService,
    private val accessService: AccessService
) {

    @GetMapping
    suspend fun listByPlan(@RequestParam("planId") planId: UUID): Flow<TaskAuditData> = run {
        withAuthorizedUserId {
            accessService.checkPlanAccess(userId = it, planId = planId, accessType = AccessType.Read)
            service.listByPlan(planId)
        }
    }
}
