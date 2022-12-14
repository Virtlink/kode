package net.pelsmaeker.kode.types

import net.pelsmaeker.kode.JvmParam

/**
 * A JVM method signature.
 */
data class JvmMethodSignature @JvmOverloads constructor(
    /** The type of the method's return value. */
    val returnType: JvmType,
    /** The parameters of the method. */
    val parameters: List<JvmParam> = emptyList(),
    /** The types of the method's type parameters. */
    val typeParameters: List<JvmTypeParam> = emptyList(),
    /** The types of the method's checked throwables. */
    val throwableTypes: List<JvmType> = emptyList(),
) {

    /** The arity of the method. */
    val arity: Int get() = parameters.size

    /** The method's JVM descriptor. */
    val descriptor: String get() = StringBuilder().apply {
        parameters.joinTo(this, prefix = "(", postfix = ")", separator = "") { it.type.descriptor }
        append(returnType.descriptor)
    }.toString()

    /** The method's JVM signature. */
    val signature: String = StringBuilder().apply {
        if (typeParameters.isNotEmpty()) {
            typeParameters.joinTo(this, prefix = "<", postfix = ">", separator = "") { it.signature }
        }
        parameters.joinTo(this, prefix = "(", postfix = ")", separator = "") { it.type.signature }
        append(returnType.signature)
        throwableTypes.joinTo(this, prefix = "^", separator = "^")
    }.toString()

    override fun toString(): String = StringBuilder().apply {
        if (typeParameters.isNotEmpty()) {
            typeParameters.joinTo(this, prefix = "<", postfix = ">", separator = ",")
        }
        parameters.joinTo(this, prefix = "(", postfix = ")") { it.type.signature }
        append(returnType)
        if (throwableTypes.isNotEmpty()) {
            append(" throws ")
            throwableTypes.joinTo(this)
        }
    }.toString()
}