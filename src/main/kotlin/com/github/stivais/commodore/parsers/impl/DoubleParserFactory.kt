package com.github.stivais.commodore.parsers.impl

import com.github.stivais.commodore.parsers.Parser
import com.github.stivais.commodore.parsers.ParserFactory
import com.mojang.brigadier.StringReader

object DoubleParserFactory : ParserFactory<Double> {
    override fun <S> create(name: String): Parser<S, Double> {
        return object : Parser<S, Double>(name, Double::class.java) {
            override fun parse(reader: StringReader): Double {
                return reader.readDouble()
            }
        }
    }
}
