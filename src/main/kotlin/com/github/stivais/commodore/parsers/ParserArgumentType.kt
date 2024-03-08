package com.github.stivais.commodore.parsers

import com.github.stivais.commodore.utils.RequiredBuilder
import com.mojang.brigadier.Command
import com.mojang.brigadier.arguments.ArgumentType
import com.mojang.brigadier.context.CommandContext

abstract class ParserArgumentType<S, T>(private val name: String, private val clazz: Class<T>) : ArgumentType<T> {

    var previous: ParserArgumentType<S, *>? = null

    var optional = false

    val builder: RequiredBuilder<S> by lazy { RequiredBuilder.argument(name, this) }

    fun runs(cmd: Command<S>) {
        builder.executes(cmd)
        if (optional) previous?.runs(cmd)
    }

    fun getValue(context: CommandContext<*>): T? = try {
        context.getArgument(name, clazz)
    } catch (e: IllegalArgumentException) {
        null
    }
}
