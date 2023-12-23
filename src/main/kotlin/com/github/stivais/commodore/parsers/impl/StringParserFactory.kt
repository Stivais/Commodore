package com.github.stivais.commodore.parsers.impl

import com.github.stivais.commodore.parsers.ParserFactory
import com.mojang.brigadier.StringReader

object StringParserFactory : ParserFactory<String>(String::class.java) {
    override fun parse(reader: StringReader): String {
        return reader.readString()
    }
}
