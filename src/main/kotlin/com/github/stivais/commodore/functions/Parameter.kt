package com.github.stivais.commodore.functions

/**
 * Represents a parameter for a function.
 *
 * @see com.github.stivais.commodore.functions.FunctionInvoker
 */
data class Parameter<T>(val name: String, val type: Class<T>, val isNullable: Boolean)
