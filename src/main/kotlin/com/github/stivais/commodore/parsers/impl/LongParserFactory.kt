package com.github.stivais.commodore.parsers.impl

import com.github.stivais.commodore.parsers.ParserFactory
import com.mojang.brigadier.StringReader

object LongParserFactory : ParserFactory<Long>(Long::class.java) {
    override fun parse(reader: StringReader): Long {
        return reader.readLong()
    }
}
