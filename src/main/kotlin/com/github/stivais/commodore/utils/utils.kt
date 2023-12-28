package com.github.stivais.commodore.utils

import net.minecraft.client.Minecraft
import org.jetbrains.annotations.ApiStatus.Internal

@get:Internal
inline val mc: Minecraft get() = Minecraft.getMinecraft()

// add functions useful for parsing not found inside brigadier but are found inside like modern mc idk
