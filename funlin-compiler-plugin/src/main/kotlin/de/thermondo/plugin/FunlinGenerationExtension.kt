package de.thermondo.plugin

import de.thermondo.util.DebugLogger
import de.thermondo.transformations.FunlinTransformer
import org.jetbrains.kotlin.backend.common.extensions.IrGenerationExtension
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.ir.declarations.IrModuleFragment

/**
 * Entry point for the compiler plugin.
 * This class is responsible for generating the plugin code.
 * It implements the [IrGenerationExtension] interface.
 */
internal class FunlinGenerationExtension(
    private val debugLogger: DebugLogger,
    private val annotation: String,
    private val callableTargetFullPath: String
) : IrGenerationExtension {

    /**
     * This function is called by the compiler when the plugin is loaded as an Entry Point.
     * It is responsible for registering the transformer with the compiler.
     */
    override fun generate(
        moduleFragment: IrModuleFragment,
        pluginContext: IrPluginContext
    ) {
        // log the annotation and the callable target full path in debug mode.
        debugLogger.log("funlinGenerationExtension: generate called")
        debugLogger.log("user provided annotation: $annotation")
        debugLogger.log("callableTargetFullPath: $callableTargetFullPath")

        // moduleFragment is the root of the IR tree, which means it contains all the code for our codebase.
        // This will traverse the tree and apply our transformer to each node.
        moduleFragment.transform(
            // our code transformer
            transformer = FunlinTransformer(
                pluginContext = pluginContext,
                logger = debugLogger,
                annotation = annotation,
                callableTargetFullPath = callableTargetFullPath
            ),
            // no need to pass any data
            data = null
        )
    }
}