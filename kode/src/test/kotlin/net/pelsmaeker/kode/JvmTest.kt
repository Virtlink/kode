package net.pelsmaeker.kode

import net.pelsmaeker.kode.types.*
import net.pelsmaeker.kode.types.JvmMethodRef.Companion.getMethod
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

/**
 * Runs the specified method of the compiled class.
 *
 * @param methodDecl the method declaration of the method to invoke
 * @param args the arguments to pass to the method, which may be `null`
 * @return the result of the method invocation, which may be `null`
 */
fun JvmCompiledClass.runMethod(methodDecl: JvmMethodDecl, vararg args: Any?): Any? {
    val cls = this.load<Any>()
    val instance = cls.getConstructor().newInstance()
    val method = cls.getMethod(methodDecl.ref(this.type.ref()))
    return method.invoke(instance, *args)
}

/**
 * Runs the specified method of the compiled class.
 *
 * @param methodDecl the method declaration of the method to invoke
 * @param args the arguments to pass to the method, which may be `null`
 * @return the result of the method invocation, which may be `null`
 */
fun <R> JvmCompiledClass.runMethodTo(methodDecl: JvmMethodDecl, vararg args: Any?): R {
    @Suppress("UNCHECKED_CAST")
    return runMethod(methodDecl, *args) as R
}

/**
 * Builds a test class containing a method named `eval` with the specified return type and parameters.
 *
 * @param returnType the return type of the method
 * @param parameters the parameters of the method
 * @param builder builds the body of the method
 * @return a pair of the method declaration and the compiled class
 */
fun buildEvalMethodWith(
    returnType: JvmType = JvmVoid,
    parameters: List<JvmParam> = emptyList(),
    builder: JvmScopeBuilder.() -> Unit,
): Pair<JvmMethodDecl, JvmCompiledClass> {
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
 * Builds a test class named `com.example.HelloWorld` with one type parameter `T`.
 *
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
        compiledClass = createClass(
            JvmClassDecl("HelloWorld", JvmPackageDecl("com.example").ref(), signature = JvmClassSignature(
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