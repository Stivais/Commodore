package com.github.stivais.commodore.parsers

import com.github.stivais.commodore.functions.Parameter
import com.github.stivais.commodore.utils.SyntaxException
import com.mojang.brigadier.StringReader
import com.mojang.brigadier.arguments.ArgumentType
import com.mojang.brigadier.builder.RequiredArgumentBuilder
import com.mojang.brigadier.builder.RequiredArgumentBuilder.argument
import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.exceptions.CommandExceptionType
import com.mojang.brigadier.exceptions.CommandSyntaxException
import com.mojang.brigadier.suggestion.Suggestions
import com.mojang.brigadier.suggestion.SuggestionsBuilder
import java.util.concurrent.CompletableFuture

/**
 * Implementation of [ArgumentType], to integrate into Brigadier.
 *
 * @see CommandParser
 */
class ParserArgumentType<T>(
    private val parameter: Parameter<T>,
    parser: CommandParser<*>
) : ArgumentType<T> {

    /**
     * Parser for this argument type.
     */
    @Suppress("UNCHECKED_CAST")
    private val parser: CommandParser<T> = parser as CommandParser<T>

    /**
     * The [Argument builder][RequiredArgumentBuilder] tied to this class
     */
    val builder: RequiredArgumentBuilder<Any?, T> = argument(parameter.name, this)

    /**
     * Callback for getting suggestions for this parameter.
     */
    var suggestionCallback : (() -> Collection<String>)? = null

    override fun parse(reader: StringReader): T {
        try {
            return parser.parse(reader)
        } catch (e: SyntaxException) {
            throw CommandSyntaxException(
                // not sure what this does, but it is needed
                object : CommandExceptionType {},
                { e.message },
                reader.string,
                reader.cursor
            )
        }
    }

    override fun <S : Any?> listSuggestions(context: CommandContext<S>, builder: SuggestionsBuilder): CompletableFuture<Suggestions> {
        val suggestions = suggestionCallback?.invoke() ?: parser.suggestions()
        for (str in suggestions) {
            if (str.startsWith(builder.remaining)) {
                builder.suggest(str)
            }
        }
        return builder.buildFuture()
    }

    /**
     * Gets the value from the [context][CommandContext]
     *
     * @return the value or null if it failed.
     */
    fun getValue(ctx: CommandContext<Any?>): T? = try {
        println("${parameter.name} ${parameter.type}")
        ctx.getArgument(parameter.name, parameter.type)
    } catch (e: IllegalArgumentException) {
        null
    }

    fun name() = parameter.name

    /**
     * Checks if this parameter is optional,
     * meaning it isn't required for an [Executable][com.github.stivais.commodore.nodes.Executable], to run.
     *
     * A parameter is optional if it is nullable.
     */
    fun optional() = parameter.isNullable
}
