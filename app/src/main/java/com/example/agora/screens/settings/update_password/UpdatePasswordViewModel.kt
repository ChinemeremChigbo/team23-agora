import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

class UpdatePasswordViewModel : ViewModel() {
    private val auth = FirebaseAuth.getInstance()

    var passwordUpdated = mutableStateOf(false)
    var errorMessage = mutableStateOf<String?>(null)

    fun updatePassword(currentPassword: String, newPassword: String) {
        val user = auth.currentUser

        if (user != null && user.email != null) {
            val credential = EmailAuthProvider.getCredential(user.email!!, currentPassword)

            viewModelScope.launch {
                user.reauthenticate(credential)
                    .addOnSuccessListener {
                        user.updatePassword(newPassword)
                            .addOnSuccessListener {
                                Log.d("UpdatePasswordViewModel", "Password updated successfully")
                                passwordUpdated.value = true
                            }
                            .addOnFailureListener { e ->
                                Log.e(
                                    "UpdatePasswordViewModel",
                                    "Error updating password: ${e.message}"
                                )
                                errorMessage.value = e.message
                            }
                    }
                    .addOnFailureListener { e ->
                        Log.e("UpdatePasswordViewModel", "Reauthentication failed: ${e.message}")
                        errorMessage.value = "Incorrect current password."
                    }
            }
        } else {
            errorMessage.value = "User not logged in."
        }
    }
}
