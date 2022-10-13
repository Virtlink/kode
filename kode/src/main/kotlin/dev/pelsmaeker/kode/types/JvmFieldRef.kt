package dev.pelsmaeker.kode.types

import java.lang.reflect.Field
import java.lang.reflect.Modifier

/**
 * A JVM field reference.
 */
class JvmFieldRef(
    /** The name of the member. */
    override val name: String,
    /** The class that declares this member. */
    override val owner: JvmClassRef,
    /** Whether this is an instance member. */
    override val isInstance: Boolean,
    /** The field's signature. */
    val signature: JvmFieldSignature
) : JvmMemberRef {

    constructor(name: String, owner: JvmClassRef, isInstance: Boolean, type: JvmType) : this(
        name,
        owner,
        isInstance,
        JvmFieldSignature(type)
    )

    override val internalName: String get() = "${owner.internalName}#$name"
    override val javaName: String get() = "${owner.javaName}#$name"
    override val isStatic: Boolean get() = !isInstance
    override val isConstructor: Boolean get() = false
    override val isField: Boolean get() = true
    override val isMethod: Boolean get() = false

    /** The field's type. */
    val type: JvmType get() = signature.type

    override fun toString(): String {
        return "field: ${owner.javaName}${if (isInstance) ".this" else ""}::$name $signature"
    }

    companion object {
        /**
         * Gets the JVM reference of the specified field.
         *
         * @param field the field
         * @return the field reference
         */
        fun of(field: Field): JvmFieldRef {
            val name = field.name
            val owner = JvmClassRef.of(field.declaringClass)
            val type = JvmType.of(field.type)
            val signature = JvmFieldSignature(type)
            val isInstance = !Modifier.isStatic(field.modifiers)
            return JvmFieldRef(name, owner, isInstance, signature)
        }
    }
}