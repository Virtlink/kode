package dev.pelsmaeker.kode

import dev.pelsmaeker.kode.types.*
import dev.pelsmaeker.kode.types.JvmMethodRef.Companion.getMethod
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
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