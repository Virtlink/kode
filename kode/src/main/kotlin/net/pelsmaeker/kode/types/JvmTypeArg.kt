package net.pelsmaeker.kode.types

import java.lang.reflect.Type
import java.lang.reflect.WildcardType

/**
 * A JVM type argument.
 */
data class JvmTypeArg(
    /** The element type of the argument. */
    val type: JvmType = JvmTypes.Object.ref(),
    /** The sort of type argument. */
    val argSort: JvmTypeArgSort = JvmTypeArgSort.Invariant,
) {
    init {
        require(argSort != JvmTypeArgSort.Wildcard || type == JvmTypes.Object.ref()) {
            "Element type must be JvmTypes.Object when it is a wildcard type argument."
        }
    }

    /**
     * Convenience constructor that creates a named type argument.
     *
     * For example, use this to create a type argument `T` for a class `C<T>`:
     * ```kotlin
     * JvmTypeArg("T")
     * ```
     * instead of the more verbose:
     * ```kotlin
     * JvmTypeArg(JvmTypeVar("T"), JvmTypeArgSort.Invariant)
     * ```
     *
     * @param name the name of the type argument
     * @param argSort the sort of type argument
     * @return the created type argument
     */
    constructor(name: String, argSort: JvmTypeArgSort = JvmTypeArgSort.Invariant): this(JvmTypeVar(name), argSort)

    /** The signature for this type argument. */
    val signature: String
        get() = when (argSort) {
            JvmTypeArgSort.Invariant -> type.signature
            JvmTypeArgSort.Covariant -> "+" + type.signature
            JvmTypeArgSort.Contravariant -> "-" + type.signature
            JvmTypeArgSort.Wildcard -> "*"
        }

    override fun toString(): String {
        return when (argSort) {
            JvmTypeArgSort.Invariant -> "$type"
            JvmTypeArgSort.Covariant -> "out $type"
            JvmTypeArgSort.Contravariant -> "in $type"
            JvmTypeArgSort.Wildcard -> "*"
        }
    }

    companion object {
        /**
         * Gets the JVM type argument of the specified type argument.
         *
         * @param type the type argument
         * @return the JVM type argument
         */
        fun of(type: Type): JvmTypeArg = when (type) {
            is WildcardType -> of(type)
            else -> JvmTypeArg(JvmType.of(type), JvmTypeArgSort.Invariant)
        }

        /**
         * Gets the JVM type argument of the specified type argument.
         *
         * @param type the type argument
         * @return the JVM type argument
         */
        @Suppress("ReplaceSizeCheckWithIsNotEmpty", "ReplaceSizeZeroCheckWithIsEmpty")
        fun of(type: WildcardType): JvmTypeArg = when {
            // "A wildcard can have only one bound. In can neither have both an upper and a lower bound
            // nor several upper or lower bounds."
            // See: http://www.angelikalanger.com/GenericsFAQ/FAQSections/TypeArguments.html#FAQ102
            type.upperBounds.size > 0 -> {
                // ? super T (contravariant)
                assert(type.lowerBounds.size == 0)
                assert(type.upperBounds.size == 1)
                val upperBound: JvmType = JvmType.of(type.upperBounds[0])
                JvmTypeArg(upperBound, JvmTypeArgSort.Contravariant)
            }
            type.upperBounds.size > 0 -> {
                // ? extends T (covariant)
                assert(type.lowerBounds.size == 1)
                assert(type.upperBounds.size == 0)
                val lowerBound: JvmType = JvmType.of(type.lowerBounds[0])
                JvmTypeArg(lowerBound, JvmTypeArgSort.Covariant)
            }
            else -> {
                // wildcard
                assert(type.lowerBounds.size == 0)
                assert(type.upperBounds.size == 0)
                JvmTypeArg(JvmTypes.Object.ref(), JvmTypeArgSort.Wildcard)
            }
        }
    }
}