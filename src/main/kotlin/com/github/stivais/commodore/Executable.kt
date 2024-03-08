package com.github.stivais.commodore

import com.github.stivais.commodore.parsers.ParserArgumentType
import com.github.stivais.commodore.parsers.Parser
import com.github.stivais.commodore.utils.RequiredBuilder
import com.mojang.brigadier.Command.SINGLE_SUCCESS
import com.mojang.brigadier.context.CommandContext

class Executable<S>(function: Function<*>) {

    private val function = FunctionInvoker(function)

    private val parsers = mutableListOf<ParserArgumentType<S, *>>()

    fun setup(): RequiredBuilder<S> {
        val params = function.parameters

        var previous: ParserArgumentType<S, *>? = null
        for (param in params) {
            val parser = Parser.create<S>(param.name, param.clazz) ?: throw ParserCreationException("Parser not found")
            parser.optional = param.isNullable
            parser.previous = previous
            previous = parser
            parsers.add(parser)
        }

        parsers.last().runs {
            function.invoke(getValues(it))
            SINGLE_SUCCESS
        }

        for (parser in parsers.reversed()) {
            parser.previous?.builder?.then(parser.builder) ?: return parser.builder
        }

/*        // reverses params
        // if first in list, executes
        for (i in params.size - 1 downTo 0) {
            val parser = Parser.get<S>(params[i].name, params[i].clazz) ?: throw ParserCreationException("Parser not found")
            parsers.add(0, parser)

            if (i == params.size - 1) {
                parser.builder.executes {
                    function.invoke(getValues(it))
                    0
                }
            } else {
                parser.builder.then(parsers[1].builder)
            }

            if (i == 0) return parser.builder
        }*/
        throw ParserCreationException("Couldn't")
    }

    private fun getValues(ctx: CommandContext<S>): MutableList<Any?> { // gets actual values to invoke into function
        return MutableList(parsers.size) { index -> parsers[index].getValue(ctx) }
    }
}

class ParserException : Exception("[Commodore] Failed to parse", null)

class ParserCreationException(msg: String) : Exception("[Commodore] Failed to create parsers: $msg", null)
