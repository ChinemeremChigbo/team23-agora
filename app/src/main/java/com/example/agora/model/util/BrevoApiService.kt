package com.example.agora.model.util

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface BrevoApiService {
    @Headers("Accept: application/json", "Content-Type: application/json")
    @POST("https://api.brevo.com/v3/smtp/email")
    fun sendEmail(
        @Body request: EmailRequest
    ): Call<EmailResponse>
}


data class EmailRequest(
    val sender: Sender,
    val to: List<Recipient>,
    val subject: String,
    val htmlContent: String
)

data class Sender(val name: String, val email: String)
data class Recipient(val email: String, val name: String)
data class EmailResponse(val messageId: String)

object EmailTemplate {
    private fun generateVerificationCode(): String {
        val randomCode = (100000..999999).random().toString() // 6-digit code
        return randomCode
    }
    var verificationCode = "123456"
    fun generateHtmlContent(): String {
        verificationCode = generateVerificationCode()
        val htmlContent = """
    <!DOCTYPE html>
    <html>
    <head>
        <style>
            body {
                font-family: Arial, sans-serif;
                background-color: #ffffff;
                margin: 0;
                padding: 0;
            }
            .container {
                max-width: 500px;
                background: #ffffff;
                margin: 20px auto;
                padding: 20px;
                border-radius: 10px;
                box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);
                text-align: center;
            }
            h2 {
                color: #333;
            }
            .code {
                display: inline-block;
                font-size: 24px;
                font-weight: bold;
                color: #ffffff;
                background: #007BFF;
                padding: 10px 20px;
                border-radius: 5px;
                margin: 10px 0;
            }
            .footer {
                font-size: 12px;
                color: #777;
                margin-top: 20px;
            }
        </style>
    </head>
    <body>
        <div class="container">
            <h2>Thank You for Signing Up!</h2>
            <p>Here is your verification code:</p>
            <div class="code">$verificationCode</div>
            <p>Please enter this code to verify your account.</p>
            <div class="footer">If you didn't request this, please ignore this email.</div>
        </div>
    </body>
    </html>
""".trimIndent()
        return htmlContent
    }
}