package com.github.stivais.commodore.utils

/**
 * Used as a command argument, that takes all remaining strings from the input
 * ```
 *  runs { greedy: GreedyString ->
 *      println("$greedy") // when given the input of "hello world" it accepts it and prints "hello world"
 *  }
 *
 *  runs { string: String ->
 *      println("$string") // when given the input of "hello world" it doesn't accept it because too many arguments
 *  }
 * ```
 */
data class GreedyString(val string: String) {
    override fun toString(): String = string
}
