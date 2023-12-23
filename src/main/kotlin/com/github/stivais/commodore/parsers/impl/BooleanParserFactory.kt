package com.github.stivais.commodore.parsers.impl

import com.github.stivais.commodore.parsers.Parser
import com.github.stivais.commodore.parsers.ParserFactory
import com.mojang.brigadier.StringReader

object BooleanParserFactory : ParserFactory<Boolean> {
    override fun <S> create(name: String): Parser<S, Boolean> {
        return object : Parser<S, Boolean>(name, Boolean::class.java) {
            override fun parse(reader: StringReader): Boolean {
                return reader.readBoolean()
            }
        }
    }
}
