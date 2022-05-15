package com.github.akhpkn.pdp.domain.user.dao

import com.github.akhpkn.pdp.domain.user.model.User
import kotlinx.coroutines.flow.Flow
import java.util.UUID

interface UserDao {

    suspend fun getUser(id: UUID) = find(id) ?: throw RuntimeException("User $id not found")

    suspend fun find(id: UUID): User?

    suspend fun find(email: String): User?

    fun listByEmailLike(email: String): Flow<User>

    suspend fun update(id: UUID, name: String, surname: String)

    suspend fun insert(user: User)
}
