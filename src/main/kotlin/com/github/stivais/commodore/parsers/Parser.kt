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
            // reason for both java.lang and the primitive itself, is because when getting the class of a nullable primitive
            // it returns java.lang, but that can't cast to a non-nullable primitive
            java.lang.Integer::class.java to IntParser,
            Int::class.java to IntParser,
            java.lang.Long::class.java to LongParser,
            Long::class.java to LongParser,
            java.lang.Float::class.java to FloatParser,
            Float::class.java to FloatParser,
            java.lang.Double::class.java to DoubleParser,
            Double::class.java to DoubleParser,
            java.lang.Boolean::class.java to BooleanParser,
            Boolean::class.java to BooleanParser,
            String::class.java to StringParser,
            GreedyString::class.java to GreedyStringParser,
        )

        fun <S> create(name: String, clazz: Class<*>): ParserArgumentType<S, *>? {
            return parserMap[clazz]?.create(name)
        }

        fun <T> register(clazz: Class<T>, parser: Parser<T>) {
            parserMap[clazz] = parser
        }

        inline fun <reified T> register(parser: Parser<T>) {
            register(T::class.java, parser)
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
