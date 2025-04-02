package com.example.agora.util

import android.content.Context
import android.util.Log
import java.util.Properties

fun loadAwsCredentials(context: Context): Map<String, String> {
    val properties = Properties()
    return try {
        context.assets.open("local.properties").use { properties.load(it) }
        mapOf(
            "AWS_ACCESS_KEY" to (properties.getProperty("AWS_ACCESS_KEY") ?: ""),
            "AWS_SECRET_KEY" to (properties.getProperty("AWS_SECRET_KEY") ?: ""),
            "AWS_REGION" to (properties.getProperty("AWS_REGION") ?: ""),
            "S3_BUCKET_NAME" to (properties.getProperty("S3_BUCKET_NAME") ?: "")
        )
    } catch (e: Exception) {
        Log.e("AWSConfig", "Error reading local.properties: ${e.message}")
        emptyMap() // prevents crash by returning an empty map
    }
}
