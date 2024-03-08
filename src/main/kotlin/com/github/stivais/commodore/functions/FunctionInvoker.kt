package com.github.stivais.commodore.functions

import java.lang.invoke.MethodHandle
import java.lang.invoke.MethodHandles
import kotlin.reflect.KClass
import kotlin.reflect.jvm.ExperimentalReflectionOnLambdas
import kotlin.reflect.jvm.reflect

/**
 * A class that handles extracting data and invoking [functions][Function].
 * It is used to provide customizable parameters on a lambda, and makes invoking the function easy.
 */
class FunctionInvoker(function: Function<*>) {

    /**
     * List of parameters from the function
     */
    val parameters: List<Parameter>

    /**
     * Used for invoking the function
     */
    private val mHandle: MethodHandle

    init {
        try {
            val method = function.javaClass.declaredMethods[1]
            if (!method.isAccessible) method.isAccessible = true
            mHandle = MethodHandles.lookup().unreflect(method).bindTo(function)

            @OptIn(ExperimentalReflectionOnLambdas::class)
            val kFun = function.reflect() ?: throw Exception("[Commodore] Failed to reflect function.")
            parameters = kFun.parameters.map {
                Parameter(it.name.toString(), (it.type.classifier as KClass<*>).java, it.type.isMarkedNullable)
            }
        } catch (e: Exception) {
            throw Exception("[Commodore] Error creating Function Invoker.", e.cause)
        }
    }

    fun invoke(list: MutableList<Any?>) {
        mHandle.invokeWithArguments(list)
    }
}
