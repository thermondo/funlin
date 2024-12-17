package de.thermondo.util

import org.jetbrains.kotlin.cli.common.messages.CompilerMessageSeverity
import org.jetbrains.kotlin.cli.common.messages.MessageCollector

/**
 * A simple logger that logs debug messages if the debug flag is set to true.
 */
internal class DebugLogger(
    private val debug: Boolean,
    private val messageCollector: MessageCollector
) {
    internal fun log(message: String) {
        if (debug) {
            messageCollector.report(CompilerMessageSeverity.STRONG_WARNING, message)
        }
    }
}