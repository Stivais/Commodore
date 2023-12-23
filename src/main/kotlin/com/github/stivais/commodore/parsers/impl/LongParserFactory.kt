package com.github.stivais.commodore.parsers.impl

import com.github.stivais.commodore.parsers.Parser
import com.github.stivais.commodore.parsers.ParserFactory
import com.mojang.brigadier.StringReader

object LongParserFactory : ParserFactory<Long> {
    override fun <S> create(name: String): Parser<S, Long> {
        return object : Parser<S, Long>(name, Long::class.java) {
            override fun parse(reader: StringReader): Long {
                return reader.readLong()
            }
        }
    }
}
