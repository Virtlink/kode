package dev.pelsmaeker.kode.types

import dev.pelsmaeker.kode.JvmParam

/**
 * A JVM method signature.
 */
data class JvmMethodSignature(
    /** The type of the method's return value. */
    val returnType: JvmType,
    /** The method's parameters. */
    val parameters: List<JvmParam> = emptyList(),
    /** The types of the method's type parameters. */
    val typeParameters: List<JvmTypeParam> = emptyList(),
    /** The types of the method's checked throwables. */
    val throwables: List<JvmType> = emptyList(),
) {

    constructor(returnType: JvmType)
        : this(returnType, emptyList(), emptyList(), emptyList())

    /** The arity of the method. */
    val arity: Int get() = parameters.size

    /**
     * Gets the method's JVM descriptor.
     *
     * @return the JVM descriptor string
     */
    val descriptor: String get() = StringBuilder().apply {
        parameters.joinTo(this, prefix = "(", postfix = ")") { it.type.descriptor }
        append(returnType.descriptor)
    }.toString()

    /** The method's JVM signature. */
    val signature: String = StringBuilder().apply {
        if (typeParameters.isNotEmpty()) {
            typeParameters.joinTo(this, prefix = "<", postfix = ">") { it.signature }
        }
        parameters.joinTo(this, prefix = "(", postfix = ")") { it.type.signature }
        append(returnType.signature)
        throwables.joinTo(this, prefix = "^", separator = "^")
    }.toString()

    override fun toString(): String = StringBuilder().apply {
        if (typeParameters.isNotEmpty()) {
            typeParameters.joinTo(this, prefix = "<", postfix = ">", separator = ",")
        }
        parameters.joinTo(this, prefix = "(", postfix = ")") { it.type.signature }
        append(returnType)
        if (throwables.isNotEmpty()) {
            append(" throws ")
            throwables.joinTo(this)
        }
    }.toString()
}