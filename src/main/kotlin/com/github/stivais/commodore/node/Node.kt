@file:Suppress("UNUSED")

package com.github.stivais.commodore.node

import com.github.stivais.commodore.Executable
import com.github.stivais.commodore.functions.FunctionInvoker
import com.mojang.brigadier.Command
import com.mojang.brigadier.builder.LiteralArgumentBuilder

/**
 * ## Command Node
 *
 * This class is used to create simple, branching commands, that automatically handle parsing and tab completions.
 *
 * You can either add more branches to the command or add an "exit-point" where a command is executed.
 *
 * @see runs
 * @see literal
 */
class Node(val name: String): INode {

    /**
     * Used to describe what a node does
     */
    var description: String? = null

    /**
     * Parent of this node.
     */
    var parent: Node? = null

    /**
     * Literal builder
     */
    val builder: LiteralArgumentBuilder<Any?> = LiteralArgumentBuilder.literal<Any?>(name)

    /**
     * Nodes that branch of the current one
     */
    var children: MutableList<Node>? = null

    /**
     * Executables under this node
     */
    var executables: MutableList<Executable>? = null

    /**
     * Sets up the node and its children
     *
     * NOTE: Once you build, you are unable to make any changes.
     */
    fun build() {
        executables?.let {
            for (executable in it) {
                executable.build()
            }
        }
        children?.let {
            for (node in it) {
                node.build()
                builder.then(node.builder)
            }
        }
    }

    override fun literal(name: String, block: Node.() -> Unit): Node {
        if (children == null) children = mutableListOf()
        return Node(name).also {
            it.block()
            it.parent = this
            children?.add(it)
        }
    }

    override fun runs(block: Function<Unit>): Executable {
        if (executables == null) executables = mutableListOf()
        return Executable(this, FunctionInvoker(block)).also { executables?.add(it) }
    }

    override fun runs(block: () -> Unit) {
        builder.executes {
            block()
            Command.SINGLE_SUCCESS
        }
    }

    /** Sets the [description] of the node */
    infix fun description(description: String) {
        this.description = description
    }
}