package dev.pelsmaeker.kode.types

/**
 * A JVM field signature.
 */
data class JvmFieldSignature(
    /** The type of the field's value. */
    val type: JvmType
) {

    /** The field's JVM descriptor. */
    val descriptor: String get() = type.descriptor

    /** The field's JVM signature. */
    val signature: String get() = type.signature

    override fun toString(): String {
        return type.toString()
    }
}