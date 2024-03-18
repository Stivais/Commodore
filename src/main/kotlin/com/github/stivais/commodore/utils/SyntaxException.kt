package com.github.stivais.commodore.utils

import com.mojang.brigadier.Message
import com.mojang.brigadier.exceptions.CommandExceptionType
import com.mojang.brigadier.exceptions.CommandSyntaxException

/**
 * Useful in [runs][com.github.stivais.commodore.Node.runs] to define if parsing has failed to allow for better error handling
 *
 * Using this removes the need for custom parsing
 */
class SyntaxException : CommandSyntaxException(object : CommandExceptionType {}, Message { "Error parsing command." })
