package com.github.akhpkn.pdp.auth

import com.github.akhpkn.pdp.exception.BadRequestException

class WrongPasswordException : BadRequestException("Неправильный пароль")
