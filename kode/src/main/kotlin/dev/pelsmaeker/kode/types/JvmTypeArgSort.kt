package dev.pelsmaeker.kode.types


/**
 * Specifies the sort of type argument.
 */
enum class JvmTypeArgSort {
    Invariant,
    Covariant,      // Out, +, `? extends T`
    Contravariant,  // In, -, `? super T`
    Wildcard
}