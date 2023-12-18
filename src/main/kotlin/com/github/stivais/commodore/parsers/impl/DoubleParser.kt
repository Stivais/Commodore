package com.github.stivais.commodore.parsers.impl

import com.github.stivais.commodore.parsers.Parser
import com.github.stivais.commodore.utils.RequiredBuilder
import com.mojang.brigadier.arguments.DoubleArgumentType
import com.mojang.brigadier.context.CommandContext

class DoubleParser<S>(override val name: String) : Parser<S, Double> {
    override val builder: RequiredBuilder<S> = RequiredBuilder.argument(name, DoubleArgumentType.doubleArg())
    override fun parse(ctx: CommandContext<S>): Double = DoubleArgumentType.getDouble(ctx, name)
}