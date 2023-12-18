package com.github.stivais.commodore.utils

import com.mojang.brigadier.builder.LiteralArgumentBuilder
import com.mojang.brigadier.builder.RequiredArgumentBuilder

typealias RequiredBuilder<C> = RequiredArgumentBuilder<C, *>
typealias LiteralBuilder<C> = LiteralArgumentBuilder<C>