@file:Suppress("UNUSED")

package com.github.stivais.commodore

import com.github.stivais.commodore.functions.FunctionInvoker
import com.github.stivais.commodore.utils.findCorrespondingNode
import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.exceptions.CommandSyntaxException
import net.minecraft.command.CommandBase
import net.minecraft.util.BlockPos
import net.minecraftforge.client.ClientCommandHandler
import net.minecraft.command.ICommandSender as CMDSender

/**
 * Commodore command using [CommandBase] for MC 1.8.9.
 *
 * @param names Names for the command
 */
open class Commodore(private vararg val names: String): CommandBase() {

    internal val node = Node(names[0])

    /**
     * Copy of [Node.literal] for convince
     *
     * @see [Node.literal]
     */
    fun literal(name: String, block: Node.() -> Unit = {}): Node {
        if (node.children == null) node.children = mutableListOf()
        return Node(name).also {
            it.block()
            it.parent = node
            node.children?.add(it)
        }
    }

    /**
     * Copy of [Node.runs] for convince
     *
     * @see [Node.runs]
     */
    fun runs(block: Function<Unit>): Executable {
        if (node.executables == null) node.executables = mutableListOf()
        return Executable(node, FunctionInvoker(block)).also { node.executables?.add(it) }
    }

    override fun getCommandName(): String = names[0]

    override fun getCommandAliases(): List<String> = names.drop(1)

    override fun getCommandUsage(sender: CMDSender): String = "/${commandName}"

    override fun getRequiredPermissionLevel(): Int = 0

    override fun processCommand(sender: CMDSender, args: Array<out String>) {
        val arguments = args.fix()
        val parse = dispatcher.parse(arguments, null)
        try {
            dispatcher.execute(parse)
        } catch (e: CommandSyntaxException) {
            if (exceptionHandler != null) {
                val cause = findCorrespondingNode(node, parse) ?: return
                exceptionHandler!!.handle(node, cause)
            }
        }
    }

    override fun addTabCompletionOptions(sender: CMDSender, args: Array<out String>, pos: BlockPos): List<String> {
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

        val dispatcher = CommandDispatcher<Any?>()

        var exceptionHandler: ExceptionHandler? = null

        fun registerCommands(vararg commodore: Commodore) {
            for (cmd in commodore) {
                cmd.node.build()
                dispatcher.register(cmd.node.builder)
                ClientCommandHandler.instance.registerCommand(cmd)
            }
        }
    }
}

fun interface ExceptionHandler {
    fun handle(root: Node, cause: Node)
}
