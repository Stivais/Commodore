package com.github.stivais.commodore.parsers.impl

import com.github.stivais.commodore.parsers.ParserFactory
import com.mojang.brigadier.StringReader

object GreedyStringParserFactory : ParserFactory<GreedyString>(GreedyString::class.java) {
    override fun parse(reader: StringReader): GreedyString {
        return GreedyString(reader.remaining)
    }
}

data class GreedyString(val name: String)
