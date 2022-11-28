package net.pelsmaeker.kode

import net.pelsmaeker.kode.types.*
import net.pelsmaeker.kode.types.JvmMethodRef.Companion.getMethod
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.DynamicTest.dynamicTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestFactory
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows
import org.objectweb.asm.Opcodes
import java.lang.reflect.InvocationTargetException
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

/** Tests the [JvmScopeBuilder] class. */
class JvmScopeBuilderTests {

    //////////
    // LOAD //
    //////////
    
    @Test
    fun `iLoad() should load an Integer onto the stack`() {
        // Arrange
        val value = 6
        val (methodDecl, compiledClass) = buildEvalMethodWith(JvmInteger, listOf(JvmParam(JvmInteger, "a"))) {
            val a = vars.getArgument("a")!!
            iLoad(a)
            iReturn()
        }

        // Act
        val result = compiledClass.runEvalMethod(methodDecl, value)

        // Assert
        assertEquals(value, result)
    }

    @Test
    fun `lLoad() should load a Long onto the stack`() {
        // Arrange
        val value = 6L
        val (methodDecl, compiledClass) = buildEvalMethodWith(JvmLong, listOf(JvmParam(JvmLong, "a"))) {
            val a = vars.getArgument("a")!!
            lLoad(a)
            lReturn()
        }

        // Act
        val result = compiledClass.runEvalMethod(methodDecl, value)

        // Assert
        assertEquals(value, result)
    }

    @Test
    fun `fLoad() should load a Float onto the stack`() {
        // Arrange
        val value = 6.0f
        val (methodDecl, compiledClass) = buildEvalMethodWith(JvmFloat, listOf(JvmParam(JvmFloat, "a"))) {
            val a = vars.getArgument("a")!!
            fLoad(a)
            fReturn()
        }

        // Act
        val result = compiledClass.runEvalMethod(methodDecl, value)

        // Assert
        assertEquals(value, result)
    }

    @Test
    fun `dLoad() should load a Double onto the stack`() {
        // Arrange
        val value = 6.0
        val (methodDecl, compiledClass) = buildEvalMethodWith(JvmDouble, listOf(JvmParam(JvmDouble, "a"))) {
            val a = vars.getArgument("a")!!
            dLoad(a)
            dReturn()
        }

        // Act
        val result = compiledClass.runEvalMethod(methodDecl, value)

        // Assert
        assertEquals(value, result)
    }

    @Test
    fun `aLoad() should load an Object onto the stack`() {
        // Arrange
        val value = "My object"
        val (methodDecl, compiledClass) = buildEvalMethodWith(JvmTypes.Object.ref(), listOf(JvmParam(JvmTypes.Object.ref(), "a"))) {
            val a = vars.getArgument("a")!!
            aLoad(a)
            aReturn()
        }

        // Act
        val result = compiledClass.runEvalMethod(methodDecl, value)

        // Assert
        assertEquals(value, result)
    }

    @Test
    fun `load() should load a value onto the stack`() {
        // Arrange
        val (methodDecl, compiledClass) = buildEvalMethodWith(JvmVoid, listOf(
            JvmParam(JvmInteger, "i"),
            JvmParam(JvmLong, "l"),
            JvmParam(JvmFloat, "f"),
            JvmParam(JvmDouble, "d"),
            JvmParam(JvmTypes.Object.ref(), "a"),
        )) {
            val i = vars.getArgument("i")!!
            val l = vars.getArgument("l")!!
            val f = vars.getArgument("f")!!
            val d = vars.getArgument("d")!!
            val a = vars.getArgument("a")!!
            load(i)
            load(l)
            load(f)
            load(d)
            load(a)
            pop1()
            pop2()
            pop1()
            pop2()
            pop1()
            vReturn()
        }

        // Act/Assert
        compiledClass.runEvalMethod(methodDecl, 1, 2L, 3.0f, 4.0, "S")
    }

    ///////////
    // STORE //
    ///////////

    @Test
    fun `iStore() should store an Integer from the stack`() {
        // Arrange
        val value = 6
        val (methodDecl, compiledClass) = buildEvalMethodWith(JvmInteger) {
            val v = vars.addLocalVar(JvmInteger, "v")
            iConst(value)
            iStore(v)
            iLoad(v)
            iReturn()
        }

        // Act
        val result = compiledClass.runEvalMethod(methodDecl)

        // Assert
        assertEquals(value, result)
    }

    @Test
    fun `lStore() should store a Long from the stack`() {
        // Arrange
        val value = 6L
        val (methodDecl, compiledClass) = buildEvalMethodWith(JvmLong) {
            val v = vars.addLocalVar(JvmLong, "v")
            lConst(value)
            lStore(v)
            lLoad(v)
            lReturn()
        }

        // Act
        val result = compiledClass.runEvalMethod(methodDecl)

        // Assert
        assertEquals(value, result)
    }

    @Test
    fun `fStore() should store a Float from the stack`() {
        // Arrange
        val value = 6.0f
        val (methodDecl, compiledClass) = buildEvalMethodWith(JvmFloat) {
            val v = vars.addLocalVar(JvmFloat, "v")
            fConst(value)
            fStore(v)
            fLoad(v)
            fReturn()
        }

        // Act
        val result = compiledClass.runEvalMethod(methodDecl)

        // Assert
        assertEquals(value, result)
    }

    @Test
    fun `dStore() should store a Double from the stack`() {
        // Arrange
        val value = 6.0
        val (methodDecl, compiledClass) = buildEvalMethodWith(JvmDouble) {
            val v = vars.addLocalVar(JvmDouble, "v")
            dConst(value)
            dStore(v)
            dLoad(v)
            dReturn()
        }

        // Act
        val result = compiledClass.runEvalMethod(methodDecl)

        // Assert
        assertEquals(value, result)
    }

    @Test
    fun `aStore() should store an Object from the stack`() {
        // Arrange
        val value = "My object"
        val (methodDecl, compiledClass) = buildEvalMethodWith(JvmTypes.Object.ref()) {
            val v = vars.addLocalVar(JvmTypes.Object.ref(), "v")
            ldc(value)
            aStore(v)
            aLoad(v)
            aReturn()
        }

        // Act
        val result = compiledClass.runEvalMethod(methodDecl)

        // Assert
        assertEquals(value, result)
    }

    @Test
    fun `store() should store a value from the stack`() {
        // Arrange
        val (methodDecl, compiledClass) = buildEvalMethodWith() {
            val i = vars.addLocalVar(JvmInteger, "i")
            val l = vars.addLocalVar(JvmLong, "l")
            val f = vars.addLocalVar(JvmFloat, "f")
            val d = vars.addLocalVar(JvmDouble, "d")
            val a = vars.addLocalVar(JvmTypes.Object.ref(), "a")
            ldc("S")
            dConst(4.0)
            fConst(3.0f)
            lConst(2L)
            iConst(1)
            store(i)
            store(l)
            store(f)
            store(d)
            store(a)
            vReturn()
        }

        // Act/Assert
        compiledClass.runEvalMethod(methodDecl)
    }

    ///////////
    // STACK //
    ///////////

    @Test
    fun `pop1() should pop a category 1 value from the stack`() {
        // Arrange
        val value = 6
        val (methodDecl, compiledClass) = buildEvalMethodWith(JvmInteger) {
            iConst(value)
            iConst(42)
            pop1()      // value, 42 -> value
            iReturn()
        }

        // Act
        val result = compiledClass.runEvalMethod(methodDecl)

        // Assert
        assertEquals(value, result)
    }

    @Test
    fun `pop2() should pop a category 2 value from the stack`() {
        // Arrange
        val value = 6
        val (methodDecl, compiledClass) = buildEvalMethodWith(JvmInteger) {
            iConst(value)
            lConst(42L)
            pop2()      // value, 42L -> value
            iReturn()
        }

        // Act
        val result = compiledClass.runEvalMethod(methodDecl)

        // Assert
        assertEquals(value, result)
    }

    @Test
    fun `pop2() should pop two category 1 values from the stack`() {
        // Arrange
        val value = 6
        val (methodDecl, compiledClass) = buildEvalMethodWith(JvmInteger) {
            iConst(value)
            iConst(42)
            iConst(84)
            pop2()      // value, 42, 84 -> value
            iReturn()
        }

        // Act
        val result = compiledClass.runEvalMethod(methodDecl)

        // Assert
        assertEquals(value, result)
    }

    @Test
    fun `swap() should swap two values on the top of the stack`() {
        // Arrange
        val value = 6
        val (methodDecl, compiledClass) = buildEvalMethodWith(JvmInteger) {
            iConst(value)
            iConst(42)
            swap()      // value, 42 -> 42, value
            iReturn()
        }

        // Act
        val result = compiledClass.runEvalMethod(methodDecl)

        // Assert
        assertEquals(value, result)
    }

    @Test
    fun `dup1() should duplicate a category 1 value on the stack`() {
        // Arrange
        val value = 6
        val (methodDecl, compiledClass) = buildEvalMethodWith(JvmInteger) {
            iConst(value)
            dup1()      // value -> value, value
            pop1()      // value, value -> value
            iReturn()
        }

        // Act
        val result = compiledClass.runEvalMethod(methodDecl)

        // Assert
        assertEquals(value, result)
    }

    @Test
    fun `dup2() should duplicate a category 2 value on the stack`() {
        // Arrange
        val value = 6L
        val (methodDecl, compiledClass) = buildEvalMethodWith(JvmLong) {
            lConst(value)
            dup2()      // value -> value, value
            pop2()      // value, value -> value
            lReturn()
        }

        // Act
        val result = compiledClass.runEvalMethod(methodDecl)

        // Assert
        assertEquals(value, result)
    }

    @Test
    fun `dup2() should duplicate two category 1 values on the stack`() {
        // Arrange
        val value = 6
        val (methodDecl, compiledClass) = buildEvalMethodWith(JvmInteger) {
            iConst(42)
            iConst(value)
            dup2()      // 42, value -> 42, value, 42, value
            swap()      // 42, value, 42, value -> 42, value, value, 42
            pop2()      // 42, value, value, 42 -> 42, value
            swap()      // 42, value -> value, 42
            pop1()      // value, 42 -> value
            iReturn()
        }

        // Act
        val result = compiledClass.runEvalMethod(methodDecl)

        // Assert
        assertEquals(value, result)
    }

    @Test
    fun `dup() should duplicate a value on the stack`() {
        // Arrange
        val (methodDecl, compiledClass) = buildEvalMethodWith() {
            iConst(42)          // - -> 42
            lConst(42L)         // 42 -> 42, 42L
            dup(JvmLong)        // 42, 42L -> 42, 42L, 42L
            pop2()              // 42, 42L, 42L -> 42, 42L
            pop2()              // 42, 42L -> 42
            dup(JvmInteger)     // 42 -> 42, 42
            pop1()              // 42, 42 -> 42
            pop1()              // 42 -> -
            vReturn()
        }

        // Act/Assert
        assertDoesNotThrow {
            compiledClass.runEvalMethod(methodDecl)
        }
    }

    @Test
    fun `dup1_x1() should duplicate a category 1 value on the stack, skipping one`() {
        // Arrange
        val value = 6
        val (methodDecl, compiledClass) = buildEvalMethodWith(JvmInteger) {
            iConst(value)
            iConst(42)
            dup1_x1()      // value, 42 -> 42, value, 42
            pop1()         // 42, value, 42 -> 42, value
            iReturn()
        }

        // Act
        val result = compiledClass.runEvalMethod(methodDecl)

        // Assert
        assertEquals(value, result)
    }

    @Test
    fun `dup2_x1() should duplicate a category 2 value on the stack, skipping one`() {
        // Arrange
        val value = 6L
        val (methodDecl, compiledClass) = buildEvalMethodWith(JvmLong) {
            iConst(42)
            lConst(value)
            dup2_x1()      // 42, value -> value, 42, value
            pop2()         // value, 42, value -> value, 42
            pop1()         // value, 42 -> value
            lReturn()
        }

        // Act
        val result = compiledClass.runEvalMethod(methodDecl)

        // Assert
        assertEquals(value, result)
    }

    @Test
    fun `dup2_x1() should duplicate two category 1 values on the stack, skipping one`() {
        // Arrange
        val value = 6
        val (methodDecl, compiledClass) = buildEvalMethodWith(JvmInteger) {
            iConst(84)
            iConst(value)
            iConst(126)
            dup2_x1()      // 84, value, 126 -> value, 126, 84, value, 126
            pop1()         // value, 126, 84, value, 126 -> value, 126, 84, value
            iReturn()
        }

        // Act
        val result = compiledClass.runEvalMethod(methodDecl)

        // Assert
        assertEquals(value, result)
    }

    @Test
    fun `dup_x1() should duplicate a value on the stack, skipping one`() {
        // Arrange
        val (methodDecl, compiledClass) = buildEvalMethodWith() {
            iConst(42)          // - -> 42
            iConst(84)          // 42 -> 42, 84
            dup_x1(JvmInteger)  // 42, 84 -> 84, 42, 84
            pop1()              // 84, 42, 84 -> 84, 42
            pop1()              // 84, 42 -> 84
            lConst(21L)         // 84 -> 84, 21L
            dup_x1(JvmLong)     // 84, 21L -> 21L, 84, 21L
            pop2()              // 21L, 84, 21L -> 21L, 84
            pop1()              // 21L, 84 -> 21L
            pop2()              // 21L -> -
            vReturn()
        }

        // Act/Assert
        assertDoesNotThrow {
            compiledClass.runEvalMethod(methodDecl)
        }
    }

    @Test
    fun `dup1_x2() should duplicate a category 1 value on the stack, skipping one`() {
        // Arrange
        val value = 6
        val (methodDecl, compiledClass) = buildEvalMethodWith(JvmInteger) {
            iConst(21)
            iConst(value)
            iConst(42)
            dup1_x2()      // 21, value, 42 -> 21, 42, value, 42
            pop1()         // 21, 42, value, 42 -> 21, 42, value
            iReturn()
        }

        // Act
        val result = compiledClass.runEvalMethod(methodDecl)

        // Assert
        assertEquals(value, result)
    }

    @Test
    fun `dup2_x2() should duplicate a category 2 value on the stack, skipping two`() {
        // Arrange
        val value = 6L
        val (methodDecl, compiledClass) = buildEvalMethodWith(JvmLong) {
            lConst(21L)
            lConst(value)
            lConst(42L)
            dup2_x2()      // 21, value, 42 -> 21, 42, value, 42
            pop2()         // 21, 42, value, 42 -> 21, 42, value
            lReturn()
        }

        // Act
        val result = compiledClass.runEvalMethod(methodDecl)

        // Assert
        assertEquals(value, result)
    }

    @Test
    fun `dup2_x2() should duplicate two category 1 values on the stack, skipping two`() {
        // Arrange
        val value = 6
        val (methodDecl, compiledClass) = buildEvalMethodWith(JvmInteger) {
            iConst(84)
            iConst(21)
            iConst(value)
            iConst(126)
            dup2_x2()      // 84, 21, value, 126 -> value, 126, 84, 21, value, 126
            pop2()         // value, 126, 84, 21, value, 126 -> value, 126, 84, 21
            pop2()         // value, 126, 84, 21 -> value, 126
            pop1()         // value, 126 -> value
            iReturn()
        }

        // Act
        val result = compiledClass.runEvalMethod(methodDecl)

        // Assert
        assertEquals(value, result)
    }

    @Test
    fun `dup_x2() should duplicate a value on the stack, skipping two`() {
        // Arrange
        val (methodDecl, compiledClass) = buildEvalMethodWith() {
            iConst(21)          // - -> 21
            iConst(42)          // 21 -> 21, 42
            iConst(84)          // 21, 42 -> 21, 42, 84
            dup_x2(JvmInteger)  // 21, 42, 84 -> 84, 21, 42, 84
            pop2()              // 84, 21, 42, 84 -> 84, 21
            lConst(126L)        // 84, 21 -> 84, 21, 126L
            dup_x2(JvmLong)     // 84, 21, 126L -> 126L, 84, 21, 126L
            pop2()              // 126L, 84, 21, 126L -> 126L, 84, 21
            pop2()              // 126L, 84, 21 -> 126L
            pop2()              // 126L -> -
            vReturn()
        }

        // Act/Assert
        assertDoesNotThrow {
            compiledClass.runEvalMethod(methodDecl)
        }
    }

    ///////////////
    // CONSTANTS //
    ///////////////

    @Test
    fun `iConst_m1() should load an Integer constant -1 onto the stack`() {
        // Arrange
        val (methodDecl, compiledClass) = buildEvalMethodWith(JvmInteger) {
            iConst_m1()
            iReturn()
        }

        // Act
        val result = compiledClass.runEvalMethod(methodDecl)

        // Assert
        assertEquals(-1, result)
    }

    @Test
    fun `iConst_0() should load an Integer constant 0 onto the stack`() {
        // Arrange
        val (methodDecl, compiledClass) = buildEvalMethodWith(JvmInteger) {
            iConst_0()
            iReturn()
        }

        // Act
        val result = compiledClass.runEvalMethod(methodDecl)

        // Assert
        assertEquals(0, result)
    }

    @Test
    fun `iConst_1() should load an Integer constant 1 onto the stack`() {
        // Arrange
        val (methodDecl, compiledClass) = buildEvalMethodWith(JvmInteger) {
            iConst_1()
            iReturn()
        }

        // Act
        val result = compiledClass.runEvalMethod(methodDecl)

        // Assert
        assertEquals(1, result)
    }

    @Test
    fun `iConst_2() should load an Integer constant 2 onto the stack`() {
        // Arrange
        val (methodDecl, compiledClass) = buildEvalMethodWith(JvmInteger) {
            iConst_2()
            iReturn()
        }

        // Act
        val result = compiledClass.runEvalMethod(methodDecl)

        // Assert
        assertEquals(2, result)
    }

    @Test
    fun `iConst_3() should load an Integer constant 3 onto the stack`() {
        // Arrange
        val (methodDecl, compiledClass) = buildEvalMethodWith(JvmInteger) {
            iConst_3()
            iReturn()
        }

        // Act
        val result = compiledClass.runEvalMethod(methodDecl)

        // Assert
        assertEquals(3, result)
    }

    @Test
    fun `iConst_4() should load an Integer constant 4 onto the stack`() {
        // Arrange
        val (methodDecl, compiledClass) = buildEvalMethodWith(JvmInteger) {
            iConst_4()
            iReturn()
        }

        // Act
        val result = compiledClass.runEvalMethod(methodDecl)

        // Assert
        assertEquals(4, result)
    }

    @Test
    fun `iConst_5() should load an Integer constant 5 onto the stack`() {
        // Arrange
        val (methodDecl, compiledClass) = buildEvalMethodWith(JvmInteger) {
            iConst_5()
            iReturn()
        }

        // Act
        val result = compiledClass.runEvalMethod(methodDecl)

        // Assert
        assertEquals(5, result)
    }

    @Test
    fun `iConst() should load an Integer constant onto the stack`() {
        // Arrange
        val (methodDecl, compiledClass) = buildEvalMethodWith(JvmVoid) {
            iConst(Int.MIN_VALUE)
            iConst(Short.MIN_VALUE.toInt())
            iConst(Byte.MIN_VALUE.toInt())
            iConst(-3)
            iConst(-2)
            iConst(-1)
            iConst(0)
            iConst(1)
            iConst(2)
            iConst(3)
            iConst(4)
            iConst(5)
            iConst(6)
            iConst(7)
            iConst(Byte.MAX_VALUE.toInt())
            iConst(Short.MAX_VALUE.toInt())
            iConst(Int.MAX_VALUE)
            repeat(17) { pop1() }
            vReturn()
        }

        // Act/Assert
        compiledClass.runEvalMethod(methodDecl)
    }

    @Test
    fun `biPush() should load a Byte constant onto the stack as an Integer`() {
        // Arrange
        val value: Byte = 120
        val (methodDecl, compiledClass) = buildEvalMethodWith(JvmInteger) {
            biPush(value)
            iReturn()
        }

        // Act
        val result = compiledClass.runEvalMethod(methodDecl)

        // Assert
        assertEquals(value.toInt(), result)
    }

    @Test
    fun `siPush() should load a Short constant onto the stack as an Integer`() {
        // Arrange
        val value: Short = 30123
        val (methodDecl, compiledClass) = buildEvalMethodWith(JvmInteger) {
            siPush(value)
            iReturn()
        }

        // Act
        val result = compiledClass.runEvalMethod(methodDecl)

        // Assert
        assertEquals(value.toInt(), result)
    }

    @Test
    fun `lConst_0() should load a Long constant 0 onto the stack`() {
        // Arrange
        val (methodDecl, compiledClass) = buildEvalMethodWith(JvmLong) {
            lConst_0()
            lReturn()
        }

        // Act
        val result = compiledClass.runEvalMethod(methodDecl)

        // Assert
        assertEquals(0L, result)
    }

    @Test
    fun `lConst_1() should load a Long constant 1 onto the stack`() {
        // Arrange
        val (methodDecl, compiledClass) = buildEvalMethodWith(JvmLong) {
            lConst_1()
            lReturn()
        }

        // Act
        val result = compiledClass.runEvalMethod(methodDecl)

        // Assert
        assertEquals(1L, result)
    }

    @Test
    fun `lConst() should load a Long constant onto the stack`() {
        // Arrange
        val (methodDecl, compiledClass) = buildEvalMethodWith(JvmVoid) {
            lConst(Long.MIN_VALUE)
            lConst(0)
            lConst(1)
            lConst(Long.MAX_VALUE)
            repeat(4) { pop2() }
            vReturn()
        }

        // Act/Assert
        compiledClass.runEvalMethod(methodDecl)
    }

    @Test
    fun `fConst_0() should load a Float constant 0 onto the stack`() {
        // Arrange
        val (methodDecl, compiledClass) = buildEvalMethodWith(JvmFloat) {
            fConst_0()
            fReturn()
        }

        // Act
        val result = compiledClass.runEvalMethod(methodDecl)

        // Assert
        assertEquals(0.0f, result)
    }

    @Test
    fun `fConst_1() should load a Float constant 1 onto the stack`() {
        // Arrange
        val (methodDecl, compiledClass) = buildEvalMethodWith(JvmFloat) {
            fConst_1()
            fReturn()
        }

        // Act
        val result = compiledClass.runEvalMethod(methodDecl)

        // Assert
        assertEquals(1.0f, result)
    }

    @Test
    fun `fConst_2() should load a Float constant 2 onto the stack`() {
        // Arrange
        val (methodDecl, compiledClass) = buildEvalMethodWith(JvmFloat) {
            fConst_2()
            fReturn()
        }

        // Act
        val result = compiledClass.runEvalMethod(methodDecl)

        // Assert
        assertEquals(2.0f, result)
    }

    @Test
    fun `fConst() should load a Float constant onto the stack`() {
        // Arrange
        val (methodDecl, compiledClass) = buildEvalMethodWith(JvmVoid) {
            fConst(Float.MIN_VALUE)
            fConst(0.0f)
            fConst(1.0f)
            fConst(2.0f)
            fConst(Float.MAX_VALUE)
            repeat(5) { pop1() }
            vReturn()
        }

        // Act/Assert
        compiledClass.runEvalMethod(methodDecl)
    }


    @Test
    fun `dConst_0() should load a Double constant 0 onto the stack`() {
        // Arrange
        val (methodDecl, compiledClass) = buildEvalMethodWith(JvmDouble) {
            dConst_0()
            dReturn()
        }

        // Act
        val result = compiledClass.runEvalMethod(methodDecl)

        // Assert
        assertEquals(0.0, result)
    }

    @Test
    fun `dConst_1() should load a Double constant 1 onto the stack`() {
        // Arrange
        val (methodDecl, compiledClass) = buildEvalMethodWith(JvmDouble) {
            dConst_1()
            dReturn()
        }

        // Act
        val result = compiledClass.runEvalMethod(methodDecl)

        // Assert
        assertEquals(1.0, result)
    }

    @Test
    fun `dConst() should load a Float constant onto the stack`() {
        // Arrange
        val (methodDecl, compiledClass) = buildEvalMethodWith(JvmVoid) {
            dConst(Double.MIN_VALUE)
            dConst(0.0)
            dConst(1.0)
            dConst(Double.MAX_VALUE)
            repeat(4) { pop2() }
            vReturn()
        }

        // Act/Assert
        compiledClass.runEvalMethod(methodDecl)
    }

    @Test
    fun `aConst_Null() should load an Object constant 'null' onto the stack`() {
        // Arrange
        val (methodDecl, compiledClass) = buildEvalMethodWith(JvmTypes.Object.ref()) {
            aConst_Null()
            aReturn()
        }

        // Act
        val result = compiledClass.runEvalMethod(methodDecl)

        // Assert
        assertEquals(null, result)
    }

    @Test
    fun `ldc() should load an Integer constant onto the stack`() {
        // Arrange
        val value: Int = 42
        val (methodDecl, compiledClass) = buildEvalMethodWith(JvmInteger) {
            ldc(value)
            iReturn()
        }

        // Act
        val result = compiledClass.runEvalMethod(methodDecl)

        // Assert
        assertEquals(value, result)
    }

    @Test
    fun `ldc() should load a Long constant onto the stack`() {
        // Arrange
        val value: Long = 42L
        val (methodDecl, compiledClass) = buildEvalMethodWith(JvmLong) {
            ldc(value)
            lReturn()
        }

        // Act
        val result = compiledClass.runEvalMethod(methodDecl)

        // Assert
        assertEquals(value, result)
    }

    @Test
    fun `ldc() should load a Float constant onto the stack`() {
        // Arrange
        val value: Float = 42.1337f
        val (methodDecl, compiledClass) = buildEvalMethodWith(JvmFloat) {
            ldc(value)
            fReturn()
        }

        // Act
        val result = compiledClass.runEvalMethod(methodDecl)

        // Assert
        assertEquals(value, result)
    }

    @Test
    fun `ldc() should load a Double constant onto the stack`() {
        // Arrange
        val value: Double = 42.1337
        val (methodDecl, compiledClass) = buildEvalMethodWith(JvmDouble) {
            ldc(value)
            dReturn()
        }

        // Act
        val result = compiledClass.runEvalMethod(methodDecl)

        // Assert
        assertEquals(value, result)
    }

    @Test
    fun `ldc() should load a string constant onto the stack`() {
        // Arrange
        val value: String = "My string constant"
        val (methodDecl, compiledClass) = buildEvalMethodWith(JvmTypes.String.ref()) {
            ldc(value)
            aReturn()
        }

        // Act
        val result = compiledClass.runEvalMethod(methodDecl)

        // Assert
        assertEquals(value, result)
    }

    @Test
    fun `const() should load a constant onto the stack`() {
        // Arrange
        val (methodDecl, compiledClass) = buildEvalMethodWith(JvmVoid) {
            // Long
            const(Long.MIN_VALUE)
            const(-1L)
            const(0L)
            const(1L)
            const(2L)
            const(3L)
            const(4L)
            const(5L)
            const(Long.MAX_VALUE)
            repeat(9) { pop2() }
            // Double
            const(Double.MIN_VALUE)
            const(-1.0)
            const(0.0)
            const(1.0)
            const(2.0)
            const(3.0)
            const(4.0)
            const(5.0)
            const(Double.MAX_VALUE)
            repeat(9) { pop2() }
            // Byte
            const(Byte.MIN_VALUE)
            const((-1).toByte())
            const(0.toByte())
            const(1.toByte())
            const(2.toByte())
            const(3.toByte())
            const(4.toByte())
            const(5.toByte())
            const(Byte.MAX_VALUE)
            repeat(9) { pop1() }
            // Short
            const(Short.MIN_VALUE)
            const((-1).toShort())
            const(0.toShort())
            const(1.toShort())
            const(2.toShort())
            const(3.toShort())
            const(4.toShort())
            const(5.toShort())
            const(Short.MAX_VALUE)
            repeat(9) { pop1() }
            // Float
            const(Float.MIN_VALUE)
            const(-1.0f)
            const(0.0f)
            const(1.0f)
            const(2.0f)
            const(3.0f)
            const(4.0f)
            const(5.0f)
            const(Float.MAX_VALUE)
            repeat(9) { pop1() }
            // Int
            const(Int.MIN_VALUE)
            const(-1)
            const(0)
            const(1)
            const(2)
            const(3)
            const(4)
            const(5)
            const(Int.MAX_VALUE)
            repeat(9) { pop1() }
            // Objects
            const("My String Value")
            const(null)
            repeat(2) { pop1() }
            vReturn()
        }

        // Act/Assert
        compiledClass.runEvalMethod(methodDecl)
    }

    //////////////////////////
    // ARITHMETIC and LOGIC //
    //////////////////////////

    ///////////
    // CASTS //
    ///////////

    /////////////
    // OBJECTS //
    /////////////

    ////////////
    // ARRAYS //
    ////////////

    ///////////
    // JUMPS //
    ///////////

    @Test
    fun `ifGt() should branch if the value is greater than 0`() {
        // Arrange
        val value = 1
        val (methodDecl, compiledClass) = buildEvalMethodWith(JvmBoolean) {
            const(value)
            val ifTrue = JvmLabel()
            ifGt(ifTrue)
            const(0)
            iReturn()
            label(ifTrue)
            const(1)
            iReturn()
        }

        // Act
        val result = compiledClass.runEvalMethod(methodDecl) as Boolean

        // Assert
        assertTrue(result)
    }

    @Test
    fun `ifGt() should not branch if the value is equal to 0`() {
        // Arrange
        val value = 0
        val (methodDecl, compiledClass) = buildEvalMethodWith(JvmBoolean) {
            const(value)
            val ifTrue = JvmLabel()
            ifGt(ifTrue)
            const(0)
            iReturn()
            label(ifTrue)
            const(1)
            iReturn()
        }

        // Act
        val result = compiledClass.runEvalMethod(methodDecl) as Boolean

        // Assert
        assertFalse(result)
    }

    @Test
    fun `ifGt() should not branch if the value is less than 0`() {
        // Arrange
        val value = -1
        val (methodDecl, compiledClass) = buildEvalMethodWith(JvmBoolean) {
            const(value)
            val ifTrue = JvmLabel()
            ifGt(ifTrue)
            const(0)
            iReturn()
            label(ifTrue)
            const(1)
            iReturn()
        }

        // Act
        val result = compiledClass.runEvalMethod(methodDecl) as Boolean

        // Assert
        assertFalse(result)
    }

    @Test
    fun `ifGe() should branch if the value is greater than 0`() {
        // Arrange
        val value = 1
        val (methodDecl, compiledClass) = buildEvalMethodWith(JvmBoolean) {
            const(value)
            val ifTrue = JvmLabel()
            ifGe(ifTrue)
            const(0)
            iReturn()
            label(ifTrue)
            const(1)
            iReturn()
        }

        // Act
        val result = compiledClass.runEvalMethod(methodDecl) as Boolean

        // Assert
        assertTrue(result)
    }

    @Test
    fun `ifGe() should branch if the value is equal to 0`() {
        // Arrange
        val value = 0
        val (methodDecl, compiledClass) = buildEvalMethodWith(JvmBoolean) {
            const(value)
            val ifTrue = JvmLabel()
            ifGe(ifTrue)
            const(0)
            iReturn()
            label(ifTrue)
            const(1)
            iReturn()
        }

        // Act
        val result = compiledClass.runEvalMethod(methodDecl) as Boolean

        // Assert
        assertTrue(result)
    }

    @Test
    fun `ifGe() should not branch if the value is less than 0`() {
        // Arrange
        val value = -1
        val (methodDecl, compiledClass) = buildEvalMethodWith(JvmBoolean) {
            const(value)
            val ifTrue = JvmLabel()
            ifGe(ifTrue)
            const(0)
            iReturn()
            label(ifTrue)
            const(1)
            iReturn()
        }

        // Act
        val result = compiledClass.runEvalMethod(methodDecl) as Boolean

        // Assert
        assertFalse(result)
    }
    
    @Test
    fun `ifEq() should not branch if the value is greater than 0`() {
        // Arrange
        val value = 1
        val (methodDecl, compiledClass) = buildEvalMethodWith(JvmBoolean) {
            const(value)
            val ifTrue = JvmLabel()
            ifEq(ifTrue)
            const(0)
            iReturn()
            label(ifTrue)
            const(1)
            iReturn()
        }

        // Act
        val result = compiledClass.runEvalMethod(methodDecl) as Boolean

        // Assert
        assertFalse(result)
    }

    @Test
    fun `ifEq() should branch if the value is equal to 0`() {
        // Arrange
        val value = 0
        val (methodDecl, compiledClass) = buildEvalMethodWith(JvmBoolean) {
            const(value)
            val ifTrue = JvmLabel()
            ifEq(ifTrue)
            const(0)
            iReturn()
            label(ifTrue)
            const(1)
            iReturn()
        }

        // Act
        val result = compiledClass.runEvalMethod(methodDecl) as Boolean

        // Assert
        assertTrue(result)
    }

    @Test
    fun `ifEq() should not branch if the value is less than 0`() {
        // Arrange
        val value = -1
        val (methodDecl, compiledClass) = buildEvalMethodWith(JvmBoolean) {
            const(value)
            val ifTrue = JvmLabel()
            ifEq(ifTrue)
            const(0)
            iReturn()
            label(ifTrue)
            const(1)
            iReturn()
        }

        // Act
        val result = compiledClass.runEvalMethod(methodDecl) as Boolean

        // Assert
        assertFalse(result)
    }

    @Test
    fun `ifNe() should branch if the value is greater than 0`() {
        // Arrange
        val value = 1
        val (methodDecl, compiledClass) = buildEvalMethodWith(JvmBoolean) {
            const(value)
            val ifTrue = JvmLabel()
            ifNe(ifTrue)
            const(0)
            iReturn()
            label(ifTrue)
            const(1)
            iReturn()
        }

        // Act
        val result = compiledClass.runEvalMethod(methodDecl) as Boolean

        // Assert
        assertTrue(result)
    }

    @Test
    fun `ifNe() should not branch if the value is equal to 0`() {
        // Arrange
        val value = 0
        val (methodDecl, compiledClass) = buildEvalMethodWith(JvmBoolean) {
            const(value)
            val ifTrue = JvmLabel()
            ifNe(ifTrue)
            const(0)
            iReturn()
            label(ifTrue)
            const(1)
            iReturn()
        }

        // Act
        val result = compiledClass.runEvalMethod(methodDecl) as Boolean

        // Assert
        assertFalse(result)
    }

    @Test
    fun `ifNe() should branch if the value is less than 0`() {
        // Arrange
        val value = -1
        val (methodDecl, compiledClass) = buildEvalMethodWith(JvmBoolean) {
            const(value)
            val ifTrue = JvmLabel()
            ifNe(ifTrue)
            const(0)
            iReturn()
            label(ifTrue)
            const(1)
            iReturn()
        }

        // Act
        val result = compiledClass.runEvalMethod(methodDecl) as Boolean

        // Assert
        assertTrue(result)
    }
    
    @Test
    fun `ifLe() should not branch if the value is greater than 0`() {
        // Arrange
        val value = 1
        val (methodDecl, compiledClass) = buildEvalMethodWith(JvmBoolean) {
            const(value)
            val ifTrue = JvmLabel()
            ifLe(ifTrue)
            const(0)
            iReturn()
            label(ifTrue)
            const(1)
            iReturn()
        }

        // Act
        val result = compiledClass.runEvalMethod(methodDecl) as Boolean

        // Assert
        assertFalse(result)
    }

    @Test
    fun `ifLe() should branch if the value is equal to 0`() {
        // Arrange
        val value = 0
        val (methodDecl, compiledClass) = buildEvalMethodWith(JvmBoolean) {
            const(value)
            val ifTrue = JvmLabel()
            ifLe(ifTrue)
            const(0)
            iReturn()
            label(ifTrue)
            const(1)
            iReturn()
        }

        // Act
        val result = compiledClass.runEvalMethod(methodDecl) as Boolean

        // Assert
        assertTrue(result)
    }

    @Test
    fun `ifLe() should branch if the value is less than 0`() {
        // Arrange
        val value = -1
        val (methodDecl, compiledClass) = buildEvalMethodWith(JvmBoolean) {
            const(value)
            val ifTrue = JvmLabel()
            ifLe(ifTrue)
            const(0)
            iReturn()
            label(ifTrue)
            const(1)
            iReturn()
        }

        // Act
        val result = compiledClass.runEvalMethod(methodDecl) as Boolean

        // Assert
        assertTrue(result)
    }

    @Test
    fun `ifLt() should not branch if the value is greater than 0`() {
        // Arrange
        val value = 1
        val (methodDecl, compiledClass) = buildEvalMethodWith(JvmBoolean) {
            const(value)
            val ifTrue = JvmLabel()
            ifLt(ifTrue)
            const(0)
            iReturn()
            label(ifTrue)
            const(1)
            iReturn()
        }

        // Act
        val result = compiledClass.runEvalMethod(methodDecl) as Boolean

        // Assert
        assertFalse(result)
    }

    @Test
    fun `ifLt() should not branch if the value is equal to 0`() {
        // Arrange
        val value = 0
        val (methodDecl, compiledClass) = buildEvalMethodWith(JvmBoolean) {
            const(value)
            val ifTrue = JvmLabel()
            ifLt(ifTrue)
            const(0)
            iReturn()
            label(ifTrue)
            const(1)
            iReturn()
        }

        // Act
        val result = compiledClass.runEvalMethod(methodDecl) as Boolean

        // Assert
        assertFalse(result)
    }

    @Test
    fun `ifLt() should branch if the value is less than 0`() {
        // Arrange
        val value = -1
        val (methodDecl, compiledClass) = buildEvalMethodWith(JvmBoolean) {
            const(value)
            val ifTrue = JvmLabel()
            ifLt(ifTrue)
            const(0)
            iReturn()
            label(ifTrue)
            const(1)
            iReturn()
        }

        // Act
        val result = compiledClass.runEvalMethod(methodDecl) as Boolean

        // Assert
        assertTrue(result)
    }

    @Test
    fun `ifNull() should branch if the value is 'null'`() {
        // Arrange
        val (methodDecl, compiledClass) = buildEvalMethodWith(JvmBoolean) {
            const(null)
            val ifTrue = JvmLabel()
            ifNull(ifTrue)
            const(0)
            iReturn()
            label(ifTrue)
            const(1)
            iReturn()
        }

        // Act
        val result = compiledClass.runEvalMethod(methodDecl) as Boolean

        // Assert
        assertTrue(result)
    }

    @Test
    fun `ifNull() should not branch if the value is not 'null'`() {
        // Arrange
        val (methodDecl, compiledClass) = buildEvalMethodWith(JvmBoolean) {
            const("Abc!")
            val ifTrue = JvmLabel()
            ifNull(ifTrue)
            const(0)
            iReturn()
            label(ifTrue)
            const(1)
            iReturn()
        }

        // Act
        val result = compiledClass.runEvalMethod(methodDecl) as Boolean

        // Assert
        assertFalse(result)
    }

    @Test
    fun `ifNonNull() should not branch if the value is 'null'`() {
        // Arrange
        val (methodDecl, compiledClass) = buildEvalMethodWith(JvmBoolean) {
            const(null)
            val ifTrue = JvmLabel()
            ifNonNull(ifTrue)
            const(0)
            iReturn()
            label(ifTrue)
            const(1)
            iReturn()
        }

        // Act
        val result = compiledClass.runEvalMethod(methodDecl) as Boolean

        // Assert
        assertFalse(result)
    }

    @Test
    fun `ifNonNull() should branch if the value is not 'null'`() {
        // Arrange
        val (methodDecl, compiledClass) = buildEvalMethodWith(JvmBoolean) {
            const("Abc!")
            val ifTrue = JvmLabel()
            ifNonNull(ifTrue)
            const(0)
            iReturn()
            label(ifTrue)
            const(1)
            iReturn()
        }

        // Act
        val result = compiledClass.runEvalMethod(methodDecl) as Boolean

        // Assert
        assertTrue(result)
    }

    @Test
    fun `jump() should branch unconditionally`() {
        // Arrange
        val (methodDecl, compiledClass) = buildEvalMethodWith(JvmBoolean) {
            val ifTrue = JvmLabel()
            jump(ifTrue)
            const(0)
            iReturn()
            label(ifTrue)
            const(1)
            iReturn()
        }

        // Act
        val result = compiledClass.runEvalMethod(methodDecl) as Boolean

        // Assert
        assertTrue(result)
    }

    /////////////////////////////
    // LABELS and LINE NUMBERS //
    /////////////////////////////

    @Test
    fun `label() should emit a label for branching`() {
        // Arrange
        val (methodDecl, compiledClass) = buildEvalMethodWith(JvmVoid) {
            val l2 = JvmLabel()
            val l3 = JvmLabel()
            jump(l3)
            val l1 = label("Start")
            vReturn()
            label(l2)
            jump(l1)
            label(l3)
            jump(l2)
        }

        // Act/Assert
        assertDoesNotThrow {
            compiledClass.runEvalMethod(methodDecl)
        }
    }

    //////////////////
    // CONTROL FLOW //
    //////////////////

    @Test
    fun `aThrow() should throw an object`() {
        // Arrange
        val illegalStateExceptionType = JvmClassRef.of(IllegalStateException::class.java)
        val (methodDecl, compiledClass) = buildEvalMethodWith(JvmVoid) {
            newInst(illegalStateExceptionType)
            dup(illegalStateExceptionType)
            ldc("Test exception!")
            invokeConstructor(illegalStateExceptionType, JvmMethodSignature(JvmVoid, listOf(JvmParam(JvmTypes.String.ref()))))
            aThrow()
        }

        // Act/Assert
        assertThrows<IllegalStateException> {
            try {
                compiledClass.runEvalMethod(methodDecl)
            } catch(ex: InvocationTargetException) {
                throw ex.cause ?: ex
            }
        }
    }

    @Test
    fun `iReturn() should return an Integer`() {
        // Arrange
        val value: Int = 6
        val (methodDecl, compiledClass) = buildEvalMethodWith(JvmInteger) {
            const(value)
            iReturn()
        }

        // Act
        val result = compiledClass.runEvalMethod(methodDecl)

        // Assert
        assertEquals(value, result)
    }

    @Test
    fun `lReturn() should return a Long`() {
        // Arrange
        val value: Long = 6L
        val (methodDecl, compiledClass) = buildEvalMethodWith(JvmLong) {
            const(value)
            lReturn()
        }

        // Act
        val result = compiledClass.runEvalMethod(methodDecl)

        // Assert
        assertEquals(value, result)
    }

    @Test
    fun `fReturn() should return a Float`() {
        // Arrange
        val value: Float = 4.2f;
        val (methodDecl, compiledClass) = buildEvalMethodWith(JvmFloat) {
            const(value)
            fReturn()
        }

        // Act
        val result = compiledClass.runEvalMethod(methodDecl)

        // Assert
        assertEquals(value, result)
    }

    @Test
    fun `dReturn() should return a Double`() {
        // Arrange
        val value: Double = 4.2;
        val (methodDecl, compiledClass) = buildEvalMethodWith(JvmDouble) {
            const(value)
            dReturn()
        }

        // Act
        val result = compiledClass.runEvalMethod(methodDecl)

        // Assert
        assertEquals(value, result)
    }

    @Test
    fun `aReturn() should return an Object`() {
        // Arrange
        val value: String = "My String!";
        val (methodDecl, compiledClass) = buildEvalMethodWith(JvmTypes.String.ref()) {
            const(value)
            aReturn()
        }

        // Act
        val result = compiledClass.runEvalMethod(methodDecl)

        // Assert
        assertEquals(value, result)
    }

    @Test
    fun `vReturn() should return void`() {
        // Arrange
        val (methodDecl, compiledClass) = buildEvalMethodWith(JvmVoid) {
            vReturn()
        }

        // Act
        val result = compiledClass.runEvalMethod(methodDecl)

        // Assert
        assertNull(result)
    }


    
    private fun JvmCompiledClass.runEvalMethod(methodDecl: JvmMethodDecl, vararg args: Any?): Any? {
        val cls = this.load<Any>()
        val instance = cls.getConstructor().newInstance()
        val method = cls.getMethod(methodDecl.ref(this.type.ref()))
        return method.invoke(instance, *args)
    }

    private fun buildEvalMethodWith(returnType: JvmType = JvmVoid, parameters: List<JvmParam> = emptyList(), builder: JvmScopeBuilder.() -> Unit): Pair<JvmMethodDecl, JvmCompiledClass> {
        var methodDecl: JvmMethodDecl
        val compiledClass = buildClassWith {
            methodDecl = createMethod("eval", returnType, parameters, modifiers = JvmMethodModifiers.Public).apply {
                beginCode().apply {
                    builder()
                }.build()
            }.build()
        }
        return methodDecl to compiledClass
    }

    /**
     * Builds a test class named `HelloWorld` with one type parameter `T`.
     * @param builder builds the members of the class
     * @return the compiled class
     */
    @OptIn(ExperimentalContracts::class)
    private fun buildClassWith(builder: JvmClassBuilder.() -> Unit): JvmCompiledClass {
        contract {
            callsInPlace(builder, InvocationKind.EXACTLY_ONCE)
        }
        val compiledClass: JvmCompiledClass
        JvmModuleBuilder().apply {
            // package com.example
            // public class HelloWorld<T>
            compiledClass = createClass(JvmClassDecl("HelloWorld", JvmPackageDecl("com.example").ref(), signature = JvmClassSignature(
                typeParameters = listOf(JvmTypeParam("T"))
            )), JvmClassModifiers.Public).apply {

                createDefaultConstructor(JvmMethodModifiers.Public)

                builder()
            }.build()
        }.build()
        try {
            compiledClass.check()
        } catch (ex: Throwable) {
            println("An error occurred: ${ex.message}")
            println(compiledClass)
            throw ex
        }
        return compiledClass
    }
}