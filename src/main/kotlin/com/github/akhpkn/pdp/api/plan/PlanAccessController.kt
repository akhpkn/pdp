package com.github.akhpkn.pdp.api.plan

import com.github.akhpkn.pdp.api.plan.protocol.PlanAccessCreationRequest
import com.github.akhpkn.pdp.api.plan.protocol.PlanAccessDeletionRequest
import com.github.akhpkn.pdp.api.plan.protocol.PlanAccessMultipleCreationRequest
import com.github.akhpkn.pdp.auth.withAuthorizedUserId
import com.github.akhpkn.pdp.domain.plan.model.PlanAccessInfo
import com.github.akhpkn.pdp.domain.plan.service.PlanAccessService
import com.github.akhpkn.pdp.security.AccessService
import com.github.akhpkn.pdp.security.AccessType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterNot
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
@RequestMapping("/api/v1/plan-access")
class PlanAccessController(
    private val service: PlanAccessService,
    private val accessService: AccessService
) {

    @GetMapping
    suspend fun listUsersByPlan(@RequestParam("planId") planId: UUID) : Flow<PlanAccessInfo> = run {
        withAuthorizedUserId { userId ->
            accessService.checkPlanAccess(userId, planId, AccessType.Owner)
            service.listUsersWithPlanAccess(planId)
                .filterNot { it.userId ==  userId }
        }
    }

    @PostMapping
    suspend fun grantAccess(@RequestBody request: PlanAccessCreationRequest) {
        withAuthorizedUserId {
            if (request.userId != it) {
                accessService.checkPlanAccess(userId = it, planId = request.planId, AccessType.Owner)
                service.grant(planId = request.planId, userId = request.userId, request.type)
            }
        }
    }

    @PostMapping("/multiple")
    suspend fun grantAccessMultiple(@RequestBody request: PlanAccessMultipleCreationRequest) {
        withAuthorizedUserId {
            accessService.checkPlanAccess(userId = it, planId = request.planId, AccessType.Owner)
            service.grant(planId = request.planId, userIds = request.userIds, request.type)
        }
    }

    @DeleteMapping
    suspend fun removeAccess(@RequestBody request: PlanAccessDeletionRequest) {
        withAuthorizedUserId {
            if (request.userId != it) {
                accessService.checkPlanAccess(userId = it, planId = request.planId, AccessType.Owner)
                service.delete(planId = request.planId, userId = request.userId)
            }
        }
    }
}
