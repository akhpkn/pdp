package com.github.akhpkn.pdp.auth

import com.github.akhpkn.pdp.exception.BadRequestException

class WrongEmailOrPasswordException : BadRequestException("Неверный email или пароль")
