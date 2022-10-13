package dev.pelsmaeker.kode

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import kotlin.random.Random

/** Tests the [JvmFieldModifiers] implementations. */
class JvmFieldModifiersTests {

    private fun create(value: Int): JvmFieldModifiers = JvmFieldModifiers(value)

    private fun from(modifiers: Iterable<JvmFieldModifiers>): JvmFieldModifiers = JvmFieldModifiers.from(modifiers)

    private fun getMembers(): List<JvmFieldModifiers> = JvmFieldModifiers.allMembers

    @Test
    fun `value should return the value of the enum`() {
        // Arrange
        val rnd = Random(123)
        val member = getMembers().random(rnd)

        // Act
        val value = member.value

        // Assert
        assertEquals(member.value, value)
    }

    @Test
    fun `size should return the number of members in the enum`() {
        // Arrange
        val rnd = Random(123)
        val selected = getMembers().shuffled(rnd).take(3)
        // Or-ing members together should result in a combined enum
        val combined = selected.fold(create(0)) { acc, it -> acc or it }

        // Act
        val size = combined.size

        // Assert
        assertEquals(selected.size, size)
    }

    @Test
    fun `isEmpty() should return true when the enum is empty`() {
        // Arrange
        val rnd = Random(123)
        val selected = getMembers().shuffled(rnd).take(2)
        // And-ing unrelated members together should result in an empty enum
        val combined = selected.fold(create(0)) { acc, it -> acc and it }

        // Act
        val isEmpty = combined.isEmpty()

        // Assert
        assertEquals(true, isEmpty)
    }

    @Test
    fun `or() should return a new enum with the members from both arguments`() {
        // Arrange
        val rnd = Random(123)
        val shuffled = getMembers().shuffled(rnd)
        val allSelected = shuffled.take(5)
        val selected1 = allSelected.take(allSelected.size / 2)
        val selected2 = allSelected.drop(allSelected.size / 2)
        val common = allSelected.shuffled(rnd).take(2)
        val value1 = from(selected1 + common)
        val value2 = from(selected2 + common)

        // Act
        val combined = value1 or value2

        // Assert
        assertEquals((selected1 + selected2 + common).toSet().sorted(), combined.toList().sorted())
    }

    @Test
    fun `and() should return a new enum with the members common in both arguments`() {
        // Arrange
        val rnd = Random(123)
        val shuffled = getMembers().shuffled(rnd)
        val allSelected = shuffled.take(5)
        val selected1 = allSelected.take(allSelected.size / 2)
        val selected2 = allSelected.drop(allSelected.size / 2)
        val common = allSelected.shuffled(rnd).take(2)
        val value1 = from(selected1 + common)
        val value2 = from(selected2 + common)

        // Act
        val combined = value1 and value2

        // Assert
        assertEquals(common.sorted(), combined.toList().sorted())
    }

    @Test
    fun `not() should return all members not in the enum`() {
        // Arrange
        val rnd = Random(123)
        val selected = getMembers().shuffled(rnd).take(3)
        val value = from(selected)

        // Act
        val notSelected = value.not()

        // Assert
        @Suppress("ConvertArgumentToSet")
        assertEquals((getMembers() - selected).sorted(), notSelected.toList().sorted())
    }

    @Test
    fun `iterator() should return the members of the enum in ascending order`() {
        // Arrange
        val rnd = Random(123)
        val selected = getMembers().shuffled(rnd).take(3)
        val value = from(selected)

        // Act
        val members = value.toList()

        // Assert
        assertEquals(selected.sorted(), members)
    }

    @Test
    fun `iterator() can iterate through all members`() {
        // Arrange
        val allMembers = getMembers()
        val value = from(allMembers)

        // Act
        val members = value.toList()

        // Assert
        assertEquals(allMembers.sorted(), members)
    }

    @Test
    fun `toString() can print the names of all members`() {
        // Arrange
        val allMembers = getMembers()
        val value = from(allMembers)

        // Act/Assert
        assertDoesNotThrow {
            value.toString()
        }
    }

}