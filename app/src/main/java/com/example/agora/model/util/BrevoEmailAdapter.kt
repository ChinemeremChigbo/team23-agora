package com.example.agora.model.util

class BrevoEmailAdapter(private val apiService: BrevoApiService) : EmailService {
    override fun sendEmail(request: EmailRequest) {
        apiService.sendEmail(request).enqueue(object : retrofit2.Callback<EmailResponse> {
            override fun onResponse(call: retrofit2.Call<EmailResponse>, response: retrofit2.Response<EmailResponse>) {
                if (response.isSuccessful) {
                    println("Brevo Email sent successfully: ${response.body()?.messageId}")
                } else {
                    println("Brevo Failed to send email: ${response.errorBody()?.string()}")
                }
            }

            override fun onFailure(call: retrofit2.Call<EmailResponse>, t: Throwable) {
                println("Brevo Error: ${t.message}")
            }
        })
    }
}


