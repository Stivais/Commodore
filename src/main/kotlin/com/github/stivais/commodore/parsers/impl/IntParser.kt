package com.github.stivais.commodore.parsers.impl

import com.github.stivais.commodore.parsers.Parser
import com.mojang.brigadier.StringReader

object IntParser : Parser<Int>(Int::class.java) {
    override fun parse(reader: StringReader): Int {
        return reader.readInt()
    }
}
