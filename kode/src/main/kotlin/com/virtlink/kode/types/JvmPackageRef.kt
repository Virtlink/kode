package com.virtlink.kode.types

/**
 * A JVM package reference.
 *
 * Use [ref] to create an instance of this class from a [Package].
 */
class JvmPackageRef internal constructor(
    /** The package declaration. */
    val declaration: JvmPackageDecl,
) : JvmRef {

    override val name: String get() = declaration.name
    override val debugName: String get() = declaration.debugName
    override val javaName: String get() = declaration.javaName
    override val internalName: String get() = declaration.internalName

    /** Whether the package name is empty. */
    val isEmpty: Boolean get() = declaration.isEmpty

    // Destructuring declarations
    operator fun component1() = name

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        val that = other as JvmPackageRef
        // @formatter:off
        return this.declaration == that.declaration
        // @formatter:on
    }

    override fun hashCode(): Int {
        var result = 17
        result = 31 * result + declaration.hashCode()
        return result
    }

    override fun toString(): String {
        return javaName
    }

    companion object {
        /**
         * Gets the JVM type of the specified Java Reflection package
         * (or `null` if it's the unnamed package).
         * @return the JVM package reference
         */
        fun Package?.ref(): JvmPackageRef {
            // TODO: Can we cache this?
            val decl = JvmPackageDecl(this?.name ?: "")
            return decl.ref()
        }
    }
}