@file:Suppress("UNUSED", "NOTHING_TO_INLINE")

package com.github.stivais.commodore

import com.github.stivais.commodore.utils.mc
import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.exceptions.CommandSyntaxException
import net.minecraft.command.CommandBase
import net.minecraft.command.ICommandSender
import net.minecraft.util.BlockPos
import net.minecraft.util.ChatComponentText
import net.minecraftforge.client.ClientCommandHandler

interface Commodore {
    val command: CommandNode<Any>

    fun literal(name: String, block: CommandNode<Any>.() -> Unit = {}) : CommandNode<Any> {
        val cmd = CommandNode<Any>(name)
        cmd.block()
        return cmd
    }

    companion object {
        fun Commodore.register() {
            val cmd = command
            ClientCommandHandler.instance.registerCommand(
                object : CommandBase() {
                    val dispatcher = CommandDispatcher<Any>()

                    init {
                        cmd.setup()
                        dispatcher.register(cmd.builder)
                    }

                    override fun getCommandName(): String = cmd.name

                    override fun getCommandUsage(sender: ICommandSender?): String = "/${commandName}"

                    override fun getRequiredPermissionLevel(): Int = 0

                    override fun processCommand(sender: ICommandSender, args: Array<out String>) {
                        try {
                            dispatcher.execute(args.fix(), null)
                        } catch (e: CommandSyntaxException) {
                            mc.thePlayer.addChatMessage(ChatComponentText("§c" + (e.message ?: return)))
                        }
                    }

                    override fun addTabCompletionOptions(
                        sender: ICommandSender,
                        args: Array<out String>,
                        pos: BlockPos,
                    ): List<String> {
                        val result = dispatcher.parse(args.fix(), null)
                        return dispatcher.getCompletionSuggestions(result).get().list.map { it.text }
                    }

                    private inline fun Array<out String>.fix(): String {
                        return if (this.isEmpty()) commandName else "$commandName ${this.joinToString(separator = " ")}"
                    }
                }
            )
        }

        fun register(vararg commands: Commodore) {
            for (cmd in commands) {
                cmd.register()
            }
        }
    }
}
