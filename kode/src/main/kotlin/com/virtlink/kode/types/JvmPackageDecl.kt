package com.virtlink.kode.types

import com.virtlink.kode.Jvm
import java.nio.file.Path

/**
 * A package declaration.
 */
class JvmPackageDecl(
    /** The dot-separated Java name of the package; or an empty string. */
    name: String
) {
    init {
        require(name.split('.').all { com.virtlink.kode.Jvm.isValidPackageName(it) }) {
            "The package name is invalid: $name"
        }
    }

    /** The dot-separated name of the package; or an empty string. */
    val name: String get() = javaName
    /** The debug name of the package. */
    val debugName: String get() = name
    /** The java name of the package, with segments separated with a dot (`.`). */
    val javaName: String get() = internalName.replace('/', '.')
    /** The slash-separated internal name of the package; or an empty string. */
    val internalName: String = name.replace('.', '/')

    /** Whether the package name is empty. */
    val isEmpty: Boolean get() = internalName.isEmpty()

    /**
     * Resolves this declaration in the specified path.
     *
     * @param rootPath the root path to resolve in
     * @return the path to the package directory
     */
    fun resolveInPath(rootPath: Path): Path {
        return rootPath.resolve(internalName)
    }

    // Store the reference of the plain type.
    private val reference: JvmPackageRef = JvmPackageRef(this)

    /**
     * Gets a reference to this declaration.
     * @return the package reference
     */
    fun ref(): JvmPackageRef {
        return reference
    }

    // Destructuring declarations
    operator fun component1() = name

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        val that = other as JvmPackageDecl
        // @formatter:off
        return this.internalName == that.internalName
        // @formatter:on
    }

    override fun hashCode(): Int {
        var result = 17
        result = 31 * result + internalName.hashCode()
        return result
    }

    override fun toString(): String = StringBuilder().apply {
        append("package ")
        append(javaName)
    }.toString()
}