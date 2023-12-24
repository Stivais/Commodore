package com.github.stivais.commodore.parsers.impl

import com.github.stivais.commodore.parsers.Parser
import com.mojang.brigadier.StringReader

object GreedyStringParser : Parser<GreedyString>(GreedyString::class.java) {
    override fun parse(reader: StringReader): GreedyString {
        val string = reader.remaining
        reader.cursor = reader.totalLength
        return GreedyString(string)
    }
}

data class GreedyString(val string: String) {
    override fun toString(): String = string
}
