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
    /**
     * The unique zero-based offset of the local variable.
     *
     * The offset need not match the index, for example if there are preceding category 2 variables
     * (those that take up two slots, such as variables of type Long and Double).
     */
    val offset: Int,
) {
    override fun toString(): String {
        return "${name ?: ""}@$offset : $type"
    }
}