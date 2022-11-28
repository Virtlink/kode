package net.pelsmaeker.kode.types

import net.pelsmaeker.kode.types.JvmPackageRef.Companion.ref
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Test
import java.nio.file.Paths

/** Tests the [JvmPackageRef] class. */
class JvmPackageRefTests {

    @Test
    fun `of() should construct a JvmPackageRef from a package`() {
        // Arrange
        val pkg = this::class.java.classLoader.getDefinedPackage("net.pelsmaeker.kode.types")

        // Act
        val ref = pkg.ref()

        // Assert
        assertEquals("net/pelsmaeker/kode/types", ref.internalName)
        assertEquals("net.pelsmaeker.kode.types", ref.javaName)
        assertFalse(ref.isEmpty)
    }

    // TODO: Move this to JvmPackageDeclTests
    @Test
    fun `resolveInPath() should resolve the package to a directory in the given path`() {
        // Arrange
        val rootPath = Paths.get("some/path")
        val pkgDecl = JvmPackageDecl("net.pelsmaeker.kode.types")

        // Act
        val path = pkgDecl.resolveInPath(rootPath)

        // Assert
        assertEquals(rootPath.resolve("net/pelsmaeker/kode/types"), path)
    }

}