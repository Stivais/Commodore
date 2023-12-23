package com.github.stivais.commodore.parsers.impl

import com.github.stivais.commodore.parsers.Parser
import com.mojang.brigadier.StringReader

object DoubleParser : Parser<Double>(Double::class.java) {
    override fun parse(reader: StringReader): Double {
        return reader.readDouble()
    }
}
