@file:Suppress("UNUSED")

package com.github.stivais.commodore

import com.mojang.brigadier.CommandDispatcher

/**
 * @param S Command source
 */
interface Commodore<S> {
    val command: CommandNode<S>

    fun literal(name: String, block: CommandNode<S>.() -> Unit = {}) : CommandNode<S> {
        val cmd = CommandNode<S>(name)
        cmd.block()
        return cmd
    }
}

fun <S> CommandDispatcher<S>.register(command: Commodore<S>) {
    val cmd = command.command
    cmd.setup()
    this.register(cmd.builder)
}