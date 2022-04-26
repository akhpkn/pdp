package com.github.akhpkn.pdp.security.userdetails

import com.github.akhpkn.pdp.domain.user.dao.UserCredentialsDao
import com.github.akhpkn.pdp.domain.user.dao.UserDao
import kotlinx.coroutines.reactor.mono
import org.springframework.security.core.userdetails.ReactiveUserDetailsService
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import java.util.UUID

@Component
class CustomUserDetailsService(private val userCredentialsDao: UserCredentialsDao) : ReactiveUserDetailsService {

    override fun findByUsername(username: String?): Mono<UserDetails> = mono {
        val userCredentials = userCredentialsDao.find(username!!) ?: throw RuntimeException("User not found")
        with(userCredentials) {
            CustomUserDetails(id = userId, email = email, password = password)
        }
    }

    fun findById(id: UUID): Mono<UserDetails> = mono {
        val userCredentials = userCredentialsDao.find(id) ?: throw RuntimeException("User not found")
        with(userCredentials) {
            CustomUserDetails(id = userId, email = email, password = password)
        }
    }
}
