package net.pelsmaeker.kode.types

import java.lang.reflect.*

/**
 * A JVM class reference.
 *
 * Use [JvmClassRef.of] or [JvmClassDecl.ref] to create an instance of this class.
 */
data class JvmClassRef internal constructor(
    /** The class declaration. */
    val declaration: JvmClassDecl,
    /** The type arguments. */
    val typeArguments: List<JvmTypeArg>,
    /** The nullability of the type. */
    val nullability: JvmNullability,
    /** The enclosing class reference, if any; otherwise, `null`. */
    // Owner?
    val enclosingClassRef: JvmClassRef?,
): JvmType, JvmRef {

    init {
        require(typeArguments.size == declaration.signature.typeParameters.size) {
            "Expected ${declaration.signature.typeParameters.size} type arguments, got ${typeArguments.size}: ${typeArguments.joinToString()}"
        }
        require(declaration.enclosingClass != null == (enclosingClassRef != null)) {
            "The declaring $declaration has ${if (declaration.enclosingClass != null) "an enclosing ${declaration.enclosingClass}" else "no enclosing class"}, " +
                    "but ${if (enclosingClassRef != null) "an enclosing $enclosingClassRef" else "no enclosing class"} reference was provided."
        }
    }

    override val name: String get() = declaration.name
    override val debugName: String get() = name
    override val internalName: String get() = declaration.internalName
    override val javaName: String get() = declaration.javaName
    override val sort: JvmTypeSort get() = JvmTypeSort.Class
    override val kind: JvmTypeKind get() = JvmTypeKind.Object
    override val descriptor: String get() = "L$internalName;"
    override val signature: String get() = "L$cleanSignature;"

    override val isPrimitive: Boolean get() = false
    override val isClass: Boolean get() = declaration.isClass
    override val isInterface: Boolean get() = declaration.isInterface
    override val isArray: Boolean get() = false
    override val isTypeVariable: Boolean get() = false

    /** The signature of the class without the `L`-prefix and without the `;`-suffix. */
    private val cleanSignature: String get() = StringBuilder().apply {
        if (enclosingClassRef != null) {
            append(enclosingClassRef.cleanSignature).append('.').append(declaration.name)
        } else {
            append(declaration.internalName)
        }
        if (typeArguments.isNotEmpty()) {
            append('<')
            for (typeArg in typeArguments) {
                append(typeArg.signature)
            }
            append('>')
        }
    }.toString()

    override fun toJavaType(classLoader: ClassLoader?): Type {
        // TODO: Use classLoader
        return Class.forName(javaName)
    }

    override fun toString(): String = StringBuilder().apply {
        append(if (declaration.isInterface) "interface " else "class ")
        append(declaration.javaName)
        if (typeArguments.isNotEmpty()) typeArguments.joinTo(this, prefix = "<", postfix = ">")
        when {
            nullability === JvmNullability.NotNull -> append("!")
            nullability === JvmNullability.Nullable -> append("?")
        }
    }.toString()

    companion object {
        /**
         * Creates a class reference from the given class type.
         *
         * @param type the class type
         * @return the class reference
         */
        fun of(type: Class<*>): JvmClassRef {
            return JvmClassDecl.of(type).ref()
        }

        /**
         * Creates a class reference from the given parameterized type.
         *
         * @param type the parameterized type
         * @return the class reference
         */
        fun of(type: ParameterizedType): JvmClassRef {
            val declaration = JvmClassDecl.of(type.rawType as Class<*>)
            val typeArguments = type.actualTypeArguments.map(JvmTypeArg.Companion::of)
            val enclosingClass = if (declaration.isInnerClass) JvmType.of(type.ownerType!!) as JvmClassRef else null
            return JvmClassRef(declaration, typeArguments, JvmNullability.Maybe, enclosingClass)
        }
    }
}