package com.github.stivais.commodore

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
open class Commodore private constructor(val node: Node) {

    constructor(name: String, block: Node.() -> Unit) : this(Node(name)) {
        node.block()
        node.build()
    }

    fun register(dispatcher: CommandDispatcher<Any?>) {
        dispatcher.register(node.builder)
    }
}
