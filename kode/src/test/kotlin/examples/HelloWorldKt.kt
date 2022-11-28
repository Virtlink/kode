package examples

import net.pelsmaeker.kode.*
import net.pelsmaeker.kode.types.*
import org.junit.jupiter.api.Test
import java.io.PrintStream

/**
 * A simple Kotlin program that builds a static main() method which prints "Hello, World!".
 */
class HelloWorldKt {

    @Test
    fun test() {
        val compiledClass: JvmCompiledClass
        JvmModuleBuilder().apply {
            val pkgRef = JvmPackageDecl("com.example").ref()
            val classDecl = JvmClassDecl("HelloWorld", pkgRef, signature = JvmClassSignature())

            // package com.example
            // public class HelloWorld
            compiledClass = createClass(classDecl, JvmClassModifiers.Public).apply {
                // public static void main(String[] args)
                createMethod(
                    "main",
                    JvmMethodSignature(JvmVoid, listOf(JvmParam(JvmArray(JvmTypes.String.ref()), "args"))),
                    JvmMethodModifiers.Public or JvmMethodModifiers.Static,
                ).apply {
                    beginCode().apply {
                        // System.out.println("Hello, World!")
                        val printStreamType = JvmClassRef.of(PrintStream::class.java)
                        // TODO: Simplify this JvmFieldDecl.ref() business to just JvmFieldRef() or something else
                        getField(JvmFieldDecl("out", JvmTypes.System, printStreamType, false).ref(JvmTypes.System.ref()))
                        ldc("Hello, World!")
                        invokeMethod(
                            JvmMethodDecl(
                                "println",
                                printStreamType.declaration,
                                JvmMethodSignature(JvmVoid, listOf(JvmParam(JvmTypes.String.ref()))),
                                JvmMethodModifiers.None,
                            ).ref(printStreamType)
                        )
                        // return
                        vReturn()
                    }.build()
                }.build()
            }.build()
        }.build()

        compiledClass.check()
        val cls = compiledClass.load<Any>()
        val method = cls.getMethod("main", Array<String>::class.java)
        method.invoke(null, arrayOf<String>())
    }

}