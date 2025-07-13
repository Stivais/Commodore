@file:Suppress("UNUSED", "UNCHECKED_CAST")

package com.github.stivais.commodore

import com.github.stivais.commodore.nodes.LiteralNode
import com.github.stivais.commodore.utils.findCorrespondingNode
import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.builder.LiteralArgumentBuilder.literal
import com.mojang.brigadier.exceptions.CommandSyntaxException

//#if LEGACY
import net.minecraft.command.CommandBase
import net.minecraft.command.ICommandSender
import net.minecraft.util.BlockPos
import net.minecraftforge.client.ClientCommandHandler
//#endif

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

    //#if LEGACY
    /**
     * [CommandBase] instance for this command.
     * This handles executing a commodore command in Minecraft versions,
     * that do not support Brigadier (versions <= 1.12.2).
     */
    val commandBase: CommandBase = object : CommandBase() {

        override fun getCommandName() = root.name

        override fun getCommandAliases() = root.aliases

        override fun getCommandUsage(sender: ICommandSender?) = "/$commandName"

        override fun getRequiredPermissionLevel() = 0

        override fun processCommand(sender: ICommandSender?, args: Array<out String>) {
            val str: String = if (args.isEmpty()) root.name else "${root.name} ${args.joinToString(" ")}"
            val parse = dispatcher.parse(str, null)
            try {
                dispatcher.execute(parse)
            } catch (e : CommandSyntaxException) {
                val cause = findCorrespondingNode(root, parse) ?: return
                errorCallback.invoke(e.localizedMessage, cause)
            }
        }

        override fun addTabCompletionOptions(
            sender: ICommandSender,
            args: Array<out String>,
            pos: BlockPos
        ): List<String> {
            val str: String = if (args.isEmpty()) root.name else "${root.name} ${args.joinToString(" ")}"
            val result = dispatcher.parse(str, null)
            return dispatcher.getCompletionSuggestions(result).get().list.map { it.text }
        }
    }

    /**
     * Error callback used when a Commodore command fails to parse correctly.
     *
     * This is necessary due to the lack of proper error handling in legacy Minecraft versions.
     */
    lateinit var errorCallback: (problem: String, cause: LiteralNode) -> Unit
    //#endif

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
    fun register(
        //#if !LEGACY
        //$$dispatcher: CommandDispatcher<*>,
        //#else
        errorCallback: (problem: String, cause: LiteralNode) -> Unit
        //#endif
    ) {
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
        //#if LEGACY
        ClientCommandHandler.instance.registerCommand(commandBase)
        this.errorCallback = errorCallback
        //#endif
    }

    //#if LEGACY
    companion object {
        /**
         * Dispatcher used to execute a command from [commandBase].
         */
        val dispatcher = CommandDispatcher<Any?>()
    }
    //#endif
}