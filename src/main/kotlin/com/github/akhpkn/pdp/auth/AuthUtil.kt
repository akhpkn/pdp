package com.github.akhpkn.pdp.auth

import java.util.UUID

suspend fun <T> withAuthorizedUserId(action: suspend (UUID) -> T): T = action(AuthContext.userId())
