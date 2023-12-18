package com.github.stivais.commodore.parsers.impl

import com.github.stivais.commodore.parsers.Parser
import com.github.stivais.commodore.utils.RequiredBuilder
import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.context.CommandContext

class GreedyStringParser<S>(override val name: String = "String") : Parser<S, GreedyString> {
    override val builder: RequiredBuilder<S> = RequiredBuilder.argument(name, StringArgumentType.greedyString())
    override fun parse(ctx: CommandContext<S>): GreedyString = GreedyString(StringArgumentType.getString(ctx, name))
}

data class GreedyString(val name: String)