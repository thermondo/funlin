@file:OptIn(ExperimentalCompilerApi::class)

package de.thermondo.plugin

import com.google.auto.service.AutoService
import de.thermondo.funlin.BuildConfig.ENABLED
import de.thermondo.funlin.BuildConfig.LOGGING
import de.thermondo.funlin.BuildConfig.CALLABLE_TARGET_PATH
import de.thermondo.funlin.BuildConfig.PLUGIN_ID
import de.thermondo.funlin.BuildConfig.ANNOTATION
import org.jetbrains.kotlin.compiler.plugin.AbstractCliOption
import org.jetbrains.kotlin.compiler.plugin.CliOption
import org.jetbrains.kotlin.compiler.plugin.CommandLineProcessor
import org.jetbrains.kotlin.compiler.plugin.ExperimentalCompilerApi
import org.jetbrains.kotlin.config.CompilerConfiguration

@AutoService(CommandLineProcessor::class)
public class FunlinCommandLineProcessor : CommandLineProcessor {

    /**
     * The id of the compiler plugin, as defined in the [de.thermondo.funlin.BuildConfig] class.
     * see [de.thermondo.funlin.BuildConfig.PLUGIN_ID]
     * Used by the gradleSubPlugin to identify the plugin and load it.
     */
    override val pluginId: String = PLUGIN_ID

    /**
     * The options [FunLineOptions.cliOptions] that the compiler plugin can process.
     */
    override val pluginOptions: Collection<CliOption> = FunLineOptions.cliOptions

    /**
     * Processes the options passed to the compiler plugin from either the command line or the gradleSubPlugin.
     */
    override fun processOption(
        option: AbstractCliOption,
        value: String,
        configuration: CompilerConfiguration
    ): Unit = when (option.optionName) {

        ENABLED -> configuration.put(FunLineOptions.ENABLED.compilerKey, value.toBoolean())
        LOGGING -> configuration.put(FunLineOptions.LOGGING.compilerKey, value.toBoolean())

        CALLABLE_TARGET_PATH -> configuration.put(
            FunLineOptions.CALLABLE_TARGET_PATH.compilerKey,
            value
        )

        ANNOTATION -> configuration.put(FunLineOptions.ANNOTATION.compilerKey, value)

        // If the option is not recognized, we disable the plugin, as it's not configured correctly.
        else -> configuration.put(FunLineOptions.ENABLED.compilerKey, false)
    }
}


