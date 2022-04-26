package com.github.akhpkn.pdp.domain.plan.model

import java.time.Instant
import java.util.UUID

data class Plan(
    val id: UUID,
    val title: String,
    val userId: UUID,
    val createDt: Instant,
    val dueTo: Instant
) {

    companion object {

        fun new(title: String, userId: UUID, dueTo: Instant) = Plan(
            id = UUID.randomUUID(), createDt = Instant.now(),
            title = title, userId = userId, dueTo = dueTo
        )
    }
}
