package dev.pelsmaeker.kode

import dev.pelsmaeker.kode.types.JvmClassRef

/**
 * An annotation.
 */
data class JvmAnnotation(
    /** The type of the annotation. */
    val type: JvmClassRef
)