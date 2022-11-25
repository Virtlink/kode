package com.virtlink.kode.types

import com.virtlink.kode.JvmFieldModifiers

/**
 * A field declaration.
 *
 * Note that only the [JvmFieldModifiers.Static] modifier is included as part of [equals] or [hashCode],
 * because other modifiers will not be known when this declaration is used to back a [JvmFieldRef].
 */
class JvmFieldDecl(
    /** The name of the field. */
    val name: String,
    /** The class that declares this field. */
    val owner: JvmClassDecl,
    /** The signature of the field. */
    val signature: JvmFieldSignature,
    /** The modifiers of the field. */
    val modifiers: JvmFieldModifiers,
) {

    /**
     * @param name the name of the field
     * @param owner the class that declares this field
     * @param type the type of the field
     * @param isInstance whether this is an instance field or not (i.e., a static field)
     */
    constructor(name: String, owner: JvmClassDecl, type: JvmType, isInstance: Boolean = false) : this(
        name,
        owner,
        JvmFieldSignature(type),
        if (isInstance) JvmFieldModifiers.None else JvmFieldModifiers.Static
    )

    /** The debug name of the field. */
    val debugName: String get() = name

    /** Whether this is an instance field. This influences the generated instructions used to get/set the field. */
    val isInstance: Boolean get() = !isStatic

    /** Whether this is a static field. This influences the generated instructions used to get/set the field. */
    val isStatic: Boolean get() = modifiers.contains(JvmFieldModifiers.Static)

    /**
     * Gets a reference to this declaration.
     * @param owner a reference to the owner of the field
     * @return the field reference
     */
    fun ref(owner: JvmClassRef): JvmFieldRef {
        return JvmFieldRef(this, owner)
    }

    // Destructuring declarations
    operator fun component1() = name
    operator fun component2() = owner
    operator fun component3() = signature
    operator fun component4() = modifiers

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        val that = other as JvmFieldDecl
        // @formatter:off
        return this.name == that.name
            && this.signature == that.signature
            && this.owner == that.owner
            && this.isInstance == that.isInstance
        // @formatter:on
    }

    override fun hashCode(): Int {
        var result = 17
        result = 31 * result + name.hashCode()
        result = 31 * result + owner.hashCode()
        result = 31 * result + signature.hashCode()
        result = 31 * result + isInstance.hashCode()
        return result
    }

    override fun toString(): String = StringBuilder().apply {
        append(if (isInstance) "instance field " else "static field ")
        append(owner.javaName)
        append('.')
        append(name)
        append(": ")
        append(signature)
    }.toString()
}