package dev.pelsmaeker.kode.examples

import dev.pelsmaeker.kode.*
import dev.pelsmaeker.kode.types.*
import org.junit.jupiter.api.Test
import java.io.PrintStream

/**
 * Shows a simple program that creates a static main() method which prints "Hello, World!".
 */
class HelloWorld {

    @Test
    fun test() {
        val jvm = Jvm()
        val pkgRef = JvmPackageRef("com/example")
        val classDecl = JvmClassDecl("HelloWorld", pkgRef)
        val methodRef = JvmMethodRef("main", classDecl.ref(), false, JvmMethodSignature(JvmVoid, listOf(JvmParam(JvmArray(JvmTypes.String.ref()), "args"))))

        // package com.example
        // public class HelloWorld
        val compiledClass = jvm.createClass(classDecl, JvmClassModifiers.Public).apply {
            // public static void main(String[] args)
            createMethod(methodRef, JvmMethodModifiers.Public or JvmMethodModifiers.Static).apply {
                beginCode().apply {
                    // System.out.println("Hello, World!")
                    val printStreamType = JvmClassRef.of(PrintStream::class.java)
                    getField(JvmFieldRef("out", JvmTypes.System.ref(), false, printStreamType))
                    ldc("Hello, World!")
                    invokeMethod(JvmMethodRef("println", printStreamType, true, JvmMethodSignature(JvmVoid, listOf(JvmParam(JvmTypes.String.ref())))))
                    // return
                    ret()
                }.build()
            }.build()
        }.build()

        compiledClass.check()
        val cls = compiledClass.load<Any>()
        cls.getMethod("main", Array<String>::class.java).invoke(null, arrayOf<String>())
    }

}