package com.github.akhpkn.pdp.api.feedback

import com.github.akhpkn.pdp.api.feedback.protocol.FeedbackRequestInput
import com.github.akhpkn.pdp.api.feedback.protocol.FeedbackSendInput
import com.github.akhpkn.pdp.auth.withAuthorizedUserId
import com.github.akhpkn.pdp.domain.feedback.FeedbackService
import com.github.akhpkn.pdp.domain.feedback.TaskFeedbackView
import com.github.akhpkn.pdp.security.AccessService
import com.github.akhpkn.pdp.security.AccessType
import kotlinx.coroutines.flow.Flow
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
@RequestMapping("/api/v1/feedback")
class FeedbackController(
    private val service: FeedbackService,
    private val accessService: AccessService
) {

    @GetMapping
    suspend fun getByTaskId(@RequestParam("taskId") taskId: UUID): Flow<TaskFeedbackView> = run {
        withAuthorizedUserId {
            accessService.checkTaskAccess(userId = it, taskId = taskId, accessType = AccessType.Read)
            service.listFeedbacks(taskId)
        }
    }

    @PostMapping("/request")
    suspend fun request(@RequestBody input: FeedbackRequestInput) {
        withAuthorizedUserId {
            accessService.checkTaskAccess(userId = it, taskId = input.taskId, accessType = AccessType.Owner)
            service.request(userId = it, assigneeId = input.assigneeId, taskId = input.taskId)
        }
    }

    @PostMapping("/send")
    suspend fun send(@RequestBody input: FeedbackSendInput) {
        withAuthorizedUserId {
            service.send(requestId = input.requestId, userId = it, text = input.text)
        }
    }
}
