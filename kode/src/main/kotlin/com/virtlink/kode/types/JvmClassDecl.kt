package com.virtlink.kode.types

import com.virtlink.kode.JvmClassSignature
import com.virtlink.kode.types.JvmPackageRef.Companion.ref
import java.lang.reflect.Modifier
import java.nio.file.Path

/**
 * A class declaration.
 *
 * A class declaration specifies the kind of class (class or interface), its supertypes,
 * and its type parameters (if any).
 */
data class JvmClassDecl @JvmOverloads constructor(
    /** The name of the class. If this is a nested class, the name is preceded by the name of the enclosing class, separated with a dollar sign (`$`). */
    val name: String,
    /** The package that contains the class. */
    val pkg: JvmPackageRef,
    /** Whether this is an interface. This influences the generated instructions used to invoke members of this class. */
    val isInterface: Boolean = false,
    /** The class signature. */
    val signature: JvmClassSignature,
    /** The enclosing class of this inner class; or `null` if this is not an inner class. */
    val enclosingClass: JvmClassDecl? = null,
) {

    // Store the reference of the plain type.
    private val reference: JvmClassRef = JvmClassRef(
        this,
        signature.typeParameters.map { it: JvmTypeParam -> JvmTypeArg(it.name) },
        JvmNullability.Maybe,
        enclosingClass?.ref()
    )

    /** Whether this is a class. This influences the generated instructions used to invoke members of this class. */
    val isClass: Boolean get() = !isInterface

    /** Whether this is an inner class. */
    val isInnerClass: Boolean get() = enclosingClass != null

    /**
     * The fully-qualified internal name.
     *
     * Package names are separated with forward slash (`/`).
     * Class names are separated with dollar sign (`$`) for both
     * inner classes and static nested classes.
     */
    val internalName: String get() =
        if (enclosingClass != null) "${enclosingClass.internalName}\$$name" else "${pkg.internalName}/$name"

    /**
     * Gets the fully-qualified Java name.
     *
     * Package names are separated with dot (`.`).
     * Class names are separated with dollar sign (`$`) for both
     * inner classes and static nested classes.
     */
    val javaName: String get() =
        if (enclosingClass != null) "${enclosingClass.javaName}\$$name" else "${pkg.javaName}.$name"

    /**
     * Resolves this declaration in the specified path.
     *
     * @param rootPath the root path to resolve in
     * @return the path to the class `.class` file
     */
    fun resolveInPath(rootPath: Path): Path {
        return rootPath.resolve("$internalName.class")
    }

    /**
     * Gets a reference to this declaration
     * with the type variables of the class as invariant type arguments.
     *
     * @return the reference to the class
     */
    fun ref(): JvmClassRef {
        return reference
    }

    /**
     * Gets a reference to this declaration
     * with the specified types as invariant type arguments.
     *
     * @param typeArgumentTypes the type argument types
     * @return the reference to the instantiated class
     */
    fun ref(vararg typeArgumentTypes: JvmType): JvmClassRef {
        return ref(typeArgumentTypes.map { JvmTypeArg(it, JvmTypeArgSort.Invariant) })
    }

    /**
     * Gets a reference to this declaration
     * with the specified type arguments.
     *
     * @param typeArguments the type arguments
     * @return the reference to the instantiated class
     */
    fun ref(vararg typeArguments: JvmTypeArg): JvmClassRef {
        return ref(typeArguments.toList())
    }

    /**
     * Gets a reference to this declaration
     * with the specified type arguments.
     *
     * @param typeArguments     the list of type arguments
     * @param nullability       the nullability of the reference
     * @param enclosingClassRef the enclosing class reference; or `null`
     * @return the reference to the instantiated class
     */
    fun ref(
        typeArguments: List<JvmTypeArg>,
        nullability: JvmNullability = JvmNullability.Maybe,
        enclosingClassRef: JvmClassRef? = enclosingClass?.ref(),
    ): JvmClassRef {
        require(typeArguments.size == signature.typeParameters.size) {
            "Expected ${signature.typeParameters.size} type arguments, got ${typeArguments.size}: ${typeArguments.joinToString()}"
        }
        require(enclosingClass != null == (enclosingClassRef != null)) {
            "The declaring $this has ${if (enclosingClass != null) "an enclosing $enclosingClass" else "no enclosing class"}, " +
                    "but ${if (enclosingClassRef != null) "an enclosing $enclosingClassRef" else "no enclosing class"} reference was provided."
        }
        return JvmClassRef(this, typeArguments, nullability, enclosingClassRef)
    }

    override fun toString(): String = StringBuilder().apply {
        append(if (isInterface) "interface " else "class ")
        if (enclosingClass != null) {
            append(enclosingClass.javaName).append(".").append(name)
        } else {
            append(javaName)
        }
    }.toString()

    companion object {
        /**
         * Gets the JVM type of the specified class.
         *
         * @param cls the class
         * @return the JVM class type
         */
        fun of(cls: Class<*>): JvmClassDecl {
            require(!cls.isPrimitive) { "Argument 'cls' cannot be a primitive type." }
            require(!cls.isArray) { "Argument 'cls' cannot be an array type." }

            // Determine the type parameters of the class.
            val typeParams = mutableListOf<JvmTypeParam>()
            for (typeVar in cls.typeParameters) {
                val typeParam: JvmTypeParam = JvmTypeParam.of(typeVar)
                typeParams.add(typeParam)
            }
            val isInterface = Modifier.isInterface(cls.modifiers)
            val pkg: JvmPackageRef = cls.getPackage().ref()

            // TODO: Fix names of inner classes and non-inner classes
            val className: String
            val enclosingClass: JvmClassDecl?
            if (cls.enclosingClass != null) {
                if (Modifier.isStatic(cls.modifiers)) {
                    // Nested class
                    enclosingClass = null
                    className = cls.name.substring(pkg.internalName.length + 1)
                } else {
                    // Inner class
                    assert(!isInterface) { "Inner class cannot be an interface." }
                    enclosingClass = of(cls.enclosingClass)
                    className = cls.simpleName
                }
            } else {
                // Normal class
                enclosingClass = null
                className = cls.simpleName
            }
            return JvmClassDecl(
                className,
                pkg,
                isInterface,
                // TODO: Super class and super interfaces
                JvmClassSignature(typeParameters = typeParams),
                enclosingClass
            )
        }
    }
}