package com.github.stivais.commodore.functions

/**
 * Represents a function parameter. It provides the name and class and whether it is nullable.
 *
 * @see com.github.stivais.commodore.Executable
 */
data class Parameter(val name: String, val clazz: Class<*>, val isNullable: Boolean)
