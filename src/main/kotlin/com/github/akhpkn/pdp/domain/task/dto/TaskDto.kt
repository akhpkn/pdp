package com.github.akhpkn.pdp.domain.task.dto

import com.github.akhpkn.pdp.domain.feedback.FeedbackRequest
import com.github.akhpkn.pdp.domain.task.model.Task
import com.github.akhpkn.pdp.domain.task.model.TaskStatus
import com.github.akhpkn.pdp.security.AccessType
import java.time.Instant
import java.util.UUID

data class TaskDto(
    val id: UUID,
    val title: String,
    val description: String,
    val acceptanceCriteria: String,
    val planId: UUID,
    val status: TaskStatus,
    val readOnly: Boolean,
    val dueTo: Instant,
    val feedbackRequested: Boolean,
    val feedbackRequestId: UUID?,
    val owned: Boolean
) {

    companion object {

        fun from(task: Task, access: AccessType, feedbackRequestOpt: FeedbackRequest?): TaskDto {
            return with(task) {
                TaskDto(
                    id = id,
                    title = title,
                    description = description,
                    acceptanceCriteria = acceptanceCriteria,
                    planId = planId,
                    status = status,
                    readOnly = (access == AccessType.Read),
                    dueTo = dueTo,
                    feedbackRequested = feedbackRequestOpt != null,
                    feedbackRequestId = feedbackRequestOpt?.id,
                    owned = (access == AccessType.Owner)
                )
            }
        }
    }
}
