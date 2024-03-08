package com.github.stivais.commodore.parsers

import com.mojang.brigadier.Command
import com.mojang.brigadier.arguments.ArgumentType
import com.mojang.brigadier.builder.RequiredArgumentBuilder
import com.mojang.brigadier.context.CommandContext

/**
 * This class simplifies the process of obtaining an arguments value.
 *
 * @param id The id/parameter name of the Argument
 * @param clazz The type of class the parser handles
 *
 * @see ParserBuilder
 */
abstract class ParserArgumentType<T>(val id: String, private val clazz: Class<T>) : ArgumentType<T> {

    /**
     * The [Argument builder][RequiredArgumentBuilder] tied to this class
     */
    val builder: RequiredArgumentBuilder<Any, T> by lazy { RequiredArgumentBuilder.argument(id, this) }

    /**
     * Used to check whether the class is nullable, and is used to create optional arguments
     */
    var isOptional: Boolean = false

    /**
     * Reference to the previous parser of the main function
     *
     * @see runs
     */
    var previous: ParserArgumentType<*>? = null

    /**
     * Gets the value from the [context][CommandContext]
     *
     * @return the value or null if it failed.
     */
    fun getValue(ctx: CommandContext<Any?>): T? = try {
        ctx.getArgument(id, clazz)
    } catch (e: IllegalArgumentException) {
        null
    }

    /**
     * Setups up the [executes][RequiredArgumentBuilder.executes] for [builder].
     *
     * If this class [is optional][isOptional], it will also apply it to the previous argument builder
     *
     * @return false if [previous] is null and this class [is optional][isOptional]
     */
    fun runs(command: Command<Any?>): Boolean {
        builder.executes(command)
        if (isOptional) {
            if (previous == null) {
                return false
            }
            return previous!!.runs(command)
        }
        return true
    }
}
