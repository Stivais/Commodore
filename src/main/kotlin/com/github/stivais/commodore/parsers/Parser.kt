@file:Suppress("UNUSED")

package com.github.stivais.commodore.parsers

import com.github.stivais.commodore.parsers.impl.*
import com.github.stivais.commodore.utils.BrigadierSuggestions
import com.github.stivais.commodore.utils.SuggestionLambda
import com.github.stivais.commodore.utils.Suggestions
import com.mojang.brigadier.StringReader
import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.suggestion.SuggestionsBuilder
import java.util.*

abstract class Parser<T>(val clazz: Class<T>) {

    fun <S> create(name: String) : ParserArgumentType<S, T> {
        return object : ParserArgumentType<S, T>(name, clazz) {
            override fun parse(reader: StringReader): T {
                return this@Parser.parse(reader)
            }

            override fun getExamples(): Collection<String> {
                return this@Parser.getExamples()
            }

            override fun <S> listSuggestions(ctx: CommandContext<S>, builder: SuggestionsBuilder): Suggestions {
                return this@Parser.listSuggestions(ctx, builder)
            }
        }
    }

    abstract fun parse(reader: StringReader): T

    open fun getExamples(): Collection<String> {
        return Collections.emptyList()
    }

    open fun <S> listSuggestions(ctx: CommandContext<S>, builder: SuggestionsBuilder): Suggestions {
        return BrigadierSuggestions.empty()
    }

    final override fun toString(): String = "Parser(class=${clazz.simpleName})"

    companion object {
        private val parserMap = mutableMapOf<Class<*>, Parser<*>>(
            Int::class.java to IntParser,
            Long::class.java to LongParser,
            Float::class.java to FloatParser,
            Double::class.java to DoubleParser,
            String::class.java to StringParser,
            GreedyString::class.java to GreedyStringParser,
            Boolean::class.java to BooleanParser
        )

        fun <S> get(name: String, clazz: Class<*>): ParserArgumentType<S, *>? {
            return parserMap[clazz]?.create(name)
        }

        fun <T> register(clazz: Class<T>, parser: Parser<T>) {
            parserMap[clazz] = parser
        }

        inline fun <reified T> createParser(
            crossinline parse: (reader: StringReader) -> T,
            crossinline examples: () -> Collection<String> = { Collections.emptyList() },
            crossinline suggestions: SuggestionLambda = { _, _ -> BrigadierSuggestions.empty() }
        ): Parser<T> {
            val parser = object : Parser<T>(T::class.java) {
                override fun parse(reader: StringReader): T {
                    return parse(reader)
                }

                override fun getExamples(): Collection<String> {
                    return examples()
                }

                override fun <S> listSuggestions(ctx: CommandContext<S>, builder: SuggestionsBuilder): Suggestions {
                    return suggestions(ctx, builder)
                }
            }
            register(T::class.java, parser)
            return parser
        }
    }
}
