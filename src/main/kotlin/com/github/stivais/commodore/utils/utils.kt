package com.github.stivais.commodore.utils

import com.github.stivais.commodore.Node
import com.mojang.brigadier.ParseResults
import com.mojang.brigadier.tree.LiteralCommandNode


/**
 * Returns the latest [Node] from a string.
 *
 * @return The corresponding node
 */
fun findCorrespondingNode(node: Node, name: String): Node? {
    if (node.children != null) {
        for (child in node.children!!) {
            return findCorrespondingNode(child, name)
        }
    }
    return if (node.name == name) node else null
}

/**
 * Returns the latest [Node] from a [parse result][ParseResults]
 *
 * @return The corresponding node
 */
fun findCorrespondingNode(node: Node, results: ParseResults<Any?>): Node? {
    val last = results.context.nodes.last { it.node is LiteralCommandNode }.node.name
    return findCorrespondingNode(node, last)
}
