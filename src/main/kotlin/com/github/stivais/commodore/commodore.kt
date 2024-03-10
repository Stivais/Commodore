@file:Suppress("UNUSED")

package com.github.stivais.commodore

import com.github.stivais.commodore.functions.FunctionInvoker
import com.mojang.brigadier.CommandDispatcher

/**
 * Creates the root node for a command.
 *
 * @param name Name of the command.
 * @return The root node for a command.
 */
fun commodore(name: String, block: Node.() -> Unit): Node {
    return Node(name).also {
        it.block()
        it.build()
    }
}

/**
 * Class that acts as the root node for a command.
 */
open class Commodore(private val node: Node) {

    constructor(name: String, block: Node.() -> Unit) : this(Node(name)) {
        node.block()
    }

    /**
     * Copy of [Node.literal] for convince
     *
     * @see [Node.literal]
     */
    fun literal(name: String, block: Node.() -> Unit = {}): Node {
        if (node.children == null) node.children = mutableListOf()
        return Node(name).also {
            it.block()
            node.children?.add(it)
        }
    }

    /**
     * Copy of [Node.runs] for convince
     *
     * @see [Node.runs]
     */
    fun runs(block: Function<Unit>): Executable {
        if (node.executables == null) node.executables = mutableListOf()
        return Executable(node, FunctionInvoker(block)).also { node.executables?.add(it) }
    }

    /**
     * Registers this class to a dispatcher
     */
    fun register(dispatcher: CommandDispatcher<Any?>) {
        node.build()
        dispatcher.register(node.builder)
    }
}
