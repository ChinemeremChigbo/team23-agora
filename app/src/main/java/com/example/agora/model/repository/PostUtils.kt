package com.example.agora.model.repository

import com.example.agora.model.data.Post
import com.example.agora.model.data.Post.Companion.convertDBEntryToPostDetail
import com.google.firebase.firestore.FirebaseFirestore
import okhttp3.Callback

class PostUtils {
    companion object {
        fun getPost(postId: String, callback: (Post?) -> Unit) {
            val db = FirebaseFirestore.getInstance()

            db.collection("posts").document(postId)
                .get()
                .addOnSuccessListener { post ->
                    if (post.exists()) {
                        callback(post.data?.let { convertDBEntryToPostDetail(it) })
                    } else {
                        callback(null)
                    }
                }
        }
    }
}