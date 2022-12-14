// Temporary fix for IntelliJ issue where `libs` is errored: https://youtrack.jetbrains.com/issue/KTIJ-19369
@Suppress(
    "DSL_SCOPE_VIOLATION",
    "MISSING_DEPENDENCY_CLASS",
    "UNRESOLVED_REFERENCE_WRONG_RECEIVER",
    "FUNCTION_CALL_EXPECTED"
)
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
    testImplementation  (libs.junit)
    testImplementation  (libs.awaitility.core)
    testImplementation  (libs.awaitility.kotlin)
}
