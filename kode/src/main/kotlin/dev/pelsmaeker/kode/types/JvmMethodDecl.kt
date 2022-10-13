package dev.pelsmaeker.kode.types

import dev.pelsmaeker.kode.JvmMethodModifiers
import dev.pelsmaeker.kode.JvmParam

/**
 * A method declaration.
 */
class JvmMethodDecl(
    /** The name of the method; or `null` when it is a static or instance constructor. */
    val name: String?,
    /** The class that declares this method. */
    val owner: JvmClassDecl,
    /** The modifiers of the method. If this method is purely used as a reference, the only relevant modifier is [JvmMethodModifiers.Static]. */
    val modifiers: JvmMethodModifiers,
    /** The return type of the method. */
    val returnType: JvmType,
    /** The parameters of the method. */
    val parameters: List<JvmParam> = emptyList(),
    /** The type parameters of the method. */
    val typeParameters: List<JvmTypeParam> = emptyList(),
    /** The checked throwable types of the method. */
    val throwableTypes: List<JvmType> = emptyList(),
) {

    /** Whether this is a static method. This influences the generated instructions used to invoke the method. */
    val isStatic: Boolean get() = modifiers.contains(JvmMethodModifiers.Static)
    /** Whether this is an instance method. This influences the generated instructions used to invoke the method. */
    val isInstance: Boolean get() = !isStatic
    /** Whether this is a static or instance constructor. */
    val isConstructor: Boolean get() = name == null

    /**
     * Gets a reference to this declaration.
     *
     * @return the reference to the instantiated method
     */
    fun ref(): JvmMethodRef {
        return ref(emptyList())
    }

    /**
     * Gets a reference to this declaration
     * with the specified types as invariant type arguments.
     *
     * @param typeArgumentTypes the type argument types
     * @return the reference to the instantiated method
     */
    fun ref(vararg typeArgumentTypes: JvmType): JvmMethodRef {
        return ref(typeArgumentTypes.map { JvmTypeArg(it, JvmTypeArgSort.Invariant) })
    }

    /**
     * Gets a reference to this declaration
     * with the specified type arguments.
     *
     * @param typeArguments the type arguments
     * @return the reference to the instantiated method
     */
    fun ref(vararg typeArguments: JvmTypeArg): JvmMethodRef {
        return ref(typeArguments.toList())
    }

    /**
     * Gets a reference to this declaration
     * with the specified type arguments.
     *
     * @param typeArguments the list of type arguments
     * @return the reference to the instantiated method
     */
    fun ref(typeArguments: List<JvmTypeArg>): JvmMethodRef {
        require(typeArguments.size == typeParameters.size) { "Expected ${typeParameters.size} type arguments, got ${typeArguments.size}: ${typeArguments.joinToString()}" }
        TODO()
    }

    override fun toString(): String = StringBuilder().apply {
        append(if (isStatic) "static method " else "instance method ")
        if (typeParameters.isNotEmpty()) typeParameters.joinTo(this, prefix = "<", postfix = "> ")
        append(owner.javaName)
        append('.')
        append(name)
        parameters.joinTo(this, prefix = "(", postfix = "): ")
        append(returnType)
        if (throwableTypes.isNotEmpty()) {
            append(" throws ")
            throwableTypes.joinTo(this)
        }
    }.toString()
}