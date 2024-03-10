@file:Suppress("UNUSED")

package com.github.stivais.commodore.utils

import com.github.stivais.commodore.Commodore
import com.github.stivais.commodore.Node
import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.exceptions.CommandSyntaxException

/**
 * Useful for implementing Commodore in older versions of minecraft that don't directly support Brigadier
 *
 * Only use this if you're on a version that doesn't have brigadier manually implemented (1.13>)
 */
object LegacyCommodore {

    /**
     * Global dispatcher
     */
    private val dispatcher: CommandDispatcher<Any?> by lazy { CommandDispatcher() }

    /**
     * @see ExceptionHandler
     */
    var exceptionHandler: ExceptionHandler? = null

    /** Registers a commodore class to the dispatcher */
    fun register(commodore: Commodore) {
        commodore.register(dispatcher)
    }

    /** Registers this node to the dispatcher */
    fun register(node: Node) {
        dispatcher.register(node.builder)
    }

    /**
     * Executes a command from a string.
     */
    fun execute(node: Node, arguments: String) {
        val parse = dispatcher.parse(arguments, null)
        try {
            dispatcher.execute(parse)
        } catch (e: CommandSyntaxException) {
            if (exceptionHandler != null) {
                val cause = findCorrespondingNode(node, parse) ?: return
                exceptionHandler!!.handle(cause)
            }
        }
    }

    /**
     * Gets completions from a string.
     */
    fun completions(arguments: String): List<String> {
        val result = dispatcher.parse(arguments, null)
        return dispatcher.getCompletionSuggestions(result).get().list.map { it.text }
    }

    /**
     * Used to handle syntax exceptions caused from executing a command to allow for more descriptive errors.
     */
    fun interface ExceptionHandler {
        fun handle(cause: Node)
    }
}
