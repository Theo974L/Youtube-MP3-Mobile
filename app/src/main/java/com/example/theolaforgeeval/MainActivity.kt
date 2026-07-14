package com.example.theolaforgeeval

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.navigation.compose.rememberNavController
import com.example.theolaforgeeval.navhost.AppNavHost
import com.example.theolaforgeeval.core.ui.theme.TheoLaforgeEvalTheme
import com.example.theolaforgeeval.core.ui.utils.enableFullScreenMode

class MainActivity : ComponentActivity() {

    // Permission notifications (Android 13+) pour afficher la notification média.
    private val notifPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        enableFullScreenMode()

        requestNotificationPermissionIfNeeded()

        setContent {
            TheoLaforgeEvalTheme {
                val navController = rememberNavController()
                AppNavHost(navController = navController)


            }
        }
    }


    private fun requestNotificationPermissionIfNeeded() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val granted = ContextCompat.checkSelfPermission(
                this, Manifest.permission.POST_NOTIFICATIONS,
            ) == PackageManager.PERMISSION_GRANTED
            if (!granted) {
                notifPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

    override fun onPause() {
        super.onPause()
        Log.d("MainActivity", "App mise en pause")
    }


    override fun onDestroy() {
        super.onDestroy()
        Log.d("MainActivity", "App est detruite")

    }
}