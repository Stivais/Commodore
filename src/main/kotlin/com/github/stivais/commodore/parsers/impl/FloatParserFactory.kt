package com.github.stivais.commodore.parsers.impl

import com.github.stivais.commodore.parsers.Parser
import com.github.stivais.commodore.parsers.ParserFactory
import com.mojang.brigadier.StringReader

object FloatParserFactory : ParserFactory<Float> {
    override fun <S> create(name: String): Parser<S, Float> {
        return object : Parser<S, Float>(name, Float::class.java) {
            override fun parse(reader: StringReader): Float {
                return reader.readFloat()
            }
        }
    }
}
