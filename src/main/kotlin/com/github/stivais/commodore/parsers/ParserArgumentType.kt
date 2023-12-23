package com.github.stivais.commodore.parsers

import com.github.stivais.commodore.utils.RequiredBuilder
import com.mojang.brigadier.arguments.ArgumentType
import com.mojang.brigadier.context.CommandContext

abstract class ParserArgumentType<S, T>(private val name: String, private val clazz: Class<T>) : ArgumentType<T> {
    val builder: RequiredBuilder<S> by lazy { RequiredBuilder.argument(name, this) }
    fun getValue(context: CommandContext<*>): T = context.getArgument(name, clazz)
}
