@file:Suppress("UNUSED")

package com.github.stivais.commodore

import com.github.stivais.commodore.Commodore.Companion.setup
import com.github.stivais.commodore.node.INode
import com.github.stivais.commodore.node.Node
import com.github.stivais.commodore.utils.findCorrespondingNode
import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.exceptions.CommandSyntaxException
import net.minecraft.command.CommandBase
import net.minecraft.util.BlockPos
import net.minecraft.util.ChatComponentText
import net.minecraftforge.client.ClientCommandHandler
import java.util.logging.Logger
import net.minecraft.command.ICommandSender as CMDSender

/**
 * Commodore command using [CommandBase] for MC 1.8.9.
 *
 * It is recommended you register all your commands at once with [setup] and provide an exception handler.
 *
 * @param names Names for the command
 */
open class Commodore(
    private vararg val names: String,
    internal val node: Node = Node(names[0])
): CommandBase(), INode by node {

    final override fun getCommandName(): String = names[0]

    final override fun getCommandAliases(): List<String> = names.drop(1)

    final override fun getCommandUsage(sender: CMDSender): String = "/${commandName}"

    final override fun getRequiredPermissionLevel(): Int = 0

    final override fun processCommand(sender: CMDSender, args: Array<out String>) {
        val arguments = args.fix()
        val parse = dispatcher.parse(arguments, null)
        try {
            dispatcher.execute(parse)
        } catch (e: CommandSyntaxException) {
            if (exceptionHandler != null) {
                val cause = findCorrespondingNode(node, parse) ?: return
                exceptionHandler!!.handle(node, cause)
            } else {
                sender.addChatMessage(ChatComponentText("§cCommand failed. ${e.rawMessage}."))
            }
        }
    }

    final override fun addTabCompletionOptions(sender: CMDSender, args: Array<out String>, pos: BlockPos): List<String> {
        val arguments = args.fix()
        val result = dispatcher.parse(arguments, null)
        return dispatcher.getCompletionSuggestions(result).get().list.map { it.text }
    }

    private fun Array<out String>.fix(): String {
        val builder = StringBuilder(commandName)
        if (size != 0) {
            for (i in 0..<size) {
                builder.append(" ")
                builder.append(this[i])
            }
        }
        return builder.toString()
    }

    companion object {

        /** Logger for commodore */
        private val logger: Logger by lazy { Logger.getLogger("Commodore") }

        /**
         * Dispatcher for commodore commands.
         */
        val dispatcher = CommandDispatcher<Any?>()

        /**
         * Handles exceptions caused by wrong input when command is ran.
         */
        var exceptionHandler: ExceptionHandler? = null

        /**
         * Initializes commands and registers them to the [ClientCommandHandler]
         *
         * NOTE: It is highly recommended to create a proper exception handler
         * to provide clarity for when input for a command is invalid
         *
         * @see ExceptionHandler
         * @see Commodore
         */
        fun setup(onException: ExceptionHandler? = null, vararg commodore: Commodore) {
            exceptionHandler = onException
            if (exceptionHandler == null) {
                logger.warning("No exception handler set! It is recommended create one to provide clarity when a command fails.")
            }
            for (cmd in commodore) {
                cmd.node.build()
                dispatcher.register(cmd.node.builder)
                ClientCommandHandler.instance.registerCommand(cmd)
            }
        }
    }
}
