package com.github.akhpkn.pdp.domain.user.service

import com.github.akhpkn.pdp.domain.user.dao.UserDao
import com.github.akhpkn.pdp.domain.user.model.User
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class UserService(private val dao: UserDao) {

    suspend fun findById(id: UUID) = dao.find(id) ?: throw RuntimeException("User not found")

    suspend fun findByEmail(email: String) = dao.find(email) ?: throw RuntimeException("User not found")

    fun listByEmailContaining(emailSubstring: String) = dao.listByEmailLike(emailSubstring)

    suspend fun existsByEmail(email: String) = dao.find(email) != null

    suspend fun updateUserData(id: UUID, name: String, surname: String) {
        dao.update(id, name, surname)
    }

    suspend fun createUser(email: String, name: String, surname: String): UUID {
        val user = makeUser(email, name, surname)
        dao.insert(user)
        return user.id
    }

    private fun makeUser(email: String, name: String, surname: String) =
        User(
            id = UUID.randomUUID(),
            email = email,
            name = name,
            surname = surname,
            teamId = null
        )
}
