package com.github.akhpkn.pdp.api.user

import com.github.akhpkn.pdp.api.user.protocol.UserUpdateRequest
import com.github.akhpkn.pdp.auth.withAuthorizedUserId
import com.github.akhpkn.pdp.domain.user.model.User
import com.github.akhpkn.pdp.domain.user.service.UserService
import com.github.akhpkn.pdp.security.AccessService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.filterNot
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
@RequestMapping("/api/v1/user")
class UserController(
    private val service: UserService,
    private val accessService: AccessService
) {

    @GetMapping
    suspend fun getByEmailSubstring(@RequestParam("email") email: String): Flow<User> = run {
        withAuthorizedUserId { userId ->
            service.listByEmailContaining(email)
                .filterNot { it.id == userId }
        }
    }

    @PutMapping("/{id}")
    suspend fun updateUser(@PathVariable id: UUID, @RequestBody request: UserUpdateRequest) {
        withAuthorizedUserId {
            accessService.checkUserAccess(it, id)
            service.updateUserData(it, request.name, request.surname)
        }
    }

    @GetMapping("/profile")
    suspend fun getProfile(): User = run {
        withAuthorizedUserId {
            service.findById(it)
        }
    }

    @PutMapping("/profile")
    suspend fun updateProfile(@RequestBody request: UserUpdateRequest) {
        withAuthorizedUserId {
            service.updateUserData(it, request.name, request.surname)
        }
    }
}
