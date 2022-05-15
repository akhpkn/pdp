package com.github.akhpkn.pdp.auth

import com.github.akhpkn.pdp.exception.NotFoundException

class UserNotFoundException : NotFoundException("Пользователь не существует")
