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
    Object;

    /** The number of stack slots occupied by this kind of type. */
    val slotCount: Int get() = when (this) {
        Void -> 0 // FIXME: Not sure about this one
        Integer, Float, Object -> 1
        Long, Double -> 2
    }
}