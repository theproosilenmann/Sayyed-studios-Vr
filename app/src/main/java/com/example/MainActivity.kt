package com.example

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.example.ui.screens.PassthroughScreen
import com.example.ui.screens.PermissionGate
import com.example.ui.screens.SplashScreen
import com.example.ui.theme.DarkBackground
import com.example.ui.theme.MyApplicationTheme
import com.example.viewmodel.CameraViewModel

/**
 * Screen navigation states for the Sayyed Studios VR passthrough experience.
 */
sealed interface AppScreen {
    data object Splash : AppScreen
    data object PermissionGate : AppScreen
    data object Passthrough : AppScreen
}

/**
 * Single Activity hosting the VR Passthrough app.
 * Adheres strictly to the specified prototype flow:
 * 1. Branded Splash Screen (2.5s)
 * 2. Camera Permission Gate (if not yet granted)
 * 3. Live Edge-to-Edge Camera Passthrough with Floating 16:9 3D Virtual Plane
 */
class MainActivity : ComponentActivity() {

    private val cameraViewModel: CameraViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            MyApplicationTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = DarkBackground
                ) {
                    AppNavigationHost(
                        viewModel = cameraViewModel,
                        hasCameraPermission = { checkCameraPermission() }
                    )
                }
            }
        }
    }

    private fun checkCameraPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED
    }
}

@Composable
fun AppNavigationHost(
    viewModel: CameraViewModel,
    hasCameraPermission: () -> Boolean,
    modifier: Modifier = Modifier
) {
    var currentScreen by remember { mutableStateOf<AppScreen>(AppScreen.Splash) }
    val lifecycleOwner = LocalLifecycleOwner.current

    // Observe lifecycle ON_RESUME to catch permission grants when user returns from Settings
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                if (currentScreen != AppScreen.Splash) {
                    if (hasCameraPermission()) {
                        viewModel.updatePermissionState(granted = true)
                        currentScreen = AppScreen.Passthrough
                    }
                }
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    Crossfade(
        targetState = currentScreen,
        animationSpec = tween(400),
        label = "screen_crossfade",
        modifier = modifier.fillMaxSize()
    ) { screen ->
        when (screen) {
            is AppScreen.Splash -> {
                SplashScreen(
                    onSplashFinished = {
                        // Request permission or enter passthrough ONLY after splash finishes
                        if (hasCameraPermission()) {
                            viewModel.updatePermissionState(granted = true)
                            currentScreen = AppScreen.Passthrough
                        } else {
                            viewModel.updatePermissionState(granted = false)
                            currentScreen = AppScreen.PermissionGate
                        }
                    }
                )
            }

            is AppScreen.PermissionGate -> {
                PermissionGate(
                    onPermissionGranted = {
                        viewModel.updatePermissionState(granted = true)
                        currentScreen = AppScreen.Passthrough
                    }
                )
            }

            is AppScreen.Passthrough -> {
                PassthroughScreen(
                    viewModel = viewModel
                )
            }
        }
    }
}
