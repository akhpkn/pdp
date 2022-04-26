package com.github.akhpkn.pdp.db

import com.github.akhpkn.pdp.domain.team.Team
import com.github.akhpkn.pdp.domain.team.TeamDao
import com.github.akhpkn.pdp.domain.user.model.User
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.reactive.asFlow
import kotlinx.coroutines.reactor.awaitSingleOrNull
import org.springframework.r2dbc.core.DatabaseClient
import org.springframework.r2dbc.core.await
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
class TeamDaoImpl(private val databaseClient: DatabaseClient) : TeamDao {

    override suspend fun find(id: UUID): Team? = run {
        databaseClient
            .sql("select * from team where id=:id")
            .bind("id", id)
            .map(MappingFunctions.toTeam)
            .first()
            .awaitSingleOrNull()
    }

    override suspend fun findByUser(userId: UUID): Team? = run {
        databaseClient
            .sql(
                """
                    select t.id as id, t.title as title, t.description, t.lead_id as lead_id
                    from team t 
                    join "user" u on t.id=u.team_id
                    where u.id=:userId
                """.trimIndent()
            )
            .bind("userId", userId)
            .map(MappingFunctions.toTeam)
            .first()
            .awaitSingleOrNull()
    }

    override fun listUsers(teamId: UUID): Flow<User> = run {
        databaseClient
            .sql("select * from \"user\" where team_id=:teamId")
            .bind("teamId", teamId)
            .map(MappingFunctions.toUser)
            .all()
            .asFlow()
    }

    override suspend fun updateLead(teamId: UUID, leadId: UUID) {
        databaseClient
            .sql("update team set lead_id=:leadId where id=:teamId")
            .bind("teamId", teamId)
            .bind("leadId", leadId)
            .await()
    }

    override suspend fun insert(team: Team) {
        databaseClient
            .sql("insert into team(id, lead_id, title, description) values (:id, :leadId, :title, :description)")
            .bind("id", team.id)
            .bind("leadId", team.leadId)
            .bind("title", team.title)
            .bind("description", team.description)
            .await()
    }

    override suspend fun linkUserToTeam(userId: UUID, teamId: UUID) {
        databaseClient
            .sql("update \"user\" set team_id=:teamId where id=:userId")
            .bind("teamId", teamId)
            .bind("userId", userId)
            .await()
    }
}
