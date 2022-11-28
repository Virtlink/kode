package net.pelsmaeker.kode.types

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

/** Tests the [JvmTypeArg] class. */
class JvmTypeArgTests {

    @Test
    fun `should work as expected, when type is invariant`() {
        // Arrange
        val stringType = JvmTypes.String.ref()

        // Act
        val typeArg = JvmTypeArg(stringType)

        // Assert
        assertEquals(stringType, typeArg.type)
        assertEquals("Ljava/lang/String;", typeArg.signature)
        assertEquals("class java.lang.String", typeArg.toString())
    }

    @Test
    fun `should work as expected, when type is covariant`() {
        // Arrange
        val stringType = JvmTypes.String.ref()

        // Act
        val typeArg = JvmTypeArg(stringType, JvmTypeArgSort.Covariant)

        // Assert
        assertEquals(stringType, typeArg.type)
        assertEquals("+Ljava/lang/String;", typeArg.signature)
        assertEquals("out class java.lang.String", typeArg.toString())
    }

    @Test
    fun `should work as expected, when type is contravariant`() {
        // Arrange
        val stringType = JvmTypes.String.ref()

        // Act
        val typeArg = JvmTypeArg(stringType, JvmTypeArgSort.Contravariant)

        // Assert
        assertEquals(stringType, typeArg.type)
        assertEquals("-Ljava/lang/String;", typeArg.signature)
        assertEquals("in class java.lang.String", typeArg.toString())
    }

    @Test
    fun `should work as expected, when it is a wildcard`() {
        // Arrange
        val objectType = JvmTypes.Object.ref()

        // Act
        val typeArg = JvmTypeArg(objectType, JvmTypeArgSort.Wildcard)

        // Assert
        assertEquals(objectType, typeArg.type)
        assertEquals("*", typeArg.signature)
        assertEquals("*", typeArg.toString())
    }

    @Test
    fun `should throw, when it is a wildcard but the bound is not Object`() {
        // Arrange
        val stringType = JvmTypes.String.ref()

        // Act/Assert
        assertThrows<IllegalArgumentException> {
            JvmTypeArg(stringType, JvmTypeArgSort.Wildcard)
        }
    }

}