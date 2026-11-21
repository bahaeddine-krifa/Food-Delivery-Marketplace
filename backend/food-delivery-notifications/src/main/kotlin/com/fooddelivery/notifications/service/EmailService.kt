package com.fooddelivery.notifications.service

import com.fooddelivery.notifications.domain.Notification
import jakarta.mail.internet.MimeMessage
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.MimeMessageHelper
import org.springframework.stereotype.Service

@Service
class EmailService(
    private val mailSender: JavaMailSender
) {

    fun sendNotificationEmail(notification: Notification) {
        val user = notification.user
            ?: return

        val message = mailSender.createMimeMessage()
        val helper = MimeMessageHelper(message, true, "UTF-8")

        helper.setTo(user.email)
        helper.setSubject(notification.title)
        helper.setText(buildEmailBody(notification), true)

        mailSender.send(message)
    }

    private fun buildEmailBody(notification: Notification): String {
        return """
        <!DOCTYPE html>
        <html>
        <head>
            <meta charset="UTF-8">
            <style>
                body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }
                .container { max-width: 600px; margin: 0 auto; padding: 20px; }
                .header { background: #4CAF50; color: white; padding: 20px; text-align: center; }
                .content { padding: 20px; background: #f9f9f9; }
                .footer { padding: 20px; text-align: center; font-size: 12px; color: #666; }
            </style>
        </head>
        <body>
            <div class="container">
                <div class="header">
                    <h1>Food Delivery Platform</h1>
                </div>
                <div class="content">
                    <h2>${notification.title}</h2>
                    <p>${notification.body ?: ""}</p>
                    ${notification.data?.let { "<p><small>Data: $it</small></p>" } ?: ""}
                </div>
                <div class="footer">
                    <p>This is an automated message. Please do not reply.</p>
                </div>
            </div>
        </body>
        </html>
        """.trimIndent()
    }
}