package dev.pelsmaeker.kode

/**
 * A JVM scope in a method body.
 */
interface JvmScope {
    /** The label signifying the start of the scope. */
    val startLabel: JvmLabel

    /** The label signifying the end of the scope. */
    val endLabel: JvmLabel
}