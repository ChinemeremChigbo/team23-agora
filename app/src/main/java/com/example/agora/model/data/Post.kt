package com.example.agora.model.data

import android.util.Log
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore

enum class PostStatus {
    ACTIVE, RESOLVED, DELETED
}

enum class Category {
    SELL, RIDESHARE, SUBLET, OTHER;

    companion object {
        private val categoryToNumber = mapOf(
            SELL to 1,
            RIDESHARE to 2,
            SUBLET to 3,
            OTHER to 4
        )

        private val numberToCategory = categoryToNumber.entries.associate { (key, value) -> value to key }

        fun toNumber(category: Category): Int {
            return categoryToNumber[category] ?: 4
        }

        fun fromNumber(number: Int): Category {
            return numberToCategory[number] ?: OTHER
        }
    }
}

class Post(
    var postId: String = "",
    var status: PostStatus = PostStatus.ACTIVE,
    var createdAt: Timestamp = Timestamp.now(),
    var title: String = "",
    var description: String = "",
    var price: Double = 0.0,
    var category: Category = Category.OTHER,
    var images: List<String> = listOf("https://files.catbox.moe/ioidxm.JPG"),
    var userId: String = "",
    var address: Address = Address.create("1 Elm Lane", "Toronto", "Ontario", "M2N 0A5", "Canada") as Address // Default empty address
) {
    // Getters and Setters
    fun getPostId(): String = postId
    fun setPostId(value: String) { postId = value }

    fun getUserId(): String = userId
    fun setUserId(value: String) { userId = value }

    fun getStatus(): PostStatus = status
    fun setStatus(value: PostStatus) { status = value }

    fun getCreatedAt(): Timestamp = createdAt
    fun setCreatedAt(value: Timestamp) { createdAt = value }

    fun updateInfo(newInfo: Map<String, Any>) {
        // TODO
    }

//    fun addComment(text: String) {
//        val comment: Comment = Comment(text=text, creatorId="")
//        val mentions: List<String> = comment.findMentions()
//        for (mention in mentions) {
//            // TODO: create a notification
//        }
//        comments.add(comment)
//    }

    fun removeComment(comment: Comment) {
        // TODO
    }

    fun changeStatus(newStatus: PostStatus) {
        status = newStatus
    }


    companion object {
        private val DEFAULT_IMAGE = "https://files.catbox.moe/ioidxm.JPG"

        /** Fetch User's Address from Firestore */
        private fun fetchUserAddress(
            userId: String,
            onSuccess: (Address) -> Unit,
            onFailure: (Exception) -> Unit
        ) {
            val db = FirebaseFirestore.getInstance()
            db.collection("users").document(userId).get()
                .addOnSuccessListener { document ->
                    val addressMap = document.get("address") as? Map<*, *>
                    if (addressMap != null) {
                        val address = Address.create(
                            street = addressMap["address"] as? String ?: "",
                            city = addressMap["city"] as? String ?: "",
                            state = addressMap["state"] as? String ?: "",
                            postalCode = addressMap["postalCode"] as? String ?: "",
                            country = addressMap["country"] as? String ?: ""
                        ) as? Address
                        if (address != null) {
                            onSuccess(address)
                        } else {
                            onFailure(Exception("Invalid address format"))
                        }
                    } else {
                        onFailure(Exception("No address found for user"))
                    }
                }
                .addOnFailureListener { onFailure(it) }
        }

        /** Create a new post in Firestore with User's Address */
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
            fetchUserAddress(
                userId = userId,
                onSuccess = { address ->
                    val db = FirebaseFirestore.getInstance()
                    val postId = db.collection("posts").document().id
//                  Placeholder image for now
                    val validImages = images?.ifEmpty { listOf(DEFAULT_IMAGE) }
                    val newPost = hashMapOf(
                        "postId" to postId,
                        "title" to title,
                        "description" to description,
                        "price" to price,
                        "category" to category.name,  // Store Enum as String
                        "images" to validImages,
                        "createdAt" to Timestamp.now(),
                        "userId" to userId,
                        "status" to PostStatus.ACTIVE.name,
                        "address" to mapOf(  // Store Address as a Map
                            "address" to address.getStreet(),
                            "city" to address.getCity(),
                            "state" to address.getState(),
                            "postalCode" to address.getPostalCode(),
                            "country" to address.getCountry()
                        )
                    )

                    db.collection("posts").document(postId)
                        .set(newPost)
                        .addOnSuccessListener { onSuccess(postId) }
                        .addOnFailureListener { onFailure(it) }
                },
                onFailure = { exception ->
                    onFailure(Exception("Failed to fetch user address: ${exception.message}"))
                }
            )
        }

        /** Fetch all posts from Firestore */
        fun fetchPosts(
            onSuccess: (List<Post>) -> Unit,
            onFailure: (Exception) -> Unit
        ) {
            val db = FirebaseFirestore.getInstance()
            db.collection("posts")
                .get()
                .addOnSuccessListener { documents ->
                    val posts = mutableListOf<Post>()
                    for (document in documents) {
                        try {
                            val categoryString = document.getString("category") ?: "OTHER"
                            val categoryEnum = try {
                                Category.valueOf(categoryString) // Convert String to Enum
                            } catch (e: IllegalArgumentException) {
                                Category.OTHER
                            }

                            val addressMap = document.get("address") as? Map<String, Any> ?: emptyMap()
                            val address = Address.create(
                                street = addressMap["street"] as? String ?: "",
                                city = addressMap["city"] as? String ?: "",
                                state = addressMap["state"] as? String ?: "",
                                postalCode = addressMap["postalCode"] as? String ?: "",
                                country = addressMap["country"] as? String ?: ""
                            ) as? Address ?: Address.create("", "", "", "", "") as Address

                            val post = document.toObject(Post::class.java).apply {
                                category = categoryEnum
                                this.address = address
                            }
                            posts.add(post)
                        } catch (e: Exception) {
                            onFailure(e)
                            return@addOnSuccessListener
                        }
                    }
                    onSuccess(posts)
                }
                .addOnFailureListener { onFailure(it) }
        }


    }
}
