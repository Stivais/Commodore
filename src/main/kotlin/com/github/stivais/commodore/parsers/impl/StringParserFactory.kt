package com.github.stivais.commodore.parsers.impl

import com.github.stivais.commodore.parsers.Parser
import com.github.stivais.commodore.parsers.ParserFactory
import com.mojang.brigadier.StringReader

object StringParserFactory : ParserFactory<String> {
    override fun <S> create(name: String): Parser<S, String> {
        return object : Parser<S, String>(name, String::class.java) {
            override fun parse(reader: StringReader): String {
                return reader.readString()
            }
        }
    }
}

