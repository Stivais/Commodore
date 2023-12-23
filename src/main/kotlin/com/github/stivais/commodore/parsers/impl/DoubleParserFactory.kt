package com.github.stivais.commodore.parsers.impl

import com.github.stivais.commodore.parsers.ParserFactory
import com.mojang.brigadier.StringReader

object DoubleParserFactory : ParserFactory<Double>(Double::class.java) {
    override fun parse(reader: StringReader): Double {
        return reader.readDouble()
    }
}
