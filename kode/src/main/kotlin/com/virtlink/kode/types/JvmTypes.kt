package com.virtlink.kode.types

import kotlin.String

/**
 * Pre-defined JVM types.
 */
object JvmTypes {

    /** Void class type. */
    val VoidClass = JvmClassDecl.of(java.lang.Void::class.java)
    /** Boolean class type. */
    val BooleanClass = JvmClassDecl.of(java.lang.Boolean::class.java)
    /** Character class type. */
    val CharacterClass = JvmClassDecl.of(java.lang.Character::class.java)
    /** Byte class type. */
    val ByteClass = JvmClassDecl.of(java.lang.Byte::class.java)
    /** Short class type. */
    val ShortClass = JvmClassDecl.of(java.lang.Short::class.java)
    /** Integer class type. */
    val IntegerClass = JvmClassDecl.of(java.lang.Integer::class.java)
    /** Long class type. */
    val LongClass = JvmClassDecl.of(java.lang.Long::class.java)
    /** Float class type. */
    val FloatClass = JvmClassDecl.of(java.lang.Float::class.java)
    /** Double class type. */
    val DoubleClass = JvmClassDecl.of(java.lang.Double::class.java)

    /** Package [java.lang]. */
    private val Java_Lang = JvmPackageDecl("java.lang").ref()

    /** Class [java.lang.Object]. */
    val Object = JvmClassDecl.of(java.lang.Object::class.java)
    /** Class [java.lang.Class]. */
    val Class = JvmClassDecl.of(java.lang.Class::class.java)
    /** Class [java.lang.String]. */
    val String = JvmClassDecl.of(java.lang.String::class.java)
    /** Class [java.lang.AssertionError]. */
    val AssertionError = JvmClassDecl.of(java.lang.AssertionError::class.java)
    /** Class [java.lang.NullPointerException]. */
    val NullPointerException = JvmClassDecl.of(java.lang.NullPointerException::class.java)
    /** Class [java.util.List]. */
    val List = JvmClassDecl.of(java.util.List::class.java)
    /** Class [java.lang.System]. */
    val System = JvmClassDecl.of(java.lang.System::class.java)

}