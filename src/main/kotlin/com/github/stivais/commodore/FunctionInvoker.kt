package com.github.stivais.commodore

import java.lang.invoke.MethodHandle
import java.lang.invoke.MethodHandles
import kotlin.reflect.KClass
import kotlin.reflect.jvm.ExperimentalReflectionOnLambdas
import kotlin.reflect.jvm.reflect

class FunctionInvoker(function: Function<*>) {
    val parameters: List<Parameter>

    private val mHandle: MethodHandle

    init {
        try {
            val method = function.javaClass.declaredMethods[1]
            if (!method.isAccessible) method.isAccessible = true
            mHandle = MethodHandles.lookup().unreflect(method).bindTo(function)

            @OptIn(ExperimentalReflectionOnLambdas::class)
            val kFun = function.reflect() ?: throw InvokerCreationException()
            parameters = kFun.parameters.map { Parameter(it.name.toString(), (it.type.classifier as KClass<*>).java) }
        } catch (e: Exception) {
            throw InvokerCreationException(cause = e)
        }
    }

    fun invoke(list: MutableList<Any>) {
        mHandle.invokeWithArguments(list)
    }
}

data class Parameter(val name: String, val clazz: Class<*>)

class InvokerCreationException(cause: Throwable? = null) : Exception("Error creating Commodore Function.", cause)
