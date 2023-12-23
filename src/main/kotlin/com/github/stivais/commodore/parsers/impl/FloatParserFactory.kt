package com.github.stivais.commodore.parsers.impl

import com.github.stivais.commodore.parsers.ParserFactory
import com.mojang.brigadier.StringReader

object FloatParserFactory : ParserFactory<Float>(Float::class.java) {
    override fun parse(reader: StringReader): Float {
        return reader.readFloat()
    }
}
