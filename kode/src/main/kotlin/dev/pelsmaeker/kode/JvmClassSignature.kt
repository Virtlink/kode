package dev.pelsmaeker.kode


import dev.pelsmaeker.kode.types.JvmClassRef
import dev.pelsmaeker.kode.types.JvmTypeParam
import dev.pelsmaeker.kode.types.JvmTypes


/**
 * A JVM class signature.
 */
data class JvmClassSignature(
    /** The super class of the type. */
    val superClass: JvmClassRef = JvmTypes.Object.ref(),
    /** The super interfaces of the type. */
    val superInterfaces: List<JvmClassRef> = emptyList(),
) {
    
    constructor(superClass: JvmClassRef = JvmTypes.Object.ref(), vararg superInterfaces: JvmClassRef)
        : this(superClass, superInterfaces.toList())

    /**
     * Gets the signature for the class.
     *
     * @param typeParameters the type parameters
     * @return the class signature
     */
    fun getSignature(typeParameters: List<JvmTypeParam>): String = StringBuilder().apply {
        if (typeParameters.isNotEmpty()) {
            typeParameters.joinTo(this, prefix = "<", postfix = ">") { it.signature }
        }
        append(superClass.signature)
        for (implementsInterface in superInterfaces) {
            append(implementsInterface.signature)
        }
    }.toString()

    override fun toString(): String = StringBuilder().apply {
        append(superClass.signature)
        superInterfaces.joinTo(this, prefix = ", ")
    }.toString()
}