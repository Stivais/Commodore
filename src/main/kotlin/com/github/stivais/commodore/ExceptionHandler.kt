package com.github.stivais.commodore

/**
 * Interface for handling exceptions that are caused by wrong input when a command is run.
 *
 * It is designed to be high customizable, however could be potentially messy.
 */
fun interface ExceptionHandler {
    fun handle(root: Node, cause: Node)
}