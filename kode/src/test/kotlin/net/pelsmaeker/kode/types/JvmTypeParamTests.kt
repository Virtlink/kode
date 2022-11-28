package net.pelsmaeker.kode.types

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

/** Tests the [JvmTypeParam] class. */
class JvmTypeParamTests {

    @Test
    fun `should work as expected, when type is invariant`() {
        // Arrange
        val classType = JvmTypes.String.ref()
        val interfaceType1 = JvmType.of(I::class.java) as JvmClassRef
        val interfaceType2 = JvmType.of(IT::class.java) as JvmClassRef

        // Act
        val typeParam = JvmTypeParam("T", classType, interfaceType1, interfaceType2)

        // Assert
        assertEquals(classType, typeParam.classBound)
        assertEquals(listOf(interfaceType1, interfaceType2), typeParam.interfaceBounds)
        assertEquals("T:Ljava/lang/String;:Lnet/pelsmaeker/kode/types/I;:Lnet/pelsmaeker/kode/types/IT<TT;>;", typeParam.signature)
    }

}