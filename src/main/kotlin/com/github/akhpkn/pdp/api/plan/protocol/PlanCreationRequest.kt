package com.github.akhpkn.pdp.api.plan.protocol

import java.time.Instant

data class PlanCreationRequest(
    val title: String,
    val dueTo: Instant
)
