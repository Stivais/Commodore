package com.github.stivais.commodore

import com.github.stivais.commodore.parsers.Parser
import com.github.stivais.commodore.utils.RequiredBuilder
import com.mojang.brigadier.context.CommandContext
import java.lang.invoke.MethodHandle
import java.lang.invoke.MethodHandles
import kotlin.reflect.KClass
import kotlin.reflect.jvm.ExperimentalReflectionOnLambdas
import kotlin.reflect.jvm.reflect

class Executable<S>(function: Function<*>) {

    private val function = ReflectedFunction(function)

    private val parsers = mutableListOf<Parser<S, *>>()

    fun setup(): RequiredBuilder<S> {
        val params = function.parameters
        var builder: RequiredBuilder<S>? = null

        for (i in params.size - 1 downTo 0) {
            val parser = Parser.getParser<S>(params[i].clazz, params[i].name) ?: throw ParserCreationException()
            parsers.add(0, parser)

            if (i == params.size - 1) {
                parser.builder.executes {
                    function.invoke(getValues(it))
                    0
                }
            } else {
                parser.builder.then(parsers[1].builder)
            }

            if (i == 0) builder = parser.builder
        }
        return builder ?: throw ParserCreationException()
    }

    private fun getValues(ctx: CommandContext<S>): MutableList<Any> { // gets actual values to invoke into function
        return MutableList(parsers.size) { index ->
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
    val parameters: List<Parameter>
    private val mHandle: MethodHandle

    init {
        try {
            val method = function.javaClass.declaredMethods[1]
            if (!method.isAccessible) method.isAccessible = true
            mHandle = MethodHandles.lookup().unreflect(method).bindTo(function)

            @OptIn(ExperimentalReflectionOnLambdas::class)
            val kFun = function.reflect() ?: throw FunctionCreationException()
            parameters = kFun.parameters.map { Parameter(it.name.toString(), (it.type.classifier as KClass<*>).java) }
        } catch (e: Exception) {
            throw FunctionCreationException(cause = e)
        }
    }

    fun invoke(array: MutableList<Any>) {
        mHandle.invokeWithArguments(array)
    }
}

data class Parameter(val name: String, val clazz: Class<*>)

class FunctionCreationException(cause: Throwable? = null) : Exception("Error creating Commodore Function.", cause)
