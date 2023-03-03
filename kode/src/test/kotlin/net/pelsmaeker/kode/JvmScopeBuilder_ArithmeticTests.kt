package net.pelsmaeker.kode

import io.kotest.core.spec.style.FunSpec
import net.pelsmaeker.kode.types.JvmDouble
import net.pelsmaeker.kode.types.JvmFloat
import net.pelsmaeker.kode.types.JvmInteger
import net.pelsmaeker.kode.types.JvmLong
import org.junit.jupiter.api.Assertions

/**
 * Arithmetic tests.
 */
@Suppress("ClassName")
class JvmScopeBuilder_ArithmeticTests: FunSpec({


    fun intBinOpTest(value1: Int, value2: Int, expected: Int, op: JvmScopeBuilder.() -> Unit) {
        // Arrange
        val (methodDecl, compiledClass) = buildEvalMethodWith(JvmInteger) {
            iConst(value1)
            iConst(value2)
            op()
            iReturn()
        }

        // Act
        val result: Int = compiledClass.runMethodTo(methodDecl)

        // Assert
        Assertions.assertEquals(expected, result)
    }

    fun intUnOpTest(value: Int, expected: Int, op: JvmScopeBuilder.() -> Unit) {
        // Arrange
        val (methodDecl, compiledClass) = buildEvalMethodWith(JvmInteger) {
            iConst(value)
            op()
            iReturn()
        }

        // Act
        val result: Int = compiledClass.runMethodTo(methodDecl)

        // Assert
        Assertions.assertEquals(expected, result)
    }

    fun longBinOpTest(value1: Long, value2: Long, expected: Long, op: JvmScopeBuilder.() -> Unit) {
        // Arrange
        val (methodDecl, compiledClass) = buildEvalMethodWith(JvmLong) {
            lConst(value1)
            lConst(value2)
            op()
            lReturn()
        }

        // Act
        val result: Long = compiledClass.runMethodTo(methodDecl)

        // Assert
        Assertions.assertEquals(expected, result)
    }

    // Second value is an Int, for SHL/SHR
    fun longBinOpITest(value1: Long, value2: Int, expected: Long, op: JvmScopeBuilder.() -> Unit) {
        // Arrange
        val (methodDecl, compiledClass) = buildEvalMethodWith(JvmLong) {
            lConst(value1)
            iConst(value2)
            op()
            lReturn()
        }

        // Act
        val result: Long = compiledClass.runMethodTo(methodDecl)

        // Assert
        Assertions.assertEquals(expected, result)
    }

    fun longUnOpTest(value: Long, expected: Long, op: JvmScopeBuilder.() -> Unit) {
        // Arrange
        val (methodDecl, compiledClass) = buildEvalMethodWith(JvmLong) {
            lConst(value)
            op()
            lReturn()
        }

        // Act
        val result: Long = compiledClass.runMethodTo(methodDecl)

        // Assert
        Assertions.assertEquals(expected, result)
    }

    fun floatBinOpTest(value1: Float, value2: Float, expected: Float, op: JvmScopeBuilder.() -> Unit) {
        // Arrange
        val (methodDecl, compiledClass) = buildEvalMethodWith(JvmFloat) {
            fConst(value1)
            fConst(value2)
            op()
            fReturn()
        }

        // Act
        val result: Float = compiledClass.runMethodTo(methodDecl)

        // Assert
        Assertions.assertEquals(expected, result)
    }

    fun floatUnOpTest(value: Float, expected: Float, op: JvmScopeBuilder.() -> Unit) {
        // Arrange
        val (methodDecl, compiledClass) = buildEvalMethodWith(JvmFloat) {
            fConst(value)
            op()
            fReturn()
        }

        // Act
        val result: Float = compiledClass.runMethodTo(methodDecl)

        // Assert
        Assertions.assertEquals(expected, result)
    }
    
    fun doubleBinOpTest(value1: Double, value2: Double, expected: Double, op: JvmScopeBuilder.() -> Unit) {
        // Arrange
        val (methodDecl, compiledClass) = buildEvalMethodWith(JvmDouble) {
            dConst(value1)
            dConst(value2)
            op()
            dReturn()
        }

        // Act
        val result: Double = compiledClass.runMethodTo(methodDecl)

        // Assert
        Assertions.assertEquals(expected, result)
    }

    fun doubleUnOpTest(value: Double, expected: Double, op: JvmScopeBuilder.() -> Unit) {
        // Arrange
        val (methodDecl, compiledClass) = buildEvalMethodWith(JvmDouble) {
            dConst(value)
            op()
            dReturn()
        }

        // Act
        val result: Double = compiledClass.runMethodTo(methodDecl)

        // Assert
        Assertions.assertEquals(expected, result)
    }

    /////////////
    // INTEGER //
    /////////////
    
    test("iAdd() should add two int values") {
        intBinOpTest(35, 20, 55) { iAdd() }
    }

    test("iSub() should subtract two int values") {
        intBinOpTest(35, 20, 15) { iSub() }
    }

    test("iDiv() should divide two int values") {
        intBinOpTest(35, 2, 17) { iDiv() }
    }

    test("iMul() should multiple two int values") {
        intBinOpTest(35, 2, 70) { iMul() }
    }

    test("iRem() should return remainder of dividing two int values") {
        intBinOpTest(35, 20, 15) { iRem() }
    }

    test("iNeg() should return negation of a int value") {
        intUnOpTest(35, -35) { iNeg() }
    }

    test("iAnd() should bitwise-AND two int values") {
        intBinOpTest(35, 23, 3) { iAnd() }
    }

    test("iOr() should bitwise-OR two int values") {
        intBinOpTest(35, 23, 55) { iOr() }
    }

    test("iXor() should bitwise-XOR two int values") {
        intBinOpTest(35, 23, 52) { iXor() }
    }

    test("iShl() should left-shift an int value") {
        intBinOpTest(35, 2, 140) { iShl() }
    }

    test("iShr() should right-shift an int value") {
        intBinOpTest(35, 2, 8) { iShr() }
    }

    test("iuShr() should unsigned right-shift an int value") {
        intBinOpTest(-35, 2, 1_073_741_815) { iuShr() }
    }

    
    //////////
    // LONG //
    //////////

    test("lAdd() should add two long values") {
        longBinOpTest(35L, 20L, 55L) { lAdd() }
    }

    test("lSub() should subtract two long values") {
        longBinOpTest(35L, 20L, 15L) { lSub() }
    }

    test("lDiv() should divide two long values") {
        longBinOpTest(35L, 2L, 17L) { lDiv() }
    }

    test("lMul() should multiple two long values") {
        longBinOpTest(35L, 2L, 70L) { lMul() }
    }

    test("lRem() should return remainder of dividing two long values") {
        longBinOpTest(35L, 20L, 15L) { lRem() }
    }

    test("lNeg() should return negation of a long value") {
        longUnOpTest(35L, -35L) { lNeg() }
    }

    test("lAnd() should bitwise-AND two long values") {
        longBinOpTest(35L, 23L, 3L) { lAnd() }
    }

    test("lOr() should bitwise-OR two long values") {
        longBinOpTest(35L, 23L, 55L) { lOr() }
    }

    test("lXor() should bitwise-XOR two long values") {
        longBinOpTest(35L, 23L, 52L) { lXor() }
    }

    test("lShl() should left-shift an long value") {
        longBinOpITest(35L, 2, 140L) { lShl() }
    }

    test("lShr() should right-shift an long value") {
        longBinOpITest(35L, 2, 8L) { lShr() }
    }

    test("luShr() should unsigned right-shift an long value") {
        longBinOpITest(-35L, 2, 4_611_686_018_427_387_895L) { luShr() }
    }


    ///////////
    // FLOAT //
    ///////////

    test("fAdd() should add two float values") {
        floatBinOpTest(3.5f, 2.0f, 5.5f) { fAdd() }
    }

    test("fSub() should subtract two float values") {
        floatBinOpTest(3.5f, 2.0f, 1.5f) { fSub() }
    }

    test("fDiv() should divide two float values") {
        floatBinOpTest(3.5f, 2.0f, 1.75f) { fDiv() }
    }

    test("fMul() should multiple two float values") {
        floatBinOpTest(3.5f, 2.0f, 7.0f) { fMul() }
    }

    test("fRem() should return remainder of dividing two float values") {
        floatBinOpTest(3.5f, 2.0f, 1.5f) { fRem() }
    }

    test("fNeg() should return negation of a float value") {
        floatUnOpTest(3.5f, -3.5f) { fNeg() }
    }


    ////////////
    // DOUBLE //
    ////////////
    
    test("dAdd() should add two double values") {
        doubleBinOpTest(3.5, 2.0, 5.5) { dAdd() }
    }

    test("dSub() should subtract two double values") {
        doubleBinOpTest(3.5, 2.0, 1.5) { dSub() }
    }

    test("dDiv() should divide two double values") {
        doubleBinOpTest(3.5, 2.0, 1.75) { dDiv() }
    }

    test("dMul() should multiple two double values") {
        doubleBinOpTest(3.5, 2.0, 7.0) { dMul() }
    }

    test("dRem() should return remainder of dividing two double values") {
        doubleBinOpTest(3.5, 2.0, 1.5) { dRem() }
    }

    test("dNeg() should return negation of a double value") {
        doubleUnOpTest(3.5, -3.5) { dNeg() }
    }


})