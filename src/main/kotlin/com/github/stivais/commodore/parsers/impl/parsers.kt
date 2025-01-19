package com.github.stivais.commodore.parsers.impl

import com.github.stivais.commodore.parsers.CommandParser
import com.mojang.brigadier.StringReader

object BooleanParser : CommandParser<Boolean> {
    override fun parse(reader: StringReader): Boolean {
        return reader.readBoolean()
    }
}
