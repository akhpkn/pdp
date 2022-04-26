package com.github.akhpkn.pdp.domain.team

import java.util.UUID

data class Team(
    val id: UUID,
    val title: String,
    val description: String,
    val leadId: UUID
)
