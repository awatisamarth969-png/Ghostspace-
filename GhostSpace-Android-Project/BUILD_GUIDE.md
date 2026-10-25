# GhostSpace — Android Build & Release Engineering Guide

## 1. Prerequisites
- **Android Studio**: Iguana (2023.2.1), Jellyfish (2023.3.1), Koala (2024.1.1), or Ladybug (2024.2.1+)
- **JDK**: Java 17 (bundled inside Android Studio under Settings > Build, Execution, Deployment > Build Tools > Gradle > Gradle JDK)
- **Android SDK**: API Level 34 installed (Tools > SDK Manager)
- **Minimum Target**: Android 6.0 (API 23 Marshmallow)

---

## 2. Opening the Project
1. Extract the downloaded `GhostSpace-Android-Project.zip` archive.
2. In Android Studio, select **File > Open...** and navigate to the extracted directory.
3. Allow Android Studio to complete Gradle sync.
4. If prompted to upgrade Android Gradle Plugin (AGP), you may click **Accept**.

---

## 3. Building Debug APK
To compile and install directly onto a connected physical Android device or emulator:
```bash
# Linux / macOS
./gradlew assembleDebug

# Windows (Command Prompt or PowerShell)
gradlew.bat assembleDebug
```
The generated APK will be located at:
`app/build/outputs/apk/debug/app-debug.apk`

To install directly to a connected USB device with ADB enabled:
```bash
adb install app/build/outputs/apk/debug/app-debug.apk
```

---

## 4. Generating a Secure Production Keystore
For Google Play Store submission and signed production releases, generate an RSA 4096-bit or 2048-bit release keystore:
```bash
keytool -genkey -v -keystore ghostspace-release-key.jks \
    -keyalg RSA -keysize 2048 -validity 10000 \
    -alias ghostspace_key
```
Store `ghostspace-release-key.jks` safely outside of your public source repository.

---

## 5. Configuring Signing in `app/build.gradle.kts`
Add your signing credentials to `app/build.gradle.kts` (or create a `keystore.properties` file):
```kotlin
android {
    signingConfigs {
        create("release") {
            storeFile = file("../ghostspace-release-key.jks")
            storePassword = System.getenv("KEYSTORE_PASSWORD") ?: "your_password"
            keyAlias = "ghostspace_key"
            keyPassword = System.getenv("KEY_PASSWORD") ?: "your_password"
        }
    }

    buildTypes {
        release {
            signingConfig = signingConfigs.getByName("release")
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
}
```

---

## 6. Building Signed Android App Bundle (AAB) for Google Play
Google Play requires `.aab` (Android App Bundle) format:
```bash
# Linux / macOS
./gradlew bundleRelease

# Windows
gradlew.bat bundleRelease
```
The generated bundle will be located at:
`app/build/outputs/bundle/release/app-release.aab`

---

## 7. Android 6.0 (API 23) Compatibility Notes
- **Hardware Keystore**: Android 6.0 introduced the official AndroidKeyStore provider for AES/GCM encryption. `CryptoEngine.kt` takes advantage of this hardware security module.
- **BiometricPrompt**: AndroidX Biometric library transparently falls back to `FingerprintManagerCompat` on Android 6.0–8.1, and uses modern `BiometricPrompt` on Android 9.0 (API 28) and above.
- **Runtime Permissions**: Tested with runtime permission dispatchers for `CAMERA`, `RECORD_AUDIO`, and granular storage handling.
