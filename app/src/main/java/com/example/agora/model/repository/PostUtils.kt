import com.example.agora.model.data.Category
import com.example.agora.model.data.Comment
import com.example.agora.model.data.Post
import com.example.agora.model.data.PostStatus
import com.google.firebase.Timestamp

class SearchFilterUtils {
    companion object {
        fun extractPosts(postMaps: List<Map<String, Any>>): List<Post> {
            return postMaps.map { postMap ->
                Post(
                    postId = postMap["postId"] as? String ?: "",
                    userId = postMap["userId"] as? String ?: "123",
                    status = (postMap["status"] as? String)?.let { PostStatus.valueOf(it) } ?: PostStatus.ACTIVE,
                    createdAt = postMap["createdAt"] as? Timestamp ?: Timestamp.now(),
                    title = postMap["title"] as? String ?: "",
                    description = postMap["description"] as? String ?: "",
                    price = (postMap["price"] as? Number)?.toDouble() ?: 0.0,
                    category = (postMap["category"] as? String)?.let { Category.valueOf(it) } ?: Category.OTHER,
                    images = (postMap["images"] as? List<String>)?.toTypedArray() ?: arrayOf("https://picsum.photos/200"),
                )
            }
        }
    }
}