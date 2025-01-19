package com.github.stivais.commodore.parsers.impl

import com.github.stivais.commodore.functions.FunctionInvoker
import com.github.stivais.commodore.parsers.CommandParser
import com.github.stivais.commodore.parsers.CommandParser.Companion.getParser
import com.mojang.brigadier.StringReader

/**
 * ## FunctionParser
 *
 * A parser that uses a provided function to process input parameters and output a value.
 * The function's parameters define the expected input types, and its result is returned
 * after processing the input.
 *
 * Example:
 * ```kotlin
 * // This command expects two float values. If they are the same, it throws an error.
 * // The function outputs a Pair<Float, Float> if valid.
 * parser { x: Float, y: Float ->
 *     if (x == y) throw SyntaxException("Values cannot be equal")
 *     Pair(x, y)
 * }
 * ```
 *
 * Unlike an [Executable][com.github.stivais.commodore.nodes.Executable] function, the parameters cannot be optional.
 */
class FunctionParser<T>(
    function: Function<T>
) : CommandParser<T> {

    /**
     * [FunctionInvoker] which handles correctly invoking the function.
     */
    private val funInvoker = FunctionInvoker.from(function)

    /**
     * List of [command parsers][CommandParser] to handle safely inputting the correct values for the function.
     */
    private val parsers: ArrayList<CommandParser<*>> = arrayListOf()

    init {
        for (parameter in funInvoker.parameters) {
//            if (parameter.isNullable) {
//                println("FunctionParsers do not support optional types")
//            }
            val parser = getParser(parameter.type)
                ?: throw IllegalStateException("No parser found for parameter: ${parameter.name}(type=${parameter.type})")
            parsers.add(parser)
        }
        require(parsers.size != 0) { "You need at least one parameter in the function for this parser." }
    }

    override fun parse(reader: StringReader): T {
        val arguments = mutableListOf<Any?>()
        for (parser in parsers) {
            reader.skipWhitespace()
            arguments.add(parser.parse(reader))
        }
        return funInvoker.invoke(arguments)
    }
}