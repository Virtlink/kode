@file:Suppress("unused")

package net.pelsmaeker.kode.types

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

/** Tests the [JvmClassDecl] class. */
class JvmClassDeclTests {

    @Test
    fun `of(Void) built-in class`() {
        // Act
        val classDecl = JvmClassDecl.of(java.lang.Void::class.java)

        // Assert
        assertEquals("Void", classDecl.name)
        assertEquals("java/lang", classDecl.pkg.internalName)
        assertEquals("java/lang/Void", classDecl.internalName)
        assertEquals("java.lang.Void", classDecl.javaName)
        assertEquals(emptyList<JvmTypeParam>(), classDecl.signature.typeParameters)
        assertNull(classDecl.enclosingClass)
        assertFalse(classDecl.isInterface)
        assertTrue(classDecl.isClass)
        assertFalse(classDecl.isInnerClass)
    }

    @Test
    fun `of(C) top-level class`() {
        // Act
        val classDecl = JvmClassDecl.of(C::class.java)

        // Assert
        assertEquals("C", classDecl.name)
        assertEquals("net/pelsmaeker/kode/types", classDecl.pkg.internalName)
        assertEquals("net/pelsmaeker/kode/types/C", classDecl.internalName)
        assertEquals("net.pelsmaeker.kode.types.C", classDecl.javaName)
        assertEquals(emptyList<JvmTypeParam>(), classDecl.signature.typeParameters)
        assertNull(classDecl.enclosingClass)
        assertFalse(classDecl.isInterface)
        assertTrue(classDecl.isClass)
        assertFalse(classDecl.isInnerClass)
    }

    @Test
    fun `of(C_CIC) inner class`() {
        // Act
        val classDecl = JvmClassDecl.of(C.CIC::class.java)

        // Assert
        assertEquals("CIC", classDecl.name)
        assertEquals("net/pelsmaeker/kode/types", classDecl.pkg.internalName)
        assertEquals("net/pelsmaeker/kode/types/C\$CIC", classDecl.internalName)
        assertEquals("net.pelsmaeker.kode.types.C\$CIC", classDecl.javaName)
        assertEquals(emptyList<JvmTypeParam>(), classDecl.signature.typeParameters)
        assertEquals(JvmClassDecl.of(C::class.java), classDecl.enclosingClass)
        assertFalse(classDecl.isInterface)
        assertTrue(classDecl.isClass)
        assertTrue(classDecl.isInnerClass)
    }

    @Test
    fun `of(C_CNC) nested class`() {
        // Act
        val classDecl = JvmClassDecl.of(C.CNC::class.java)

        // Assert
        assertEquals("C\$CNC", classDecl.name)
        assertEquals("net/pelsmaeker/kode/types", classDecl.pkg.internalName)
        assertEquals("net/pelsmaeker/kode/types/C\$CNC", classDecl.internalName)
        assertEquals("net.pelsmaeker.kode.types.C\$CNC", classDecl.javaName)
        assertEquals(emptyList<JvmTypeParam>(), classDecl.signature.typeParameters)
        assertNull(classDecl.enclosingClass)
        assertFalse(classDecl.isInterface)
        assertTrue(classDecl.isClass)
        assertFalse(classDecl.isInnerClass)
    }

    @Test
    fun `of(C_CNI) nested interface`() {
        // Act
        val classDecl = JvmClassDecl.of(C.CNI::class.java)

        // Assert
        assertEquals("C\$CNI", classDecl.name)
        assertEquals("net/pelsmaeker/kode/types", classDecl.pkg.internalName)
        assertEquals("net/pelsmaeker/kode/types/C\$CNI", classDecl.internalName)
        assertEquals("net.pelsmaeker.kode.types.C\$CNI", classDecl.javaName)
        assertEquals(emptyList<JvmTypeParam>(), classDecl.signature.typeParameters)
        assertNull(classDecl.enclosingClass)
        assertTrue(classDecl.isInterface)
        assertFalse(classDecl.isClass)
        assertFalse(classDecl.isInnerClass)
    }

    @Test
    fun `of(I) top-level interface`() {
        // Act
        val classDecl = JvmClassDecl.of(I::class.java)

        // Assert
        assertEquals("I", classDecl.name)
        assertEquals("net/pelsmaeker/kode/types", classDecl.pkg.internalName)
        assertEquals("net/pelsmaeker/kode/types/I", classDecl.internalName)
        assertEquals("net.pelsmaeker.kode.types.I", classDecl.javaName)
        assertEquals(emptyList<JvmTypeParam>(), classDecl.signature.typeParameters)
        assertNull(classDecl.enclosingClass)
        assertTrue(classDecl.isInterface)
        assertFalse(classDecl.isClass)
        assertFalse(classDecl.isInnerClass)
    }

    @Test
    fun `of(I_INC) nested class`() {
        // Act
        val classDecl = JvmClassDecl.of(I.INC::class.java)

        // Assert
        assertEquals("I\$INC", classDecl.name)
        assertEquals("net/pelsmaeker/kode/types", classDecl.pkg.internalName)
        assertEquals("net/pelsmaeker/kode/types/I\$INC", classDecl.internalName)
        assertEquals("net.pelsmaeker.kode.types.I\$INC", classDecl.javaName)
        assertEquals(emptyList<JvmTypeParam>(), classDecl.signature.typeParameters)
        assertNull(classDecl.enclosingClass)
        assertFalse(classDecl.isInterface)
        assertTrue(classDecl.isClass)
        assertFalse(classDecl.isInnerClass)
    }

    @Test
    fun `of(I_INI) nested interface`() {
        // Act
        val classDecl = JvmClassDecl.of(I.INI::class.java)

        // Assert
        assertEquals("I\$INI", classDecl.name)
        assertEquals("net/pelsmaeker/kode/types", classDecl.pkg.internalName)
        assertEquals("net/pelsmaeker/kode/types/I\$INI", classDecl.internalName)
        assertEquals("net.pelsmaeker.kode.types.I\$INI", classDecl.javaName)
        assertEquals(emptyList<JvmTypeParam>(), classDecl.signature.typeParameters)
        assertNull(classDecl.enclosingClass)
        assertTrue(classDecl.isInterface)
        assertFalse(classDecl.isClass)
        assertFalse(classDecl.isInnerClass)
    }

    @Test
    fun `of(GC) generic top-level class`() {
        // Act
        val classDecl = JvmClassDecl.of(GC::class.java)

        // Assert
        assertEquals("GC", classDecl.name)
        assertEquals("net/pelsmaeker/kode/types", classDecl.pkg.internalName)
        assertEquals("net/pelsmaeker/kode/types/GC", classDecl.internalName)
        assertEquals("net.pelsmaeker.kode.types.GC", classDecl.javaName)
        assertEquals(listOf(
            JvmTypeParam("T", JvmTypes.Object.ref()),
        ), classDecl.signature.typeParameters)
        assertNull(classDecl.enclosingClass)
        assertFalse(classDecl.isInterface)
        assertTrue(classDecl.isClass)
        assertFalse(classDecl.isInnerClass)
    }

    @Test
    fun `of(GC_GCIC) generic inner class`() {
        // Act
        val classDecl = JvmClassDecl.of(GC.GCIC::class.java)

        // Assert
        assertEquals("GCIC", classDecl.name)
        assertEquals("net/pelsmaeker/kode/types", classDecl.pkg.internalName)
        assertEquals("net/pelsmaeker/kode/types/GC\$GCIC", classDecl.internalName)
        assertEquals("net.pelsmaeker.kode.types.GC\$GCIC", classDecl.javaName)
        assertEquals(listOf(
            JvmTypeParam("T", JvmTypes.Object.ref()),
            JvmTypeParam("U", JvmTypes.Object.ref()),
        ), classDecl.signature.typeParameters)
        assertEquals(JvmClassDecl.of(GC::class.java), classDecl.enclosingClass)
        assertFalse(classDecl.isInterface)
        assertTrue(classDecl.isClass)
        assertTrue(classDecl.isInnerClass)
    }

    @Test
    fun `of(GC_GCNC) generic nested class`() {
        // Act
        val classDecl = JvmClassDecl.of(GC.GCNC::class.java)

        // Assert
        assertEquals("GC\$GCNC", classDecl.name)
        assertEquals("net/pelsmaeker/kode/types", classDecl.pkg.internalName)
        assertEquals("net/pelsmaeker/kode/types/GC\$GCNC", classDecl.internalName)
        assertEquals("net.pelsmaeker.kode.types.GC\$GCNC", classDecl.javaName)
        assertEquals(listOf(
            JvmTypeParam("T", JvmTypes.Object.ref()),
            JvmTypeParam("U", JvmTypes.Object.ref()),
        ), classDecl.signature.typeParameters)
        assertNull(classDecl.enclosingClass)
        assertFalse(classDecl.isInterface)
        assertTrue(classDecl.isClass)
        assertFalse(classDecl.isInnerClass)
    }

    @Test
    fun `of(GC_GCNI) generic nested interface`() {
        // Act
        val classDecl = JvmClassDecl.of(GC.GCNI::class.java)

        // Assert
        assertEquals("GC\$GCNI", classDecl.name)
        assertEquals("net/pelsmaeker/kode/types", classDecl.pkg.internalName)
        assertEquals("net/pelsmaeker/kode/types/GC\$GCNI", classDecl.internalName)
        assertEquals("net.pelsmaeker.kode.types.GC\$GCNI", classDecl.javaName)
        assertEquals(listOf(
            JvmTypeParam("T", JvmTypes.Object.ref()),
            JvmTypeParam("U", JvmTypes.Object.ref()),
        ), classDecl.signature.typeParameters)
        assertNull(classDecl.enclosingClass)
        assertTrue(classDecl.isInterface)
        assertFalse(classDecl.isClass)
        assertFalse(classDecl.isInnerClass)
    }

    @Test
    fun `of(GI) generic top-level interface`() {
        // Act
        val classDecl = JvmClassDecl.of(GI::class.java)

        // Assert
        assertEquals("GI", classDecl.name)
        assertEquals("net/pelsmaeker/kode/types", classDecl.pkg.internalName)
        assertEquals("net/pelsmaeker/kode/types/GI", classDecl.internalName)
        assertEquals("net.pelsmaeker.kode.types.GI", classDecl.javaName)
        assertEquals(listOf(
            JvmTypeParam("T", JvmTypes.Object.ref()),
        ), classDecl.signature.typeParameters)
        assertNull(classDecl.enclosingClass)
        assertTrue(classDecl.isInterface)
        assertFalse(classDecl.isClass)
        assertFalse(classDecl.isInnerClass)
    }

    @Test
    fun `of(GI_GINC) generic nested class`() {
        // Act
        val classDecl = JvmClassDecl.of(GI.GINC::class.java)

        // Assert
        assertEquals("GI\$GINC", classDecl.name)
        assertEquals("net/pelsmaeker/kode/types", classDecl.pkg.internalName)
        assertEquals("net/pelsmaeker/kode/types/GI\$GINC", classDecl.internalName)
        assertEquals("net.pelsmaeker.kode.types.GI\$GINC", classDecl.javaName)
        assertEquals(listOf(
            JvmTypeParam("T", JvmTypes.Object.ref()),
            JvmTypeParam("U", JvmTypes.Object.ref()),
        ), classDecl.signature.typeParameters)
        assertNull(classDecl.enclosingClass)
        assertFalse(classDecl.isInterface)
        assertTrue(classDecl.isClass)
        assertFalse(classDecl.isInnerClass)
    }

    @Test
    fun `of(GI_GINI) generic nested interface`() {
        // Act
        val classDecl = JvmClassDecl.of(GI.GINI::class.java)

        // Assert
        assertEquals("GI\$GINI", classDecl.name)
        assertEquals("net/pelsmaeker/kode/types", classDecl.pkg.internalName)
        assertEquals("net/pelsmaeker/kode/types/GI\$GINI", classDecl.internalName)
        assertEquals("net.pelsmaeker.kode.types.GI\$GINI", classDecl.javaName)
        assertEquals(listOf(
            JvmTypeParam("T", JvmTypes.Object.ref()),
            JvmTypeParam("U", JvmTypes.Object.ref()),
        ), classDecl.signature.typeParameters)
        assertNull(classDecl.enclosingClass)
        assertTrue(classDecl.isInterface)
        assertFalse(classDecl.isClass)
        assertFalse(classDecl.isInnerClass)
    }

    @Suppress("LocalVariableName")
    @Test
    fun `of(GC2) generic class with various type parameters`() {
        // Arrange
        val gc = JvmClassDecl.of(GC::class.java)
        val gc_gcic = JvmClassDecl.of(GC.GCIC::class.java)
        val c = JvmClassDecl.of(C::class.java)
        val i = JvmClassDecl.of(I::class.java)
        val gc_gcni = JvmClassDecl.of(GC.GCNI::class.java)
        val gi = JvmClassDecl.of(GI::class.java)

        // Act
        val classDecl = JvmClassDecl.of(GC2::class.java)

        // Assert
        assertEquals("GC2", classDecl.name)
        assertEquals("net/pelsmaeker/kode/types", classDecl.pkg.internalName)
        assertEquals("net/pelsmaeker/kode/types/GC2", classDecl.internalName)
        assertEquals("net.pelsmaeker.kode.types.GC2", classDecl.javaName)
        assertEquals(listOf(
            JvmTypeParam("T", JvmTypes.Object.ref()),
            JvmTypeParam("U", gc.ref(), listOf(gi.ref())),
            JvmTypeParam("V", gc_gcic.ref(JvmTypeArg("T"), JvmTypeArg(c.ref()))),
            JvmTypeParam("W", null, listOf(gc_gcni.ref(JvmTypeArg(c.ref()), JvmTypeArg("U")))),
            JvmTypeParam("X", c.ref(), listOf(i.ref())),
            JvmTypeParam("Y", c.ref(), listOf(i.ref())),
        ), classDecl.signature.typeParameters)
        assertNull(classDecl.enclosingClass)
        assertFalse(classDecl.isInterface)
        assertTrue(classDecl.isClass)
        assertFalse(classDecl.isInnerClass)
    }
}