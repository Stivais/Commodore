package com.github.stivais.commodore.parsers.impl

import com.github.stivais.commodore.parsers.Parser
import com.mojang.brigadier.StringReader

object LongParser : Parser<Long>(Long::class.java) {
    override fun parse(reader: StringReader): Long {
        return reader.readLong()
    }
}
