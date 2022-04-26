package com.github.akhpkn.pdp.api.task.protocol

import com.github.akhpkn.pdp.domain.task.model.TaskStatus

data class StatusRequest(
    val status: TaskStatus
)
