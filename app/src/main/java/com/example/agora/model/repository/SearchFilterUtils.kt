package com.example.agora.model.repository

import android.util.Log
import com.example.agora.model.data.Category
import com.example.agora.model.data.Post
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class SearchFilterUtils {
    companion object {
        fun filterPosts(
            minPrice: Int? = null,
            maxPrice: Int? = null,
            category: Category? = null,
            sortByPrice: Boolean = false,
            priceLowToHi: Boolean = true,
            limit: Int = -1,
            callback: (List<Map<String, Any>>) -> Unit
        ) {
            val db = FirebaseFirestore.getInstance()

            var query: Query = db.collection("posts")

            minPrice?.let {
                query = query.whereGreaterThanOrEqualTo("price", minPrice)
            }

            maxPrice?.let {
                query = query.whereLessThanOrEqualTo("price", maxPrice)
            }

            category?.let {
                query = query.whereEqualTo("category", category.name)
            }

            query = if (sortByPrice) {
                query.orderBy(
                    "price",
                    if (priceLowToHi) Query.Direction.ASCENDING else Query.Direction.DESCENDING
                )
            } else {
                query.orderBy(
                    "createdAt", Query.Direction.DESCENDING
                )
            }

            // TODO: Enable this when all posts have been populated with status
            // query = query.whereNotEqualTo(
            //    "status",
            //    PostStatus.DELETED.name
            //)

            if(limit != -1) {
                query = query.limit(limit.toLong())
            }


            query.get()
                .addOnSuccessListener { documents ->
                    val resultList = mutableListOf<Map<String, Any>>()
                    for (document in documents) {
                        resultList.add(document.data)
                    }
                    callback(resultList)
                }
                .addOnFailureListener { exception ->
                    println("Error getting posts: $exception")
                    callback(emptyList())
                }
        }

        fun isPostInWishlist(userId: String, postId: String): Boolean {
            var wishlist = emptyList<Post>()

            WishlistUtils.getWishList(userId) { posts ->
                Log.i("HELLO", "hehehaha")
                Log.i(posts.toString() + "HELLO", "bing bong")
                wishlist = posts.map { post -> Post.convertDBEntryToPostDetail(post)}
            }

            Log.i(wishlist.toString(), "wishlist")

            return wishlist.filter {post -> post.postId == postId}.isNotEmpty()
        }
    }
}