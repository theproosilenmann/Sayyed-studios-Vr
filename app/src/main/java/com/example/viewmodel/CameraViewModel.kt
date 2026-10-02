package com.example.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/**
 * UI State for camera passthrough and VR overlay configuration.
 */
data class CameraUiState(
    val hasPermission: Boolean = false,
    val permissionRequestedCount: Int = 0,
    val isPermanentlyDenied: Boolean = false,
    val isCameraBound: Boolean = false,
    val errorMessage: String? = null,
    val isSettingsDialogOpen: Boolean = false,
    val virtualPlaneOpacity: Float = 0.85f,
    val isTorchActive: Boolean = false
)

/**
 * ViewModel managing camera passthrough state and settings.
 */
class CameraViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(CameraUiState())
    val uiState: StateFlow<CameraUiState> = _uiState.asStateFlow()

    fun updatePermissionState(granted: Boolean, isPermanentlyDenied: Boolean = false) {
        _uiState.update { current ->
            current.copy(
                hasPermission = granted,
                permissionRequestedCount = if (!granted) current.permissionRequestedCount + 1 else current.permissionRequestedCount,
                isPermanentlyDenied = isPermanentlyDenied,
                errorMessage = if (granted) null else current.errorMessage
            )
        }
    }

    fun onCameraBound(success: Boolean) {
        _uiState.update { current ->
            current.copy(isCameraBound = success)
        }
    }

    fun setCameraError(error: String?) {
        _uiState.update { current ->
            current.copy(errorMessage = error, isCameraBound = false)
        }
    }

    fun clearError() {
        _uiState.update { current ->
            current.copy(errorMessage = null)
        }
    }

    fun setSettingsDialogVisible(visible: Boolean) {
        _uiState.update { current ->
            current.copy(isSettingsDialogOpen = visible)
        }
    }

    fun setVirtualPlaneOpacity(opacity: Float) {
        _uiState.update { current ->
            current.copy(virtualPlaneOpacity = opacity.coerceIn(0.1f, 1.0f))
        }
    }

    fun toggleTorch() {
        _uiState.update { current ->
            current.copy(isTorchActive = !current.isTorchActive)
        }
    }
}
