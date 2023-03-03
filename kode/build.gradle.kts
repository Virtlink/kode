// Workaround for IntelliJ issue where `libs` is errored: https://youtrack.jetbrains.com/issue/KTIJ-19369
@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    `java-library`
    alias(libs.plugins.kotlin.jvm)
}

dependencies {
    // NOTE: Versions are specified in gradle/libs.versions.toml

    // ASM
    implementation      (libs.asm)
    implementation      (libs.asm.util)

    // Testing
    testImplementation  (libs.kotlin.reflect)
    testImplementation  (libs.kotest)
    testImplementation  (libs.junit)
    testImplementation  (libs.awaitility.core)
    testImplementation  (libs.awaitility.kotlin)
}
