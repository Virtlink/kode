package examples;

import com.virtlink.kode.JvmModuleBuilder;
import com.virtlink.kode.JvmClassBuilder;
import com.virtlink.kode.JvmClassModifiers;
import com.virtlink.kode.JvmCompiledClass;
import com.virtlink.kode.JvmMethodBuilder;
import com.virtlink.kode.JvmMethodModifiers;
import com.virtlink.kode.JvmParam;
import com.virtlink.kode.JvmScopeBuilder;
import com.virtlink.kode.types.JvmArray;
import com.virtlink.kode.types.JvmClassDecl;
import com.virtlink.kode.types.JvmClassRef;
import com.virtlink.kode.types.JvmFieldDecl;
import com.virtlink.kode.types.JvmFieldRef;
import com.virtlink.kode.types.JvmMethodDecl;
import com.virtlink.kode.types.JvmMethodRef;
import com.virtlink.kode.types.JvmMethodSignature;
import com.virtlink.kode.types.JvmPackageDecl;
import com.virtlink.kode.types.JvmPackageRef;
import com.virtlink.kode.types.JvmTypes;
import com.virtlink.kode.types.JvmVoid;
import org.junit.jupiter.api.Test;

import java.io.PrintStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

/**
 * A simple Java program that builds a static main() method which prints "Hello, World!".
 */
public class HelloWorldJ {
//
//    @Test
//    public void test() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
//        final JvmCompiledClass compiledClass;
//        try (final JvmModuleBuilder moduleBuilder = new JvmModuleBuilder()) {
//            final JvmPackageRef pkgRef = new JvmPackageDecl("com.example").ref();
//            final JvmClassDecl classDecl = new JvmClassDecl("HelloWorld", pkgRef);
//
//            // package com.example
//            // public class HelloWorld
//            final JvmClassBuilder classBuilder = moduleBuilder.createClass(classDecl, JvmClassModifiers.Public());
//            try (classBuilder) {
//                // public static void main(String[] args)
//                try (final JvmMethodBuilder methodBuilder = classBuilder.createMethod(
//                        "main",
//                        new JvmMethodSignature(JvmVoid.INSTANCE, List.of(new JvmParam(new JvmArray(JvmTypes.INSTANCE.getString().ref()), "args"))),
//                        JvmMethodModifiers.Public() | JvmMethodModifiers.Static()
//                )) {
//                    try (final JvmScopeBuilder scopeBuilder = methodBuilder.beginCode()) {
//                        // System.out.println("Hello, World!");
//                        final JvmClassRef printStreamType = JvmClassRef.Companion.of(PrintStream.class);
//                        // TODO: Simplify this new JvmFieldDecl.ref() business to just new JvmFieldRef() or something else
//                        scopeBuilder.getField(new JvmFieldDecl("out", JvmTypes.INSTANCE.getSystem(), printStreamType, false).ref(JvmTypes.INSTANCE.getSystem().ref()));
//                        scopeBuilder.ldc("Hello, World!");
//                        scopeBuilder.invokeMethod(new JvmMethodDecl(
//                                "println",
//                                printStreamType.getDeclaration(),
//                                new JvmMethodSignature(JvmVoid.INSTANCE, List.of(new JvmParam(JvmTypes.INSTANCE.getString().ref()))),
//                                JvmMethodModifiers.None()
//                        ).ref(printStreamType));
//                        // return
//                        scopeBuilder.ret();
//                    }
//                }
//            }
//            compiledClass = classBuilder.build();
//        }
//
//        compiledClass.check();
//        final Class<Object> cls = compiledClass.load();
//        final Method method = cls.getMethod("main", String[].class);
//        method.invoke(null, (Object) new String[0]);
//    }

}
