package com.github.stivais.commodore

import com.github.stivais.commodore.parsers.Parser
import com.github.stivais.commodore.utils.RequiredBuilder
import com.mojang.brigadier.context.CommandContext
import java.lang.invoke.MethodHandles
import java.lang.reflect.Parameter

class Executable<S>(function: Function<*>) {

    private val function = ReflectedFunction(function)

    private val parsers = mutableListOf<Parser<S, *>>()

    fun setup(): RequiredBuilder<S> {
        val params = function.parameters
        var first: RequiredBuilder<S>? = null

        for (i in params.size - 1 downTo 0) {
            val parser =
                Parser.getParser<S>(params[i].type, params[i].type.simpleName) ?: throw ParserCreationException()
            parsers.add(0, parser)

            if (i == params.size - 1) {
                parser.builder.executes {
                    function.function.apply(getValues(it))
                    0
                }
            } else {
                if (i == 0) {
                    first = parser.builder
                }
                parser.builder.then(parsers[1].builder)
            }
        }
        return first ?: throw ParserCreationException()
    }

    private fun getValues(ctx: CommandContext<S>): Array<Any> { // gets actual values to invoke into function
        return Array(parsers.size) { index ->
            parsers[index].parse(ctx) ?: throw ParserException()
        }
    }
}

class ParserException : Exception("Commodore: Failed to parse", null)

class ParserCreationException : Exception("Commodore: Failed to create parsers", null)

/**
 * Converts a [Function], so its able to be invoked with an array
 */
class ReflectedFunction(function: Function<*>) {
    val function: java.util.function.Function<Array<*>, *>
    val parameters: Array<Parameter>

    init {
        try {
            val method = function.javaClass.declaredMethods[1]
            if (!method.isAccessible) method.isAccessible = true

            val methodHandle = MethodHandles.lookup().unreflect(method).bindTo(function)
            this.function = java.util.function.Function { return@Function methodHandle.invokeWithArguments(*it) }
            this.parameters = method.parameters
        } catch (e: Exception) {
            throw FunctionCreationException(cause = e)
        }
    }
}

class FunctionCreationException(cause: Throwable?) : Exception("Error creating Commodore Function.", cause)