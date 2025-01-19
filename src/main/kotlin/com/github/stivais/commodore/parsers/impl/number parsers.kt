package com.github.stivais.commodore.parsers.impl

import com.github.stivais.commodore.parsers.CommandParser
import com.mojang.brigadier.StringReader

object LongParser : CommandParser<Long> {
    override fun parse(reader: StringReader): Long {
        return reader.readLong()
    }
}

object IntParser : CommandParser<Int> {
    override fun parse(reader: StringReader): Int {
        return reader.readInt()
    }
}

object FloatParser : CommandParser<Float> {
    override fun parse(reader: StringReader): Float {
        return reader.readFloat()
    }
}

object DoubleParser : CommandParser<Double> {
    override fun parse(reader: StringReader): Double {
        return reader.readDouble()
    }
}