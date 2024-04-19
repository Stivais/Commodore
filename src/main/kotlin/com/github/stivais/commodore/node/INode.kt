package com.github.stivais.commodore.node

import com.github.stivais.commodore.Executable

/**
 * Interface for a node, providing [literal] and [runs]
 */
internal interface INode {

    /**
     * Creates a new node that branches from the current one.
     */
    fun literal(name: String, block: Node.() -> Unit = {}): Node

    /**
     * Lets you add a "function" to this node
     * with as many parameters as you want, as long as the parameter's class has a parser.
     *
     * Default classes that are parseable: [String], [GreedyString][com.github.stivais.commodore.utils.GreedyString],
     * [Int], [Long], [Float], [Double], [Boolean].
     *
     * Example:
     * ```
     *  runs { hello: String, x: Double, y: Int, z: Float ->
     *      println("$hello ${x + y + z}) // input of "world 1 2 3" prints "world 6.0"
     *  }
     * ```
     * It is possible for parameters to be optional (not required to run the function)
     *
     * For example:
     * ```
     *  runs { string: String? ->
     *      println("hello$string") // if input is empty string will be null, and with the input of "world" it prints "helloworld"
     *  }
     * ```
     *
     *
     * It is possible to register your own parsers using [registerParser][com.github.stivais.commodore.parsers.ParserBuilder.registerParser]
     *
     * NOTE: In most cases it is recommended to throw [SyntaxException][com.github.stivais.commodore.utils.SyntaxException]
     * instead of returning, it allows for the command input
     */
    fun runs(block: Function<Unit>): Executable

    /**
     * Creates a function with 0 parameters.
     *
     * If you want to add parameters, use [runs]
     *
     * @see runs
     */
    fun runs(block: () -> Unit)

    /**
     * DSL for [literal]
     */
    operator fun String.invoke(block: Node.() -> Unit = {}) = literal(this, block)

    /**
     * DSL for [runs]
     */
    fun String.runs(block: Function<Unit>): Executable = this@INode.runs(block)

    fun String.runs(block: () -> Unit) = this@INode.runs(block)
}