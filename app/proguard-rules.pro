# =============================================================================
# Lexicon — ProGuard / R8 rules
# =============================================================================

# Keep stack-trace metadata for crash reporting (Crashlytics, Play Console)
-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile

# =============================================================================
# Kotlin
# =============================================================================
-keepattributes *Annotation*
-keepattributes Signature, InnerClasses, EnclosingMethod
-keepattributes RuntimeVisibleAnnotations, RuntimeVisibleParameterAnnotations
-keepattributes AnnotationDefault

# Keep Kotlin metadata so reflection-based libs (Gson, Hilt) work correctly
-keep class kotlin.Metadata { *; }
-dontwarn kotlin.**
-keepclassmembers class **$WhenMappings { <fields>; }
-keepclassmembers class kotlin.Lazy { *; }

# =============================================================================
# Coroutines
# =============================================================================
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}
-keepclassmembernames class kotlinx.** { volatile <fields>; }
-dontwarn kotlinx.coroutines.**

# =============================================================================
# Hilt / Dagger
# =============================================================================
-keep class dagger.hilt.** { *; }
-keep class javax.inject.** { *; }
-keep class * extends dagger.hilt.android.internal.managers.ViewComponentManager$FragmentContextWrapper { *; }
-dontwarn dagger.**
-dontwarn javax.annotation.**

# =============================================================================
# Retrofit
# =============================================================================
-keepclassmembers,allowshrinking,allowobfuscation interface * {
    @retrofit2.http.* <methods>;
}
-dontwarn retrofit2.**
-keep class retrofit2.** { *; }

# =============================================================================
# OkHttp
# =============================================================================
-keep class okhttp3.** { *; }
-keep interface okhttp3.** { *; }
-dontwarn okhttp3.**
-dontwarn okio.**

# =============================================================================
# Gson — remote DTOs and domain models used with Gson serialization
# =============================================================================
-keep class com.google.gson.** { *; }
-dontwarn com.google.gson.**

# Keep @SerializedName annotated fields (Gson needs them at runtime)
-keepclassmembers,allowobfuscation class * {
    @com.google.gson.annotations.SerializedName <fields>;
}

# Keep all remote DTOs (Gson deserialises reflectively; @Keep on the class itself
# is a belt-and-suspenders safety net, but explicit rules are cleaner in R8)
-keep class labs.creative.dictornarymvvm.data.remote.model.** { *; }

# Keep domain models (referenced by ViewModel StateFlows, could be serialised)
-keep class labs.creative.dictornarymvvm.domain.model.** { *; }

# Keep Room entities (Room generates adapters referenced by class name at runtime)
-keep class labs.creative.dictornarymvvm.data.local.entity.** { *; }

# =============================================================================
# Room
# =============================================================================
-keep class * extends androidx.room.RoomDatabase { *; }
-dontwarn androidx.room.paging.**

# =============================================================================
# AndroidX / Navigation
# =============================================================================
-keep class androidx.navigation.** { *; }
-keep class androidx.lifecycle.** { *; }
-dontwarn androidx.navigation.**

# =============================================================================
# DataStore
# =============================================================================
-dontwarn androidx.datastore.**

# =============================================================================
# Suppress noisy warnings from transitive dependencies
# =============================================================================
-dontwarn java.lang.invoke.**
-dontwarn org.codehaus.mojo.**