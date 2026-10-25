# GhostSpace ProGuard Rules for Production Release
-keepattributes *Annotation*
-keepattributes Signature
-keepattributes SourceFile,LineNumberTable

# Keep Cryptography & Keystore models
-keep class androidx.security.crypto.** { *; }
-keep class com.ghostspace.privacy.security.** { *; }
-keep class com.ghostspace.privacy.data.db.** { *; }

# Keep Room database & DAO
-keep class androidx.room.** { *; }
-dontwarn androidx.room.**

# Keep Media3 ExoPlayer
-keep class androidx.media3.** { *; }
-dontwarn androidx.media3.**

# Keep Compose
-keepclassmembers class * {
    @androidx.compose.runtime.Composable *;
}
