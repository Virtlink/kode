package dev.pelsmaeker.kode.types


/**
 * A Java primitive type.
 */
sealed interface JvmPrimitiveType : JvmType {
    override val isPrimitive: Boolean get() = true
    override val isArray: Boolean get() = false
    override val isTypeVariable: Boolean get() = false
    override val isClass: Boolean get() = false
    override val isInterface: Boolean get() = false
    override val signature: String get() = descriptor

    /**
     * Gets a boxed JVM type of this primitive type.
     *
     * Boxed types are used to represent nullable primitive types.
     * While most object in Java can be `null`, primitive types (such as integer and boolean) cannot.
     * However, by boxing them we force the compiler to use the corresponding non-primitive type
     * (e.g., `java.lang.Integer` and `java.lang.Boolean`), which *can* be `null`.
     *
     * @return the boxed type
     */
    fun boxed(): JvmClassRef
}


/**
 * A void primitive type.
 */
object JvmVoid: JvmPrimitiveType {
    override val sort get() = JvmTypeSort.Void
    override val kind get() = JvmTypeKind.Void
    override val descriptor get() = "V"
    override fun boxed() = JvmTypes.VoidClass.ref()
    override fun toString() = "void"
}


/**
 * A boolean primitive type.
 */
object JvmBoolean: JvmPrimitiveType {
    override val sort get() = JvmTypeSort.Boolean
    override val kind get() = JvmTypeKind.Integer
    override val descriptor get() = "Z"
    override fun boxed() = JvmTypes.BooleanClass.ref()
    override fun toString() = "boolean"
}


/**
 * A 16-bit Unicode primitive type.
 */
object JvmCharacter: JvmPrimitiveType {
    override val sort get() = JvmTypeSort.Character
    override val kind get() = JvmTypeKind.Integer
    override val descriptor get() = "C"
    override fun boxed() = JvmTypes.CharacterClass.ref()
    override fun toString() = "char"
}


/**
 * A signed 8-bit integer primitive type.
 */
object JvmByte: JvmPrimitiveType {
    override val sort get() = JvmTypeSort.Byte
    override val kind get() = JvmTypeKind.Integer
    override val descriptor get() = "B"
    override fun boxed() = JvmTypes.ByteClass.ref()
    override fun toString() = "byte"
}


/**
 * A signed 16-bit integer primitive type.
 */
object JvmShort: JvmPrimitiveType {
    override val sort get() = JvmTypeSort.Short
    override val kind get() = JvmTypeKind.Integer
    override val descriptor get() = "S"
    override fun boxed() = JvmTypes.ShortClass.ref()
    override fun toString() = "short"
}


/**
 * A signed 32-bit integer primitive type.
 */
object JvmInteger: JvmPrimitiveType {
    override val sort get() = JvmTypeSort.Integer
    override val kind get() = JvmTypeKind.Integer
    override val descriptor get() = "I"
    override fun boxed() = JvmTypes.IntegerClass.ref()
    override fun toString() = "int"
}


/**
 * A signed 64-bit integer primitive type.
 */
object JvmLong: JvmPrimitiveType {
    override val sort get() = JvmTypeSort.Long
    override val kind get() = JvmTypeKind.Long
    override val descriptor get() = "J"
    override fun boxed() = JvmTypes.LongClass.ref()
    override fun toString() = "long"
}


/**
 * A 32-bit floating-point primitive type.
 */
object JvmFloat: JvmPrimitiveType {
    override val sort get() = JvmTypeSort.Float
    override val kind get() = JvmTypeKind.Float
    override val descriptor get() = "F"
    override fun boxed() = JvmTypes.FloatClass.ref()
    override fun toString() = "float"
}


/**
 * A 64-bit floating-point primitive type.
 */
object JvmDouble: JvmPrimitiveType {
    override val sort get() = JvmTypeSort.Double
    override val kind get() = JvmTypeKind.Double
    override val descriptor get() = "D"
    override fun boxed() = JvmTypes.DoubleClass.ref()
    override fun toString() = "double"
}
