package com.github.akhpkn.pdp.domain.user.dao

import com.github.akhpkn.pdp.domain.user.model.User
import com.github.akhpkn.pdp.domain.user.model.UserCredentials
import com.github.akhpkn.pdp.domain.user.model.UserCredentialsModel
import java.util.UUID

interface UserCredentialsDao {

    suspend fun find(userId: UUID): UserCredentials?

    suspend fun find(email: String): UserCredentials?

    suspend fun insert(credentials: UserCredentialsModel)
}
