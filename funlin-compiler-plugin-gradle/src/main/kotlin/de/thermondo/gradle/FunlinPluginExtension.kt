package de.thermondo.gradle

import de.thermondo.funlin.BuildConfig.EMPTY_STRING
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.Property

/**
 * Used inside [FunlinSubPlugin] to create the extension with default values for the setup options for our plugin.
 * This will be equivalent to the following in the build.gradle.kts file:
 * ```kotlin
 * funlin {
 *    enabled.set(false)
 *    ...
 * }
 * ```
 */
public open class FunlinPluginExtension internal constructor(factory: ObjectFactory) {

    /**
     * The options that will be used in the build.gradle.kts file to configure the plugin.
     * The default values are set to false to not apply the plugin by default unless the user enables and configures it.
     */
    public val enabled: Property<Boolean> = factory.property { set(false) }

    /**
     * Whether to enable logging for the plugin or not.
     */
    public val logging: Property<Boolean> = factory.property { set(false) }

    /**
     * The path to the callable function inside an Object-
     * that the plugin will inject a call to inside each visited function with the specified annotation.
     */
    public val callableTargetPath: Property<String> = factory.property { set(EMPTY_STRING) }

    /**
     * The annotation that the plugin will look for on functions, to inject the call to the callable function inside, in the beginning of it's body.
     */
    public val annotation: Property<String> = factory.property { set(EMPTY_STRING) }
}