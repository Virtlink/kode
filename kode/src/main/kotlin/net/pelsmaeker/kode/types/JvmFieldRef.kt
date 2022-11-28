package net.pelsmaeker.kode.types

import net.pelsmaeker.kode.JvmFieldModifiers
import net.pelsmaeker.kode.types.JvmPackageRef.Companion.ref
import java.lang.reflect.Field

/**
 * A JVM field reference.
 *
 * Use [ref] to create an instance of this class from a [Field].
 */
class JvmFieldRef internal constructor(
    /** The field declaration. */
    val declaration: JvmFieldDecl,
    /** The owning class, if any; otherwise, `null`. */
    override val owner: JvmClassRef,
) : JvmMemberRef {

    init {
        require(owner.declaration == declaration.owner) {
            "The field is declared in ${declaration.owner}, but the owner is given as ${owner.declaration}."
        }
    }

    override val name: String get() = declaration.name
    override val debugName: String get() = name
    override val javaName: String get() = "${owner.javaName}#$name"
    override val internalName: String get() = "${owner.internalName}#$name"

    override val isInstance: Boolean get() = declaration.isInstance
    override val isStatic: Boolean get() = declaration.isStatic
    override val isConstructor: Boolean get() = false
    override val isField: Boolean get() = true
    override val isMethod: Boolean get() = false

    /** The field's type. */
    val type: JvmType get() = signature.type

    /** The field's signature. */
    val signature: JvmFieldSignature get() = declaration.signature

    // Destructuring declarations
    operator fun component1() = name
    operator fun component2() = owner
    operator fun component3() = signature
    operator fun component4() = isInstance

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        val that = other as JvmFieldRef
        // @formatter:off
        return this.declaration == that.declaration
            && this.owner == that.owner
        // @formatter:on
    }

    override fun hashCode(): Int {
        var result = 17
        result = 31 * result + declaration.hashCode()
        result = 31 * result + owner.hashCode()
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

    companion object {
        /**
         * Gets the JVM reference to the given Java Reflection field.
         * @return the JVM field reference
         */
        fun Field.ref(): JvmFieldRef {
            val name = this.name
            val owner = JvmClassRef.of(this.declaringClass)
            val type = JvmType.of(this.type)
            val signature = JvmFieldSignature(type)
            val modifiers = JvmFieldModifiers(this.modifiers)
            val decl = JvmFieldDecl(
                name,
                owner.declaration,
                signature,
                modifiers,
            )
            return JvmFieldRef(decl, owner)
        }
    }
}