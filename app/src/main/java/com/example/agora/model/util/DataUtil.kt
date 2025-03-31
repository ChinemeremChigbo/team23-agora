package com.example.agora.model.util

import com.google.firebase.Timestamp
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

class DataUtil {
    companion object {
        fun convertStringToTimestamp(value: String): Timestamp? {
            val dateFormat = SimpleDateFormat("MMMM d, yyyy 'at' h:mm:ss a z", Locale.US).apply {
                timeZone = TimeZone.getTimeZone("UTC")
            }

            val timestamp = try {
                val date = dateFormat.parse(value)
                if (date != null) {
                    Timestamp(date)
                } else {
                    null
                }
            } catch (e: Exception) {
                null
            }
            return timestamp
        }
    }
}
