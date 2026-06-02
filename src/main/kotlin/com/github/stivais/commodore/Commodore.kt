@file:Suppress("UNUSED", "UNCHECKED_CAST")

package com.github.stivais.commodore

import com.github.stivais.commodore.nodes.LiteralNode
import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.builder.LiteralArgumentBuilder.literal

/**
 * # Commodore
 *
 * A multi-version command library that simplifies the creation of command trees.
 * It leverages powerful Kotlin DSL features and reflection to easily build
 * complex command structures, streamlining the use of [Mojang's Brigadier](https://github.com/Mojang/brigadier).
 *
 * The command tree is constructed from a root [LiteralNode], with subsequent branches
 * formed by additional nodes.
 * The tree must always end in an [Executable][com.github.stivais.commodore.nodes.Executable],
 * which serves as an exit point for a command.
 */
open class Commodore(private val root: LiteralNode) {

    constructor(
        vararg name: String,
        block: LiteralNode.() -> Unit
    ) : this(LiteralNode(name[0], name.drop(1))) {
        root.block()
    }

    /**
     * Registers this command to a dispatcher.
     *
     * For versions that support Brigadier (>=1.13), register the command to a dispatcher using:
     * ```kotlin
     * command.register(dispatcher)
     * ```
     *
     * For legacy versions, simply call the function and specify an error callback:
     * ```kotlin
     * command.register { problem: String, cause: LiteralNode ->
     *     println("$problem")
     * }
     * ```
     */
    fun register(dispatcher: CommandDispatcher<*>) {
        for (node in root.children) {
            node.build(root)
        }
        (dispatcher as CommandDispatcher<Any?>).register(root.builder)
        val rootCommand = root.builder.build()
        for (alias in root.aliases) {
            val aliasBuilder = literal<Any?>(alias)
            if (rootCommand.command != null) aliasBuilder.executes(rootCommand.command)
            aliasBuilder.redirect(rootCommand)
            dispatcher.register(aliasBuilder)
        }
    }
}