package com.agora.agora.model.util

import android.content.res.Resources
import com.agora.agora.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class FirebaseTestUtil {
    companion object {
        fun configureFirebaseServices(resources: Resources) {
            val LOCALHOST = resources.getString(R.string.localhost)
            val AUTH_PORT = resources.getInteger(R.integer.auth_port)
            val FIRESTORE_PORT = resources.getInteger(R.integer.firestore_port)
            // port might be available: run `lsof -i :<port-number>` to get process PID and `kill -9 <PID>` to kill process
            FirebaseAuth.getInstance().useEmulator(LOCALHOST, AUTH_PORT)
            FirebaseFirestore.getInstance().useEmulator(LOCALHOST, FIRESTORE_PORT)
        }
    }
}
