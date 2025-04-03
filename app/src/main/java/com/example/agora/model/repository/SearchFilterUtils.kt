package com.example.agora.model.repository

import com.example.agora.model.data.Address
import com.example.agora.model.data.Address.Companion.convertDBEntryToAddress
import com.example.agora.model.data.Category
import com.example.agora.model.data.PostStatus
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

enum class SortOptions(val value: String) {
    NEWEST("Newest"), LOWESTPRICE("Lowest price"), HIGHESTPRICE("Highest price"), DISTANCE(
        "Distance"
    )
}

class SearchFilterUtils {
    companion object {
        val priceFilterOptions = mapOf(
            "UNDER $25" to Pair(null, 24.99),
            "$25 TO $50" to Pair(25.0, 49.99),
            "$50 TO $100" to Pair(50.0, 99.99),
            "$100 TO $200" to Pair(100.0, 199.99),
            "$200 AND ABOVE" to Pair(200.0, null)
        )

        fun getPosts(
            minPrice: Double? = null,
            maxPrice: Double? = null,
            category: Category? = null,
            sortByPrice: Boolean = false,
            priceLowToHi: Boolean = true,
            sortByDistance: Boolean = false,
            selfAddress: Address? = null,
            searchString: String? = null,
            limit: Int = -1,
            callback: (List<Map<String, Any>>) -> Unit
        ) {
            val db = FirebaseFirestore.getInstance()

            var query: Query = db.collection("posts").whereIn(
                "status",
                listOf(PostStatus.ACTIVE.name)
            )

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

             query = query.whereNotEqualTo(
                "status",
                PostStatus.DELETED.value
             )

            if (limit != -1) {
                query = query.limit(limit.toLong())
            }

            query.get().addOnSuccessListener { documents ->
                val resultList = mutableListOf<Map<String, Any>>()
                val formattedSearchString = searchString?.trim()?.lowercase()

                for (document in documents) {
                    val data = document.data
                    val title = (data["title"] as? String)?.trim()?.lowercase()

                    if (formattedSearchString.isNullOrEmpty() || (
                        title != null && title.contains(
                                formattedSearchString
                            )
                        )
                    ) {
                        resultList.add(data)
                    }
                }

                val sortedList = if (sortByDistance && selfAddress != null) {
                    resultList.sortedBy { post ->
                        val addressMap =
                            post["address"] as? Map<*, *> ?: return@sortedBy Double.MAX_VALUE
                        val postAddress = convertDBEntryToAddress(addressMap as Map<String, Any>)
                        if (postAddress != null) {
                            selfAddress.distanceTo(postAddress)
                        } else {
                            Double.MAX_VALUE
                        }
                    }
                } else {
                    resultList
                }

                callback(sortedList)
            }.addOnFailureListener { exception ->
                println("Error getting posts: $exception")
                callback(emptyList())
            }
        }
    }
}
