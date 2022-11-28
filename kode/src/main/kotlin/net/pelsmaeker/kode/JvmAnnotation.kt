package net.pelsmaeker.kode

import net.pelsmaeker.kode.types.JvmClassRef

/**
 * An annotation.
 */
data class JvmAnnotation(
    /** The type of the annotation. */
    val type: JvmClassRef
)