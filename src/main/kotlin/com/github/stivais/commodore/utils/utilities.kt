package com.github.stivais.commodore.utils

import com.github.stivais.commodore.nodes.LiteralNode
import com.mojang.brigadier.ParseResults
import com.mojang.brigadier.tree.LiteralCommandNode

/**
 * Returns the latest [LiteralNode] from a string.
 *
 * @return The corresponding node
 */
fun findCorrespondingNode(node: LiteralNode, name: String): LiteralNode? {
    for (child in node.children) {
        if (child !is LiteralNode) continue
        findCorrespondingNode(child, name)?.let { return it }
    }
    return if (node.name == name) node else null
}

/**
 * Returns the latest [LiteralNode] from a [parse result][ParseResults]
 *
 * @return The corresponding node
 */
fun findCorrespondingNode(node: LiteralNode, results: ParseResults<Any?>): LiteralNode? {
    val last = results.context.nodes.last { it.node is LiteralCommandNode }.node.name
    return findCorrespondingNode(node, last)
}

/**
 * Returns a list of strings for the arguments required to activate a node.
 */
fun getArgumentsRequired(node: LiteralNode): List<String> {
    val mutableList = mutableListOf(node.name)
    var current = node
    while (current.parent != null) {
        current = current.parent!!
        mutableList.add(0, current.name)
    }
    return mutableList
}

fun getRootNode(from: LiteralNode): LiteralNode {
    var current = from
    while (current.parent != null) {
        current = current.parent!!
    }
    return current
}

/**
 * Returns if a node is a root node
 */
fun LiteralNode.isRoot(): Boolean {
    return parent == null
}