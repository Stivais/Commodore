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

abstract class ParserFactory<T>(val clazz: Class<T>) {

    fun <S> create(name: String) : AbstractParser<S, T> {
        return object : AbstractParser<S, T>(name, clazz) {
            override fun parse(reader: StringReader): T {
                return this@ParserFactory.parse(reader)
            }

            override fun getExamples(): Collection<String> {
                return this@ParserFactory.getExamples()
            }

            override fun <S> listSuggestions(ctx: CommandContext<S>, builder: SuggestionsBuilder): Suggestions {
                return this@ParserFactory.listSuggestions(ctx, builder)
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

    companion object {
        val parserMap = mutableMapOf<Class<*>, ParserFactory<*>>(
            Int::class.java to IntParserFactory,
            Long::class.java to LongParserFactory,
            Float::class.java to FloatParserFactory,
            Double::class.java to DoubleParserFactory,
            String::class.java to StringParserFactory,
            GreedyString::class.java to GreedyStringParserFactory,
            Boolean::class.java to BooleanParserFactory
        )

        fun <S> get(name: String, clazz: Class<*>): AbstractParser<S, *>? {
            return parserMap[clazz]?.create(name)
        }

        inline fun <reified T> createParser(
            crossinline parse: (reader: StringReader) -> T,
            crossinline examples: () -> Collection<String> = { Collections.emptyList() },
            crossinline suggestions: SuggestionLambda = { _, _ -> BrigadierSuggestions.empty() }
        ): ParserFactory<T> {
            val parser = object : ParserFactory<T>(T::class.java) {
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
            parserMap[T::class.java] = parser
            return parser
        }
    }
}
