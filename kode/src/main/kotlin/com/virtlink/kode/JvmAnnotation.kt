package com.virtlink.kode

import com.virtlink.kode.types.JvmClassRef

/**
 * An annotation.
 */
data class JvmAnnotation(
    /** The type of the annotation. */
    val type: JvmClassRef
)