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
    java
    jacoco
    `maven-publish`
    signing
    alias(libs.plugins.kotlin.jvm) apply false
    alias(libs.plugins.dokka) apply false
    alias(libs.plugins.gitversion)
    alias(libs.plugins.versions)
}

allprojects {
    apply(plugin = "com.palantir.git-version")
    apply(plugin = "com.github.ben-manes.versions")

    val gitVersion: groovy.lang.Closure<String> by extra

    group = "net.pelsmaeker"
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
    apply(plugin = "signing")
    apply(plugin = "jacoco")
    apply(plugin = "project-report")

    tasks.test {
        useJUnitPlatform()
    }

    configure<JavaPluginExtension> {
        toolchain.languageVersion.set(JavaLanguageVersion.of(11))
        withSourcesJar()
        withJavadocJar()
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

                pom {
                    name.set("Kode")
                    description.set(project.description)
                    url.set("https://github.com/Virtlink/kode")
                    inceptionYear.set("2022")
                    licenses {
                        // From: https://spdx.org/licenses/
                        license {
                            name.set("MIT")
                            url.set("https://opensource.org/licenses/MIT")
                            distribution.set("repo")
                        }
                    }
                    developers {
                        developer {
                            id.set("Virtlink")
                            name.set("Daniel A. A. Pelsmaeker")
                            email.set("developer@pelsmaeker.net")
                        }
                    }
                    scm {
                        connection.set("scm:git@github.com:Virtlink/kode.git")
                        developerConnection.set("scm:git@github.com:Virtlink/kode.git")
                        url.set("scm:git@github.com:Virtlink/kode.git")
                    }
                }
            }
        }
        repositories {
            maven {
                name = "GitHub"
                url = uri("https://maven.pkg.github.com/Virtlink/kode")
                credentials {
                    username = project.findProperty("gpr.user") as String? ?: System.getenv("GITHUB_ACTOR")
                    password = project.findProperty("gpr.key") as String? ?: System.getenv("GITHUB_TOKEN")
                }
            }
            maven {
                name = "OSSRH"
                url = uri("https://oss.sonatype.org/service/local/staging/deploy/maven2/")
                credentials {
                    username = System.getenv("OSSRH_USERNAME")
                    password = System.getenv("OSSRH_TOKEN")
                }
            }
        }
    }

    signing {
        sign(publishing.publications["library"])
    }
}
