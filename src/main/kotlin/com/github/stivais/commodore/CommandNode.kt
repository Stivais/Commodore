@file:Suppress("UNUSED")

package com.github.stivais.commodore

import com.github.stivais.commodore.utils.LiteralBuilder

class CommandNode<S>(name: String) {

    val builder: LiteralBuilder<S> = LiteralBuilder.literal(name)

    private val children: MutableList<CommandNode<S>> = mutableListOf()

    fun literal(name: String, block: CommandNode<S>.() -> Unit = {}): CommandNode<S> {
        val cmd = CommandNode<S>(name)
        cmd.block()
        children.add(cmd)
        return cmd
    }

    fun runs(block: () -> Unit) {
        builder.executes {
            block()
            0
        }
    }

    fun runs(block: Function<*>) {
        val executable = Executable<S>(block)
        builder.then(executable.setup())
    }

    fun setup() {
        for (i in children) {
            i.setup()
            builder.then(i.builder)
        }
    }
}
