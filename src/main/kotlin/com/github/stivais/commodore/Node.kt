@file:Suppress("UNUSED")

package com.github.stivais.commodore

import com.github.stivais.commodore.functions.FunctionInvoker
import com.mojang.brigadier.Command.SINGLE_SUCCESS
import com.mojang.brigadier.builder.LiteralArgumentBuilder

/**
 * This class is used to create branch-like commands.
 *
 * A command, starts with the root node and branches of.
 */
class Node(name: String) {

    /**
     * Literal builder
     */
    val builder: LiteralArgumentBuilder<Any?> = LiteralArgumentBuilder.literal<Any?>(name)

    /**
     * Nodes that branch of the current one
     */
    private var children: MutableList<Node>? = null

    /**
     * Executables under this node
     */
    private var executables: MutableList<Executable>? = null

    /**
     * Sets up the node and its children
     */
    fun build() {
        executables?.let {
            for (executable in it) {
                executable.build()
            }
        }
        children?.let {
            for (node in it) {
                node.build()
                builder.then(node.builder)
            }
        }
    }

    /**
     * Creates a new node that branches from the current one.
     */
    fun literal(name: String, block: Node.() -> Unit = {}): Node {
        if (children == null) children = mutableListOf()
        return Node(name).also {
            it.block()
            children?.add(it)
        }
    }

    /**
     * Lets you add a function to this node
     * with as many parameters as you want, as long as the parameter's class has a parser.
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
     *      println("foo$string") // if input is empty foo will be null, and with the input of "bar" it prints "foobar"
     *  }
     * ```
     *
     * Available classes that are parseable: [String], [GreedyString][com.github.stivais.commodore.utils.GreedyString],
     * [Int], [Long], [Float], [Double], [Boolean].
     *
     * It is possible to register your own parsers using [registerParser][com.github.stivais.commodore.parsers.ParserBuilder.registerParser]
     *
     */
    fun runs(block: Function<Unit>): Executable {
        if (executables == null) executables = mutableListOf()
        return Executable(this, FunctionInvoker(block)).also { executables?.add(it) }
    }

    /**
     * Creates a function with 0 parameters, used to not waste resources on an executable with 0 parameters
     */
    fun runs(block: () -> Unit) {
        builder.executes {
            block()
            SINGLE_SUCCESS
        }
    }
}
