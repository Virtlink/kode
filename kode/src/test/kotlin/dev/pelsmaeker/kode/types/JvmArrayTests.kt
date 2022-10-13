package dev.pelsmaeker.kode.types

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

/** Tests the [JvmArray] class. */
class JvmArrayTests {

    @Test
    fun `constructor should construct a JvmArray from a JvmType`() {
        // Arrange
        val stringType = JvmTypes.String.ref()

        // Act
        val arrayType = JvmArray(stringType)

        // Assert
        assertEquals(JvmTypeSort.Array, arrayType.sort)
        assertEquals(JvmTypeKind.Object, arrayType.kind)
        assertEquals("[Ljava/lang/String;", arrayType.descriptor)
        assertEquals("[Ljava/lang/String;", arrayType.signature)
        assertFalse(arrayType.isPrimitive)
        assertFalse(arrayType.isClass)
        assertFalse(arrayType.isInterface)
        assertTrue(arrayType.isArray)
        assertFalse(arrayType.isTypeVariable)
        assertEquals(1, arrayType.dimensionCount)
    }

    @Test
    fun `constructor should construct nested JvmArrays`() {
        // Arrange
        val stringType = JvmTypes.String.ref()
        val arrayType = JvmArray(stringType)

        // Act
        val arrayArrayType = JvmArray(arrayType)

        // Assert
        assertEquals(JvmTypeSort.Array, arrayArrayType.sort)
        assertEquals(JvmTypeKind.Object, arrayArrayType.kind)
        assertEquals("[[Ljava/lang/String;", arrayArrayType.descriptor)
        assertEquals("[[Ljava/lang/String;", arrayArrayType.signature)
        assertFalse(arrayArrayType.isPrimitive)
        assertFalse(arrayArrayType.isClass)
        assertFalse(arrayArrayType.isInterface)
        assertTrue(arrayArrayType.isArray)
        assertFalse(arrayArrayType.isTypeVariable)
        assertEquals(2, arrayArrayType.dimensionCount)
    }

}