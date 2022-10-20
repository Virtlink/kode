package dev.pelsmaeker.kode

import dev.pelsmaeker.kode.types.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

/** Tests the [JvmLocalVars] class. */
class JvmLocalVarsTests {

    @Test
    fun `addThis() should add the implicit 'this' argument`() {
        // Arrange
        val scope = JvmSimpleScope()
        val thisType = JvmClassRef.of(String::class.java)
        val localVars = JvmLocalVars(scope)

        // Act
        val v = localVars.addThis(thisType)

        // Assert
        assertEquals(thisType, v.type)
        assertEquals(0, v.offset)
        assertEquals(scope, v.scope)
        assertNull(v.name)
    }

    @Test
    fun `addThis() should throw, when called twice`() {
        // Arrange
        val thisType = JvmClassRef.of(String::class.java)
        val localVars = JvmLocalVars(JvmSimpleScope())
        localVars.addThis(thisType)

        // Act/Assert
        assertThrows<IllegalStateException> {
            localVars.addThis(thisType)
        }
    }

    @Test
    fun `addThis() should throw, when other arguments are already present`() {
        // Arrange
        val thisType = JvmClassRef.of(String::class.java)
        val localVars = JvmLocalVars(JvmSimpleScope())
        localVars.addArgument(JvmParam(JvmInteger))

        // Act/Assert
        assertThrows<IllegalStateException> {
            localVars.addThis(thisType)
        }
    }

    @Test
    fun `addThis() should throw, when other variables are already present`() {
        // Arrange
        val thisType = JvmClassRef.of(String::class.java)
        val localVars = JvmLocalVars(JvmSimpleScope())
        localVars.addLocalVar(JvmInteger)

        // Act/Assert
        assertThrows<IllegalStateException> {
            localVars.addThis(thisType)
        }
    }

    @Test
    fun `addThis() should throw, when 'this' is already present from a parent`() {
        // Arrange
        val thisType = JvmClassRef.of(String::class.java)
        val parentLocalVars = JvmLocalVars(JvmSimpleScope())
        parentLocalVars.addThis(thisType)
        val localVars = JvmLocalVars(JvmSimpleScope(), parent = parentLocalVars)

        // Act/Assert
        assertThrows<IllegalStateException> {
            localVars.addThis(thisType)
        }
    }

    @Test
    fun `addThis() should throw, when other arguments and variables are already present from a parent`() {
        // Arrange
        val thisType = JvmClassRef.of(String::class.java)
        val parentLocalVars = JvmLocalVars(JvmSimpleScope())
        parentLocalVars.addLocalVar(JvmInteger)
        val localVars = JvmLocalVars(JvmSimpleScope(), parent = parentLocalVars)

        // Act/Assert
        assertThrows<IllegalStateException> {
            localVars.addThis(thisType)
        }
    }

    @Test
    fun `addArgument() should add an argument`() {
        // Arrange
        val scope = JvmSimpleScope()
        val name = "myArg"
        val type = JvmClassRef.of(String::class.java)
        val localVars = JvmLocalVars(scope)

        // Act
        val v = localVars.addArgument(JvmParam(type, name))

        // Assert
        assertEquals(type, v.type)
        assertEquals(0, v.offset)
        assertEquals(scope, v.scope)
        assertEquals(name, v.name)
    }

    @Test
    fun `addArgument() should add an argument without a name`() {
        // Arrange
        val scope = JvmSimpleScope()
        val type = JvmClassRef.of(String::class.java)
        val localVars = JvmLocalVars(scope)

        // Act
        val v = localVars.addArgument(JvmParam(type))

        // Assert
        assertEquals(type, v.type)
        assertEquals(0, v.offset)
        assertEquals(scope, v.scope)
        assertNull(v.name)
    }

    @Test
    fun `addArgument() should add an argument after 'this'`() {
        // Arrange
        val scope = JvmSimpleScope()
        val name = "myArg"
        val type = JvmClassRef.of(String::class.java)
        val localVars = JvmLocalVars(scope)
        localVars.addThis(JvmTypes.Object.ref())

        // Act
        val v = localVars.addArgument(JvmParam(type, name))

        // Assert
        assertEquals(type, v.type)
        assertEquals(1, v.offset)
        assertEquals(scope, v.scope)
        assertEquals(name, v.name)
    }

    @Test
    fun `addArgument() should add an argument after other arguments`() {
        // Arrange
        val scope = JvmSimpleScope()
        val name = "myArg"
        val type = JvmClassRef.of(String::class.java)
        val localVars = JvmLocalVars(scope)
        localVars.addArgument(JvmParam(JvmInteger))
        localVars.addArgument(JvmParam(JvmShort))

        // Act
        val v = localVars.addArgument(JvmParam(type, name))

        // Assert
        assertEquals(type, v.type)
        assertEquals(2, v.offset)
        assertEquals(scope, v.scope)
        assertEquals(name, v.name)
    }

    @Test
    fun `addArgument() should add an argument after 'this' and other arguments`() {
        // Arrange
        val scope = JvmSimpleScope()
        val name = "myArg"
        val type = JvmClassRef.of(String::class.java)
        val localVars = JvmLocalVars(scope)
        localVars.addThis(JvmTypes.Object.ref())
        localVars.addArgument(JvmParam(JvmInteger))
        localVars.addArgument(JvmParam(JvmShort))

        // Act
        val v = localVars.addArgument(JvmParam(type, name))

        // Assert
        assertEquals(type, v.type)
        assertEquals(3, v.offset)
        assertEquals(scope, v.scope)
        assertEquals(name, v.name)
    }

    @Test
    fun `addArgument() should add an argument after two slot arguments`() {
        // Arrange
        val scope = JvmSimpleScope()
        val name = "myArg"
        val type = JvmClassRef.of(String::class.java)
        val localVars = JvmLocalVars(scope)
        localVars.addArgument(JvmParam(JvmLong))        // 2 slots
        localVars.addArgument(JvmParam(JvmShort))
        localVars.addArgument(JvmParam(JvmDouble))      // 2 slots

        // Act
        val v = localVars.addArgument(JvmParam(type, name))

        // Assert
        assertEquals(type, v.type)
        assertEquals(5, v.offset)
        assertEquals(scope, v.scope)
        assertEquals(name, v.name)
    }

    @Test
    fun `addArgument() should add an argument after other arguments are already present from a parent`() {
        // Arrange
        val scope = JvmSimpleScope()
        val name = "myArg"
        val type = JvmClassRef.of(String::class.java)
        val parentLocalVars = JvmLocalVars(JvmSimpleScope())
        parentLocalVars.addArgument(JvmParam(JvmInteger))
        val localVars = JvmLocalVars(scope, parent = parentLocalVars)

        // Act
        val v = localVars.addArgument(JvmParam(type, name))

        // Assert
        assertEquals(type, v.type)
        assertEquals(1, v.offset)
        assertEquals(scope, v.scope)
        assertEquals(name, v.name)
    }

    @Test
    fun `addArgument() should add an argument after 'this' is already present from a parent`() {
        // Arrange
        val scope = JvmSimpleScope()
        val name = "myArg"
        val type = JvmClassRef.of(String::class.java)
        val parentLocalVars = JvmLocalVars(JvmSimpleScope())
        parentLocalVars.addThis(JvmTypes.Object.ref())
        val localVars = JvmLocalVars(scope, parent = parentLocalVars)

        // Act
        val v = localVars.addArgument(JvmParam(type, name))

        // Assert
        assertEquals(type, v.type)
        assertEquals(1, v.offset)
        assertEquals(scope, v.scope)
        assertEquals(name, v.name)
    }

    @Test
    fun `addArgument() should throw, when local variables are present`() {
        // Arrange
        val name = "myArg"
        val type = JvmClassRef.of(String::class.java)
        val localVars = JvmLocalVars(JvmSimpleScope())
        localVars.addLocalVar(JvmInteger)

        // Act/Assert
        assertThrows<IllegalStateException> {
            localVars.addArgument(JvmParam(type, name))
        }
    }

    @Test
    fun `addArgument() should throw, when local variables are present from a parent`() {
        // Arrange
        val name = "myArg"
        val type = JvmClassRef.of(String::class.java)
        val parentLocalVars = JvmLocalVars(JvmSimpleScope())
        parentLocalVars.addLocalVar(JvmInteger)
        val localVars = JvmLocalVars(JvmSimpleScope(), parent = parentLocalVars)

        // Act/Assert
        assertThrows<IllegalStateException> {
            localVars.addArgument(JvmParam(type, name))
        }
    }

    @Test
    fun `addArgument() should throw, when an argument with the same name is already present`() {
        // Arrange
        val name = "myArg"
        val type = JvmClassRef.of(String::class.java)
        val localVars = JvmLocalVars(JvmSimpleScope())
        localVars.addArgument(JvmParam(type, name))

        // Act/Assert
        assertThrows<IllegalArgumentException> {
            localVars.addArgument(JvmParam(type, name))
        }
    }

    @Test
    fun `addLocalVar() should add a local variable`() {
        // Arrange
        val scope = JvmSimpleScope()
        val name = "myVar"
        val type = JvmClassRef.of(String::class.java)
        val localVars = JvmLocalVars(scope)

        // Act
        val v = localVars.addLocalVar(type, name)

        // Assert
        assertEquals(type, v.type)
        assertEquals(0, v.offset)
        assertEquals(scope, v.scope)
        assertEquals(name, v.name)
    }

    @Test
    fun `addLocalVar() should add a local variable without a name`() {
        // Arrange
        val scope = JvmSimpleScope()
        val type = JvmClassRef.of(String::class.java)
        val localVars = JvmLocalVars(scope)

        // Act
        val v = localVars.addLocalVar(type)

        // Assert
        assertEquals(type, v.type)
        assertEquals(0, v.offset)
        assertEquals(scope, v.scope)
        assertNull(v.name)
    }

    @Test
    fun `addLocalVar() should add a local variable after 'this'`() {
        // Arrange
        val scope = JvmSimpleScope()
        val name = "myArg"
        val type = JvmClassRef.of(String::class.java)
        val localVars = JvmLocalVars(scope)
        localVars.addThis(JvmTypes.Object.ref())

        // Act
        val v = localVars.addLocalVar(type, name)

        // Assert
        assertEquals(type, v.type)
        assertEquals(1, v.offset)
        assertEquals(scope, v.scope)
        assertEquals(name, v.name)
    }

    @Test
    fun `addLocalVar() should add a local variable after arguments`() {
        // Arrange
        val scope = JvmSimpleScope()
        val name = "myArg"
        val type = JvmClassRef.of(String::class.java)
        val localVars = JvmLocalVars(scope)
        localVars.addArgument(JvmParam(JvmInteger))
        localVars.addArgument(JvmParam(JvmShort))

        // Act
        val v = localVars.addLocalVar(type, name)

        // Assert
        assertEquals(type, v.type)
        assertEquals(2, v.offset)
        assertEquals(scope, v.scope)
        assertEquals(name, v.name)
    }

    @Test
    fun `addLocalVar() should add a local variable after other local variables`() {
        // Arrange
        val scope = JvmSimpleScope()
        val name = "myArg"
        val type = JvmClassRef.of(String::class.java)
        val localVars = JvmLocalVars(scope)
        localVars.addLocalVar(JvmInteger)
        localVars.addLocalVar(JvmShort)

        // Act
        val v = localVars.addLocalVar(type, name)

        // Assert
        assertEquals(type, v.type)
        assertEquals(2, v.offset)
        assertEquals(scope, v.scope)
        assertEquals(name, v.name)
    }

    @Test
    fun `addLocalVar() should add an argument after 'this' and arguments`() {
        // Arrange
        val scope = JvmSimpleScope()
        val name = "myArg"
        val type = JvmClassRef.of(String::class.java)
        val localVars = JvmLocalVars(scope)
        localVars.addThis(JvmTypes.Object.ref())
        localVars.addArgument(JvmParam(JvmInteger))
        localVars.addArgument(JvmParam(JvmShort))

        // Act
        val v = localVars.addLocalVar(type, name)

        // Assert
        assertEquals(type, v.type)
        assertEquals(3, v.offset)
        assertEquals(scope, v.scope)
        assertEquals(name, v.name)
    }

    @Test
    fun `addLocalVar() should add an argument after 'this' and arguments and local variables`() {
        // Arrange
        val scope = JvmSimpleScope()
        val name = "myArg"
        val type = JvmClassRef.of(String::class.java)
        val localVars = JvmLocalVars(scope)
        localVars.addThis(JvmTypes.Object.ref())
        localVars.addArgument(JvmParam(JvmInteger))
        localVars.addArgument(JvmParam(JvmShort))
        localVars.addLocalVar(JvmByte)

        // Act
        val v = localVars.addLocalVar(type, name)

        // Assert
        assertEquals(type, v.type)
        assertEquals(4, v.offset)
        assertEquals(scope, v.scope)
        assertEquals(name, v.name)
    }

    @Test
    fun `addLocalVar() should add a local variable after two slot local variables`() {
        // Arrange
        val scope = JvmSimpleScope()
        val name = "myArg"
        val type = JvmClassRef.of(String::class.java)
        val localVars = JvmLocalVars(scope)
        localVars.addLocalVar(JvmLong)        // 2 slots
        localVars.addLocalVar(JvmShort)
        localVars.addLocalVar(JvmDouble)      // 2 slots

        // Act
        val v = localVars.addLocalVar(type, name)

        // Assert
        assertEquals(type, v.type)
        assertEquals(5, v.offset)
        assertEquals(scope, v.scope)
        assertEquals(name, v.name)
    }

    @Test
    fun `addLocalVar() should add an argument after other local variables are already present from a parent`() {
        // Arrange
        val scope = JvmSimpleScope()
        val name = "myArg"
        val type = JvmClassRef.of(String::class.java)
        val parentLocalVars = JvmLocalVars(JvmSimpleScope())
        parentLocalVars.addLocalVar(JvmInteger)
        val localVars = JvmLocalVars(scope, parent = parentLocalVars)

        // Act
        val v = localVars.addLocalVar(type, name)

        // Assert
        assertEquals(type, v.type)
        assertEquals(1, v.offset)
        assertEquals(scope, v.scope)
        assertEquals(name, v.name)
    }

    @Test
    fun `addLocalVar() should add an argument after 'this' is already present from a parent`() {
        // Arrange
        val scope = JvmSimpleScope()
        val name = "myArg"
        val type = JvmClassRef.of(String::class.java)
        val parentLocalVars = JvmLocalVars(JvmSimpleScope())
        parentLocalVars.addThis(JvmTypes.Object.ref())
        val localVars = JvmLocalVars(scope, parent = parentLocalVars)

        // Act
        val v = localVars.addLocalVar(type, name)

        // Assert
        assertEquals(type, v.type)
        assertEquals(1, v.offset)
        assertEquals(scope, v.scope)
        assertEquals(name, v.name)
    }

    @Test
    fun `addLocalVar() should throw, when an argument with the same name is already present`() {
        // Arrange
        val name = "myArg"
        val type = JvmClassRef.of(String::class.java)
        val localVars = JvmLocalVars(JvmSimpleScope())
        localVars.addArgument(JvmParam(type, name))

        // Act/Assert
        assertThrows<IllegalArgumentException> {
            localVars.addLocalVar(type, name)
        }
    }

    @Test
    fun `addLocalVar() should throw, when a local variable with the same name is already present`() {
        // Arrange
        val name = "myArg"
        val type = JvmClassRef.of(String::class.java)
        val localVars = JvmLocalVars(JvmSimpleScope())
        localVars.addLocalVar(type, name)

        // Act/Assert
        assertThrows<IllegalArgumentException> {
            localVars.addLocalVar(type, name)
        }
    }
}