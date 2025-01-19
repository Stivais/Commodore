package com.github.stivais.commodore.parsers.impl

import com.github.stivais.commodore.parsers.CommandParser
import com.github.stivais.commodore.utils.GreedyString
import com.mojang.brigadier.StringReader

object GreedyStringParser : CommandParser<GreedyString> {
    override fun parse(reader: StringReader): GreedyString {
        val string = reader.remaining
        reader.cursor = reader.totalLength
        return GreedyString(string)
    }
}

object StringParser : CommandParser<String> {
    override fun parse(reader: StringReader): String {
        return reader.readString()
    }
}