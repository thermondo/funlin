@file:OptIn(ExperimentalCompilerApi::class)

package de.thermondo.plugin

import com.google.auto.service.AutoService
import de.thermondo.util.DebugLogger
import org.jetbrains.kotlin.backend.common.extensions.IrGenerationExtension
import org.jetbrains.kotlin.cli.common.messages.MessageCollector
import org.jetbrains.kotlin.compiler.plugin.CompilerPluginRegistrar
import org.jetbrains.kotlin.compiler.plugin.ExperimentalCompilerApi
import org.jetbrains.kotlin.config.CommonConfigurationKeys
import org.jetbrains.kotlin.config.CompilerConfiguration

/**
 * This class is responsible for registering the compiler plugin with the compiler.
 * It implements the [CompilerPluginRegistrar] interface.
 * The compiler will call this class when the plugin is loaded.
 * with the passed configuration either from the command line or the gradleSubPlugin.
 */
@AutoService(CompilerPluginRegistrar::class)
public class FunlinComponentRegistrar : CompilerPluginRegistrar() {

    // this indicates that the plugin supports K2 compiler
    override val supportsK2: Boolean get() = true

    override fun ExtensionStorage.registerExtensions(configuration: CompilerConfiguration) {
        if (configuration[FunLineOptions.ENABLED.compilerKey] == false) {
            return
        }

        val messageCollector =
            configuration.get(CommonConfigurationKeys.MESSAGE_COLLECTOR_KEY, MessageCollector.NONE)

        val loggingEnabled = configuration[FunLineOptions.LOGGING.compilerKey] == true

        val annotation =
            configuration.get(FunLineOptions.ANNOTATION.compilerKey).orEmpty()

        val callableTargetFullPath =
            configuration.get(FunLineOptions.CALLABLE_TARGET_PATH.compilerKey).orEmpty()

        // Register the extension with the compiler configurations
        IrGenerationExtension.registerExtension(
            FunlinGenerationExtension(
                debugLogger = DebugLogger(loggingEnabled, messageCollector),
                annotation = annotation,
                callableTargetFullPath = callableTargetFullPath
            )
        )
    }
}
