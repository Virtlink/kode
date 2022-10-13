package dev.pelsmaeker.kode.types

import java.lang.reflect.TypeVariable
import java.util.*
import java.util.stream.Collectors


/**
 * A JVM type parameter.
 *
 * Use [of] to create an instance of this class from a [TypeVariable].
 */
data class JvmTypeParam(
    /** The type parameter name. */
    val name: String,
    /** The type parameter class bound, if any; or `null` if not specified. */
    val classBound: JvmClassRef? = null,
    /** The type parameter interface bounds. */
    val interfaceBounds: List<JvmClassRef> = emptyList(),
) {

    constructor(name: String, classBound: JvmClassRef?, vararg interfaceBounds: JvmClassRef)
            : this(name, classBound, interfaceBounds.toList()) // FIXME: This could be cached

    /** A type variable that refers to this type parameter. */
    // FIXME: This could be cached
    val typeVar: JvmTypeVar get() = JvmTypeVar(name)

    /** The erased type of this parameter. */
    val erasedType: JvmClassRef get() = classBound ?: interfaceBounds.firstOrNull() ?: JvmTypes.Object.ref()
    // "The type erasure of its leftmost bound, or type Object if no bound was specified."
    // See: http://www.angelikalanger.com/GenericsFAQ/FAQSections/TechnicalDetails.html#What%20is%20the%20type%20erasure%20of%20a%20type%20parameter?

    /**
     * Gets the JVM signature of the type parameter.
     *
     * @return the JVM signature string
     */
    val signature: String get() = StringBuilder().apply {
        append(name)
        append(':')
        if (classBound != null) {
            append(classBound.signature)
        }
        for (interfaceBound in interfaceBounds) {
            append(':')
            append(interfaceBound.signature)
        }
    }.toString()

    override fun toString(): String = StringBuilder().apply {
        append(name)
        if (classBound != null) {
            append(" extends ")
            append(classBound)
        }
        val interfaceBoundsIterator = interfaceBounds.iterator()
        if (interfaceBoundsIterator.hasNext()) {
            append(" implements ")
            append(interfaceBoundsIterator.next())
            while (interfaceBoundsIterator.hasNext()) {
                append(',')
                append(interfaceBoundsIterator.next())
            }
        }
    }.toString()

    companion object {
        /**
         * Gets the JVM type of the specified type parameter.
         *
         * @param typeVar the type parameter
         * @return the JVM type parameter
         */
        fun of(typeVar: TypeVariable<*>): JvmTypeParam {
            val name = typeVar.name

            val allBounds = typeVar.bounds.map { JvmType.of(it) as JvmClassRef }
            // Assume that if there is a class bound, it is the first one
            val classBound: JvmClassRef? = allBounds.firstOrNull { it.isClass }
            val interfaceBounds: List<JvmClassRef> = allBounds.drop(if (classBound != null) 1 else 0)
            assert(interfaceBounds.all { it.isInterface })
            return JvmTypeParam(name, classBound, interfaceBounds)
        }
    }
}