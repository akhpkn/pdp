package com.github.akhpkn.pdp.email

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runInterruptible
import mu.KLogging
import org.springframework.mail.SimpleMailMessage
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.stereotype.Component

@Component
class EmailService(private val mailSender: JavaMailSender) {

    suspend fun send(data: EmailData) {
        val message = SimpleMailMessage().apply {
            setTo(data.receiver)
            setSubject(data.subject)
            setText(data.text)
        }

        runInterruptible(Dispatchers.IO) {
//            doSend(message)
            mockEmailSending(message)
        }
    }

    private fun doSend(message: SimpleMailMessage) {
        logger.info { "Sending [${message.subject}] message [${message.text}] to ${message.to!!.first()}" }
        mailSender.send(message)
        logger.info { "Successfully sent message" }
    }

    private fun mockEmailSending(message: SimpleMailMessage) {
        logger.info { "Sending [${message.subject}] message [${message.text}] to ${message.to!!.first()}" }
        Thread.sleep(1000)
        logger.info { "Successfully sent message" }
    }

    companion object : KLogging()
}
