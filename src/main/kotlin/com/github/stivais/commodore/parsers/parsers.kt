package com.github.stivais.commodore.parsers

import com.github.stivais.commodore.utils.GreedyString
import com.mojang.brigadier.StringReader

object GreedyStringParser : ParserBuilder<GreedyString>(GreedyString::class.java) {
    override fun parse(reader: StringReader): GreedyString {
        val string = reader.remaining
        reader.cursor = reader.totalLength
        return GreedyString(string)
    }
}

object StringParser : ParserBuilder<String>(String::class.java) {
    override fun parse(reader: StringReader): String {
        return reader.readString()
    }
}

object LongParser : ParserBuilder<Long>(Long::class.java) {
    override fun parse(reader: StringReader): Long {
        return reader.readLong()
    }
}

object IntParser : ParserBuilder<Int>(Int::class.java) {
    override fun parse(reader: StringReader): Int {
        return reader.readInt()
    }
}

object FloatParser : ParserBuilder<Float>(Float::class.java) {
    override fun parse(reader: StringReader): Float {
        return reader.readFloat()
    }
}

object DoubleParser : ParserBuilder<Double>(Double::class.java) {
    override fun parse(reader: StringReader): Double {
        return reader.readDouble()
    }
}

object BooleanParser : ParserBuilder<Boolean>(Boolean::class.java) {
    override fun parse(reader: StringReader): Boolean {
        return reader.readBoolean()
    }
}
