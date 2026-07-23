// Top-level build file
plugins {
    id("com.android.application") version "8.2.2" apply false
    id("org.jetbrains.kotlin.android") version "1.9.22" apply false
    // Updated to 4.4.2 to fix the dependency mutation crash on Gradle 8.6+
    id("com.google.gms.google-services") version "4.4.2" apply false 
}
