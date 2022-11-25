package com.virtlink.kode.types

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.lang.IllegalStateException

/** Tests the [JvmTypeVar] class. */
class JvmTypeVarTests {

    @Test
    fun `should reference a type variable with the specified name`() {
        // Act
        val typeVar = JvmTypeVar("X")

        // Assert
        assertEquals("X", typeVar.name)
        assertEquals(JvmTypeSort.TypeParam, typeVar.sort)
        //assertEquals(JvmTypeKind.?, typeVar.kind)
        assertThrows<IllegalStateException> { typeVar.descriptor }
        assertEquals("TX;", typeVar.signature)
        assertFalse(typeVar.isPrimitive)
        assertFalse(typeVar.isClass)
        assertFalse(typeVar.isInterface)
        assertFalse(typeVar.isArray)
        assertTrue(typeVar.isTypeVariable)
    }

}