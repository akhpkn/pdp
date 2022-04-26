package com.github.akhpkn.pdp.domain.team

import com.github.akhpkn.pdp.domain.user.model.User
import kotlinx.coroutines.flow.Flow
import java.util.UUID

interface TeamDao {

    suspend fun find(id: UUID): Team?

    suspend fun findByUser(userId: UUID): Team?

    fun listUsers(teamId: UUID): Flow<User>

    suspend fun updateLead(teamId: UUID, leadId: UUID)

    suspend fun insert(team: Team)

    suspend fun linkUserToTeam(userId: UUID, teamId: UUID)
}
