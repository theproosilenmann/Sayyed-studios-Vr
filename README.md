# Sayyed Studios — VR Camera Passthrough Prototype

A native Android application in Kotlin using Jetpack Compose that implements a branded splash screen followed by a live camera passthrough background and floating virtual theater screen for future VR headset experiences (e.g., Google Cardboard and cheap phone VR viewers).

- **App Name:** Sayyed Studios
- **Tagline:** A Sayyed Yuzasif Abbas Application
- **Package Name:** `com.sayyedstudios.passthrough`
- **Minimum SDK:** 24 (Android 7.0 Nougat)
- **Target SDK:** 34 (Android 14)

---

## Architecture & Features

### 1. Branded Splash Screen (`SplashScreen.kt`)
- Full-screen dark background (`#0A0A0A`) with subtle radial gradient.
- Letter-spaced title: **S A Y Y E D   S T U D I O S** with subtle divider and italic tagline (*"A Sayyed Yuzasif Abbas Application"*).
- 500ms fade-in animation, 20dp slide-up translation for tagline, 2.5s duration (`SPLASH_DURATION_MS = 2500L`), followed by a 300ms fade-out transition.
- Pure Jetpack Compose — no camera permissions are requested until after the splash finishes.

### 2. Camera Permission Gate (`PermissionGate.kt`)
- Clean dark rationale screen shown if `android.permission.CAMERA` is not yet granted.
- Material 3 styled rounded action button.
- Handles permanent denials gracefully by providing an **Open Settings** action targeting app details settings.

### 3. Live Camera Passthrough (`PassthroughScreen.kt`)
- Powered by modern **CameraX** (`androidx.camera` 1.5.0).
- Back camera (`CameraSelector.DEFAULT_BACK_CAMERA`) with target resolution `1280x720` or higher.
- `PreviewView` with `ScaleType.FILL_CENTER` filling the entire screen edge-to-edge without letterboxing or black bars.
- Tied to the Compose lifecycle.

### 4. Floating 16:9 Virtual Screen Plane (`VirtualScreenPlane.kt`)
- Centered 16:9 dark gray theater display plane occupying 80% screen width.
- Semi-transparent (~85% opacity) allowing camera passthrough visibility behind it.
- Floating 3D visual depth with drop shadow, subtle border glow, and Canvas reticle guides simulating a ~3-meter virtual projection distance.

### 5. Minimal UI Overlay (`StatusOverlay.kt`)
- Top-left: Small "Passthrough Active" semi-transparent pill with pulsing live camera indicator.
- Top-right: Minimal settings gear opening an interactive prototype sheet with opacity controls and future roadmap details.

---

## How to Install and Test on a Physical Device

### Prerequisites
1. An Android phone running **Android 7.0 (API 24)** or newer.
2. A USB cable for device connection (or wireless ADB debugging enabled).
3. Android Studio (Hedgehog / Iguana / Jellyfish / Ladybug or newer) or standard Gradle CLI.

### Steps

1. **Enable Developer Options & USB Debugging:**
   - On your Android phone, go to **Settings** > **About phone**.
   - Tap **Build number** 7 times until you see *"You are now a developer!"*.
   - Return to **Settings** > **System** > **Developer options** and turn on **USB debugging**.

2. **Connect Device:**
   - Plug the phone into your computer via USB.
   - Accept the *"Allow USB debugging?"* prompt on your phone screen.

3. **Build and Run via Android Studio:**
   - Open the project in Android Studio.
   - Select your connected physical phone in the device dropdown list at the top toolbar.
   - Click the green **Run (▶)** button (or press `Shift + F10`).

4. **Or Build and Install via Gradle Command Line:**
   ```bash
   # Assemble debug APK
   ./gradlew assembleDebug

   # Install directly onto connected physical phone
   adb install -r app/build/outputs/apk/debug/app-debug.apk
   ```

5. **Testing the Prototype Flow:**
   - **First Launch:** Observe the 2.5-second Sayyed Studios branded splash screen.
   - **Permission Gate:** Once the splash completes, the permission gate appears. Tap **Grant Permission** and accept the system camera dialog.
   - **Live Passthrough:** The real-time camera feed instantly appears edge-to-edge across your entire phone screen.
   - **VR Box Insertion:** Place your phone inside a Google Cardboard or VR box headset. The floating 16:9 virtual display sits centered with true perspective over the real-world passthrough feed.
   - **Controls:** Tap the settings gear in the top-right corner to test the virtual plane opacity slider and view prototype specs.
