package de.thermondo.plugin

import de.thermondo.funlin.BuildConfig
import org.jetbrains.kotlin.compiler.plugin.CliOption
import org.jetbrains.kotlin.config.CompilerConfigurationKey

/**
 * A class to hold the compiler keys and the cli options for-
 * the FunLine plugin that will be read after passing them using the command line or the gradleSubPlugin
 */
internal object FunLineOptions {
    private const val ANNOTATION_DESCRIPTION: String =
        "the annotation to use for the functions visitor as the name of the annotation class only"

    private const val CALLABLE_TARGET_PATH_DESCRIPTION: String =
        "the fully qualified name of the function to call" +
                " package name + object name + function name"

    private const val ENABLED_DESCRIPTION: String = "whether the plugin is enabled"
    private const val LOGGING_DESCRIPTION: String = "whether the logging is enabled"

    val ENABLED = FunlinOption(
        compilerKey = CompilerConfigurationKey<Boolean>(ENABLED_DESCRIPTION),
        cliOption = CliOption(
            optionName = BuildConfig.ENABLED,
            valueDescription = "<true|false>",
            description = ENABLED_DESCRIPTION
        )
    )

    val LOGGING = FunlinOption(
        compilerKey = CompilerConfigurationKey<Boolean>(LOGGING_DESCRIPTION),
        cliOption = CliOption(
            optionName = BuildConfig.LOGGING,
            valueDescription = "<true|false>",
            description = LOGGING_DESCRIPTION
        )
    )

    val ANNOTATION = FunlinOption(
        compilerKey = CompilerConfigurationKey<String>(ANNOTATION_DESCRIPTION),
        cliOption = CliOption(
            optionName = BuildConfig.ANNOTATION,
            valueDescription = "Your custom annotation",
            description = ANNOTATION_DESCRIPTION
        )
    )

    val CALLABLE_TARGET_PATH = FunlinOption(
        compilerKey = CompilerConfigurationKey<String>(CALLABLE_TARGET_PATH_DESCRIPTION),
        cliOption = CliOption(
            optionName = BuildConfig.CALLABLE_TARGET_PATH,
            valueDescription = "de.thermondo.android.CallableObject.functionName",
            description = CALLABLE_TARGET_PATH_DESCRIPTION
        )
    )

    val cliOptions = listOf(
        ENABLED.cliOption,
        LOGGING.cliOption,
        ANNOTATION.cliOption,
        CALLABLE_TARGET_PATH.cliOption
    )
}

/**
 * A data class to hold the compiler key and the cli option
 */
internal data class FunlinOption<T>(
    val compilerKey: CompilerConfigurationKey<T>,
    val cliOption: CliOption
)