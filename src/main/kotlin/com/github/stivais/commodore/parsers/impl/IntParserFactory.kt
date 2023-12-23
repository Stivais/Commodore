package com.github.stivais.commodore.parsers.impl

import com.github.stivais.commodore.parsers.Parser
import com.github.stivais.commodore.parsers.ParserFactory
import com.mojang.brigadier.StringReader

object IntParserFactory : ParserFactory<Int> {
    override fun <S> create(name: String): Parser<S, Int> {
        return object : Parser<S, Int>(name, Int::class.java) {
            override fun parse(reader: StringReader): Int {
                return reader.readInt()
            }
        }
    }
}
