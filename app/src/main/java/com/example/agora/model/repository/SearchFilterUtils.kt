package com.example.agora.model.repository

import com.example.agora.model.data.Category
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

enum class SortOptions(val value: String) {
    NEWEST("Newest"), LOWESTPRICE("Lowest price"), HIGHESTPRICE("Highest price")
}

class SearchFilterUtils {
    companion object {
        val priceFilterOptions = mapOf(
            "UNDER $25" to Pair(null, 25),
            "$25 TO $50" to Pair(25, 50),
            "$50 TO $100" to Pair(50, 100),
            "$100 TO $200" to Pair(100, 200),
            "$200 AND ABOVE" to Pair(200, null)
        )

        fun getPosts(
            minPrice: Int? = null,
            maxPrice: Int? = null,
            category: Category? = null,
            sortByPrice: Boolean = false,
            priceLowToHi: Boolean = true,
            searchString: String? = null,
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
                query.orderBy("createdAt", Query.Direction.DESCENDING)
            }

            // TODO: Enable this when all posts have been populated with status
            // query = query.whereNotEqualTo(
            //    "status",
            //    PostStatus.DELETED.name
            //)

            if (limit != -1) {
                query = query.limit(limit.toLong())
            }


            query.get()
                .addOnSuccessListener { documents ->
                    val resultList = mutableListOf<Map<String, Any>>()
                    val formattedSearchString = searchString?.trim()?.lowercase()

                    for (document in documents) {
                        val data = document.data
                        val title = (data["title"] as? String)?.trim()?.lowercase()

                        if (formattedSearchString.isNullOrEmpty() || (title != null && title.contains(formattedSearchString))) {
                            resultList.add(data)
                        }
                    }
                    callback(resultList)
                }
                .addOnFailureListener { exception ->
                    println("Error getting posts: $exception")
                    callback(emptyList())
                }
        }
    }
}