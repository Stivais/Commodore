package com.github.stivais.commodore.parsers.impl

import com.github.stivais.commodore.parsers.Parser
import com.mojang.brigadier.StringReader

object BooleanParser : Parser<Boolean>(Boolean::class.java) {
    override fun parse(reader: StringReader): Boolean {
        return reader.readBoolean()
    }
}
