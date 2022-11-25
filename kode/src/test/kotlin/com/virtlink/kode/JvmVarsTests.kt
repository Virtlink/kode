package com.virtlink.kode

import com.virtlink.kode.types.*
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

/** Tests the [JvmVars] class. */
class JvmVarsTests {

    @Test
    fun `addThis() should add the implicit 'this' argument`() {
        // Arrange
        val scope = JvmSimpleScope()
        val thisType = JvmClassRef.of(String::class.java)
        val vars = JvmVars(scope)

        // Act
        val v = vars.addThis(thisType)

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
        val vars = JvmVars(JvmSimpleScope())
        vars.addThis(thisType)

        // Act/Assert
        assertThrows<IllegalStateException> {
            vars.addThis(thisType)
        }
    }

    @Test
    fun `addThis() should throw, when other arguments are already present`() {
        // Arrange
        val thisType = JvmClassRef.of(String::class.java)
        val vars = JvmVars(JvmSimpleScope())
        vars.addArgument(JvmParam(JvmInteger))

        // Act/Assert
        assertThrows<IllegalStateException> {
            vars.addThis(thisType)
        }
    }

    @Test
    fun `addThis() should throw, when other variables are already present`() {
        // Arrange
        val thisType = JvmClassRef.of(String::class.java)
        val vars = JvmVars(JvmSimpleScope())
        vars.addLocalVar(JvmInteger)

        // Act/Assert
        assertThrows<IllegalStateException> {
            vars.addThis(thisType)
        }
    }

    @Test
    fun `addThis() should throw, when 'this' is already present from a parent`() {
        // Arrange
        val thisType = JvmClassRef.of(String::class.java)
        val parentVars = JvmVars(JvmSimpleScope())
        parentVars.addThis(thisType)
        val vars = JvmVars(JvmSimpleScope(), parent = parentVars)

        // Act/Assert
        assertThrows<IllegalStateException> {
            vars.addThis(thisType)
        }
    }

    @Test
    fun `addThis() should throw, when other arguments and variables are already present from a parent`() {
        // Arrange
        val thisType = JvmClassRef.of(String::class.java)
        val parentVars = JvmVars(JvmSimpleScope())
        parentVars.addLocalVar(JvmInteger)
        val vars = JvmVars(JvmSimpleScope(), parent = parentVars)

        // Act/Assert
        assertThrows<IllegalStateException> {
            vars.addThis(thisType)
        }
    }

    @Test
    fun `addArgument() should add an argument`() {
        // Arrange
        val scope = JvmSimpleScope()
        val name = "myArg"
        val type = JvmClassRef.of(String::class.java)
        val vars = JvmVars(scope)

        // Act
        val v = vars.addArgument(JvmParam(type, name))

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
        val vars = JvmVars(scope)

        // Act
        val v = vars.addArgument(JvmParam(type))

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
        val vars = JvmVars(scope)
        vars.addThis(JvmTypes.Object.ref())

        // Act
        val v = vars.addArgument(JvmParam(type, name))

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
        val vars = JvmVars(scope)
        vars.addArgument(JvmParam(JvmInteger))
        vars.addArgument(JvmParam(JvmShort))

        // Act
        val v = vars.addArgument(JvmParam(type, name))

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
        val vars = JvmVars(scope)
        vars.addThis(JvmTypes.Object.ref())
        vars.addArgument(JvmParam(JvmInteger))
        vars.addArgument(JvmParam(JvmShort))

        // Act
        val v = vars.addArgument(JvmParam(type, name))

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
        val vars = JvmVars(scope)
        vars.addArgument(JvmParam(JvmLong))        // 2 slots
        vars.addArgument(JvmParam(JvmShort))
        vars.addArgument(JvmParam(JvmDouble))      // 2 slots

        // Act
        val v = vars.addArgument(JvmParam(type, name))

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
        val parentVars = JvmVars(JvmSimpleScope())
        parentVars.addArgument(JvmParam(JvmInteger))
        val vars = JvmVars(scope, parent = parentVars)

        // Act
        val v = vars.addArgument(JvmParam(type, name))

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
        val parentVars = JvmVars(JvmSimpleScope())
        parentVars.addThis(JvmTypes.Object.ref())
        val vars = JvmVars(scope, parent = parentVars)

        // Act
        val v = vars.addArgument(JvmParam(type, name))

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
        val vars = JvmVars(JvmSimpleScope())
        vars.addLocalVar(JvmInteger)

        // Act/Assert
        assertThrows<IllegalStateException> {
            vars.addArgument(JvmParam(type, name))
        }
    }

    @Test
    fun `addArgument() should throw, when local variables are present from a parent`() {
        // Arrange
        val name = "myArg"
        val type = JvmClassRef.of(String::class.java)
        val parentVars = JvmVars(JvmSimpleScope())
        parentVars.addLocalVar(JvmInteger)
        val vars = JvmVars(JvmSimpleScope(), parent = parentVars)

        // Act/Assert
        assertThrows<IllegalStateException> {
            vars.addArgument(JvmParam(type, name))
        }
    }

    @Test
    fun `addArgument() should throw, when an argument with the same name is already present`() {
        // Arrange
        val name = "myArg"
        val type = JvmClassRef.of(String::class.java)
        val vars = JvmVars(JvmSimpleScope())
        vars.addArgument(JvmParam(type, name))

        // Act/Assert
        assertThrows<IllegalArgumentException> {
            vars.addArgument(JvmParam(type, name))
        }
    }

    @Test
    fun `addLocalVar() should add a local variable`() {
        // Arrange
        val scope = JvmSimpleScope()
        val name = "myVar"
        val type = JvmClassRef.of(String::class.java)
        val vars = JvmVars(scope)

        // Act
        val v = vars.addLocalVar(type, name)

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
        val vars = JvmVars(scope)

        // Act
        val v = vars.addLocalVar(type)

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
        val vars = JvmVars(scope)
        vars.addThis(JvmTypes.Object.ref())

        // Act
        val v = vars.addLocalVar(type, name)

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
        val vars = JvmVars(scope)
        vars.addArgument(JvmParam(JvmInteger))
        vars.addArgument(JvmParam(JvmShort))

        // Act
        val v = vars.addLocalVar(type, name)

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
        val vars = JvmVars(scope)
        vars.addLocalVar(JvmInteger)
        vars.addLocalVar(JvmShort)

        // Act
        val v = vars.addLocalVar(type, name)

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
        val vars = JvmVars(scope)
        vars.addThis(JvmTypes.Object.ref())
        vars.addArgument(JvmParam(JvmInteger))
        vars.addArgument(JvmParam(JvmShort))

        // Act
        val v = vars.addLocalVar(type, name)

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
        val vars = JvmVars(scope)
        vars.addThis(JvmTypes.Object.ref())
        vars.addArgument(JvmParam(JvmInteger))
        vars.addArgument(JvmParam(JvmShort))
        vars.addLocalVar(JvmByte)

        // Act
        val v = vars.addLocalVar(type, name)

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
        val vars = JvmVars(scope)
        vars.addLocalVar(JvmLong)        // 2 slots
        vars.addLocalVar(JvmShort)
        vars.addLocalVar(JvmDouble)      // 2 slots

        // Act
        val v = vars.addLocalVar(type, name)

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
        val parentVars = JvmVars(JvmSimpleScope())
        parentVars.addLocalVar(JvmInteger)
        val vars = JvmVars(scope, parent = parentVars)

        // Act
        val v = vars.addLocalVar(type, name)

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
        val parentVars = JvmVars(JvmSimpleScope())
        parentVars.addThis(JvmTypes.Object.ref())
        val vars = JvmVars(scope, parent = parentVars)

        // Act
        val v = vars.addLocalVar(type, name)

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
        val vars = JvmVars(JvmSimpleScope())
        vars.addArgument(JvmParam(type, name))

        // Act/Assert
        assertThrows<IllegalArgumentException> {
            vars.addLocalVar(type, name)
        }
    }

    @Test
    fun `addLocalVar() should throw, when a local variable with the same name is already present`() {
        // Arrange
        val name = "myArg"
        val type = JvmClassRef.of(String::class.java)
        val vars = JvmVars(JvmSimpleScope())
        vars.addLocalVar(type, name)

        // Act/Assert
        assertThrows<IllegalArgumentException> {
            vars.addLocalVar(type, name)
        }
    }

    @Test
    fun `getThis() should return the 'this' variable, when it has been set`() {
        // Arrange
        val scope = JvmSimpleScope()
        val thisType = JvmClassRef.of(String::class.java)
        val vars = JvmVars(scope)
        vars.addThis(thisType)

        // Act
        val v = vars.getThis()!!

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
        val parentVars = JvmVars(scope)
        parentVars.addThis(thisType)
        val vars = JvmVars(JvmSimpleScope(), parent = parentVars)

        // Act
        val v = vars.getThis()!!

        // Assert
        assertEquals(thisType, v.type)
        assertEquals(0, v.offset)
        assertEquals(scope, v.scope)
        assertNull(v.name)
    }

    @Test
    fun `getThis() should return false, when it has not been set`() {
        // Arrange
        val vars = JvmVars(JvmSimpleScope())

        // Act
        val v = vars.getThis()

        // Assert
        assertNull(v)
    }

    @Test
    fun `getArgument(String) should return the argument with the given name, when it has been added`() {
        // Arrange
        val scope = JvmSimpleScope()
        val name = "myArg"
        val type = JvmClassRef.of(String::class.java)
        val vars = JvmVars(scope)
        vars.addArgument(JvmParam(JvmInteger))
        vars.addArgument(JvmParam(JvmLong))
        vars.addArgument(JvmParam(type, name))

        // Act
        val v = vars.getArgument(name)!!

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
        val parentVars = JvmVars(scope)
        parentVars.addArgument(JvmParam(JvmInteger))
        parentVars.addArgument(JvmParam(JvmLong))
        parentVars.addArgument(JvmParam(type, name))
        val vars = JvmVars(JvmSimpleScope(), parent = parentVars)

        // Act
        val v = vars.getArgument(name)!!

        // Assert
        assertEquals(type, v.type)
        assertEquals(3, v.offset)
        assertEquals(scope, v.scope)
        assertEquals(name, v.name)
    }

    @Test
    fun `getArgument(String) should return null, when it has not been added`() {
        // Arrange
        val vars = JvmVars(JvmSimpleScope())

        // Act
        val v = vars.getArgument("myArg")

        // Assert
        assertNull(v)
    }

    @Test
    fun `getArgument(Int) should return the argument with the given index, when it has been added`() {
        // Arrange
        val scope = JvmSimpleScope()
        val name = "myArg"
        val type = JvmClassRef.of(String::class.java)
        val vars = JvmVars(scope)
        vars.addArgument(JvmParam(JvmInteger))
        vars.addArgument(JvmParam(JvmLong))
        vars.addArgument(JvmParam(type, name))

        // Act
        val v = vars.getArgument(2)

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
        val parentVars = JvmVars(scope)
        parentVars.addArgument(JvmParam(JvmInteger))
        parentVars.addArgument(JvmParam(JvmLong))
        parentVars.addArgument(JvmParam(type, name))
        val vars = JvmVars(JvmSimpleScope(), parent = parentVars)

        // Act
        val v = vars.getArgument(2)

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
        val parentVars = JvmVars(JvmSimpleScope())
        parentVars.addArgument(JvmParam(JvmInteger))
        parentVars.addArgument(JvmParam(JvmLong))
        val vars = JvmVars(scope, parent = parentVars)
        vars.addArgument(JvmParam(type, name))

        // Act
        val v = vars.getArgument(2)

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
        val vars = JvmVars(scope)
        vars.addThis(JvmTypes.Object.ref())
        vars.addArgument(JvmParam(type, name))

        // Act
        val v = vars.getArgument(0)

        // Assert
        assertEquals(type, v.type)
        assertEquals(1, v.offset)
        assertEquals(scope, v.scope)
        assertEquals(name, v.name)
    }

    @Test
    fun `getArgument(Int) should throw, when it has not been added`() {
        // Arrange
        val vars = JvmVars(JvmSimpleScope())

        // Act
        assertThrows<IllegalArgumentException> {
            vars.getArgument(2)
        }
    }

    @Test
    fun `getLocalVar(String) should return the local variable with the given name, when it has been added`() {
        // Arrange
        val scope = JvmSimpleScope()
        val name = "myVar"
        val type = JvmClassRef.of(String::class.java)
        val vars = JvmVars(scope)
        vars.addLocalVar(JvmInteger)
        vars.addLocalVar(JvmLong)
        vars.addLocalVar(type, name)

        // Act
        val v = vars.getLocalVar(name)!!

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
        val parentVars = JvmVars(scope)
        parentVars.addLocalVar(JvmInteger)
        parentVars.addLocalVar(JvmLong)
        parentVars.addLocalVar(type, name)
        val vars = JvmVars(JvmSimpleScope(), parent = parentVars)

        // Act
        val v = vars.getLocalVar(name)!!

        // Assert
        assertEquals(type, v.type)
        assertEquals(3, v.offset)
        assertEquals(scope, v.scope)
        assertEquals(name, v.name)
    }

    @Test
    fun `getLocalVar(String) should return null, when it has not been added`() {
        // Arrange
        val vars = JvmVars(JvmSimpleScope())

        // Act
        val v = vars.getLocalVar("myVar")

        // Assert
        assertNull(v)
    }

    @Test
    fun `getLocalVar(Int) should return the local variable with the given index, when it has been added`() {
        // Arrange
        val scope = JvmSimpleScope()
        val name = "myVar"
        val type = JvmClassRef.of(String::class.java)
        val vars = JvmVars(scope)
        vars.addLocalVar(JvmInteger)
        vars.addLocalVar(JvmLong)
        vars.addLocalVar(type, name)

        // Act
        val v = vars.getLocalVar(2)

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
        val parentVars = JvmVars(scope)
        parentVars.addLocalVar(JvmInteger)
        parentVars.addLocalVar(JvmLong)
        parentVars.addLocalVar(type, name)
        val vars = JvmVars(JvmSimpleScope(), parent = parentVars)

        // Act
        val v = vars.getLocalVar(2)

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
        val parentVars = JvmVars(JvmSimpleScope())
        parentVars.addLocalVar(JvmInteger)
        parentVars.addLocalVar(JvmLong)
        val vars = JvmVars(scope, parent = parentVars)
        vars.addLocalVar(type, name)

        // Act
        val v = vars.getLocalVar(2)

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
        val vars = JvmVars(scope)
        vars.addThis(JvmTypes.Object.ref())
        vars.addLocalVar(type, name)

        // Act
        val v = vars.getLocalVar(0)

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
        val vars = JvmVars(scope)
        vars.addThis(JvmTypes.Object.ref())
        vars.addArgument(JvmParam(JvmInteger))
        vars.addArgument(JvmParam(JvmLong))
        vars.addLocalVar(type, name)

        // Act
        val v = vars.getLocalVar(0)

        // Assert
        assertEquals(type, v.type)
        assertEquals(4, v.offset)
        assertEquals(scope, v.scope)
        assertEquals(name, v.name)
    }

    @Test
    fun `getLocalVar(Int) should throw, when it has not been added`() {
        // Arrange
        val vars = JvmVars(JvmSimpleScope())

        // Act
        assertThrows<IllegalArgumentException> {
            vars.getLocalVar(2)
        }
    }

    @Test
    fun `hasThis() should return true, when it has been set`() {
        // Arrange
        val scope = JvmSimpleScope()
        val thisType = JvmClassRef.of(String::class.java)
        val vars = JvmVars(scope)
        vars.addThis(thisType)

        // Act
        val result = vars.hasThis()

        // Assert
        assertTrue(result)
    }

    @Test
    fun `hasThis() should return true, when it has been set in a parent`() {
        // Arrange
        val scope = JvmSimpleScope()
        val thisType = JvmClassRef.of(String::class.java)
        val parentVars = JvmVars(scope)
        parentVars.addThis(thisType)
        val vars = JvmVars(JvmSimpleScope(), parent = parentVars)

        // Act
        val result = vars.hasThis()

        // Assert
        assertTrue(result)
    }

    @Test
    fun `hasThis() should return false, when it has not been set`() {
        // Arrange
        val vars = JvmVars(JvmSimpleScope())

        // Act
        val result = vars.hasThis()

        // Assert
        assertFalse(result)
    }

    @Test
    fun `hasArgument(String) should return true, when it has been added`() {
        // Arrange
        val scope = JvmSimpleScope()
        val name = "myArg"
        val type = JvmClassRef.of(String::class.java)
        val vars = JvmVars(scope)
        vars.addArgument(JvmParam(JvmInteger))
        vars.addArgument(JvmParam(JvmLong))
        vars.addArgument(JvmParam(type, name))

        // Act
        val result = vars.hasArgument(name)

        // Assert
        assertTrue(result)
    }

    @Test
    fun `hasArgument(String) should return true, when it has been added in a parent`() {
        // Arrange
        val scope = JvmSimpleScope()
        val name = "myArg"
        val type = JvmClassRef.of(String::class.java)
        val parentVars = JvmVars(scope)
        parentVars.addArgument(JvmParam(JvmInteger))
        parentVars.addArgument(JvmParam(JvmLong))
        parentVars.addArgument(JvmParam(type, name))
        val vars = JvmVars(JvmSimpleScope(), parent = parentVars)

        // Act
        val result = vars.hasArgument(name)

        // Assert
        assertTrue(result)
    }

    @Test
    fun `hasArgument(String) should return false, when it has not been added`() {
        // Arrange
        val vars = JvmVars(JvmSimpleScope())

        // Act
        val result = vars.hasArgument("myArg")

        // Assert
        assertFalse(result)
    }

    @Test
    fun `hasLocalVar(String) should return true, when it has been added`() {
        // Arrange
        val scope = JvmSimpleScope()
        val name = "myVar"
        val type = JvmClassRef.of(String::class.java)
        val vars = JvmVars(scope)
        vars.addLocalVar(JvmInteger)
        vars.addLocalVar(JvmLong)
        vars.addLocalVar(type, name)

        // Act
        val result = vars.hasLocalVar(name)

        // Assert
        assertTrue(result)
    }

    @Test
    fun `hasLocalVar(String) should return true, when it has been added in a parent`() {
        // Arrange
        val scope = JvmSimpleScope()
        val name = "myVar"
        val type = JvmClassRef.of(String::class.java)
        val parentVars = JvmVars(scope)
        parentVars.addLocalVar(JvmInteger)
        parentVars.addLocalVar(JvmLong)
        parentVars.addLocalVar(type, name)
        val vars = JvmVars(JvmSimpleScope(), parent = parentVars)

        // Act
        val result = vars.hasLocalVar(name)

        // Assert
        assertTrue(result)
    }

    @Test
    fun `hasLocalVar(String) should return false, when it has not been added`() {
        // Arrange
        val vars = JvmVars(JvmSimpleScope())

        // Act
        val result = vars.hasLocalVar("myVar")

        // Assert
        assertFalse(result)
    }

}