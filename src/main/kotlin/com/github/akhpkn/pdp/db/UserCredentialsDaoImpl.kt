package com.github.akhpkn.pdp.db

import com.github.akhpkn.pdp.domain.user.dao.UserCredentialsDao
import com.github.akhpkn.pdp.domain.user.model.UserCredentials
import com.github.akhpkn.pdp.domain.user.model.UserCredentialsModel
import org.springframework.r2dbc.core.DatabaseClient
import org.springframework.r2dbc.core.await
import org.springframework.r2dbc.core.awaitSingleOrNull
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
class UserCredentialsDaoImpl(private val databaseClient: DatabaseClient) : UserCredentialsDao {

    override suspend fun find(userId: UUID): UserCredentials? = run {
        databaseClient
            .sql(
                """
                    |select u.id as user_id, u.email as email, uc.password as password
                    |from "user" as u
                    |join user_credentials as uc on u.id=uc.id
                    |where u.id=:userId
                """.trimMargin()
            )
            .bind("userId", userId)
            .map(MappingFunctions.toUserCredentials)
            .awaitSingleOrNull()
    }

    override suspend fun find(email: String): UserCredentials? = run {
        databaseClient
            .sql(
                """
                    |select u.id as user_id, u.email as email, uc.password as password
                    |from "user" as u 
                    |join user_credentials as uc on u.id=uc.id
                    |where u.email=:email
                """.trimMargin()
            )
            .bind("email", email)
            .map(MappingFunctions.toUserCredentials)
            .awaitSingleOrNull()
    }

    override suspend fun insert(credentials: UserCredentialsModel) {
        databaseClient
            .sql("insert into user_credentials(id, password) values (:userId, :password)")
            .bind("userId", credentials.userId)
            .bind("password", credentials.password)
            .await()
    }
}
