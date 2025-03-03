package com.example.agora.util

import android.content.Context
import android.net.Uri
import android.util.Log
import com.amazonaws.auth.BasicAWSCredentials
import com.amazonaws.mobileconnectors.s3.transferutility.*
import com.amazonaws.services.s3.AmazonS3Client
import java.io.File
import java.io.FileOutputStream
import java.util.*

fun uploadImageToS3(context: Context, imageUri: Uri, onSuccess: (String) -> Unit, onFailure: (String) -> Unit) {
    val credentials = loadAwsCredentials(context)

    val accessKey = credentials["AWS_ACCESS_KEY"]
    val secretKey = credentials["AWS_SECRET_KEY"]
    val bucketName = credentials["S3_BUCKET_NAME"]

    if (accessKey.isNullOrBlank() || secretKey.isNullOrBlank() || bucketName.isNullOrBlank()) {
        Log.w("S3Uploader", "AWS credentials are missing! Skipping upload.")
        onFailure("S3 upload failed.")
        return// return without calling any callback
    }

    @Suppress("DEPRECATION") // AmazonS3Client is still preferred for Android
    val s3Client = AmazonS3Client(BasicAWSCredentials(accessKey, secretKey))

    val file = File(context.cacheDir, "upload_${UUID.randomUUID()}.jpg")
    context.contentResolver.openInputStream(imageUri)?.use { input ->
        FileOutputStream(file).use { output -> input.copyTo(output) }
    }

    val key = "post_images/${UUID.randomUUID()}.jpg"

    val transferUtility = TransferUtility.builder()
        .context(context)
        .s3Client(s3Client)
        .build()

    val uploadObserver = transferUtility.upload(bucketName, key, file)

    uploadObserver.setTransferListener(object : TransferListener {
        override fun onStateChanged(id: Int, state: TransferState?) {
            if (state == TransferState.COMPLETED) {
                val cloudFrontUrl = "https://d2ie62v87qyywl.cloudfront.net/$key"
                onSuccess(cloudFrontUrl)
                Log.d("S3Uploader", "Upload successful: $cloudFrontUrl")
            } else if (state == TransferState.FAILED) {
                onFailure("S3 upload failed.")
            }
        }

        override fun onError(id: Int, ex: Exception?) {
            Log.e("S3Uploader", "Upload failed", ex)
            onFailure(ex?.message ?: "Unknown S3 upload error")
        }

        override fun onProgressChanged(id: Int, bytesCurrent: Long, bytesTotal: Long) {
            val progress = if (bytesTotal > 0) (bytesCurrent * 100) / bytesTotal else 0
            Log.d("S3Uploader", "Upload Progress: $progress%")
        }
    })
}
