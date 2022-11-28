package net.pelsmaeker.kode


import net.pelsmaeker.kode.types.JvmClassRef
import net.pelsmaeker.kode.types.JvmTypeParam
import net.pelsmaeker.kode.types.JvmTypes


/**
 * A JVM class signature.
 */
data class JvmClassSignature(
    /** The super class of the class; or `null` when it is `Object` */
    val superClass: JvmClassRef? = null,
    /** The super interfaces of the class. */
    val superInterfaces: List<JvmClassRef> = emptyList(),
    /** The type parameters of the class. */
    val typeParameters: List<JvmTypeParam> = emptyList(),
) {
    /** The signature for the class. */
    val signature: String get() = StringBuilder().apply {
        if (typeParameters.isNotEmpty()) {
            typeParameters.joinTo(this, prefix = "<", postfix = ">") { it.signature }
        }
        append((superClass ?: JvmTypes.Object.ref()).signature)
        for (implementsInterface in superInterfaces) {
            append(implementsInterface.signature)
        }
    }.toString()

    override fun toString(): String = StringBuilder().apply {
        if (typeParameters.isNotEmpty()) typeParameters.joinTo(this, prefix = "<", postfix = ">")
        if (superClass != null) {
            append("extends ")
            append(superClass)
        }
        if (superInterfaces.isNotEmpty()) {
            append("implements ")
            superInterfaces.joinTo(this, prefix = ", ")
        }
    }.toString()
}