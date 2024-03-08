@file:Suppress("UNUSED")

package com.github.stivais.commodore

import com.github.stivais.commodore.utils.LiteralBuilder

/**
 * # CommandNode
 *
 * Acts a literal, where you can set it to run a custom function
 *
 */
class CommandNode<S>(private val name: String) {

    val builder: LiteralBuilder<S> = LiteralBuilder.literal(name)

    private var children: MutableList<CommandNode<S>> = mutableListOf()

    private var executables: MutableList<Executable<S>> = mutableListOf()

    fun literal(name: String, block: CommandNode<S>.() -> Unit = {}): CommandNode<S> {
        val cmd = CommandNode<S>(name)
        cmd.block()
        children.add(cmd)
        return cmd
    }

    fun runs(block: () -> Unit): CommandNode<S> {
        builder.executes {
            block()
            0
        }
        return this
    }

    /**
     * Creates
     */
    fun runs(block: Function<Unit>): CommandNode<S> {
        val executable = Executable<S>(block)
        builder.then(executable.setup())
        return this
    }

    fun requires(block: () -> Boolean): CommandNode<S> {
        builder.requires {
            block()
        }
        return this
    }

    fun setup() {
        for (i in children) {
            i.setup()
            builder.then(i.builder)
        }
    }

    override fun toString(): String = "CommandNode(name=$name)"
}
