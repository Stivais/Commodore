package com.github.stivais.commodore.parsers.impl

import com.github.stivais.commodore.parsers.Parser
import com.github.stivais.commodore.utils.RequiredBuilder
import com.mojang.brigadier.arguments.LongArgumentType
import com.mojang.brigadier.context.CommandContext

class LongParser<S>(override val name: String = "Number") : Parser<S, Long> {
    override val builder: RequiredBuilder<S> = RequiredBuilder.argument(name, LongArgumentType.longArg())
    override fun parse(ctx: CommandContext<S>): Long = LongArgumentType.getLong(ctx, name)
}