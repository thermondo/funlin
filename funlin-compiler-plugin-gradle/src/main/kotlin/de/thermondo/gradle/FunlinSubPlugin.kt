package de.thermondo.gradle


import de.thermondo.funlin.BuildConfig.ANNOTATION
import de.thermondo.funlin.BuildConfig.ARTIFACT_ID
import de.thermondo.funlin.BuildConfig.CALLABLE_TARGET_PATH
import de.thermondo.funlin.BuildConfig.ENABLED
import de.thermondo.funlin.BuildConfig.EXTENSION_NAME
import de.thermondo.funlin.BuildConfig.GROUP_ID
import de.thermondo.funlin.BuildConfig.LOGGING
import de.thermondo.funlin.BuildConfig.PLUGIN_ID
import de.thermondo.funlin.BuildConfig.VERSION
import org.gradle.api.Project
import org.gradle.api.provider.Provider
import org.jetbrains.kotlin.gradle.plugin.KotlinCompilation
import org.jetbrains.kotlin.gradle.plugin.KotlinCompilerPluginSupportPlugin
import org.jetbrains.kotlin.gradle.plugin.SubpluginArtifact
import org.jetbrains.kotlin.gradle.plugin.SubpluginOption

/**
 * The main entry point for the compiler gradle plugin
 * This class is responsible for setting up the plugin and applying it to the project
 * as well as providing the compiler plugin id and the artifact id and passing the options to the compiler plugin.
 */
public class FunlinSubPlugin : KotlinCompilerPluginSupportPlugin {

    /**
     * The name of the extension that will be used in the build.gradle.kts file
     * see [FunlinPluginExtension]
     */
    override fun apply(target: Project) {
        target.extensions.create(EXTENSION_NAME, FunlinPluginExtension::class.java)
    }

    /**
     * The compiler plugin id that this gradleSubPlugin will apply to our projects.
     */
    override fun getCompilerPluginId(): String = PLUGIN_ID

    /**
     * This method is responsible for checking if the plugin is applicable to the current project,
     * in our case it is always applicable, but we can add some checks here if needed.
     * for example to check if we have other plugins applied or not that we would be depending on.
     */
    override fun isApplicable(kotlinCompilation: KotlinCompilation<*>): Boolean = true

    /**
     * This method is responsible for providing the artifact id of the compiler plugin-
     * in case had it locally published with more than one version.
     */
    override fun getPluginArtifact(): SubpluginArtifact = SubpluginArtifact(
        groupId = GROUP_ID,
        artifactId = ARTIFACT_ID,
        version = VERSION
    )

    /**
     * This method is responsible for passing the options from the extension in build.gradle.kts (gradle setup) to the compiler plugin
     */
    override fun applyToCompilation(kotlinCompilation: KotlinCompilation<*>): Provider<List<SubpluginOption>> {
        val project = kotlinCompilation.target.project
        val extension = project.extensions.getByType(FunlinPluginExtension::class.java)
            ?: return kotlinCompilation.target.project.provider { emptyList() }

        val enabled = extension.enabled.get().toString()
        val logging = extension.logging.get().toString()

        val annotation = extension.annotation.get()
        val callableTargetFullPath = extension.callableTargetPath.get()


        // This will share the options with the compiler plugin
        return kotlinCompilation.target.project.provider {
            mutableListOf(
                SubpluginOption(ENABLED, enabled),
                SubpluginOption(LOGGING, logging),
                SubpluginOption(ANNOTATION, annotation),
                SubpluginOption(CALLABLE_TARGET_PATH, callableTargetFullPath),
            )
        }
    }

}