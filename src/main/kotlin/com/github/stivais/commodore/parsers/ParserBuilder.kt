package com.github.stivais.commodore.parsers

import com.github.stivais.commodore.utils.GreedyString
import com.mojang.brigadier.StringReader
import java.util.*

/**
 * ParserBuilder is a class that is used to create [Parsers][ParserArgumentType],
 * which handle invoking the correct parameters for your function
 */
abstract class ParserBuilder<T>(val clazz: Class<T>) {
    /**
     * Parses the input.
     */
    abstract fun parse(reader: StringReader): T

    // im not quite sure what it does in modern versions so ill keep this here just in case it has a use
    open fun examples(): Collection<String> = Collections.emptyList()


    /**
     * Creates the [ParserArgumentType], using the builder's [parse], [examples] functions.
     *
     * @param id - used to identify the parser (usually being the parameter name)
     */
    fun buildParser(id: String): ParserArgumentType<T> = object : ParserArgumentType<T>(id, clazz) {
        init {
            if (suggestions == null && this@ParserBuilder is Suggests) {
                suggestions = { suggests() }
            }
        }

        override fun parse(reader: StringReader): T = this@ParserBuilder.parse(reader)

        override fun getExamples(): Collection<String> = this@ParserBuilder.examples()
    }

    companion object {
        /**
         * Map of custom parsers, if you want to parse your own classes.
         */
        private val parserMap: MutableMap<Class<*>, ParserBuilder<*>> by lazy { mutableMapOf() }

        /**
         * Registers a [parser builder][ParserBuilder] to the [custom parsers map][parserMap]].
         *
         * @param clazz the key for the map
         * @param builder the parser builder
         */
        fun <T> registerParser(clazz: Class<T>, builder: ParserBuilder<T>) {
            parserMap[clazz] = builder
        }

        /**
         * Creates a parser from a list of predefined parsers, or your custom parsers.
         *
         * Existing parsers for these classes: [String], [GreedyString][com.github.stivais.commodore.utils.GreedyString],
         * [Int], [Long], [Float], [Double], [Boolean]
         */
        fun <T> create(id: String, clazz: Class<T>): ParserArgumentType<*> {
            val builder = when (clazz) {
                String::class.java -> StringParser
                GreedyString::class.java -> GreedyStringParser
                java.lang.Integer::class.java, Int::class.java -> IntParser
                java.lang.Long::class.java, Long::class.java -> LongParser
                java.lang.Float::class.java, Float::class.java -> FloatParser
                java.lang.Double::class.java, Double::class.java -> DoubleParser
                java.lang.Boolean::class.java, Boolean::class.java -> BooleanParser
                else -> parserMap[clazz] ?: throw Exception("Parser isn't found")
            }
            return builder.buildParser(id)
        }
    }
}
