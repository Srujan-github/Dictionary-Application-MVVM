// Top-level build file where you can add configuration options common to all sub-projects/modules.
import com.diffplug.gradle.spotless.SpotlessPlugin
import io.gitlab.arturbosch.detekt.DetektPlugin
import org.jetbrains.kotlin.builtins.StandardNames.FqNames.target
import org.jetbrains.kotlin.js.translate.context.Namer.kotlin


plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.jetbrains.kotlin.android) apply false
    id("com.google.dagger.hilt.android") version "2.48" apply false
    alias(libs.plugins.detekt)
    alias(libs.plugins.spotless)
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