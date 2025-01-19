package com.github.stivais.commodore.functions

import java.lang.invoke.MethodHandle
import java.lang.invoke.MethodHandles
import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import kotlin.reflect.jvm.ExperimentalReflectionOnLambdas
import kotlin.reflect.jvm.reflect

/**
 * # FunctionInvoker
 * 
 * Interface to simplify invoking functions, using Kotlin reflection.
 * 
 * @see LambdaInvoker
 * @see KFunctionInvoker
 */
sealed interface FunctionInvoker<T> {

    /**
     * List of parameters from the function.
     */
    val parameters: List<Parameter<*>>

    /**
     * Invokes the function, with a list of arguments.
     *
     * This can error, due to incorrect input.
     */
    fun invoke(arguments: List<Any?>): T

    /**
     * [FunctionInvoker] implementation, that takes a [KFunction].
     *
     * This implementation is primarily for named functions, or constructors.
     *
     * It is recommended to use [FunctionInvoker.from] instead of directly initializing this class.
     */
    class KFunctionInvoker<T> internal constructor(private val kFun: KFunction<T>) : FunctionInvoker<T> {

        override val parameters: List<Parameter<*>> = kFun.parameters.map {
            Parameter(it.name.toString(), (it.type.classifier as KClass<*>).java, it.type.isMarkedNullable)
        }

        override fun invoke(arguments: List<Any?>): T {
            return kFun.call(*arguments.toTypedArray())
        }
    }

    /**
     * [FunctionInvoker] implementation, that is used for anonymous [functions][Function], like lambdas.
     *
     * This class uses [MethodHandle] to invoke the function.
     *
     * It is recommended to use [FunctionInvoker.from] instead of directly initializing this class.
     */
    class LambdaInvoker<T> internal constructor(lambda: Function<T>) : FunctionInvoker<T> {

        override val parameters: List<Parameter<*>>

        /**
         * Used for invoking the function.
         */
        private val mHandle: MethodHandle

        init {
            try {
                // IMPORTANT!!!
                // due to how k2 compiles lambdas,
                // for this to work you need to use @JvmSerializableLambda,
                // or add "-Xlambdas=class" to freeCompilerArgs.
                val method = lambda.javaClass.declaredMethods[1]
                if (!method.isAccessible) method.isAccessible = true
                mHandle = MethodHandles.lookup().unreflect(method).bindTo(lambda)

                @OptIn(ExperimentalReflectionOnLambdas::class)
                val kFun = lambda.reflect() ?: throw Throwable("[Commodore] Failed to reflect function.")

                parameters = kFun.parameters.map {
                    Parameter(it.name.toString(), (it.type.classifier as KClass<*>).java, it.type.isMarkedNullable)
                }
            } catch (e: Exception) {
                throw Exception("[Commodore] Error creating Function Invoker.", e)
                e.printStackTrace()
            }
        }

        @Suppress("UNCHECKED_CAST")
        override fun invoke(arguments: List<Any?>): T {
            return mHandle.invokeWithArguments(arguments) as T
        }
    }

    companion object {
        /**
         * Interprets if a function is an anonymous function/lambda, or an actual function and returns a suitable invoker.
         */
        fun <T> from(function: Function<T>): FunctionInvoker<T> {
            return when (function) {
                is KFunction<T> -> KFunctionInvoker(function)
                else -> LambdaInvoker(function)
            }
        }
    }
}