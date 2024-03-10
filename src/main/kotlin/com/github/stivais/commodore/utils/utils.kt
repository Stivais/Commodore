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
            findCorrespondingNode(child, name)?.let { return it }
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

/**
 * Returns a list of strings for the arguments required to activate a node.
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
 * Returns if a node is a root node
 */
fun Node.isRoot(): Boolean {
    return parent == null
}