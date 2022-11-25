package com.virtlink.kode.utils

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows
import java.lang.IllegalStateException

/** Tests the [Eponymizer] class. */
class EponymizerTests {

    @Test
    fun `get() should generate names with an increasing index`() {
        // Arrange
        val eponymizer = Eponymizer("root")

        // Act
        val name0 = eponymizer.get("foo")
        val name1 = eponymizer.get("foo")
        val name2 = eponymizer.get("foo")
        val name3 = eponymizer.get("foo")

        // Assert
        assertEquals("foo", name0)
        assertEquals("foo1", name1)
        assertEquals("foo2", name2)
        assertEquals("foo3", name3)
    }

    @Test
    fun `get() should generate names with an increasing index, when no hint is provided`() {
        // Arrange
        val eponymizer = Eponymizer("root")

        // Act
        val name0 = eponymizer.get()
        val name1 = eponymizer.get()
        val name2 = eponymizer.get()
        val name3 = eponymizer.get()

        // Assert
        assertEquals("x", name0)
        assertEquals("x1", name1)
        assertEquals("x2", name2)
        assertEquals("x3", name3)
    }

    @Test
    fun `get() should skip known names`() {
        // Arrange
        val eponymizer = Eponymizer("root")
        eponymizer.put("foo2")
        eponymizer.put("foo4")

        // Act
        val name0 = eponymizer.get("foo")
        val name1 = eponymizer.get("foo")
        val name3 = eponymizer.get("foo")
        val name5 = eponymizer.get("foo")
        val name6 = eponymizer.get("foo")

        // Assert
        assertEquals("foo", name0)
        assertEquals("foo1", name1)
        assertEquals("foo3", name3)
        assertEquals("foo5", name5)
        assertEquals("foo6", name6)
    }

    @Test
    fun `get() should skip known names, when they are initial`() {
        // Arrange
        val eponymizer = Eponymizer("root")
        eponymizer.put("foo")    // foo

        // Act
        val name1 = eponymizer.get("foo")
        val name2 = eponymizer.get("foo")

        // Assert
        assertEquals("foo1", name1)
        assertEquals("foo2", name2)
    }

    @Test
    fun `get() should skip known names, when they are next`() {
        // Arrange
        val eponymizer = Eponymizer("root")
        eponymizer.get("foo")       // foo
        eponymizer.get("foo")       // foo1
        eponymizer.put("foo2")   // foo2

        // Act
        val name3 = eponymizer.get("foo")
        val name4 = eponymizer.get("foo")

        // Assert
        assertEquals("foo3", name3)
        assertEquals("foo4", name4)
    }

    @Test
    fun `get() should not care about known names, when they have already been generated`() {
        // Arrange
        val eponymizer = Eponymizer("root")
        eponymizer.get("foo")       // foo
        eponymizer.get("foo")       // foo1
        eponymizer.put("foo")    // foo
        eponymizer.put("foo1")   // foo1

        // Act
        val name2 = eponymizer.get("foo")
        val name3 = eponymizer.get("foo")

        // Assert
        assertEquals("foo2", name2)
        assertEquals("foo3", name3)
    }

    @Test
    fun `get() should not care about known names, when they have leading zeros`() {
        // Arrange
        val eponymizer = Eponymizer("root")
        eponymizer.put("foo007")
        eponymizer.put("foo0")

        // Act
        val name0 = eponymizer.get("foo")
        val name1 = eponymizer.get("foo")

        // Assert
        assertEquals("foo", name0)
        assertEquals("foo1", name1)
    }

    @Test
    fun `get() should not generate names with leading zeros`() {
        // Arrange
        val eponymizer = Eponymizer("root")

        // Act
        val name0 = eponymizer.get("foo007")
        val name1 = eponymizer.get("foo007")
        val name2 = eponymizer.get("foo007")

        // Assert
        assertEquals("foo", name0)
        assertEquals("foo1", name1)
        assertEquals("foo2", name2)
    }

    @Test
    fun `get() should generate names with an increasing index, when already defined in parent`() {
        // Arrange
        val rootEponymizer = Eponymizer("root")
        rootEponymizer.get("foo")       // foo
        rootEponymizer.get("foo")       // foo1
        val eponymizer = rootEponymizer.scope("child")

        // Act
        val name2 = eponymizer.get("foo")
        val name3 = eponymizer.get("foo")

        // Assert
        assertEquals("foo2", name2)
        assertEquals("foo3", name3)
    }

    @Test
    fun `get() should skip known names, when already known to the parent`() {
        // Arrange
        val rootEponymizer = Eponymizer("root")
        rootEponymizer.put("foo2")
        rootEponymizer.put("foo4")
        val eponymizer = rootEponymizer.scope("child")

        // Act
        val name0 = eponymizer.get("foo")
        val name1 = eponymizer.get("foo")
        val name3 = eponymizer.get("foo")
        val name5 = eponymizer.get("foo")
        val name6 = eponymizer.get("foo")

        // Assert
        assertEquals("foo", name0)
        assertEquals("foo1", name1)
        assertEquals("foo3", name3)
        assertEquals("foo5", name5)
        assertEquals("foo6", name6)
    }

    @Test
    fun `get() should throw, when there are unclosed children`() {
        // Arrange
        val rootEponymizer = Eponymizer("root")
        val childEponymizer = rootEponymizer.scope("child")

        // Act/Assert
        assertThrows<IllegalStateException> {
            rootEponymizer.get("foo")
        }

        childEponymizer.close()

        assertDoesNotThrow {
            rootEponymizer.get("foo")
        }
    }

    @Test
    fun `put() should throw, when there are unclosed children`() {
        // Arrange
        val rootEponymizer = Eponymizer("root")
        val childEponymizer = rootEponymizer.scope("child")

        // Act/Assert
        assertThrows<IllegalStateException> {
            rootEponymizer.put("foo")
        }

        childEponymizer.close()

        assertDoesNotThrow {
            rootEponymizer.put("foo")
        }
    }

    @Test
    fun `close() should throw, when there are unclosed children`() {
        // Arrange
        val rootEponymizer = Eponymizer("root")
        val childEponymizer = rootEponymizer.scope("child")

        // Act/Assert
        assertThrows<IllegalStateException> {
            rootEponymizer.close()
        }

        childEponymizer.close()

        assertDoesNotThrow {
            rootEponymizer.close()
        }
    }

}