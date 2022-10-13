package dev.pelsmaeker.kode

import dev.pelsmaeker.kode.types.JvmFieldRef
import org.objectweb.asm.FieldVisitor

/**
 * Builds a JVM field.
 *
 * Call [build] when done with this builder.
 */
class JvmFieldBuilder(
    /** A reference to the field being built. */
    val reference: JvmFieldRef,
    /** The field visitor. */
    val fieldVisitor: FieldVisitor,
) : AutoCloseable {

    /** Whether this builder was closed. */
    private var closed = false

    @Deprecated("Prefer using build()")
    override fun close() {
        if (closed) return
        checkUsable()
        closed = true

        // Done!
        fieldVisitor.visitEnd()
    }

    /**
     * Builds a field from this field builder,
     * and closes the builder.
     *
     * @return the reference to the field
     */
    fun build(): JvmFieldRef {
        @Suppress("DEPRECATION")
        close()
        return reference
    }

    override fun toString(): String {
        return reference.toString()
    }

    /**
     * Checks that the object is usable.
     */
    private fun checkUsable() {
        checkNotClosed()
    }

    /**
     * Asserts that the scope was not closed.
     *
     * @throws IllegalStateException if the scope was closed
     */
    private fun checkNotClosed() {
        check(!closed) { "The builder was closed." }
    }
}