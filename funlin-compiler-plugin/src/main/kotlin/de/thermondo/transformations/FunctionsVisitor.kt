package de.thermondo.transformations

import de.thermondo.util.DebugLogger
import de.thermondo.util.buildMapOfParamsCall
import de.thermondo.util.findClass
import de.thermondo.util.findSingleFunction
import de.thermondo.util.getObjectValue
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.backend.common.lower.DeclarationIrBuilder
import org.jetbrains.kotlin.ir.builders.irBlockBody
import org.jetbrains.kotlin.ir.builders.irCall
import org.jetbrains.kotlin.ir.builders.irString
import org.jetbrains.kotlin.ir.declarations.IrFunction
import org.jetbrains.kotlin.ir.symbols.IrClassSymbol
import org.jetbrains.kotlin.ir.symbols.UnsafeDuringIrConstructionAPI
import org.jetbrains.kotlin.ir.util.dumpKotlinLike
import org.jetbrains.kotlin.ir.util.kotlinFqName
import org.jetbrains.kotlin.ir.util.statements


/**
 * This function is responsible for injecting a call to the target function inside the provided object.
 * It will replace the original function body with a new one that calls the target function and then the original body.
 * @param pluginContext: [IrPluginContext] - the plugin context, its used to find the target function and other symbols.
 * @param logger: [DebugLogger] - the logger, its used to log debug messages.
 * @param callableTargetFullPath: [String] - the full path of the target function to call.
 */
internal fun IrFunction.injectCallToTarget(
    pluginContext: IrPluginContext,
    logger: DebugLogger,
    callableTargetFullPath: String,
) {
    val classPath = callableTargetFullPath.substringBeforeLast(".")
    val functionsVisitorObject: IrClassSymbol = pluginContext.findClass(classPath)
    val visitFunctionInsideObject = functionsVisitorObject.findSingleFunction(
        callableTargetFullPath.substringAfterLast(".")
    )
    val functionVisitorObjectInstance = functionsVisitorObject.getObjectValue()
    val functionName = this@injectCallToTarget.name.asString()


    this.body?.let { originalBody ->
        this.body = DeclarationIrBuilder(pluginContext, this.symbol).irBlockBody {
            logger.log("Building new body for $functionName")

            // Calling the provided object function
            +irCall(visitFunctionInsideObject).apply {
                dispatchReceiver = functionVisitorObjectInstance
                putValueArgument(0, irString(functionName))
                putValueArgument(1, irString(parent.kotlinFqName.asString()))
                putValueArgument(2, irString(this.dumpKotlinLike()))
                putValueArgument(3, buildMapOfParamsCall(this@injectCallToTarget))
            }

            logger.log("Added function visitor call for $functionName")

            // Adding the original body statements
            originalBody.statements.forEach { statement ->
                +statement
            }

            logger.log("Added original body statements for $functionName")
        }
    }
}