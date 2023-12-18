package com.github.stivais.commodore.parsers

import com.github.stivais.commodore.parsers.impl.*
import com.github.stivais.commodore.utils.RequiredBuilder
import com.mojang.brigadier.context.CommandContext

/**
 * @param S Command source
 * @param T Type
 */
interface Parser<S, T> {
    val name: String
    val builder: RequiredBuilder<S>
    fun parse(ctx: CommandContext<S>): T

    companion object {
        fun <S> getParser(clazz: Class<*>, name: String): Parser<S, *>? {
            return when (clazz) {
                Int::class.java -> IntParser(name)
                Long::class.java -> LongParser(name)
                Float::class.java -> FloatParser(name)
                Double::class.java -> DoubleParser(name)
                String::class.java -> StringParser(name)
                GreedyString::class.java -> GreedyStringParser(name)
                else -> null
            }
        }
    }
}