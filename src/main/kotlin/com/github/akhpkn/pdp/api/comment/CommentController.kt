package com.github.akhpkn.pdp.api.comment

import com.github.akhpkn.pdp.api.comment.protocol.CommentCreationRequest
import com.github.akhpkn.pdp.api.comment.protocol.CommentDtoV2
import com.github.akhpkn.pdp.api.comment.protocol.CommentUpdateRequest
import com.github.akhpkn.pdp.auth.withAuthorizedUserId
import com.github.akhpkn.pdp.domain.comment.model.Comment
import com.github.akhpkn.pdp.domain.comment.service.CommentService
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
@RequestMapping("/api/v1/comment")
class CommentController(
    private val service: CommentService,
    private val accessService: AccessService
) {

    @GetMapping("/{id}")
    suspend fun getById(@PathVariable id: UUID): Comment = run {
        withAuthorizedUserId { userId ->
            service.getById(id).also {
                accessService.checkCommentReadAccess(it, userId)
            }
        }
    }

    @GetMapping
    suspend fun getAllByTaskId(@RequestParam("taskId") taskId: UUID): Flow<CommentDtoV2> = run {
        withAuthorizedUserId { userId ->
            accessService.checkTaskAccess(userId = userId, taskId = taskId, AccessType.Read)
            service.getByTaskIdV2(taskId)
                .map { CommentDtoV2.from(it, userId) }
        }
    }

    @PostMapping
    suspend fun createComment(@RequestBody request: CommentCreationRequest) {
        withAuthorizedUserId {
            with(request) {
                accessService.checkTaskAccess(userId = it, taskId = taskId, AccessType.Write)
                service.create(userId = it, taskId = taskId, text = text)
            }
        }
    }

    @PutMapping("/{id}")
    suspend fun updateComment(@PathVariable id: UUID, @RequestBody request: CommentUpdateRequest) {
        withAuthorizedUserId {
            service.update(id = id, text = request.text, userId = it)
        }
    }

    @DeleteMapping("/{id}")
    suspend fun deleteComment(@PathVariable id: UUID) {
        withAuthorizedUserId {
            service.delete(id = id, userId = it)
        }
    }
}
