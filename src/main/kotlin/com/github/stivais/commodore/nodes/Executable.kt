@file:Suppress("UNUSED")

package com.github.stivais.commodore.nodes

import com.github.stivais.commodore.functions.FunctionInvoker
import com.github.stivais.commodore.parsers.CommandParser
import com.github.stivais.commodore.parsers.CommandParser.Companion.getParser
import com.github.stivais.commodore.parsers.ParserArgumentType
import com.github.stivais.commodore.parsers.impl.FunctionParser
import com.mojang.brigadier.Command

/**
 * # Executable
 *
 * This [Node] implementation, acts as an exit point for a command tree.
 *
 * It takes a function's (or lambda's) parameters with reflection,
 * to create [parsers][CommandParser] that are used to read a string.
 *
 * You can define custom parsers for certain parameters using [param] before the function is defined with [runs].
 * For it to build correctly, you must have a function defined with [runs].
 */
class Executable : Node() {

    /**
     * [FunctionInvoker], representing this Executable's function.
     */
    private lateinit var funInvoker: FunctionInvoker<Unit>

    /**
     * List of [ParserArgumentType], which retrieves data to invoke [funInvoker].
     */
    val parsers: ArrayList<ParserArgumentType<*>> = arrayListOf()

    /**
     * Map, where key is the parameter name and value is the [parameter modifier][ParameterModifier].
     *
     * This gets cleared once the function is defined using [runs].
     */
    private var parameterModifiers = mutableMapOf<String, ParameterModifier>()

    /**
     * Defines a function for this executable.
     *
     * This is primarily intended to be used with lambda like:
     * ```kotlin
     *  runs { x: Float, y: Double, z: Int -> println("$x $y $z") }
     * ```
     * however it can be used with a function like:
     * ```kotlin
     *  fun example(x: Float, y: Double, z: Int) {
     *      println("$x $y $z")
     *  }
     *  runs(::example)
     * ```
     *
     * The function's parameters are used as command inputs.
     * The parameter must have a [CommandParser] available.
     *
     * Classes that already have a parser provided: [String], [GreedyString][com.github.stivais.commodore.utils.GreedyString],
     * [Int], [Long], [Float], [Double], [Boolean].
     *
     * To create custom parsers,
     * you can either:
     *
     * - Apply the [CommandParsable][com.github.stivais.commodore.parsers.CommandParsable]
     * annotation to class for it to generate a parser using the parameters of the primary constructor.
     * (All parameters for that class must be also parsable).
     *
     * - Use a [ParameterModifier] to apply a custom parser to a certain parameter.
     * You need to ensure you're parsing for the correct class.
     */
    fun runs(function: Function<Unit>) {
        funInvoker = FunctionInvoker.from(function)

        for (parameter in funInvoker.parameters) {
            val modifier = parameterModifiers[parameter.name]
            val parser = modifier?.customParser ?: getParser(parameter.type)
            if (parser == null) {
                throw IllegalStateException(
                    "No parser found for parameter: ${parameter.name}(type = ${parameter.type}). " +
                    "Consider creating a parser for ${parameter.type} or applying @CommandParsable annotation to that class if possible."
                )
            }
            val argumentType = ParserArgumentType(parameter, parser)
            if (modifier != null) argumentType.suggestionCallback = modifier.suggestionCallback
            parsers.add(argumentType)
        }

        // not used after it is built, so no reason to keep the data.
        parameterModifiers.clear()
    }

    override fun build(parent: LiteralNode) {
        if (!::funInvoker.isInitialized) {
            throw IllegalStateException(
                "Executable for ${parent.name} must have a defined function to work."
            )
        }

        val brigadierCommand = Command<Any?> { ctx ->
            funInvoker.invoke(List(parsers.size) { index ->
                parsers[index].getValue(ctx) }
            )
            // return value is supposed to represent success level of a command.
            // however, im not sure if returning a different actually changes anything.
            Command.SINGLE_SUCCESS
        }

        val allOptional = parsers.reversed().all { parser ->
            parser.builder.executes(brigadierCommand)
            parser.optional()
        }
        if (allOptional) parent.builder.executes(brigadierCommand)

        // has to be done in reverse unfortunately
        for (i in parsers.size - 1 downTo 0) {
            val parser = parsers[i]
            val nextBuilder = if (i == 0) parent.builder else parsers[i - 1].builder
            nextBuilder.then(parser.builder)
        }
    }

    /**
     * Creates and adds a modifier for the parameter with the specified [name].
     */
    fun param(name: String): ParameterModifier {
        val modifier = ParameterModifier()
        parameterModifiers[name] = modifier
        return modifier
    }

    /**
     * Creates and adds a modifier for the parameter with the specified [name].
     */
    inline fun param(name: String, block: ParameterModifier.() -> Unit): ParameterModifier {
        val param = param(name)
        param.block()
        return param
    }

    /**
     * ## ParameterModifier
     *
     * A modifier is used to apply either a custom parser, and or custom suggestions to a parameter.
     *
     * @see Executable
     */
    class ParameterModifier(
        var customParser: CommandParser<*>? = null,
        var suggestionCallback: (() -> Collection<String>)? = null
    ) {
        /**
         * Specifies a custom parser for a parameter.
         *
         * Make sure the value it returns is same as parameter you're applying this parser to.
         */
        fun <T> parser(function: Function<T>) {
            customParser = FunctionParser(function)
        }

        /**
         * Adds a callback to give mutable suggestions for a parameter.
         */
        fun suggests(callback: () -> Collection<String>) {
            suggestionCallback = callback
        }

        /**
         * Adds constant suggestions for a parameter.
         */
        fun suggests(vararg option: String) {
            val list = option.toList()
            suggestionCallback = { list }
        }
    }
}