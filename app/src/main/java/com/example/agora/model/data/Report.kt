package com.example.agora.model.data

import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore

class Report(
    private var db: FirebaseFirestore = FirebaseFirestore.getInstance(),
    var reportId: String = "",
    var postId: String = "",
    var reporterId: String = "",
    var reason: String = "",
    var createdAt: Any = FieldValue.serverTimestamp()
) {
    fun submitReport(onSuccess: () -> Unit, onFailure: (String) -> Unit) {
        val reportData = mapOf(
            "postId" to postId,
            "reporterId" to reporterId,
            "reason" to reason,
            "createdAt" to createdAt
        )

        db.collection("reports").add(reportData).addOnSuccessListener {
            println("Report successfully submitted!")
            onSuccess()
        }.addOnFailureListener { e ->
            println("Error submitting report: ${e.message}")
            onFailure(e.localizedMessage ?: "Unknown error")
        }
    }
}
