package dev.pelsmaeker.kode

import org.objectweb.asm.Label

/**
 * A label in a method body.
 */
class JvmLabel(
    /** An optional name for the label, for debugging; or `null` if not specified. */
    val name: String? = null,
) {

    /** The underlying ASM label. */
    internal val internalLabel = Label()

    override fun equals(other: Any?): Boolean {
        // Referential equality.
        return this === other
    }

    override fun hashCode(): Int {
        // Referential equality.
        return System.identityHashCode(this)
    }

    override fun toString(): String = name ?: "label"
}