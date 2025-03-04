package com.example.agora.model.repository

import SearchFilterUtils.Companion.extractPosts
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
            callback: (List<Post>) -> Unit
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

            query.get()
                .addOnSuccessListener { documents ->
                    val resultList = mutableListOf<Map<String, Any>>()
                    for (document in documents) {
                        resultList.add(document.data)
                    }
                    callback(extractPosts(resultList))
                }
                .addOnFailureListener { exception ->
                    println("Error getting posts: $exception")
                    callback(emptyList())
                }
        }
    }
}