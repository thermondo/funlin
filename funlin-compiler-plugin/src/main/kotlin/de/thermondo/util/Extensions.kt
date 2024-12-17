package de.thermondo.util

import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.ir.builders.IrBuilderWithScope
import org.jetbrains.kotlin.ir.builders.irCall
import org.jetbrains.kotlin.ir.builders.irGet
import org.jetbrains.kotlin.ir.builders.irString
import org.jetbrains.kotlin.ir.builders.irVararg
import org.jetbrains.kotlin.ir.declarations.IrFunction
import org.jetbrains.kotlin.ir.expressions.IrCall
import org.jetbrains.kotlin.ir.expressions.IrGetObjectValue
import org.jetbrains.kotlin.ir.expressions.impl.IrGetObjectValueImpl
import org.jetbrains.kotlin.ir.symbols.IrClassSymbol
import org.jetbrains.kotlin.ir.symbols.IrSimpleFunctionSymbol
import org.jetbrains.kotlin.ir.symbols.UnsafeDuringIrConstructionAPI
import org.jetbrains.kotlin.ir.types.classFqName
import org.jetbrains.kotlin.ir.types.defaultType
import org.jetbrains.kotlin.ir.util.constructors
import org.jetbrains.kotlin.ir.util.dump
import org.jetbrains.kotlin.ir.util.functions
import org.jetbrains.kotlin.name.ClassId
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.name.Name

/**
 * Checks if the [IrFunction] has the given annotation.
 */
internal fun IrFunction.hasAnnotation(annotation: String) = this.annotations.any {
    it.dump().contains(annotation)
}

/**
 * Checks if the [IrGetObjectValue] is a companion object of the Modifier class.
 * which means it is a Modifier.Companion object being accessed.
 */
internal fun IrGetObjectValue.isCompanionModifier() =
    this.type.classFqName?.asString() == "androidx.compose.ui.Modifier.Companion"

/**
 * Returns an [IrClassSymbol] for the given class full dotted path.
 * classFullDottedPath: String - full dotted path of the class: "com.example.MyClass"
 */
internal fun IrPluginContext.findClass(classFullDottedPath: String): IrClassSymbol =
    this.referenceClass(
        ClassId(
            FqName(classFullDottedPath.substringBeforeLast(".")),
            Name.identifier(classFullDottedPath.substringAfterLast("."))
        )
    ) ?: error("$classFullDottedPath was not found")


/**
 * Returns an [IrGetObjectValue] for the given [IrClassSymbol].
 * returns a Kotlin Object instance.
 */
@OptIn(UnsafeDuringIrConstructionAPI::class)
internal fun IrClassSymbol.getObjectValue(): IrGetObjectValue = IrGetObjectValueImpl(
    this.owner.startOffset,
    this.owner.endOffset,
    this.defaultType,
    this
)

/**
 * Finds a function with the given name inside the class.
 */
@OptIn(UnsafeDuringIrConstructionAPI::class)
internal fun IrClassSymbol?.findSingleFunction(functionName: String): IrSimpleFunctionSymbol =
    this?.functions?.single { it.owner.name.asString() == functionName }
        ?: error("$functionName was not found inside class: ${this?.owner?.name?.asString()}")


/**
 * Builds a call to the provided function with the given arguments.
 */
@OptIn(UnsafeDuringIrConstructionAPI::class)
internal fun IrBuilderWithScope.buildMapOfParamsCall(declaration: IrFunction): IrCall {

    val mapOfFunction = context.irBuiltIns.findFunctions(
        Name.identifier("mapOf"),
        FqName("kotlin.collections")
    ).firstOrNull {
        it.owner.valueParameters.size == 1 &&
                it.owner.valueParameters[0].varargElementType != null
    } ?: error("mapOf(vararg pairs) function not found")

    // this builds a call to the mapOf function with the given arguments as key-value pairs
    return irCall(mapOfFunction).apply {

        val pairClass =
            context.irBuiltIns.findClass(Name.identifier("Pair"), FqName("kotlin"))
                ?: error("Pair class not found")

        val pairConstructor = pairClass.owner.constructors.firstOrNull()
            ?: error("Pair constructor not found")

        val pairType = pairClass.owner.symbol.defaultType

        // Create a Pair for each parameter
        val keyValuePairs = declaration.valueParameters.map { param ->
            irCall(pairConstructor).apply {
                putValueArgument(0, irString(param.name.asString())) // Key
                putValueArgument(1, irGet(param)) // Value
            }
        }

        putValueArgument(
            0,
            // Combine pairs into a vararg
            irVararg(
                pairType,
                keyValuePairs
            )
        )
    }
}
