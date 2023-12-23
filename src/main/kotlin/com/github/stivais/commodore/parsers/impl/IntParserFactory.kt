package com.github.stivais.commodore.parsers.impl

import com.github.stivais.commodore.parsers.ParserFactory
import com.mojang.brigadier.StringReader

object IntParserFactory : ParserFactory<Int>(Int::class.java) {
    override fun parse(reader: StringReader): Int {
        return reader.readInt()
    }
}
