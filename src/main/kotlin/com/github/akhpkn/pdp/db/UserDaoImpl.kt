package com.github.akhpkn.pdp.db

import com.github.akhpkn.pdp.domain.user.dao.UserDao
import com.github.akhpkn.pdp.domain.user.model.User
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.reactive.asFlow
import kotlinx.coroutines.reactor.awaitSingleOrNull
import org.springframework.r2dbc.core.DatabaseClient
import org.springframework.r2dbc.core.await
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
class UserDaoImpl(private val databaseClient: DatabaseClient) : UserDao {

    override suspend fun find(id: UUID): User? = run {
        databaseClient
            .sql("select * from \"user\" where id=:id")
            .bind("id", id)
            .map(MappingFunctions.toUser)
            .first()
            .awaitSingleOrNull()
    }

    override suspend fun find(email: String): User? = run {
        databaseClient
            .sql("select * from \"user\" where email=:email")
            .bind("email", email)
            .map(MappingFunctions.toUser)
            .first()
            .awaitSingleOrNull()
    }

    override fun listByEmailLike(email: String): Flow<User> = run {
        databaseClient
            .sql("select * from \"user\" where email like :email")
            .bind("email", "%$email%")
            .map(MappingFunctions.toUser)
            .all()
            .asFlow()
    }

    override suspend fun update(id: UUID, name: String, surname: String) {
        databaseClient
            .sql("update \"user\" set name=:name, surname=:surname where id=:id ")
            .bind("id", id)
            .bind("name", name)
            .bind("surname", surname)
            .await()
    }

    override suspend fun insert(user: User) {
        databaseClient
            .sql("insert into \"user\"(id, email, name, surname) values (:id, :email, :name, :surname);")
            .bind("id", user.id)
            .bind("email", user.email)
            .bind("name", user.name)
            .bind("surname", user.surname)
            .await()
    }
}
