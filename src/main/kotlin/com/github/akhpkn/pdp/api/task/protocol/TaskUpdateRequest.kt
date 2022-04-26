package com.github.akhpkn.pdp.api.task.protocol

data class TaskUpdateRequest(
    val title: String,
    val description: String,
    val acceptanceCriteria: String
)
