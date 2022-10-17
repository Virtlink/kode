package examples;

import dev.pelsmaeker.kode.JvmModuleBuilder;
import dev.pelsmaeker.kode.JvmClassBuilder;
import dev.pelsmaeker.kode.JvmClassModifiers;
import dev.pelsmaeker.kode.JvmCompiledClass;
import dev.pelsmaeker.kode.JvmMethodBuilder;
import dev.pelsmaeker.kode.JvmMethodModifiers;
import dev.pelsmaeker.kode.JvmParam;
import dev.pelsmaeker.kode.JvmScopeBuilder;
import dev.pelsmaeker.kode.types.JvmArray;
import dev.pelsmaeker.kode.types.JvmClassDecl;
import dev.pelsmaeker.kode.types.JvmClassRef;
import dev.pelsmaeker.kode.types.JvmFieldRef;
import dev.pelsmaeker.kode.types.JvmMethodRef;
import dev.pelsmaeker.kode.types.JvmMethodSignature;
import dev.pelsmaeker.kode.types.JvmPackageRef;
import dev.pelsmaeker.kode.types.JvmTypes;
import dev.pelsmaeker.kode.types.JvmVoid;
import org.junit.jupiter.api.Test;

import java.io.PrintStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

/**
 * A simple Java program that builds a static main() method which prints "Hello, World!".
 */
public class HelloWorldJ {

    @Test
    public void test() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        final JvmCompiledClass compiledClass;
        try (final JvmModuleBuilder moduleBuilder = new JvmModuleBuilder()) {
            final JvmPackageRef pkgRef = new JvmPackageRef("com/example");
            final JvmClassDecl classDecl = new JvmClassDecl("HelloWorld", pkgRef);

            // package com.example
            // public class HelloWorld
            final JvmClassBuilder classBuilder = moduleBuilder.createClass(classDecl, JvmClassModifiers.Public());
            try (classBuilder) {
                // public static void main(String[] args)
                try (final JvmMethodBuilder methodBuilder = classBuilder.createMethod(
                        "main",
                        JvmMethodModifiers.Public() | JvmMethodModifiers.Static(),
                        new JvmMethodSignature(JvmVoid.INSTANCE, List.of(new JvmParam(new JvmArray(JvmTypes.INSTANCE.getString().ref()), "args")))
                )) {
                    try (final JvmScopeBuilder scopeBuilder = methodBuilder.beginCode()) {
                        // System.out.println("Hello, World!");
                        final JvmClassRef printStreamType = JvmClassRef.Companion.of(PrintStream.class);
                        scopeBuilder.getField(new JvmFieldRef("out", JvmTypes.INSTANCE.getSystem().ref(), false, printStreamType));
                        scopeBuilder.ldc("Hello, World!");
                        scopeBuilder.invokeMethod(new JvmMethodRef("println", printStreamType, true, new JvmMethodSignature(JvmVoid.INSTANCE, List.of(new JvmParam(JvmTypes.INSTANCE.getString().ref())))));
                        // return
                        scopeBuilder.ret();
                    }
                }
            }
            compiledClass = classBuilder.build();
        }

        compiledClass.check();
        final Class<Object> cls = compiledClass.load();
        final Method method = cls.getMethod("main", String[].class);
        method.invoke(null, (Object) new String[0]);
    }

}
