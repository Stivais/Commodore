package com.github.stivais.commodore.parsers

import com.github.stivais.commodore.utils.BrigadierArgumentType
import com.github.stivais.commodore.utils.RequiredBuilder
import com.mojang.brigadier.context.CommandContext

abstract class AbstractParser<S, T>(private val name: String, private val clazz: Class<T>) : BrigadierArgumentType<T> {

    val builder: RequiredBuilder<S> by lazy { RequiredBuilder.argument(name, this) }

    fun getValue(context: CommandContext<*>): T {
        return context.getArgument(name, clazz)
    }
}
