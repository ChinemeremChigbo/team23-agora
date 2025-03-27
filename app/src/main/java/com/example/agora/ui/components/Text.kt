package com.example.agora.ui.components

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import com.example.agora.model.repository.ProfileSettingUtils
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume


suspend fun highlightMentionsText(comment: String, userIds: List<String>, highlightColor: Color): AnnotatedString {
    val usernames = formatUsernames(userIds)
    val annotatedString = buildAnnotatedString {
        var lastIndex = 0
        val regex = Regex(usernames.joinToString("|") { Regex.escape(it) })

        regex.findAll(comment).forEach { matchResult ->
            append(comment.substring(lastIndex, matchResult.range.first))
            withStyle(style = SpanStyle(color = highlightColor)) {
                append(matchResult.value)
            }
            lastIndex = matchResult.range.last + 1
        }
        append(comment.substring(lastIndex))
    }

    return annotatedString
}

private suspend fun formatUsernames(userIds: List<String>): List<String> {
    return userIds.mapNotNull { userId ->
        getUsernameById(userId)?.let { username ->
            "@$username"
        }
    }
}

private suspend fun getUsernameById(userId: String): String? {
    return suspendCancellableCoroutine { continuation ->
        ProfileSettingUtils.getUserById(userId) { user ->
            continuation.resume(user?.username)
        }
    }
}

