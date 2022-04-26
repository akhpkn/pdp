package com.github.akhpkn.pdp.domain.team

import com.github.akhpkn.pdp.domain.user.model.User
import java.util.UUID

data class TeamInfo(
    val id: UUID,
    val title: String,
    val description: String,
    val leadId: UUID,
    val leadEmail: String,
    val leadName: String,
    val leadSurname: String,
    val users: List<User>
)
