package com.github.stivais.commodore.parsers

import com.github.stivais.commodore.utils.BrigadierArgumentType
import com.github.stivais.commodore.utils.RequiredBuilder
import com.mojang.brigadier.context.CommandContext

abstract class Parser<S, T>(val name: String, private val clazz: Class<T>) : BrigadierArgumentType<T> {

    lateinit var builder: RequiredBuilder<S>

    fun create(): RequiredBuilder<S> {
        val builder = RequiredBuilder.argument<S, T>(name, this)
        this.builder = builder
        return builder
    }

    fun getValue(context: CommandContext<*>): T {
        return context.getArgument(name, clazz)
    }
}
