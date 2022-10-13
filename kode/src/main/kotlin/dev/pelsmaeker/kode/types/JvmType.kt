package dev.pelsmaeker.kode.types

import dev.pelsmaeker.kode.types.*
import java.lang.reflect.*


/**
 * A JVM type.
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
}