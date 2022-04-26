package com.github.akhpkn.pdp.domain.team

import com.github.akhpkn.pdp.domain.user.model.User
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.toList
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
class TeamService(private val dao: TeamDao) {

    fun listTeamUsers(teamId: UUID): Flow<User> = dao.listUsers(teamId)

    suspend fun getTeamInfo(userId: UUID): TeamInfo {
        val team = dao.findByUser(userId) ?: throw RuntimeException("Team not found")
        val users = listTeamUsers(team.id).toList()
        val lead = users.first { it.id == team.leadId }
        return TeamInfo(
            id = team.id,
            title = team.title,
            description = team.description,
            leadId = team.leadId,
            leadEmail = lead.email,
            leadName = lead.name,
            leadSurname = lead.surname,
            users = users,
        )
    }

    @Transactional
    suspend fun createTeam(leadId: UUID, title: String, description: String) {
        val team = Team(
            id = UUID.randomUUID(),
            title = title,
            description = description,
            leadId = leadId
        )
        dao.insert(team)
        dao.linkUserToTeam(userId = leadId, teamId = team.id)
    }

    suspend fun changeLead(teamId: UUID, initiatorId: UUID, newLeadId: UUID) {
        val team = dao.find(teamId) ?: throw RuntimeException("Team not found")
        if (team.leadId != initiatorId) throw RuntimeException("No access")
        dao.updateLead(teamId, newLeadId)
    }
}
