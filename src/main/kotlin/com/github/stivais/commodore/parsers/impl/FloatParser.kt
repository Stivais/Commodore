package com.github.stivais.commodore.parsers.impl

import com.github.stivais.commodore.parsers.Parser
import com.github.stivais.commodore.utils.RequiredBuilder
import com.mojang.brigadier.arguments.FloatArgumentType
import com.mojang.brigadier.context.CommandContext

class FloatParser<S>(override val name: String = "Number") : Parser<S, Float> {
    override val builder: RequiredBuilder<S> = RequiredBuilder.argument(name, FloatArgumentType.floatArg())
    override fun parse(ctx: CommandContext<S>): Float = FloatArgumentType.getFloat(ctx, name)
}