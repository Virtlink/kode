package dev.pelsmaeker.kode.types

/**
 * Specifies the kind of JVM type.
 *
 * The kind indicates the type of the value
 * when stored on the stack.
 */
enum class JvmTypeKind {
    /** Void type. */
    Void,
    /** Int type. */
    Integer,
    /** Long type. */
    Long,
    /** Float type. */
    Float,
    /** Double type. */
    Double,
    /** Object type. */
    Object
}