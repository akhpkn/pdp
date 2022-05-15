package com.github.akhpkn.pdp.auth

import com.github.akhpkn.pdp.exception.BadRequestException

class UserExistsByEmailException : BadRequestException("Пользователь с таким email уже существует!")
