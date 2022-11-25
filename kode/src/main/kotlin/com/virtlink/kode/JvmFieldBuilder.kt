package com.virtlink.kode

import com.virtlink.kode.types.JvmFieldDecl
import com.virtlink.kode.types.JvmFieldRef
import org.objectweb.asm.FieldVisitor

/**
 * Builds a JVM field.
 *
 * Call [build] when done with this builder.
 */
class JvmFieldBuilder internal constructor(
    /** The owning class builder. */
    val classBuilder: JvmClassBuilder,
    /** The declaration of the field being built. */
    val declaration: JvmFieldDecl,
    /** The field visitor. */
    internal val fieldVisitor: FieldVisitor,
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
     * @return the declaration of the built field
     */
    fun build(): JvmFieldDecl {
        @Suppress("DEPRECATION")
        close()
        return declaration
    }

    override fun toString(): String =
        "${declaration.owner}::${declaration.debugName}"

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