@file:Suppress("UNUSED")

package com.github.stivais.commodore.utils

import com.github.stivais.commodore.Commodore
import com.github.stivais.commodore.node.Node
import com.mojang.brigadier.ParseResults
import com.mojang.brigadier.tree.LiteralCommandNode

/**
 * Gets the latest [Node] from a string.
 *
 * @return The corresponding node
 */
fun findCorrespondingNode(node: Node, name: String): Node? {
    if (node.children != null) {
        for (child in node.children!!) {
            findCorrespondingNode(child, name)?.let { return it }
        }
    }
    return if (node.name == name) node else null
}

/**
 * Gets the latest [Node] from a [parse result][ParseResults]
 *
 * @return The corresponding node
 */
fun findCorrespondingNode(node: Node, results: ParseResults<Any?>): Node? {
    val last = results.context.nodes.last { it.node is LiteralCommandNode }.node.name
    return findCorrespondingNode(node, last)
}

/**
 * @return a list of strings for the arguments required to activate a node.
 */
fun getArgumentsRequired(node: Node): List<String> {
    val mutableList = mutableListOf(node.name)
    var current = node
    while (current.parent != null) {
        current = current.parent!!
        mutableList.add(0, current.name)
    }
    return mutableList
}

/**
 * @return if a node is a root node
 */
fun Node.isRoot(): Boolean {
    return parent == null
}

/**
 * Factory function, used for creating Commodore commands
 */
inline fun commodore(name: String, block: Commodore.() -> Unit) = Commodore(name).also(block)