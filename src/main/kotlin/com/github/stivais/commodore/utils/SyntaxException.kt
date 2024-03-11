package com.github.stivais.commodore.utils

import com.mojang.brigadier.Message
import com.mojang.brigadier.exceptions.CommandExceptionType
import com.mojang.brigadier.exceptions.CommandSyntaxException

/**
 * Useful in [runs][com.github.stivais.commodore.Node.runs] to define if parsing has failed to allow for better error handling
 *
 * Don't use this in Minecraft versions 1.13 and above. This isn't intended for this.
 */
class SyntaxException : CommandSyntaxException(object : CommandExceptionType {}, Message { "Error parsing command." })
