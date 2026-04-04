// Top-level build file where you can add configuration options common to all sub-projects/modules.
import com.diffplug.gradle.spotless.SpotlessPlugin
import io.gitlab.arturbosch.detekt.DetektPlugin

plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.jetbrains.kotlin.android) apply false
    id("com.google.dagger.hilt.android") version "2.48" apply false
    alias(libs.plugins.ksp) apply false
    alias(libs.plugins.detekt)
    alias(libs.plugins.spotless)
    alias(libs.plugins.navigation.safe.args) apply false
}

configure(subprojects) {
    apply<DetektPlugin>()
    apply<SpotlessPlugin>()
    spotless {
        kotlin {
            target("**/*.kt")
            ktlint("0.48.2")
        }
    }
}