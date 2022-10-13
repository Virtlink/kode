import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.jetbrains.kotlin.gradle.tasks.UsesKotlinJavaToolchain

buildscript {
    repositories {
        mavenCentral()
    }
}

// Temporary fix for IntelliJ issue where `libs` is errored: https://youtrack.jetbrains.com/issue/KTIJ-19369
@Suppress(
    "DSL_SCOPE_VIOLATION",
    "MISSING_DEPENDENCY_CLASS",
    "UNRESOLVED_REFERENCE_WRONG_RECEIVER",
    "FUNCTION_CALL_EXPECTED"
)
plugins {
    jacoco
    `maven-publish`
    java
    alias(libs.plugins.kotlin.jvm) apply false
    alias(libs.plugins.dokka) apply false
    alias(libs.plugins.gitversion)
    alias(libs.plugins.versions)
}

allprojects {
    apply(plugin = "com.palantir.git-version")
    apply(plugin = "com.github.ben-manes.versions")

    val gitVersion: groovy.lang.Closure<String> by extra

    group = "dev.pelsmaeker"
    version = gitVersion()

    repositories {
        google()
        mavenCentral()
    }

    task<DependencyReportTask>("allDependencies") {}
    task<BuildEnvironmentReportTask>("allBuildEnvironment") {}
}

subprojects {
    apply(plugin = "kotlin")
    apply(plugin = "org.jetbrains.dokka")
    apply(plugin = "java-library")
    apply(plugin = "maven-publish")
    apply(plugin = "jacoco")
    apply(plugin = "project-report")

    tasks.test {
        useJUnitPlatform()
    }

    tasks.withType<UsesKotlinJavaToolchain>().configureEach {
        val service = project.extensions.getByType<JavaToolchainService>()
        val customLauncher = service.launcherFor {
            languageVersion.set(JavaLanguageVersion.of("11"))
        }
        kotlinJavaToolchain.toolchain.use(customLauncher)
    }

    tasks.withType<KotlinCompile>().configureEach {
        kotlinOptions {
            freeCompilerArgs += listOf("-opt-in=kotlin.RequiresOptIn", "-Xcontext-receivers")
        }
    }

    publishing {
        publications {
            create<MavenPublication>("library") {
                from(components["java"])
            }
        }
        repositories {
            maven {
                name = "GitHub"
                url = uri("https://maven.pkg.github.com/Virtlink/kode-jvm")

                credentials {
                    username = project.findProperty("gpr.user") as String? ?: System.getenv("USERNAME")
                    password = project.findProperty("gpr.key") as String? ?: System.getenv("TOKEN")
                }
                authentication {
                    create<HttpHeaderAuthentication>("header")
                }
            }
        }
    }
}
