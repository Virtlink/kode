package net.pelsmaeker.kode.types

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

    /** The category of this kind of type, which is the number of stack slots this type occupies. */
    val category: Int get() = when (this) {
        Void -> 0 // A primitive void is never on the stack.
        Integer, Float, Object -> 1
        Long, Double -> 2
    }
}