package com.virtlink.kode.utils

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

/** Tests the `IntExt` functions. */
class IntExtTests {

    @Test
    fun `bits() should return an empty sequence, when the value is 0`() {
        // Arrange
        val value = 0

        // Act
        val bits = value.bits().toList()

        // Assert
        assertEquals(emptyList<Int>(), bits)
    }

    @Test
    fun `bits() should return the bits that are set`() {
        // Arrange
        val value = 0b1000000_00110011_10101010_11101110

        // Act
        val bits = value.bits().toList()

        // Assert
        assertEquals(listOf(
            0b0000000_00000000_00000000_00000010,
            0b0000000_00000000_00000000_00000100,
            0b0000000_00000000_00000000_00001000,
            0b0000000_00000000_00000000_00100000,
            0b0000000_00000000_00000000_01000000,
            0b0000000_00000000_00000000_10000000,
            0b0000000_00000000_00000010_00000000,
            0b0000000_00000000_00001000_00000000,
            0b0000000_00000000_00100000_00000000,
            0b0000000_00000000_10000000_00000000,
            0b0000000_00000001_00000000_00000000,
            0b0000000_00000010_00000000_00000000,
            0b0000000_00010000_00000000_00000000,
            0b0000000_00100000_00000000_00000000,
            0b1000000_00000000_00000000_00000000,
        ), bits)
    }

    @Test
    fun `bits() should return all the bits, when all are set`() {
        // Arrange
        val value = -1

        // Act
        val bits = value.bits().toList()

        // Assert
        assertEquals(32, bits.size)
    }

}