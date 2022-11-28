package net.pelsmaeker.kode.types

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.lang.reflect.ParameterizedType
import kotlin.reflect.KProperty
import kotlin.reflect.javaType

/** Tests the [JvmClassRef] class. */
@OptIn(ExperimentalStdlibApi::class)
class JvmClassRefTests {

    @Suppress("SpellCheckingInspection")
    class X<T> {
        // Plain
        val i: I = TODO()

        // Invariant type parameter
        val itt: IT<T> = TODO()
        // Covariant type parameter
        val iott: IOT<T> = TODO()
        // Contravariant type parameter
        val iitt: IIT<T> = TODO()

        // Wildcard invariant type parameter
        val ittw: IT<*> = TODO()
        // Wildcard covariant type parameter
        val iottw: IOT<*> = TODO()
        // Wildcard contravariant type parameter
        val iittw: IIT<*> = TODO()

        // Invariant type parameter with bound B
        val ittb: IT<B> = TODO()
        // Covariant type parameter with bound B
        val iottb: IOT<B> = TODO()
        // Contravariant type parameter with bound B
        val iittb: IIT<B> = TODO()
    }

    @Test
    fun `of(X_i) simple interface`() {
        // Act
        val classRef = JvmClassRef.of(X<A>::i.returnType.javaType as Class<*>)

        // Assert
        assertEquals(emptyList<JvmTypeArg>(), classRef.typeArguments)
        assertEquals(JvmNullability.Maybe, classRef.nullability)
        assertNull(classRef.enclosingClassRef)

        assertEquals("net/pelsmaeker/kode/types/I", classRef.internalName)
        assertEquals("net.pelsmaeker.kode.types.I", classRef.javaName)
        assertEquals(JvmTypeSort.Class, classRef.sort)
        assertEquals(JvmTypeKind.Object, classRef.kind)
        assertEquals("Lnet/pelsmaeker/kode/types/I;", classRef.descriptor)
        assertEquals("Lnet/pelsmaeker/kode/types/I;", classRef.signature)

        assertFalse(classRef.isPrimitive)
        assertFalse(classRef.isClass)
        assertTrue(classRef.isInterface)
        assertFalse(classRef.isArray)
        assertFalse(classRef.isTypeVariable)
    }

    @Test
    fun `of(X_itt) invariant type parameter`() {
        // Act
        val classRef = JvmClassRef.of(X<A>::itt.returnType.javaType as ParameterizedType)

        // Assert
        assertEquals(listOf(
            JvmTypeArg("T")
        ), classRef.typeArguments)
        assertEquals(JvmNullability.Maybe, classRef.nullability)
        assertNull(classRef.enclosingClassRef)

        assertEquals("net/pelsmaeker/kode/types/IT", classRef.internalName)
        assertEquals("net.pelsmaeker.kode.types.IT", classRef.javaName)
        assertEquals(JvmTypeSort.Class, classRef.sort)
        assertEquals(JvmTypeKind.Object, classRef.kind)
        assertEquals("Lnet/pelsmaeker/kode/types/IT;", classRef.descriptor)
        assertEquals("Lnet/pelsmaeker/kode/types/IT<TT;>;", classRef.signature)

        assertFalse(classRef.isPrimitive)
        assertFalse(classRef.isClass)
        assertTrue(classRef.isInterface)
        assertFalse(classRef.isArray)
        assertFalse(classRef.isTypeVariable)
    }

    @Test
    fun `of(X_iott) covariant type parameter`() {
        // Act
        val classRef = JvmClassRef.of(X<A>::iott.returnType.javaType as ParameterizedType)

        // Assert
        assertEquals(listOf(
            JvmTypeArg("T")
        ), classRef.typeArguments)
        assertEquals(JvmNullability.Maybe, classRef.nullability)
        assertNull(classRef.enclosingClassRef)

        assertEquals("net/pelsmaeker/kode/types/IOT", classRef.internalName)
        assertEquals("net.pelsmaeker.kode.types.IOT", classRef.javaName)
        assertEquals(JvmTypeSort.Class, classRef.sort)
        assertEquals(JvmTypeKind.Object, classRef.kind)
        assertEquals("Lnet/pelsmaeker/kode/types/IOT;", classRef.descriptor)
        assertEquals("Lnet/pelsmaeker/kode/types/IOT<TT;>;", classRef.signature)

        assertFalse(classRef.isPrimitive)
        assertFalse(classRef.isClass)
        assertTrue(classRef.isInterface)
        assertFalse(classRef.isArray)
        assertFalse(classRef.isTypeVariable)
    }

    @Test
    fun `of(X_iitt) contravariant type parameter`() {
        // Act
        val classRef = JvmClassRef.of(X<A>::iitt.returnType.javaType as ParameterizedType)

        // Assert
        assertEquals(listOf(
            JvmTypeArg("T")
        ), classRef.typeArguments)
        assertEquals(JvmNullability.Maybe, classRef.nullability)
        assertNull(classRef.enclosingClassRef)

        assertEquals("net/pelsmaeker/kode/types/IIT", classRef.internalName)
        assertEquals("net.pelsmaeker.kode.types.IIT", classRef.javaName)
        assertEquals(JvmTypeSort.Class, classRef.sort)
        assertEquals(JvmTypeKind.Object, classRef.kind)
        assertEquals("Lnet/pelsmaeker/kode/types/IIT;", classRef.descriptor)
        assertEquals("Lnet/pelsmaeker/kode/types/IIT<TT;>;", classRef.signature)

        assertFalse(classRef.isPrimitive)
        assertFalse(classRef.isClass)
        assertTrue(classRef.isInterface)
        assertFalse(classRef.isArray)
        assertFalse(classRef.isTypeVariable)
    }

    @Test
    fun `of(X_ittw) wildcard invariant type parameter`() {
        // Act
        val classRef = JvmClassRef.of(X<A>::ittw.returnType.javaType as ParameterizedType)

        // Assert
        assertEquals(listOf(
            JvmTypeArg(JvmTypes.Object.ref(), JvmTypeArgSort.Contravariant)
        ), classRef.typeArguments)
        assertEquals(JvmNullability.Maybe, classRef.nullability)
        assertNull(classRef.enclosingClassRef)

        assertEquals("net/pelsmaeker/kode/types/IT", classRef.internalName)
        assertEquals("net.pelsmaeker.kode.types.IT", classRef.javaName)
        assertEquals(JvmTypeSort.Class, classRef.sort)
        assertEquals(JvmTypeKind.Object, classRef.kind)
        assertEquals("Lnet/pelsmaeker/kode/types/IT;", classRef.descriptor)
        assertEquals("Lnet/pelsmaeker/kode/types/IT<-Ljava/lang/Object;>;", classRef.signature)

        assertFalse(classRef.isPrimitive)
        assertFalse(classRef.isClass)
        assertTrue(classRef.isInterface)
        assertFalse(classRef.isArray)
        assertFalse(classRef.isTypeVariable)
    }

    @Test
    fun `of(X_iottw) wildcard covariant type parameter`() {
        // Act
        val classRef = JvmClassRef.of(X<A>::iottw.returnType.javaType as ParameterizedType)

        // Assert
        assertEquals(listOf(
            JvmTypeArg(JvmTypes.Object.ref(), JvmTypeArgSort.Contravariant)
        ), classRef.typeArguments)
        assertEquals(JvmNullability.Maybe, classRef.nullability)
        assertNull(classRef.enclosingClassRef)

        assertEquals("net/pelsmaeker/kode/types/IOT", classRef.internalName)
        assertEquals("net.pelsmaeker.kode.types.IOT", classRef.javaName)
        assertEquals(JvmTypeSort.Class, classRef.sort)
        assertEquals(JvmTypeKind.Object, classRef.kind)
        assertEquals("Lnet/pelsmaeker/kode/types/IOT;", classRef.descriptor)
        assertEquals("Lnet/pelsmaeker/kode/types/IOT<-Ljava/lang/Object;>;", classRef.signature)

        assertFalse(classRef.isPrimitive)
        assertFalse(classRef.isClass)
        assertTrue(classRef.isInterface)
        assertFalse(classRef.isArray)
        assertFalse(classRef.isTypeVariable)
    }

    @Test
    fun `of(X_iittw) wildcard contravariant type parameter`() {
        // Act
        val classRef = JvmClassRef.of(X<A>::iittw.returnType.javaType as ParameterizedType)

        // Assert
        assertEquals(listOf(
            JvmTypeArg(JvmTypes.Object.ref(), JvmTypeArgSort.Contravariant)
        ), classRef.typeArguments)
        assertEquals(JvmNullability.Maybe, classRef.nullability)
        assertNull(classRef.enclosingClassRef)

        assertEquals("net/pelsmaeker/kode/types/IIT", classRef.internalName)
        assertEquals("net.pelsmaeker.kode.types.IIT", classRef.javaName)
        assertEquals(JvmTypeSort.Class, classRef.sort)
        assertEquals(JvmTypeKind.Object, classRef.kind)
        assertEquals("Lnet/pelsmaeker/kode/types/IIT;", classRef.descriptor)
        assertEquals("Lnet/pelsmaeker/kode/types/IIT<-Ljava/lang/Object;>;", classRef.signature)

        assertFalse(classRef.isPrimitive)
        assertFalse(classRef.isClass)
        assertTrue(classRef.isInterface)
        assertFalse(classRef.isArray)
        assertFalse(classRef.isTypeVariable)
    }

    @Test
    fun `of(X_ittb) B invariant type parameter`() {
        // Arrange
        val b = JvmClassRef.of(B::class.java)

        // Act
        val classRef = JvmClassRef.of(X<A>::ittb.returnType.javaType as ParameterizedType)

        // Assert
        assertEquals(listOf(
            JvmTypeArg(b, JvmTypeArgSort.Invariant)
        ), classRef.typeArguments)
        assertEquals(JvmNullability.Maybe, classRef.nullability)
        assertNull(classRef.enclosingClassRef)

        assertEquals("net/pelsmaeker/kode/types/IT", classRef.internalName)
        assertEquals("net.pelsmaeker.kode.types.IT", classRef.javaName)
        assertEquals(JvmTypeSort.Class, classRef.sort)
        assertEquals(JvmTypeKind.Object, classRef.kind)
        assertEquals("Lnet/pelsmaeker/kode/types/IT;", classRef.descriptor)
        assertEquals("Lnet/pelsmaeker/kode/types/IT<Lnet/pelsmaeker/kode/types/B;>;", classRef.signature)

        assertFalse(classRef.isPrimitive)
        assertFalse(classRef.isClass)
        assertTrue(classRef.isInterface)
        assertFalse(classRef.isArray)
        assertFalse(classRef.isTypeVariable)
    }

    @Test
    fun `of(X_iottb) B covariant type parameter`() {
        // Arrange
        val b = JvmClassRef.of(B::class.java)

        // Act
        val classRef = JvmClassRef.of(X<A>::iottb.returnType.javaType as ParameterizedType)

        // Assert
        assertEquals(listOf(
            JvmTypeArg(b, JvmTypeArgSort.Invariant)
        ), classRef.typeArguments)
        assertEquals(JvmNullability.Maybe, classRef.nullability)
        assertNull(classRef.enclosingClassRef)

        assertEquals("net/pelsmaeker/kode/types/IOT", classRef.internalName)
        assertEquals("net.pelsmaeker.kode.types.IOT", classRef.javaName)
        assertEquals(JvmTypeSort.Class, classRef.sort)
        assertEquals(JvmTypeKind.Object, classRef.kind)
        assertEquals("Lnet/pelsmaeker/kode/types/IOT;", classRef.descriptor)
        assertEquals("Lnet/pelsmaeker/kode/types/IOT<Lnet/pelsmaeker/kode/types/B;>;", classRef.signature)

        assertFalse(classRef.isPrimitive)
        assertFalse(classRef.isClass)
        assertTrue(classRef.isInterface)
        assertFalse(classRef.isArray)
        assertFalse(classRef.isTypeVariable)
    }

    @Test
    fun `of(X_iittb) B contravariant type parameter`() {
        // Arrange
        val b = JvmClassRef.of(B::class.java)

        // Act
        val classRef = JvmClassRef.of(X<A>::iittb.returnType.javaType as ParameterizedType)

        // Assert
        assertEquals(listOf(
            JvmTypeArg(b, JvmTypeArgSort.Invariant)
        ), classRef.typeArguments)
        assertEquals(JvmNullability.Maybe, classRef.nullability)
        assertNull(classRef.enclosingClassRef)

        assertEquals("net/pelsmaeker/kode/types/IIT", classRef.internalName)
        assertEquals("net.pelsmaeker.kode.types.IIT", classRef.javaName)
        assertEquals(JvmTypeSort.Class, classRef.sort)
        assertEquals(JvmTypeKind.Object, classRef.kind)
        assertEquals("Lnet/pelsmaeker/kode/types/IIT;", classRef.descriptor)
        assertEquals("Lnet/pelsmaeker/kode/types/IIT<Lnet/pelsmaeker/kode/types/B;>;", classRef.signature)

        assertFalse(classRef.isPrimitive)
        assertFalse(classRef.isClass)
        assertTrue(classRef.isInterface)
        assertFalse(classRef.isArray)
        assertFalse(classRef.isTypeVariable)
    }

    @Test
    fun `ref(Void) built-in class`() {
        // Arrange
        val classDecl = JvmClassDecl.of(java.lang.Void::class.java)

        // Act
        val classRef = classDecl.ref()

        // Assert
        assertEquals(classDecl, classRef.declaration)
        assertEquals(emptyList<JvmTypeArg>(), classRef.typeArguments)
        assertEquals(JvmNullability.Maybe, classRef.nullability)
        assertNull(classRef.enclosingClassRef)

        assertEquals("java/lang/Void", classRef.internalName)
        assertEquals("java.lang.Void", classRef.javaName)
        assertEquals(JvmTypeSort.Class, classRef.sort)
        assertEquals(JvmTypeKind.Object, classRef.kind)
        assertEquals("Ljava/lang/Void;", classRef.descriptor)
        assertEquals("Ljava/lang/Void;", classRef.signature)

        assertFalse(classRef.isPrimitive)
        assertTrue(classRef.isClass)
        assertFalse(classRef.isInterface)
        assertFalse(classRef.isArray)
        assertFalse(classRef.isTypeVariable)
    }

}