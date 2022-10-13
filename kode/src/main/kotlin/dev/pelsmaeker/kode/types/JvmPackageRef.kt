package dev.pelsmaeker.kode.types

import java.nio.file.Path

/**
 * A JVM package reference.
 *
 * Use [of] to create an instance of this class from a [Package].
 */
data class JvmPackageRef(
    /** The internal name of the package. */
    override val internalName: String
) : JvmRef {

    /** Whether the package name is empty. */
    val isEmpty: Boolean get() = internalName.isEmpty()
    override val javaName: String get() = internalName.replace('/', '.')

    /**
     * Resolves this reference in the specified path.
     *
     * @param rootPath the root path to resolve in
     * @return the path to the package directory
     */
    fun resolveInPath(rootPath: Path): Path {
        return rootPath.resolve(internalName)
    }

    override fun toString(): String {
        return javaName
    }

    companion object {
        /**
         * Gets the JVM type of the specified package.
         *
         * @param pkg the package; or `null` if it's the unnamed package
         * @return the JVM package reference
         */
        fun of(pkg: Package?): JvmPackageRef {
            if (pkg == null) return JvmPackageRef("")
            val packageName = pkg.name.replace('.', '/')
            return JvmPackageRef(packageName)
        }
    }
}