package com.github.stivais.commodore.parsers

import java.util.*

/**
 * Interface used on [ParserBuilder]s to define if they suggest anything
 */
interface Suggests {
    /**
     * Suggests a collection of possible matching strings
     */
    fun suggests(): Collection<String> = Collections.emptyList()
}
