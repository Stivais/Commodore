package com.github.stivais.commodore

import java.lang.invoke.MethodHandle
import java.lang.invoke.MethodHandles

class FunctionInvoker(function: Function<*>) {
    val parameters: List<Parameter>

    private val mHandle: MethodHandle

    init {
        try {
            val method = function.javaClass.declaredMethods[1]
            if (!method.isAccessible) method.isAccessible = true
            mHandle = MethodHandles.lookup().unreflect(method).bindTo(function)

            parameters = method.parameters.map { Parameter(it.name + "[${it.type.simpleName}]", it.type) }
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
