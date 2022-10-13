package dev.pelsmaeker.kode

import dev.pelsmaeker.kode.types.JvmType

/**
 * A JVM local variable.
 */
data class JvmLocalVar(
    /** The name of the local variable, or `null` when it has no name. */
    val name: String?,
    /** The type of the local variable. */
    val type: JvmType,
    /** The scope of the local variable. */
    val scope: JvmScope,
    /** The unique zero-based index of the local variable. */
    val index: Int,
) {
    override fun toString(): String {
        return "${name ?: ""}@$index : $type"
    }
}