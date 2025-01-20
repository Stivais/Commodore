package com.github.stivais.commodore.nodes

/**
 * # Node
 *
 * Base class for all nodes for [Commodore][com.github.stivais.commodore.Commodore].
 *
 * @see LiteralNode
 * @see Executable
 */
abstract class Node {

    /**
     * This node's parent.
     *
     * This will always be a [literal node][LiteralNode], since [Executable] can never have children nodes.
     */
    var parent: LiteralNode? = null

    internal abstract fun build(parent: LiteralNode)
}