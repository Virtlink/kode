package dev.pelsmaeker.kode

import dev.pelsmaeker.kode.types.*
import org.junit.jupiter.api.Assertions.*
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

    @Test
    fun `getThis() should return the 'this' variable, when it has been set`() {
        // Arrange
        val scope = JvmSimpleScope()
        val thisType = JvmClassRef.of(String::class.java)
        val localVars = JvmLocalVars(scope)
        localVars.addThis(thisType)

        // Act
        val v = localVars.getThis()!!

        // Assert
        assertEquals(thisType, v.type)
        assertEquals(0, v.offset)
        assertEquals(scope, v.scope)
        assertNull(v.name)
    }

    @Test
    fun `getThis() should return the 'this' variable, when it has been set in a parent`() {
        // Arrange
        val scope = JvmSimpleScope()
        val thisType = JvmClassRef.of(String::class.java)
        val parentLocalVars = JvmLocalVars(scope)
        parentLocalVars.addThis(thisType)
        val localVars = JvmLocalVars(JvmSimpleScope(), parent = parentLocalVars)

        // Act
        val v = localVars.getThis()!!

        // Assert
        assertEquals(thisType, v.type)
        assertEquals(0, v.offset)
        assertEquals(scope, v.scope)
        assertNull(v.name)
    }

    @Test
    fun `getThis() should return false, when it has not been set`() {
        // Arrange
        val localVars = JvmLocalVars(JvmSimpleScope())

        // Act
        val v = localVars.getThis()

        // Assert
        assertNull(v)
    }

    @Test
    fun `getArgument(String) should return the argument with the given name, when it has been added`() {
        // Arrange
        val scope = JvmSimpleScope()
        val name = "myArg"
        val type = JvmClassRef.of(String::class.java)
        val localVars = JvmLocalVars(scope)
        localVars.addArgument(JvmParam(JvmInteger))
        localVars.addArgument(JvmParam(JvmLong))
        localVars.addArgument(JvmParam(type, name))

        // Act
        val v = localVars.getArgument(name)!!

        // Assert
        assertEquals(type, v.type)
        assertEquals(3, v.offset)
        assertEquals(scope, v.scope)
        assertEquals(name, v.name)
    }

    @Test
    fun `getArgument(String) should return the argument with the given name, when it has been added in a parent`() {
        // Arrange
        val scope = JvmSimpleScope()
        val name = "myArg"
        val type = JvmClassRef.of(String::class.java)
        val parentLocalVars = JvmLocalVars(scope)
        parentLocalVars.addArgument(JvmParam(JvmInteger))
        parentLocalVars.addArgument(JvmParam(JvmLong))
        parentLocalVars.addArgument(JvmParam(type, name))
        val localVars = JvmLocalVars(JvmSimpleScope(), parent = parentLocalVars)

        // Act
        val v = localVars.getArgument(name)!!

        // Assert
        assertEquals(type, v.type)
        assertEquals(3, v.offset)
        assertEquals(scope, v.scope)
        assertEquals(name, v.name)
    }

    @Test
    fun `getArgument(String) should return null, when it has not been added`() {
        // Arrange
        val localVars = JvmLocalVars(JvmSimpleScope())

        // Act
        val v = localVars.getArgument("myArg")

        // Assert
        assertNull(v)
    }

    @Test
    fun `getArgument(Int) should return the argument with the given index, when it has been added`() {
        // Arrange
        val scope = JvmSimpleScope()
        val name = "myArg"
        val type = JvmClassRef.of(String::class.java)
        val localVars = JvmLocalVars(scope)
        localVars.addArgument(JvmParam(JvmInteger))
        localVars.addArgument(JvmParam(JvmLong))
        localVars.addArgument(JvmParam(type, name))

        // Act
        val v = localVars.getArgument(2)

        // Assert
        assertEquals(type, v.type)
        assertEquals(3, v.offset)
        assertEquals(scope, v.scope)
        assertEquals(name, v.name)
    }

    @Test
    fun `getArgument(Int) should return the argument with the given index, when it has been added in a parent`() {
        // Arrange
        val scope = JvmSimpleScope()
        val name = "myArg"
        val type = JvmClassRef.of(String::class.java)
        val parentLocalVars = JvmLocalVars(scope)
        parentLocalVars.addArgument(JvmParam(JvmInteger))
        parentLocalVars.addArgument(JvmParam(JvmLong))
        parentLocalVars.addArgument(JvmParam(type, name))
        val localVars = JvmLocalVars(JvmSimpleScope(), parent = parentLocalVars)

        // Act
        val v = localVars.getArgument(2)

        // Assert
        assertEquals(type, v.type)
        assertEquals(3, v.offset)
        assertEquals(scope, v.scope)
        assertEquals(name, v.name)
    }

    @Test
    fun `getArgument(Int) should return the argument with the given index, when it follows arguments in the parent`() {
        // Arrange
        val scope = JvmSimpleScope()
        val name = "myArg"
        val type = JvmClassRef.of(String::class.java)
        val parentLocalVars = JvmLocalVars(JvmSimpleScope())
        parentLocalVars.addArgument(JvmParam(JvmInteger))
        parentLocalVars.addArgument(JvmParam(JvmLong))
        val localVars = JvmLocalVars(scope, parent = parentLocalVars)
        localVars.addArgument(JvmParam(type, name))

        // Act
        val v = localVars.getArgument(2)

        // Assert
        assertEquals(type, v.type)
        assertEquals(3, v.offset)
        assertEquals(scope, v.scope)
        assertEquals(name, v.name)
    }

    @Test
    fun `getArgument(Int) should return the argument with the given index, when it follows 'this'`() {
        // Arrange
        val scope = JvmSimpleScope()
        val name = "myArg"
        val type = JvmClassRef.of(String::class.java)
        val localVars = JvmLocalVars(scope)
        localVars.addThis(JvmTypes.Object.ref())
        localVars.addArgument(JvmParam(type, name))

        // Act
        val v = localVars.getArgument(0)

        // Assert
        assertEquals(type, v.type)
        assertEquals(1, v.offset)
        assertEquals(scope, v.scope)
        assertEquals(name, v.name)
    }

    @Test
    fun `getArgument(Int) should throw, when it has not been added`() {
        // Arrange
        val localVars = JvmLocalVars(JvmSimpleScope())

        // Act
        assertThrows<IllegalArgumentException> {
            localVars.getArgument(2)
        }
    }

    @Test
    fun `getLocalVar(String) should return the local variable with the given name, when it has been added`() {
        // Arrange
        val scope = JvmSimpleScope()
        val name = "myVar"
        val type = JvmClassRef.of(String::class.java)
        val localVars = JvmLocalVars(scope)
        localVars.addLocalVar(JvmInteger)
        localVars.addLocalVar(JvmLong)
        localVars.addLocalVar(type, name)

        // Act
        val v = localVars.getLocalVar(name)!!

        // Assert
        assertEquals(type, v.type)
        assertEquals(3, v.offset)
        assertEquals(scope, v.scope)
        assertEquals(name, v.name)
    }

    @Test
    fun `getLocalVar(String) should return the local variable with the given name, when it has been added in a parent`() {
        // Arrange
        val scope = JvmSimpleScope()
        val name = "myVar"
        val type = JvmClassRef.of(String::class.java)
        val parentLocalVars = JvmLocalVars(scope)
        parentLocalVars.addLocalVar(JvmInteger)
        parentLocalVars.addLocalVar(JvmLong)
        parentLocalVars.addLocalVar(type, name)
        val localVars = JvmLocalVars(JvmSimpleScope(), parent = parentLocalVars)

        // Act
        val v = localVars.getLocalVar(name)!!

        // Assert
        assertEquals(type, v.type)
        assertEquals(3, v.offset)
        assertEquals(scope, v.scope)
        assertEquals(name, v.name)
    }

    @Test
    fun `getLocalVar(String) should return null, when it has not been added`() {
        // Arrange
        val localVars = JvmLocalVars(JvmSimpleScope())

        // Act
        val v = localVars.getLocalVar("myVar")

        // Assert
        assertNull(v)
    }

    @Test
    fun `getLocalVar(Int) should return the local variable with the given index, when it has been added`() {
        // Arrange
        val scope = JvmSimpleScope()
        val name = "myVar"
        val type = JvmClassRef.of(String::class.java)
        val localVars = JvmLocalVars(scope)
        localVars.addLocalVar(JvmInteger)
        localVars.addLocalVar(JvmLong)
        localVars.addLocalVar(type, name)

        // Act
        val v = localVars.getLocalVar(2)

        // Assert
        assertEquals(type, v.type)
        assertEquals(3, v.offset)
        assertEquals(scope, v.scope)
        assertEquals(name, v.name)
    }

    @Test
    fun `getLocalVar(Int) should return the local variable with the given index, when it has been added in a parent`() {
        // Arrange
        val scope = JvmSimpleScope()
        val name = "myVar"
        val type = JvmClassRef.of(String::class.java)
        val parentLocalVars = JvmLocalVars(scope)
        parentLocalVars.addLocalVar(JvmInteger)
        parentLocalVars.addLocalVar(JvmLong)
        parentLocalVars.addLocalVar(type, name)
        val localVars = JvmLocalVars(JvmSimpleScope(), parent = parentLocalVars)

        // Act
        val v = localVars.getLocalVar(2)

        // Assert
        assertEquals(type, v.type)
        assertEquals(3, v.offset)
        assertEquals(scope, v.scope)
        assertEquals(name, v.name)
    }

    @Test
    fun `getLocalVar(Int) should return the local variable with the given index, when it follows local variables in the parent`() {
        // Arrange
        val scope = JvmSimpleScope()
        val name = "myVar"
        val type = JvmClassRef.of(String::class.java)
        val parentLocalVars = JvmLocalVars(JvmSimpleScope())
        parentLocalVars.addLocalVar(JvmInteger)
        parentLocalVars.addLocalVar(JvmLong)
        val localVars = JvmLocalVars(scope, parent = parentLocalVars)
        localVars.addLocalVar(type, name)

        // Act
        val v = localVars.getLocalVar(2)

        // Assert
        assertEquals(type, v.type)
        assertEquals(3, v.offset)
        assertEquals(scope, v.scope)
        assertEquals(name, v.name)
    }

    @Test
    fun `getLocalVar(Int) should return the local variable with the given index, when it follows 'this'`() {
        // Arrange
        val scope = JvmSimpleScope()
        val name = "myVar"
        val type = JvmClassRef.of(String::class.java)
        val localVars = JvmLocalVars(scope)
        localVars.addThis(JvmTypes.Object.ref())
        localVars.addLocalVar(type, name)

        // Act
        val v = localVars.getLocalVar(0)

        // Assert
        assertEquals(type, v.type)
        assertEquals(1, v.offset)
        assertEquals(scope, v.scope)
        assertEquals(name, v.name)
    }

    @Test
    fun `getLocalVar(Int) should return the local variable with the given index, when it follows arguments and 'this'`() {
        // Arrange
        val scope = JvmSimpleScope()
        val name = "myVar"
        val type = JvmClassRef.of(String::class.java)
        val localVars = JvmLocalVars(scope)
        localVars.addThis(JvmTypes.Object.ref())
        localVars.addArgument(JvmParam(JvmInteger))
        localVars.addArgument(JvmParam(JvmLong))
        localVars.addLocalVar(type, name)

        // Act
        val v = localVars.getLocalVar(0)

        // Assert
        assertEquals(type, v.type)
        assertEquals(4, v.offset)
        assertEquals(scope, v.scope)
        assertEquals(name, v.name)
    }

    @Test
    fun `getLocalVar(Int) should throw, when it has not been added`() {
        // Arrange
        val localVars = JvmLocalVars(JvmSimpleScope())

        // Act
        assertThrows<IllegalArgumentException> {
            localVars.getLocalVar(2)
        }
    }

    @Test
    fun `hasThis() should return true, when it has been set`() {
        // Arrange
        val scope = JvmSimpleScope()
        val thisType = JvmClassRef.of(String::class.java)
        val localVars = JvmLocalVars(scope)
        localVars.addThis(thisType)

        // Act
        val result = localVars.hasThis()

        // Assert
        assertTrue(result)
    }

    @Test
    fun `hasThis() should return true, when it has been set in a parent`() {
        // Arrange
        val scope = JvmSimpleScope()
        val thisType = JvmClassRef.of(String::class.java)
        val parentLocalVars = JvmLocalVars(scope)
        parentLocalVars.addThis(thisType)
        val localVars = JvmLocalVars(JvmSimpleScope(), parent = parentLocalVars)

        // Act
        val result = localVars.hasThis()

        // Assert
        assertTrue(result)
    }

    @Test
    fun `hasThis() should return false, when it has not been set`() {
        // Arrange
        val localVars = JvmLocalVars(JvmSimpleScope())

        // Act
        val result = localVars.hasThis()

        // Assert
        assertFalse(result)
    }

    @Test
    fun `hasArgument(String) should return true, when it has been added`() {
        // Arrange
        val scope = JvmSimpleScope()
        val name = "myArg"
        val type = JvmClassRef.of(String::class.java)
        val localVars = JvmLocalVars(scope)
        localVars.addArgument(JvmParam(JvmInteger))
        localVars.addArgument(JvmParam(JvmLong))
        localVars.addArgument(JvmParam(type, name))

        // Act
        val result = localVars.hasArgument(name)

        // Assert
        assertTrue(result)
    }

    @Test
    fun `hasArgument(String) should return true, when it has been added in a parent`() {
        // Arrange
        val scope = JvmSimpleScope()
        val name = "myArg"
        val type = JvmClassRef.of(String::class.java)
        val parentLocalVars = JvmLocalVars(scope)
        parentLocalVars.addArgument(JvmParam(JvmInteger))
        parentLocalVars.addArgument(JvmParam(JvmLong))
        parentLocalVars.addArgument(JvmParam(type, name))
        val localVars = JvmLocalVars(JvmSimpleScope(), parent = parentLocalVars)

        // Act
        val result = localVars.hasArgument(name)

        // Assert
        assertTrue(result)
    }

    @Test
    fun `hasArgument(String) should return false, when it has not been added`() {
        // Arrange
        val localVars = JvmLocalVars(JvmSimpleScope())

        // Act
        val result = localVars.hasArgument("myArg")

        // Assert
        assertFalse(result)
    }

    @Test
    fun `hasLocalVar(String) should return true, when it has been added`() {
        // Arrange
        val scope = JvmSimpleScope()
        val name = "myVar"
        val type = JvmClassRef.of(String::class.java)
        val localVars = JvmLocalVars(scope)
        localVars.addLocalVar(JvmInteger)
        localVars.addLocalVar(JvmLong)
        localVars.addLocalVar(type, name)

        // Act
        val result = localVars.hasLocalVar(name)

        // Assert
        assertTrue(result)
    }

    @Test
    fun `hasLocalVar(String) should return true, when it has been added in a parent`() {
        // Arrange
        val scope = JvmSimpleScope()
        val name = "myVar"
        val type = JvmClassRef.of(String::class.java)
        val parentLocalVars = JvmLocalVars(scope)
        parentLocalVars.addLocalVar(JvmInteger)
        parentLocalVars.addLocalVar(JvmLong)
        parentLocalVars.addLocalVar(type, name)
        val localVars = JvmLocalVars(JvmSimpleScope(), parent = parentLocalVars)

        // Act
        val result = localVars.hasLocalVar(name)

        // Assert
        assertTrue(result)
    }

    @Test
    fun `hasLocalVar(String) should return false, when it has not been added`() {
        // Arrange
        val localVars = JvmLocalVars(JvmSimpleScope())

        // Act
        val result = localVars.hasLocalVar("myVar")

        // Assert
        assertFalse(result)
    }

}