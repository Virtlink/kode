package dev.pelsmaeker.kode.types

import dev.pelsmaeker.kode.types.*
import dev.pelsmaeker.kode.types.JvmType.Companion.of
import java.lang.reflect.*


/**
 * A JVM type.
 *
 * Use [of] to create an instance of this interface from a [Type] or [Class].
 */
interface JvmType {

    /** The sort of type. */
    val sort: JvmTypeSort

    /** The kind of type. */
    val kind: JvmTypeKind

    /** The descriptor for this type. In case of parametric types, this is the erased type. */
    val descriptor: String

    /** The signature for this type, which is used to describe instantiations of parametric types. */
    val signature: String

    /** Whether this is a primitive type. */
    val isPrimitive: Boolean

    /** Whether this is a class type. */
    val isClass: Boolean

    /** Whether this is an interface type. */
    val isInterface: Boolean

    /** Whether this is an array type. */
    val isArray: Boolean

    /** Whether this is a type variable. */
    val isTypeVariable: Boolean

    /**
     * Constructs an equivalent Java Reflect type.
     * @return the constructed [Type]
     */
    fun toJavaType(classLoader: ClassLoader? = null): Type

    companion object {
        /**
         * Gets the JVM type of the specified type.
         *
         * @param type the type
         * @return the JVM type
         */
        @Suppress("UNCHECKED_CAST")
        fun of(type: Type): JvmType = when (type) {
            is ParameterizedType -> JvmClassRef.of(type)
            is GenericArrayType -> TODO()
            is TypeVariable<*> -> JvmTypeVar.of(type as TypeVariable<out Class<*>?>)
            is WildcardType -> TODO()
            is Class<*> -> of(type)
            else -> error("Unsupported type ${type::class.java}: $type")
        }

        /**
         * Gets the JVM type of the specified type.
         *
         * @param cls the type
         * @return the JVM type
         */
        fun of(cls: Class<*>): JvmType = when {
            cls.isArray -> JvmArray(of(cls.componentType))
            !cls.isPrimitive -> JvmClassDecl.of(cls).ref() /* FIXME: Not sure this is correct? */
            cls == java.lang.Void.TYPE -> JvmVoid
            cls == java.lang.Boolean.TYPE -> JvmBoolean
            cls == java.lang.Character.TYPE -> JvmCharacter
            cls == java.lang.Byte.TYPE -> JvmByte
            cls == java.lang.Short.TYPE -> JvmShort
            cls == java.lang.Integer.TYPE -> JvmInteger
            cls == java.lang.Long.TYPE -> JvmLong
            cls == java.lang.Float.TYPE -> JvmFloat
            cls == java.lang.Double.TYPE -> JvmDouble
            else -> error("Unhandled type: $cls")
        }
    }
}