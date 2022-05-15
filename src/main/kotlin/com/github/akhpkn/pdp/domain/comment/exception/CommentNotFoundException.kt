package com.github.akhpkn.pdp.domain.comment.exception

import com.github.akhpkn.pdp.exception.NotFoundException

class CommentNotFoundException : NotFoundException("Комментарий не найден")
