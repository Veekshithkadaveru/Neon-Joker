# Keep line numbers in stack traces
-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile

# Room — keep entity and DAO classes so column names survive shrinking
-keep class app.krafted.neonjoker.data.** { *; }

# ViewModel state data classes passed across composition boundaries
-keep class app.krafted.neonjoker.viewmodel.** { *; }

# Game model classes (Grid, TileMove, etc.)
-keep class app.krafted.neonjoker.game.** { *; }

# Hilt — the Gradle plugin generates keep rules, but retain component entry points
-keep @dagger.hilt.android.HiltAndroidApp class * { *; }
-keep @dagger.hilt.android.AndroidEntryPoint class * { *; }

# Kotlin coroutines
-keepclassmembernames class kotlinx.** {
    volatile <fields>;
}
