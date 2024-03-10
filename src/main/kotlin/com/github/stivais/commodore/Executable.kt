@file:Suppress("UNUSED")

package com.github.stivais.commodore

import com.github.stivais.commodore.functions.FunctionInvoker
import com.github.stivais.commodore.parsers.ParserArgumentType
import com.github.stivais.commodore.parsers.ParserBuilder
import com.mojang.brigadier.Command
import com.mojang.brigadier.context.CommandContext

/**
 * ## Executable
 *
 * The main class that handles custom functions for commands.
 *
 *
 */
class Executable(private val node: Node, private val function: FunctionInvoker) {

    /**
     * Parsers tied to this [function]
     */
    val parsers: MutableList<ParserArgumentType<*>> = mutableListOf()

    /**
     * A [brigadier command][Command], that invokes the [function].
     */
    private val command: Command<Any?> = Command { ctx ->
        function.invoke(getValues(ctx))
        Command.SINGLE_SUCCESS
    }

    init {
        for (param in function.parameters) {
            val parser = ParserBuilder.create(param.name, param.clazz)
            parser.isOptional = param.isNullable
            parsers.lastOrNull()?.let { parser.previous = it }
            parsers.add(parser)
        }
    }

    /**
     * Adds suggestions for a defined parameter.
     *
     * @param param the parameter to add the suggestions
     * @param suggestions the suggestions to add to the parameter
     */
    fun suggests(param: String, suggestions: Collection<String>) {
        val parser = parsers.find { it.id == param } ?: throw Exception("Parameter not found.")
        parser.builder.suggests { _, builder ->
            for (str in suggestions) {
                if (str.startsWith(builder.remaining)) builder.suggest(str)
            }
            builder.buildFuture()
        }
    }

    /**
     * Adds suggestions for a defined parameter.
     *
     * @param pairs An array of pairs of strings and suggestions.
     */
    fun suggests(vararg pairs: Pair<String, Collection<String>>) {
        for (i in pairs) {
            suggests(i.first, i.second)
        }
    }

    /**
     * Adds suggestions for a defined parameter.
     *
     * @param param the parameter to add the suggestions
     * @param block the suggestions to add to the parameter (as a getter)
     */
    fun suggests(param: String, block: () -> Collection<String>) = suggests(param, block())

    /**
     * Gets a list of the parameters names.
     *
     * @return List of strings
     */
    fun argumentsToString(): List<String> {
        return parsers.map { it.id }
    }


    private fun getValues(ctx: CommandContext<Any?>): MutableList<Any?> {
        return MutableList(parsers.size) { index -> parsers[index].getValue(ctx) }
    }

    internal fun build() {
        if (!parsers.last().runs(command)) {
            node.builder.executes(command)
        }
        for (parser in parsers.reversed()) {
            parser.previous?.builder?.then(parser.builder) ?: node.builder.then(parser.builder)
        }
    }
}
