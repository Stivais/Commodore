package com.github.stivais.commodore.parsers.impl

import com.github.stivais.commodore.parsers.Parser
import com.mojang.brigadier.StringReader

object FloatParser : Parser<Float>(Float::class.java) {
    override fun parse(reader: StringReader): Float {
        return reader.readFloat()
    }
}
