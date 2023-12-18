package com.github.stivais.commodore.parsers.impl

import com.github.stivais.commodore.parsers.Parser
import com.github.stivais.commodore.utils.RequiredBuilder
import com.mojang.brigadier.arguments.IntegerArgumentType
import com.mojang.brigadier.context.CommandContext

class IntParser<S>(override val name: String = "Number") : Parser<S, Int> {
    override val builder: RequiredBuilder<S> = RequiredBuilder.argument(name, IntegerArgumentType.integer())
    override fun parse(ctx: CommandContext<S>): Int = IntegerArgumentType.getInteger(ctx, name)
}