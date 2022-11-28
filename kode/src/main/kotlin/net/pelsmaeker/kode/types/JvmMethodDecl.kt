package net.pelsmaeker.kode.types

import net.pelsmaeker.kode.JvmMethodModifiers
import net.pelsmaeker.kode.JvmParam

/**
 * A JVM method declaration.
 */
class JvmMethodDecl(
    /** The name of the method; or `null` when it is a static or instance constructor. */
    val name: String?,
    /** The class that declares this method. */
    val owner: JvmClassDecl,
    /** The signature of the method. */
    val signature: JvmMethodSignature,
    /** The modifiers of the method. */
    val modifiers: JvmMethodModifiers = JvmMethodModifiers.None,
) {

    constructor(
        name: String?,
        owner: JvmClassDecl,
        returnType: JvmType,
        parameters: List<JvmParam> = emptyList(),
        modifiers: JvmMethodModifiers = JvmMethodModifiers.None,
    ): this(name, owner, JvmMethodSignature(returnType, parameters), modifiers)

    /** The debug name of the method, which is the name of the method or a special identifier if it's a constructor. */
    val debugName: String get() = name ?: if (isInstance) "<init>" else "<clinit>"

    /** The type of the method's return value. */
    val returnType: JvmType get() = signature.returnType
    /** The parameters of the method. */
    val parameters: List<JvmParam> get() = signature.parameters
    /** The types of the method's type parameters. */
    val typeParameters: List<JvmTypeParam> get() = signature.typeParameters
    /** The types of the method's checked throwables. */
    val throwableTypes: List<JvmType> get() = signature.throwableTypes

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
    fun ref(owner: JvmClassRef): JvmMethodRef {
        return ref(owner, emptyList())
    }

    /**
     * Gets a reference to this declaration
     * with the specified types as invariant type arguments.
     *
     * @param typeArgumentTypes the type argument types
     * @return the reference to the instantiated method
     */
    fun ref(owner: JvmClassRef, vararg typeArgumentTypes: JvmType): JvmMethodRef {
        return ref(owner, typeArgumentTypes.map { JvmTypeArg(it, JvmTypeArgSort.Invariant) })
    }

    /**
     * Gets a reference to this declaration
     * with the specified type arguments.
     *
     * @param typeArguments the type arguments
     * @return the reference to the instantiated method
     */
    fun ref(owner: JvmClassRef, vararg typeArguments: JvmTypeArg): JvmMethodRef {
        return ref(owner, typeArguments.toList())
    }

    /**
     * Gets a reference to this declaration
     * with the specified type arguments.
     *
     * @param typeArguments the list of type arguments
     * @return the reference to the instantiated method
     */
    fun ref(owner: JvmClassRef, typeArguments: List<JvmTypeArg>): JvmMethodRef {
        require(typeArguments.size == typeParameters.size) { "Expected ${typeParameters.size} type arguments, got ${typeArguments.size}: ${typeArguments.joinToString()}" }
        // TODO: Type parameters are not relevant?
        return JvmMethodRef(name, owner, returnType, parameters, isInstance)
    }

    override fun toString(): String = StringBuilder().apply {
        append(if (isStatic) "static " else "instance ")
        append(if (isConstructor) "constructor " else "method ")
        if (typeParameters.isNotEmpty()) typeParameters.joinTo(this, prefix = "<", postfix = "> ")
        append(owner.javaName)
        append("::")
        append(name)
        parameters.joinTo(this, prefix = "(", postfix = "): ")
        append(returnType)
        if (throwableTypes.isNotEmpty()) {
            append(" throws ")
            throwableTypes.joinTo(this)
        }
    }.toString()
}