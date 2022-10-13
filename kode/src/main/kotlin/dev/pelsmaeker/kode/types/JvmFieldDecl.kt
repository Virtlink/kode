package dev.pelsmaeker.kode.types

import dev.pelsmaeker.kode.JvmFieldModifiers

/**
 * A field declaration.
 */
class JvmFieldDecl(
    /** The name of the field. */
    val name: String,
    /** The class that declares this field. */
    val owner: JvmClassDecl,
    /** The modifiers of the field. If this field is purely used as a reference, the only relevant modifier is [JvmFieldModifiers.Static]. */
    val modifiers: JvmFieldModifiers,
    /** The type of the field. */
    val type: JvmType,
) {

    /** Whether this is a static field. This influences the generated instructions used to get/set the field. */
    val isStatic: Boolean get() = modifiers.contains(JvmFieldModifiers.Static)

    /** Whether this is an instance field. This influences the generated instructions used to get/set the field. */
    val isInstance: Boolean get() = !isStatic

    /**
     * Creates a reference to this declaration.
     */
    fun ref(): JvmFieldRef = TODO()

    override fun toString(): String = StringBuilder().apply {
        append(if (isStatic) "static field " else "instance field ")
        append(owner.javaName)
        append('.')
        append(name)
        append(": ")
        append(type)
    }.toString()
}