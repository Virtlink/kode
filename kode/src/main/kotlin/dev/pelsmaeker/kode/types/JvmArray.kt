package dev.pelsmaeker.kode.types

/**
 * An array type.
 */
data class JvmArray(
    /** The element type in the array. */
    val elementType: JvmType,
) : JvmType {

    override val sort: JvmTypeSort get() = JvmTypeSort.Array
    override val kind: JvmTypeKind get() = JvmTypeKind.Object
    override val descriptor: String get() = "[${elementType.descriptor}"
    override val signature: String get() = "[${elementType.signature}"

    override val isPrimitive: Boolean get() = false
    override val isClass: Boolean get() = false
    override val isInterface: Boolean get() = false
    override val isArray: Boolean get() = true
    override val isTypeVariable: Boolean get() = false

    /** The number of dimensions in the array. */
    val dimensionCount: Int get() = (elementType as? JvmArray)?.let { it.dimensionCount + 1} ?: 1

    override fun toString(): String = "$elementType[]"
}