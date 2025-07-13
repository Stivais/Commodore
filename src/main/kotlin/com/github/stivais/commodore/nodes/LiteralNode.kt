@file:Suppress("UNUSED", "MemberVisibilityCanBePrivate")

package com.github.stivais.commodore.nodes

import com.mojang.brigadier.Command.SINGLE_SUCCESS
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import com.mojang.brigadier.builder.LiteralArgumentBuilder.literal

/**
 * # LiteralNode
 *
 * Represents a node within a [Commodore][com.github.stivais.commodore.Commodore] command tree.
 *
 * A `LiteralNode` can either branch into more nodes that progress down the tree
 * or contain an [Executable], which acts as an exit-point for the command, which executes a function.
 *
 * The input for this node is its [name] (and any defined aliases), and it is used
 * for fixed, non-varying input in a command structure.
 */
open class LiteralNode(val name: String) : Node() {

    constructor(name: String, aliases: List<String>) : this(name) {
        this.aliases = aliases
    }

    /**
     * Aliases for this current node.
     */
    var aliases: List<String> = listOf()

    /**
     * Nodes, which branch from the current one.
     */
    var children: ArrayList<Node> = arrayListOf()

    /**
     * Brigadier builder, representing this node.
     *
     * Used to construct/finalize the command tree and make it usable on top of brigadier.
     */
    val builder: LiteralArgumentBuilder<Any?> = literal<Any?>(name)

    /**
     * Sets up the node and its children
     *
     * NOTE: Once you build, you are unable to make any changes to the command-tree.
     */
    override fun build(parent: LiteralNode) {
        for (node in children) {
            node.build(this)
        }

        parent.builder.then(builder)
        val builtNode = builder.build()

        for (alias in aliases) {
            val aliasBuilder = literal<Any?>(alias).redirect(builtNode)
            parent.builder.then(aliasBuilder)
        }
    }

    /**
     * Creates a new literal node, branching from the current one.
     */
    fun literal(string: String, block: LiteralNode.() -> Unit = {}): LiteralNode {
        val node = LiteralNode(string)
        node.block()
        return addNode(node)
    }

    /**
     * Creates a new literal node, branching from the current one.
     *
     * @param names vararg for the name and aliases of the node.
     */
    fun literal(vararg names: String, block: LiteralNode.() -> Unit = {}): LiteralNode {
        val node = LiteralNode(names[0], names.drop(1))
        node.block()
        return addNode(node)
    }

    /**
     * DSL for [literal] using operator overloading.
     *
     * Example:
     * ```
     * "node" { /* ... */  }
     * // is the same as
     * literal("node") { /* ... */  }
     * ```
     */
    operator fun String.invoke(block: LiteralNode.() -> Unit = {}): LiteralNode {
        return literal(this, block = block)
    }

    /**
     * Creates an [Executable] and apply function directly.
     *
     * This won't allow you to apply [modifiers][com.github.stivais.commodore.nodes.Executable.ParameterModifier]
     * to the function's parameters.
     *
     * @see runs
     */
    fun runs(function: Function<Unit>) = executable {
        runs(function)
    }

    /**
     * Create and adds a [Executable] node to the current literal node.
     *
     * @see Executable
     * @see runs
     */
    fun executable(block: Executable.() -> Unit): Executable {
        val executable = Executable()
        executable.block()
        return addNode(executable)
    }

    /**
     * Faster alternative to [runs], for lambdas which don't have any parameters.
     */
    inline fun runs(crossinline block: () -> Unit) {
        builder.executes {
            block()
            SINGLE_SUCCESS
        }
    }

    private fun <N : Node> addNode(node: N): N {
        children.add(node)
        node.parent = this
        return node
    }
}
