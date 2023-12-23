package com.github.stivais.commodore.parsers.impl

import com.github.stivais.commodore.parsers.ParserFactory
import com.mojang.brigadier.StringReader

object BooleanParserFactory : ParserFactory<Boolean>(Boolean::class.java) {
    override fun parse(reader: StringReader): Boolean {
        return reader.readBoolean()
    }
}
