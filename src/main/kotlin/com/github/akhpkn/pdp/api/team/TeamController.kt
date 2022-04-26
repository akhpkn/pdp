package com.github.akhpkn.pdp.api.team

import com.github.akhpkn.pdp.api.team.protocol.TeamCreationRequest
import com.github.akhpkn.pdp.auth.withAuthorizedUserId
import com.github.akhpkn.pdp.domain.team.TeamInfo
import com.github.akhpkn.pdp.domain.team.TeamService
import com.github.akhpkn.pdp.domain.user.model.User
import kotlinx.coroutines.flow.Flow
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
@RequestMapping("/api/v1/team")
class TeamController(private val service: TeamService) {

    @GetMapping("/{id}/users")
    fun listTeamUsers(@PathVariable id: UUID): Flow<User> = run {
        service.listTeamUsers(id)
    }

    @GetMapping("/my")
    suspend fun getTeamInfo(): TeamInfo = run {
        withAuthorizedUserId {
            service.getTeamInfo(it)
        }
    }

    @PostMapping
    suspend fun createTeam(@RequestBody request: TeamCreationRequest) {
        withAuthorizedUserId {
            service.createTeam(leadId = it, title = request.title, description = request.description)
        }
    }
}

