package com.github.stivais.commodore.utils

typealias RequiredBuilder<C> = com.mojang.brigadier.builder.RequiredArgumentBuilder<C, *>
typealias LiteralBuilder<C> = com.mojang.brigadier.builder.LiteralArgumentBuilder<C>
typealias BrigadierArgumentType<T> = com.mojang.brigadier.arguments.ArgumentType<T>

typealias BrigadierSuggestions = com.mojang.brigadier.suggestion.Suggestions
typealias Suggestions = java.util.concurrent.CompletableFuture<BrigadierSuggestions>
/**
 * @see com.github.stivais.commodore.parsers.ParserFactory.createParser
 */
typealias SuggestionLambda = (ctx: com.mojang.brigadier.context.CommandContext<*>, builder: com.mojang.brigadier.suggestion.SuggestionsBuilder) -> Suggestions
