package com.github.akhpkn.pdp.domain.user.exception

import com.github.akhpkn.pdp.exception.NotFoundException

class UserNotFoundException : NotFoundException("Пользователь не найден")
