package dev.pelsmaeker.kode.types

import java.lang.reflect.TypeVariable

/**
 * A reference to a type variable.
 *
 * Use [of] to create an instance of this class from a [TypeVariable].
 */
data class JvmTypeVar(
    /** The name of the type variable. */
    val name: String
) : JvmType {

    init {
        require(name.isNotBlank()) { "Type variable name must not be blank or empty." }
    }

    override val sort: JvmTypeSort get() = JvmTypeSort.TypeParam
    override val kind: JvmTypeKind get() = TODO()
    override val descriptor: String get() = error("A type variable has no descriptor.")// FIXME: Is this correct? Should we throw?

    override val signature: String get() = "T$name;"
    override val isPrimitive: Boolean get() = false
    override val isClass: Boolean get() = false
    override val isInterface: Boolean get() = false
    override val isArray: Boolean get() = false
    override val isTypeVariable: Boolean get() = true

    override fun toString(): String = name

    companion object {
        /**
         * Gets the JVM type of the specified type variable.
         *
         * @param typeVar the type variable
         * @return the JVM type variable
         */
        fun of(typeVar: TypeVariable<out Class<*>>): JvmTypeVar {
            return JvmTypeVar(typeVar.name)
        }
    }
}