package com.github.stivais.commodore.parsers.impl

import com.github.stivais.commodore.parsers.Parser
import com.github.stivais.commodore.parsers.ParserFactory
import com.mojang.brigadier.StringReader

object GreedyStringParserFactory : ParserFactory<GreedyString> {
    override fun <S> create(name: String): Parser<S, GreedyString> {
        return object : Parser<S, GreedyString>(name, GreedyString::class.java) {
            override fun parse(reader: StringReader): GreedyString {
                return GreedyString(reader.remaining)
            }
        }
    }
}

data class GreedyString(val name: String)
