package com.virtlink.kode.types

import com.virtlink.kode.types.JvmPackageRef.Companion.ref
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Test
import java.nio.file.Paths

/** Tests the [JvmPackageRef] class. */
class JvmPackageRefTests {

    @Test
    fun `of() should construct a JvmPackageRef from a package`() {
        // Arrange
        val pkg = this::class.java.classLoader.getDefinedPackage("com.virtlink.kode.types")

        // Act
        val ref = pkg.ref()

        // Assert
        assertEquals("com/virtlink/kode/types", ref.internalName)
        assertEquals("com.virtlink.kode.types", ref.javaName)
        assertFalse(ref.isEmpty)
    }

    // TODO: Move this to JvmPackageDeclTests
    @Test
    fun `resolveInPath() should resolve the package to a directory in the given path`() {
        // Arrange
        val rootPath = Paths.get("some/path")
        val pkgDecl = JvmPackageDecl("com.virtlink.kode.types")

        // Act
        val path = pkgDecl.resolveInPath(rootPath)

        // Assert
        assertEquals(rootPath.resolve("com/virtlink/kode/types"), path)
    }

}