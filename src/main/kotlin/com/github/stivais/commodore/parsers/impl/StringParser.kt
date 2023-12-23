package com.github.stivais.commodore.parsers.impl

import com.github.stivais.commodore.parsers.Parser
import com.mojang.brigadier.StringReader

object StringParser : Parser<String>(String::class.java) {
    override fun parse(reader: StringReader): String {
        return reader.readString()
    }
}
