package com.github.stivais.commodore.parsers.impl

import com.github.stivais.commodore.parsers.Parser
import com.github.stivais.commodore.utils.RequiredBuilder
import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.context.CommandContext

class StringParser<S>(override val name: String = "String") : Parser<S, String> {
    override val builder: RequiredBuilder<S> = RequiredBuilder.argument(name, StringArgumentType.word())
    override fun parse(ctx: CommandContext<S>): String = StringArgumentType.getString(ctx, name)
}