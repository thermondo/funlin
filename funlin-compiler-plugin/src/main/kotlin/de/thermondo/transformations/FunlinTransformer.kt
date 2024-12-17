package de.thermondo.transformations

import de.thermondo.util.DebugLogger
import de.thermondo.util.hasAnnotation
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.ir.IrStatement
import org.jetbrains.kotlin.ir.declarations.IrFunction
import org.jetbrains.kotlin.ir.visitors.IrElementTransformerVoid

/*
 * This class is responsible for transforming our codebase functions that are annotated with User provided annotation.
 * It extends the [IrElementTransformerVoid] class which is a visitor for the IR tree.
 * It overrides the [visitFunction] function which is called each time there is a function being visited in the codebase.
 */
internal class FunlinTransformer(
    private val pluginContext: IrPluginContext,
    private val logger: DebugLogger,
    private val annotation: String,
    private val callableTargetFullPath: String
) : IrElementTransformerVoid() {


    /**
     * This will called each time there is a function being visited in the codebase.
     * In the node there can be declarations, expressions, statements, etc.
     * Here we are interested in the function declarations only.
     */
    override fun visitFunction(declaration: IrFunction): IrStatement {

        // Make sure user provided valid annotation and callable target full path and not empty.
        if (callableTargetFullPath.isNotEmpty() && annotation.isNotEmpty()) {
            logger.log("transformFunctionsVisitor: ${declaration.name}")

            /*
             * If the function is annotated with the user provided annotation, we will transform it.
             *  this is a shot and forget operation, the compiler will take care of the rest.
             */
            if (declaration.hasAnnotation(annotation))
                declaration.injectCallToTarget(
                    pluginContext = pluginContext,
                    logger = logger,
                    callableTargetFullPath = callableTargetFullPath
                )

        }

        // we can add more features here if needed, but in this case we are done.
        return super.visitFunction(declaration)
    }
}