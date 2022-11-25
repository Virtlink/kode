package com.virtlink.kode.types

/**
 * A JVM member reference.
 */
interface JvmMemberRef : JvmRef {
    /** A reference to the class that declares this member. */
    val owner: JvmClassRef

    /** Whether this is an instance member. */
    val isInstance: Boolean
    /** Whether this is a static member. */
    val isStatic: Boolean
    /** Whether this is a static or instance constructor. */
    val isConstructor: Boolean
    /** Whether this is a field. */
    val isField: Boolean
    /** Whether this is a method (but not a constructor). */
    val isMethod: Boolean
}