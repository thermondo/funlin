import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.dsl.KotlinVersion
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    alias(deps.plugins.kotlin.jvm)
    `java-gradle-plugin`
    alias(deps.plugins.ksp)
}

dependencies {
    compileOnly(deps.kotlin.gradlePlugin)
}

java { toolchain { languageVersion.set(deps.versions.jdk.map(JavaLanguageVersion::of)) } }

tasks.withType<JavaCompile>().configureEach {
    options.release.set(deps.versions.jvmTarget.map(String::toInt))
}

tasks.withType<KotlinCompile>().configureEach {
    compilerOptions {
        jvmTarget.set(deps.versions.jvmTarget.map(JvmTarget::fromTarget))

        // Lower version for Gradle compat
        languageVersion.set(KotlinVersion.KOTLIN_1_8)
        apiVersion.set(org.jetbrains.kotlin.gradle.dsl.KotlinVersion.KOTLIN_1_8)
    }
}

gradlePlugin {
    plugins {
        create("funlinPlugin") {
            id = "de.thermondo.funlin"
            implementationClass = "de.thermondo.gradle.FunlinSubPlugin"
        }
    }
}