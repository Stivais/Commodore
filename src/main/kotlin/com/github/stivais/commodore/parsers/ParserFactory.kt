@file:Suppress("UNUSED")

package com.github.stivais.commodore.parsers

import com.github.stivais.commodore.parsers.impl.*
import com.mojang.brigadier.StringReader
import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.suggestion.Suggestions
import com.mojang.brigadier.suggestion.SuggestionsBuilder
import java.util.concurrent.CompletableFuture

interface ParserFactory<T> {
    fun <S> create(name: String): Parser<S, T>

    companion object Parsers {

        private val map = mutableMapOf<Class<*>, ParserFactory<*>>(
            Int::class.java to IntParserFactory,
            Long::class.java to LongParserFactory,
            Float::class.java to FloatParserFactory,
            Double::class.java to DoubleParserFactory,
            String::class.java to StringParserFactory,
            GreedyString::class.java to GreedyStringParserFactory,
            Boolean::class.java to BooleanParserFactory
        )

        fun <S> getParser(name: String, clazz: Class<*>): Parser<S, *>? {
            return map[clazz]?.create(name)
        }

        // function to make creating factories a lot cleaner
        fun <T> createFactory(
            clazz: Class<T>,
            parse: (reader: StringReader) -> T
        ) {
            map[clazz] = object : ParserFactory<T> {
                override fun <S> create(name: String): Parser<S, T> {
                    return object : Parser<S, T>(name, clazz) {
                        override fun parse(reader: StringReader): T {
                            return parse(reader)
                        }
                    }
                }
            }
        }

        // function to make creating factories a lot cleaner
        fun <T> createFactory(
            clazz: Class<T>,
            parse: (reader: StringReader) -> T,
            examples: () -> MutableList<String>,
            listSuggestions: (ctx: CommandContext<*>, builder: SuggestionsBuilder) -> CompletableFuture<Suggestions>
        ) {
            map[clazz] = object : ParserFactory<T> {
                override fun <S> create(name: String): Parser<S, T> {
                    return object : Parser<S, T>(name, clazz) {
                        override fun parse(reader: StringReader): T {
                            return parse(reader)
                        }

                        override fun getExamples(): MutableCollection<String> {
                            return examples()
                        }

                        override fun <S : Any?> listSuggestions(
                            context: CommandContext<S>,
                            builder: SuggestionsBuilder
                        ): CompletableFuture<Suggestions> {
                            return listSuggestions(context, builder)
                        }
                    }
                }
            }
        }
    }
}
