package com.example.agora

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.agora.databinding.ActivityMainBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Uncomment the following line if you want to run
        // against the Firebase Local Emulator Suite (FOR LOCAL TESTING!):
        // configureFirebaseServices()

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.navView

        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_explore,
                R.id.navigation_post,
                R.id.navigation_wishlist,
                R.id.navigation_comments,
                R.id.navigation_settings
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
    }

    private fun configureFirebaseServices() {
        val r = resources
        val LOCALHOST = r.getString(R.string.localhost)
        val AUTH_PORT = r.getInteger(R.integer.auth_port)
        val FIRESTORE_PORT =  r.getInteger(R.integer.firestore_port)
        // port might not be available: run `lsof -i :<port-number>` to get process PID and `kill -9 <PID>` to kill process
        Firebase.auth.useEmulator(LOCALHOST, AUTH_PORT)
        Firebase.firestore.useEmulator(LOCALHOST, FIRESTORE_PORT)
    }
}