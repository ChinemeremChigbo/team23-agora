package com.example.agora.model.data

import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.DocumentReference

enum class PostStatus {
    ACTIVE, RESOLVED, DELETED
}

enum class Category {
    SELL, RIDESHARE, SUBLET, OTHER
}

class Post(
    private var postId: String = "",
    private var status: PostStatus = PostStatus.ACTIVE,
    private var createdAt: Timestamp = Timestamp.now(),
    var title: String = "",
    var description: String = "",
    var price: Double = 0.0,
    var category: Category = Category.OTHER,
    var images: List<String> = listOf("https://picsum.photos/200"),
    var comments: MutableList<Comment> = mutableListOf()
) {

    companion object {
        private val db = FirebaseFirestore.getInstance()

        /** Create a new post in Firestore */
        fun createPost(
            title: String,
            description: String,
            price: Double,
            category: Category,
            images: List<String>,
            userId: String,
            onSuccess: (String) -> Unit,
            onFailure: (Exception) -> Unit
        ) {
            val postId = db.collection("posts").document().id

            val newPost = hashMapOf(
                "postId" to postId,
                "title" to title,
                "description" to description,
                "price" to price,
                "category" to category.name,
                "images" to images,
                "createdAt" to Timestamp.now(),
                "userId" to userId,
                "status" to PostStatus.ACTIVE.name
            )

            db.collection("posts").document(postId)
                .set(newPost)
                .addOnSuccessListener { onSuccess(postId) }
                .addOnFailureListener { onFailure(it) }
        }

        /** Fetch all posts from Firestore */
        fun fetchPosts(
            onSuccess: (List<Post>) -> Unit,
            onFailure: (Exception) -> Unit
        ) {
            db.collection("posts")
                .get()
                .addOnSuccessListener { documents ->
                    val posts = mutableListOf<Post>()
                    for (document in documents) {
                        val post = document.toObject(Post::class.java)
                        posts.add(post)
                    }
                    onSuccess(posts)
                }
                .addOnFailureListener { onFailure(it) }
        }
    }

    /** Update this post in Firestore */
    fun updatePost(
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        if (postId.isBlank()) {
            onFailure(Exception("Post ID is missing"))
            return
        }

        val postRef: DocumentReference = db.collection("posts").document(postId)

        val updatedData = mapOf(
            "title" to title,
            "description" to description,
            "price" to price,
            "category" to category.ordinal.toString(),
            "images" to images,
            "status" to status.name
        )

        postRef.update(updatedData)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onFailure(it) }
    }

    /** Getters and Setters */
    fun getPostId(): String = postId
    fun setPostId(value: String) {
        postId = value
    }

    fun getStatus(): PostStatus = status
    fun setStatus(value: PostStatus) {
        status = value
    }

    fun getCreatedAt(): Timestamp = createdAt
    fun setCreatedAt(value: Timestamp) {
        createdAt = value
    }

    /** Placeholder for future methods */
    fun updateInfo(newInfo: Map<String, Any>) {
        // TODO
    }

//    fun addComment(text: String) {
//        val comment = Comment(text = text, creatorId = "")
//        val mentions = comment.findMentions()
//        for (mention in mentions) {
//            // TODO: Create a notification
//        }
//        comments.add(comment)
//    }

    fun removeComment(comment: Comment) {
        // TODO
    }

    fun changeStatus(newStatus: PostStatus) {
        status = newStatus
    }
}
