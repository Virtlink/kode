package examples

import dev.pelsmaeker.kode.*
import dev.pelsmaeker.kode.types.*
import org.junit.jupiter.api.Test
import java.io.PrintStream

/**
 * A simple Kotlin program that builds a static main() method which prints "Hello, World!".
 */
class HelloWorldKt {

    @Test
    fun test() {
        val jvmModuleBuilder = JvmModuleBuilder()
        val pkgRef = JvmPackageRef("com/example")
        val classDecl = JvmClassDecl("HelloWorld", pkgRef)
        val methodRef = JvmMethodRef("main", classDecl.ref(), false,
            JvmMethodSignature(JvmVoid, listOf(JvmParam(JvmArray(JvmTypes.String.ref()), "args")))
        )

        // package com.example
        // public class HelloWorld
        val compiledClass = jvmModuleBuilder.createClass(classDecl, JvmClassModifiers.Public).apply {
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
        val method = cls.getMethod("main", Array<String>::class.java)
        method.invoke(null, arrayOf<String>())
    }

}