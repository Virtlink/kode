package net.pelsmaeker.kode.types

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

/** Tests the JVM primitive types. */
class JvmPrimitiveTypesTests {

    @Test
    fun `JvmVoid should be as expected`() {
        // Act
        val type = JvmVoid

        // Assert
        assertEquals(JvmTypeSort.Void, type.sort)
        assertEquals(JvmTypeKind.Void, type.kind)
        assertEquals("V", type.descriptor)
        assertEquals("V", type.signature)
        assertTrue(type.isPrimitive)
        assertFalse(type.isClass)
        assertFalse(type.isInterface)
        assertFalse(type.isArray)
        assertFalse(type.isTypeVariable)
        assertEquals("void", type.toString())
    }

    @Test
    fun `JvmVoid_boxed() should return JvmTypes_VoidClass_ref()`() {
        // Act
        val boxed = JvmVoid.boxed()

        // Assert
        assertEquals(JvmTypes.VoidClass.ref(), boxed)
    }

    @Test
    fun `JvmBoolean should be as expected`() {
        // Act
        val type = JvmBoolean

        // Assert
        assertEquals(JvmTypeSort.Boolean, type.sort)
        assertEquals(JvmTypeKind.Integer, type.kind)
        assertEquals("Z", type.descriptor)
        assertEquals("Z", type.signature)
        assertTrue(type.isPrimitive)
        assertFalse(type.isClass)
        assertFalse(type.isInterface)
        assertFalse(type.isArray)
        assertFalse(type.isTypeVariable)
        assertEquals("boolean", type.toString())
    }

    @Test
    fun `JvmBoolean_boxed() should return JvmTypes_BooleanClass_ref()`() {
        // Act
        val boxed = JvmBoolean.boxed()

        // Assert
        assertEquals(JvmTypes.BooleanClass.ref(), boxed)
    }

    @Test
    fun `JvmCharacter should be as expected`() {
        // Act
        val type = JvmCharacter

        // Assert
        assertEquals(JvmTypeSort.Character, type.sort)
        assertEquals(JvmTypeKind.Integer, type.kind)
        assertEquals("C", type.descriptor)
        assertEquals("C", type.signature)
        assertTrue(type.isPrimitive)
        assertFalse(type.isClass)
        assertFalse(type.isInterface)
        assertFalse(type.isArray)
        assertFalse(type.isTypeVariable)
        assertEquals("char", type.toString())
    }

    @Test
    fun `JvmCharacter_boxed() should return JvmTypes_CharacterClass_ref()`() {
        // Act
        val boxed = JvmCharacter.boxed()

        // Assert
        assertEquals(JvmTypes.CharacterClass.ref(), boxed)
    }

    @Test
    fun `JvmByte should be as expected`() {
        // Act
        val type = JvmByte

        // Assert
        assertEquals(JvmTypeSort.Byte, type.sort)
        assertEquals(JvmTypeKind.Integer, type.kind)
        assertEquals("B", type.descriptor)
        assertEquals("B", type.signature)
        assertTrue(type.isPrimitive)
        assertFalse(type.isClass)
        assertFalse(type.isInterface)
        assertFalse(type.isArray)
        assertFalse(type.isTypeVariable)
        assertEquals("byte", type.toString())
    }

    @Test
    fun `JvmByte_boxed() should return JvmTypes_ByteClass_ref()`() {
        // Act
        val boxed = JvmByte.boxed()

        // Assert
        assertEquals(JvmTypes.ByteClass.ref(), boxed)
    }

    @Test
    fun `JvmShort should be as expected`() {
        // Act
        val type = JvmShort

        // Assert
        assertEquals(JvmTypeSort.Short, type.sort)
        assertEquals(JvmTypeKind.Integer, type.kind)
        assertEquals("S", type.descriptor)
        assertEquals("S", type.signature)
        assertTrue(type.isPrimitive)
        assertFalse(type.isClass)
        assertFalse(type.isInterface)
        assertFalse(type.isArray)
        assertFalse(type.isTypeVariable)
        assertEquals("short", type.toString())
    }

    @Test
    fun `JvmShort_boxed() should return JvmTypes_ShortClass_ref()`() {
        // Act
        val boxed = JvmShort.boxed()

        // Assert
        assertEquals(JvmTypes.ShortClass.ref(), boxed)
    }

    @Test
    fun `JvmInteger should be as expected`() {
        // Act
        val type = JvmInteger

        // Assert
        assertEquals(JvmTypeSort.Integer, type.sort)
        assertEquals(JvmTypeKind.Integer, type.kind)
        assertEquals("I", type.descriptor)
        assertEquals("I", type.signature)
        assertTrue(type.isPrimitive)
        assertFalse(type.isClass)
        assertFalse(type.isInterface)
        assertFalse(type.isArray)
        assertFalse(type.isTypeVariable)
        assertEquals("int", type.toString())
    }

    @Test
    fun `JvmInteger_boxed() should return JvmTypes_IntegerClass_ref()`() {
        // Act
        val boxed = JvmInteger.boxed()

        // Assert
        assertEquals(JvmTypes.IntegerClass.ref(), boxed)
    }

    @Test
    fun `JvmLong should be as expected`() {
        // Act
        val type = JvmLong

        // Assert
        assertEquals(JvmTypeSort.Long, type.sort)
        assertEquals(JvmTypeKind.Long, type.kind)
        assertEquals("J", type.descriptor)
        assertEquals("J", type.signature)
        assertTrue(type.isPrimitive)
        assertFalse(type.isClass)
        assertFalse(type.isInterface)
        assertFalse(type.isArray)
        assertFalse(type.isTypeVariable)
        assertEquals("long", type.toString())
    }

    @Test
    fun `JvmLong_boxed() should return JvmTypes_LongClass_ref()`() {
        // Act
        val boxed = JvmLong.boxed()

        // Assert
        assertEquals(JvmTypes.LongClass.ref(), boxed)
    }

    @Test
    fun `JvmFloat should be as expected`() {
        // Act
        val type = JvmFloat

        // Assert
        assertEquals(JvmTypeSort.Float, type.sort)
        assertEquals(JvmTypeKind.Float, type.kind)
        assertEquals("F", type.descriptor)
        assertEquals("F", type.signature)
        assertTrue(type.isPrimitive)
        assertFalse(type.isClass)
        assertFalse(type.isInterface)
        assertFalse(type.isArray)
        assertFalse(type.isTypeVariable)
        assertEquals("float", type.toString())
    }

    @Test
    fun `JvmFloat_boxed() should return JvmTypes_FloatClass_ref()`() {
        // Act
        val boxed = JvmFloat.boxed()

        // Assert
        assertEquals(JvmTypes.FloatClass.ref(), boxed)
    }

    @Test
    fun `JvmDouble should be as expected`() {
        // Act
        val type = JvmDouble

        // Assert
        assertEquals(JvmTypeSort.Double, type.sort)
        assertEquals(JvmTypeKind.Double, type.kind)
        assertEquals("D", type.descriptor)
        assertEquals("D", type.signature)
        assertTrue(type.isPrimitive)
        assertFalse(type.isClass)
        assertFalse(type.isInterface)
        assertFalse(type.isArray)
        assertFalse(type.isTypeVariable)
        assertEquals("double", type.toString())
    }

    @Test
    fun `JvmDouble_boxed() should return JvmTypes_DoubleClass_ref()`() {
        // Act
        val boxed = JvmDouble.boxed()

        // Assert
        assertEquals(JvmTypes.DoubleClass.ref(), boxed)
    }

}