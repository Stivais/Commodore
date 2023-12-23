package com.github.stivais.commodore

import com.github.stivais.commodore.parsers.AbstractParser
import com.github.stivais.commodore.parsers.ParserFactory
import com.github.stivais.commodore.utils.RequiredBuilder
import com.mojang.brigadier.context.CommandContext

class Executable<S>(function: Function<*>) {

    private val function = FunctionInvoker(function)

    private val parsers = mutableListOf<AbstractParser<S, *>>()

    fun setup(): RequiredBuilder<S> {
        val params = function.parameters

        for (i in params.size - 1 downTo 0) {
            val parser = ParserFactory.get<S>(params[i].name, params[i].clazz)
                ?: throw ParserCreationException("Parser not found")
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
        }
        throw ParserCreationException("Couldn't")
    }

    private fun getValues(ctx: CommandContext<S>): MutableList<Any> { // gets actual values to invoke into function
        return MutableList(parsers.size) { index ->
            parsers[index].getValue(ctx) ?: throw ParserException()
        }
    }
}

class ParserException : Exception("[Commodore] Failed to parse", null)

class ParserCreationException(msg: String) : Exception("[Commodore] Failed to create parsers: $msg", null)
