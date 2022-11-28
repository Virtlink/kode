package net.pelsmaeker.kode

import net.pelsmaeker.kode.types.JvmType
import net.pelsmaeker.kode.types.JvmTypeKind

/**
 * A JVM variable, which can be the receiver (`this`), an argument, or a local variable.
 */
data class JvmVar(
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
     * See also [JvmTypeKind.category].
     */
    val offset: Int,
) {
    override fun toString(): String {
        return "${name ?: ""}@$offset : $type"
    }
}