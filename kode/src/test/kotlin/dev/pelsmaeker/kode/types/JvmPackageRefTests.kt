package dev.pelsmaeker.kode.types

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Test
import java.nio.file.Paths

/** Tests the [JvmPackageRef] class. */
class JvmPackageRefTests {

    @Test
    fun `of() should construct a JvmPackageRef from a package`() {
        // Arrange
        val pkg = this::class.java.classLoader.getDefinedPackage("dev.pelsmaeker.kode.types")

        // Act
        val ref = JvmPackageRef.of(pkg)

        // Assert
        assertEquals("dev/pelsmaeker/kode/types", ref.internalName)
        assertEquals("dev.pelsmaeker.kode.types", ref.javaName)
        assertFalse(ref.isEmpty)
    }

    @Test
    fun `resolveInPath() should resolve the package to a directory in the given path`() {
        // Arrange
        val rootPath = Paths.get("some/path")
        val pkgRef = JvmPackageRef("dev/pelsmaeker/kode/types")

        // Act
        val path = pkgRef.resolveInPath(rootPath)

        // Assert
        assertEquals(rootPath.resolve("dev/pelsmaeker/kode/types"), path)
    }

}