import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmCompilerOptions
import org.jetbrains.kotlin.gradle.dsl.KotlinProjectExtension
import org.jetbrains.kotlin.gradle.plugin.KotlinBasePlugin
import org.jetbrains.kotlin.gradle.tasks.KotlinCompilationTask
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

val jvmTargetVersion = deps.versions.jvmTarget
val buildConfigPluginId = deps.plugins.buildConfig.get().pluginId

plugins {
    alias(deps.plugins.kotlin.jvm) apply false
    alias(deps.plugins.ksp) apply false
    alias(deps.plugins.kmp) apply false
    alias(deps.plugins.buildConfig) apply false
}

subprojects {
    group = project.property("GROUP") as String
    version = project.property("VERSION_NAME") as String

    // Apply the buildConfig plugin to all subprojects
    apply(plugin = buildConfigPluginId)
    // configure the buildConfig plugin to generate the fields we need for both
    // the compiler plugin and the gradle plugin
    extensions.configure<com.github.gmazzo.buildconfig.BuildConfigExtension> {
        packageName.set("de.thermondo.funlin")
        documentation.set("Generated at build time to share between modules")

        buildConfigField<String>("PLUGIN_ID", "funlinPlugin")
        buildConfigField<String>("ENABLED", "enabled")
        buildConfigField<String>("LOGGING", "logging")
        buildConfigField<String>("FUNCTIONS_VISITOR_ENABLED", "functionsVisitorEnabled")
        buildConfigField<String>("FUNCTIONS_VISITOR_ANNOTATION", "functionsVisitorAnnotation")
        buildConfigField<String>("FUNCTIONS_VISITOR_PATH", "functionsVisitorPath")


        buildConfigField<String>("EXTENSION_NAME", "funlin")
        buildConfigField<String>("GROUP_ID", "de.thermondo.funlin")
        buildConfigField<String>("ARTIFACT_ID", "funlin-compiler-plugin")
        buildConfigField<String>("VERSION", "0.0.1")
        buildConfigField<String>("EMPTY_STRING", "")

    }

    pluginManager.withPlugin("java") {
        configure<JavaPluginExtension> {
            toolchain { languageVersion.set(deps.versions.jdk.map(JavaLanguageVersion::of)) }
        }
        tasks.withType<JavaCompile>().configureEach {
            options.release.set(jvmTargetVersion.map(String::toInt))
        }
    }

    plugins.withType<KotlinBasePlugin> {
        project.tasks.withType<KotlinCompilationTask<*>>().configureEach {
            compilerOptions {
                progressiveMode.set(true)
                if (this is KotlinJvmCompilerOptions) {
                    jvmTarget.set(jvmTargetVersion.map(JvmTarget::fromTarget))
                    freeCompilerArgs.addAll("-Xjvm-default=all")
                }
            }
        }

        configure<KotlinProjectExtension> { explicitApi() }
    }
}


