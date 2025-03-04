package com.example.agora.model.data

import com.example.agora.model.util.DataUtil
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.Timestamp

enum class PostStatus {
    ACTIVE, RESOLVED, DELETED
}

enum class Category(val value: String) {
    SELL("Marketplace"), RIDESHARE("Rideshare"), SUBLET("Sublet"), OTHER("Other")
}


class Post(
    var postId: String = "",
    var status: PostStatus = PostStatus.ACTIVE,
    var createdAt: Timestamp? = Timestamp.now(),
    var title: String = "",
    var description: String = "",
    var price: Double = 0.0,
    var category: Category = Category.OTHER,
    var images: MutableList<String> = mutableListOf("https://picsum.photos/200"),
    var comments: MutableList<Comment> = mutableListOf(),
    var userId: String = "",
    var address: Address = Address(),
    private var db: FirebaseFirestore = FirebaseFirestore.getInstance(),
) {
    fun updateInfo(newInfo: Map<String, Any>) {
        // TODO
    }

    fun addComment(text: String) {
        val comment: Comment = Comment(text=text, creatorId="")
        val mentions: List<String> = comment.findMentions()
        for (mention in mentions) {
            // TODO: create a notification
        }
        comments.add(comment)
    }

    fun removeComment(comment: Comment) {
        // TODO
    }


    fun changeStatus(newStatus: PostStatus) {
        status = newStatus
    }

    companion object {
        fun convertDBEntryToPostPreview(entry: Map<String, Any>): Post {
            return Post(
                postId = entry["postId"].toString(),
                title = entry["title"].toString(),
                price = entry["price"].toString().toDoubleOrNull() ?: 0.0,
                createdAt = DataUtil.convertStringToTimestamp(entry["createdAt"].toString()),
                images = (entry["images"] as? List<*>)?.map { it.toString() }?.toMutableList()
                    ?: mutableListOf("https://picsum.photos/200"), // Handle empty images
            )
        }

        fun convertDBEntryToPostDetail(entry: Map<String, Any>): Post {
            return Post(
                postId = entry["postId"].toString(),
                title = entry["title"].toString(),
                description = entry["description"].toString(),
                price = entry["price"].toString().toDoubleOrNull() ?: 0.0,
                status = PostStatus.entries.find { it.name == entry["status"] }
                    ?: PostStatus.DELETED,
                category = Category.entries.find { it.name == entry["category"] } ?: Category.OTHER,
                createdAt = DataUtil.convertStringToTimestamp(entry["createdAt"].toString()),
                userId = entry["userId"].toString(),
                address = (entry["address"] as? Map<String, Any>)?.let {
                    Address.AddressUtils.convertDBEntryToAddress(it)
                } ?: Address(),
                images = (entry["images"] as? List<*>)?.map { it.toString() }?.toMutableList()
                    ?: mutableListOf("https://picsum.photos/200"), // Handle empty images
            )
        }

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


                            val addressMap =
                                document.get("address") as? Map<String, Any> ?: emptyMap()
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
