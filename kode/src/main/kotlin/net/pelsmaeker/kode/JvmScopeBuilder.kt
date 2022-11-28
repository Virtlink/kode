package net.pelsmaeker.kode

import net.pelsmaeker.kode.utils.Scoped
import net.pelsmaeker.kode.types.*
import net.pelsmaeker.kode.utils.Eponymizer
import org.objectweb.asm.Type
import org.objectweb.asm.Opcodes

/**
 * Builds a JVM code scope.
 *
 * Call [build] when done with this builder.
 */
@Suppress("MemberVisibilityCanBePrivate", "unused", "FunctionName")
class JvmScopeBuilder(
    /** The owning method builder. */
    val methodBuilder: JvmMethodBuilder,
    /** The name of the scope builder, for debugging purposes. */
    name: String? = methodBuilder.toString(),
    /** The parent scope builder; or `null`. */
    parent: JvmScopeBuilder? = null,
    /** The scope eponymizer. */
    val eponymizer: Eponymizer,
): Scoped<JvmScopeBuilder>(name, parent), JvmScope {

    /** The label for the start of the scope. */
    override val startLabel: JvmLabel = JvmLabel(this.debugName + "_start")

    /** The label for the end of the scope. */
    override val endLabel: JvmLabel = JvmLabel(this.debugName + "_end")

    /** The variables in this scope. */
    val vars: JvmVars = JvmVars(this, name, parent?.vars) { methodBuilder.declaredVars.add(it) }

    init {
        // Add the start label
        methodBuilder.methodVisitor.visitLabel(startLabel.internalLabel)
    }

    /** The type of the class or interface that is being built. */
    val thisType: JvmClassRef
        get() = methodBuilder.classBuilder.declaration.ref() /* FIXME: Is this correct when the type is parameterized? */
    
    /**
     * Creates a new child [JvmScopeBuilder] representing a child scope.
     *
     * This scope cannot be used while any child scope is not closed.
     *
     * @return a new child scope
     */
    fun scope(name: String? = null): JvmScopeBuilder {
        return adoptChild(JvmScopeBuilder(methodBuilder, name ?: methodBuilder.toString(), eponymizer = eponymizer.scope(name ?: methodBuilder.toString())))
    }

    @Deprecated("Prefer using build()")
    override fun close() {
        if (!tryClose()) return

        // Add the end label
        methodBuilder.methodVisitor.visitLabel(endLabel.internalLabel)

        eponymizer.close()
    }

    /**
     * Builds a scope from this scope builder,
     * and closes the builder.
     *
     * @return the scope
     */
    fun build(): JvmScope {
        @Suppress("DEPRECATION")
        close()
        return this
    }

    /**
     * Creates a new local variable.
     *
     * The order in which the local variables are created
     * determines their order in the source. The first local variables
     * to be created should represent the method arguments.
     *
     * @param name the name of the local variable; or `null` when not specified
     * @param type the type of the local variable
     * @return the created local variable
     */
    fun localVar(name: String?, type: JvmType): JvmVar {
        checkUsable()
        return vars.addLocalVar(type, name)
    }

    /**
     * Allocates space for a reference to `this` class.
     *
     * @return the local variable
     */
    fun localThis(): JvmVar {
        return localVar(
            "this",
            methodBuilder.classBuilder.declaration.ref() /* FIXME: Is this correct when the type is parameterized? */
        )
    }

    /**
     * Emits an instruction with the given opcode.
     * @param opcode the opcode to emit
     */
    @Suppress("NOTHING_TO_INLINE", "RedundantUnitReturnType")
    private inline fun emit(opcode: Int): Unit {
        checkUsable()
        methodBuilder.methodVisitor.visitInsn(opcode)
    }

    /**
     * Emits an instruction with the given opcode and operand.
     * @param opcode the opcode to emit
     * @param operand the operand
     */
    @Suppress("NOTHING_TO_INLINE", "RedundantUnitReturnType")
    private inline fun emitInt(opcode: Int, operand: Int): Unit {
        checkUsable()
        methodBuilder.methodVisitor.visitIntInsn(opcode, operand)
    }

    /**
     * Emits an LDC instruction for the given value.
     * @param value the value
     */
    @Suppress("NOTHING_TO_INLINE", "RedundantUnitReturnType")
    private inline fun emitLdc(value: Any?): Unit {
        checkUsable()
        methodBuilder.methodVisitor.visitLdcInsn(value)
    }

    /**
     * Emits a type instruction for the given value.
     * @param opcode the opcode to emit
     * @param type the type
     */
    @Suppress("NOTHING_TO_INLINE", "RedundantUnitReturnType")
    private inline fun emitType(opcode: Int, type: JvmClassRef): Unit {
        checkUsable()
        methodBuilder.methodVisitor.visitTypeInsn(opcode, type.internalName)
    }

    /**
     * Emits a jump instruction for the given value.
     * @param opcode the opcode to emit
     * @param label the label to jump to
     */
    @Suppress("NOTHING_TO_INLINE", "RedundantUnitReturnType")
    private inline fun emitJump(opcode: Int, label: JvmLabel): Unit {
        checkUsable()
        methodBuilder.methodVisitor.visitJumpInsn(opcode, label.internalLabel)
    }

    /**
     * Emits an instruction with the given opcode and variable.
     * @param opcode the opcode to emit
     * @param variable the variable
     */
    @Suppress("NOTHING_TO_INLINE", "RedundantUnitReturnType")
    private inline fun emitVar(opcode: Int, variable: JvmVar): Unit {
        checkUsable()
        methodBuilder.methodVisitor.visitVarInsn(opcode, variable.offset)
    }

    /**
     * Emits an instruction with the given opcode and Integer variable.
     * @param opcode the opcode to emit
     * @param variable the Integer variable
     */
    @Suppress("NOTHING_TO_INLINE", "RedundantUnitReturnType")
    private inline fun emitIVar(opcode: Int, variable: JvmVar): Unit {
        require(variable.type == JvmInteger) {
            "Expected $JvmInteger, got ${variable.type}."
        }
        emitVar(opcode, variable)
    }

    /**
     * Emits an instruction with the given opcode and Long variable.
     * @param opcode the opcode to emit
     * @param variable the Long variable
     */
    @Suppress("NOTHING_TO_INLINE", "RedundantUnitReturnType")
    private inline fun emitLVar(opcode: Int, variable: JvmVar): Unit {
        require(variable.type == JvmLong) {
            "Expected $JvmLong, got ${variable.type}."
        }
        emitVar(opcode, variable)
    }

    /**
     * Emits an instruction with the given opcode and Float variable.
     * @param opcode the opcode to emit
     * @param variable the Float variable
     */
    @Suppress("NOTHING_TO_INLINE", "RedundantUnitReturnType")
    private inline fun emitFVar(opcode: Int, variable: JvmVar): Unit {
        require(variable.type == JvmFloat) {
            "Expected $JvmFloat, got ${variable.type}."
        }
        emitVar(opcode, variable)
    }

    /**
     * Emits an instruction with the given opcode and Double variable.
     * @param opcode the opcode to emit
     * @param variable the Double variable
     */
    @Suppress("NOTHING_TO_INLINE", "RedundantUnitReturnType")
    private inline fun emitDVar(opcode: Int, variable: JvmVar): Unit {
        require(variable.type == JvmDouble) {
            "Expected $JvmDouble, got ${variable.type}."
        }
        emitVar(opcode, variable)
    }

    /**
     * Emits an instruction with the given opcode and Object variable.
     * @param opcode the opcode to emit
     * @param variable the Object variable
     */
    @Suppress("NOTHING_TO_INLINE", "RedundantUnitReturnType")
    private inline fun emitAVar(opcode: Int, variable: JvmVar): Unit {
        require(variable.type.kind == JvmTypeKind.Object) {
            "Expected class or interface type, got ${variable.type}."
        }
        emitVar(opcode, variable)
    }

    //////////
    // LOAD //
    //////////

    /** Load an `int` value from a local variable on top of the stack. */
    fun iLoad(variable: JvmVar) = emitIVar(Opcodes.ILOAD, variable)
    /** Load a `long` from a local variable on top of the stack. */
    fun lLoad(variable: JvmVar) = emitLVar(Opcodes.LLOAD, variable)
    /** Load a `float` from a local variable on top of the stack. */
    fun fLoad(variable: JvmVar) = emitFVar(Opcodes.FLOAD, variable)
    /** Load a `double` from a local variable on top of the stack. */
    fun dLoad(variable: JvmVar) = emitDVar(Opcodes.DLOAD, variable)
    /** Load an object reference from a local variable on top of the stack. */
    fun aLoad(variable: JvmVar) = emitAVar(Opcodes.ALOAD, variable)

    /**
     * Loads a value from a local variable on top of the stack.
     *
     * This method picks the correct instruction for the given type.
     *
     * @param variable the local variable to load from
     */
    fun load(variable: JvmVar) = when (variable.type.kind) {
        JvmTypeKind.Integer -> iLoad(variable)
        JvmTypeKind.Long -> lLoad(variable)
        JvmTypeKind.Float -> fLoad(variable)
        JvmTypeKind.Double -> dLoad(variable)
        JvmTypeKind.Object -> aLoad(variable)
        else -> throw IllegalArgumentException("Unsupported type: " + variable.type)
    }

    ///////////
    // STORE //
    ///////////

    /** Store the `int` on top of the stack into a local variable. */
    fun iStore(variable: JvmVar) = emitIVar(Opcodes.ISTORE, variable)
    /** Store the `long` on top of the stack into a local variable. */
    fun lStore(variable: JvmVar) = emitLVar(Opcodes.LSTORE, variable)
    /** Store the `float` on top of the stack into a local variable. */
    fun fStore(variable: JvmVar) = emitFVar(Opcodes.FSTORE, variable)
    /** Store the `double` on top of the stack into a local variable. */
    fun dStore(variable: JvmVar) = emitDVar(Opcodes.DSTORE, variable)
    /** Store the object reference on top of the stack into a local variable. */
    fun aStore(variable: JvmVar) = emitAVar(Opcodes.ASTORE, variable)

    /**
     * Store the value on top of the stack into a local variable.
     *
     * This method picks the correct instruction for the given type.
     *
     * @param variable the local variable to store to
     */
    fun store(variable: JvmVar) = when (variable.type.kind) {
        JvmTypeKind.Integer -> iStore(variable)
        JvmTypeKind.Long -> lStore(variable)
        JvmTypeKind.Float -> fStore(variable)
        JvmTypeKind.Double -> dStore(variable)
        JvmTypeKind.Object -> aStore(variable)
        else -> throw UnsupportedOperationException("Unsupported type: " + variable.type)
    }

    /////////////////////
    // LOCAL VARIABLES //
    /////////////////////

    /**
     * Increment an `int` local variable by a constant value.
     *
     * @param variable the local variable to increment
     * @param value the signed constant value to increment by
     */
    fun iInc(variable: JvmVar, value: Byte) {
        TODO()
    }

    ///////////
    // STACK //
    ///////////

    /** Pop the top value from the stack. */
    fun pop1() = emit(Opcodes.POP)
    /** Pop the top two values from the stack. */
    fun pop2() = emit(Opcodes.POP2)

    /** Pops a value of the specified type from the stack. */
    fun pop(type: JvmType) = pop(type.kind)
    /** Pops a value of the specified kind from the stack. */
    fun pop(kind: JvmTypeKind) = when (kind.category) {
        1 -> pop1()
        2 -> pop2()
        else -> throw UnsupportedOperationException("Unsupported kind: $kind")
    }

    /** Swap the top two values on the stack. */
    fun swap() = emit(Opcodes.SWAP)

    /** Duplicate the top value on the stack. */
    fun dup1() = emit(Opcodes.DUP)
    /** Duplicate the top two values on the stack. */
    fun dup2() = emit(Opcodes.DUP2)

    /** Duplicate a value of the specified type on the stack. */
    fun dup(type: JvmType) = dup(type.kind)
    /** Duplicate a value of the specified kind on the stack. */
    fun dup(kind: JvmTypeKind) = when (kind.category) {
        1 -> dup1()
        2 -> dup2()
        else -> throw UnsupportedOperationException("Unsupported kind: $kind")
    }

    /** Duplicate the top value up to two values below the top of the stack. */
    fun dup1_x1() = emit(Opcodes.DUP_X1)
    /** Duplicate the two top values up to three values below the top of the stack. */
    fun dup2_x1() = emit(Opcodes.DUP2_X1)

    /** Duplicate a value of the specified type lower on the stack. */
    fun dup_x1(type: JvmType) = dup_x1(type.kind)
    /** Duplicate a value of the specified kind lower on the stack. */
    fun dup_x1(kind: JvmTypeKind) = when (kind.category) {
        1 -> dup1_x1()
        2 -> dup2_x1()
        else -> throw UnsupportedOperationException("Unsupported kind: $kind")
    }

    /** Duplicate the top value up to three values below the top of the stack. */
    fun dup1_x2() = emit(Opcodes.DUP_X2)
    /** Duplicate the up to two top values up to four values below the top of the stack. */
    fun dup2_x2() = emit(Opcodes.DUP2_X2)

    /** Duplicate a value of the specified type lower on the stack. */
    fun dup_x2(type: JvmType) = dup_x2(type.kind)
    /** Duplicate a value of the specified kind lower on the stack. */
    fun dup_x2(kind: JvmTypeKind) = when (kind.category) {
        1 -> dup1_x2()
        2 -> dup2_x2()
        else -> throw UnsupportedOperationException("Unsupported kind: $kind")
    }

    ///////////////
    // CONSTANTS //
    ///////////////

    /** Push constant integer -1 on the stack. */
    fun iConst_m1() = emit(Opcodes.ICONST_M1)
    /** Push constant integer 0 on the stack. */
    fun iConst_0() = emit(Opcodes.ICONST_0)
    /** Push constant integer 1 on the stack. */
    fun iConst_1() = emit(Opcodes.ICONST_1)
    /** Push constant integer 2 on the stack. */
    fun iConst_2() = emit(Opcodes.ICONST_2)
    /** Push constant integer 3 on the stack. */
    fun iConst_3() = emit(Opcodes.ICONST_3)
    /** Push constant integer 4 on the stack. */
    fun iConst_4() = emit(Opcodes.ICONST_4)
    /** Push constant integer 5 on the stack. */
    fun iConst_5() = emit(Opcodes.ICONST_5)

    /**
     * Push a constant `int` on the stack.
     *
     * This method picks the most efficient instruction for the given constant.
     *
     * @param value the constant `int` value
     */
    fun iConst(value: Int) = when (value) {
        -1 -> iConst_m1()
        0 -> iConst_0()
        1 -> iConst_1()
        2 -> iConst_2()
        3 -> iConst_3()
        4 -> iConst_4()
        5 -> iConst_5()
        else -> if (Byte.MIN_VALUE <= value && value <= Byte.MAX_VALUE) {
            biPush(value.toByte())
        } else if (Short.MIN_VALUE <= value && value <= Short.MAX_VALUE) {
            siPush(value.toShort())
        } else {
            ldc(Integer.valueOf(value))
        }
    }

    /** Push a constant byte value on the stack. */
    fun biPush(value: Byte) = emitInt(Opcodes.BIPUSH, value.toInt())
    /** Push a constant short value on the stack. */
    fun siPush(value: Short) = emitInt(Opcodes.SIPUSH, value.toInt())


    /** Push constant long integer 0 on the stack. */
    fun lConst_0() = emit(Opcodes.LCONST_0)
    /** Push constant long integer 1 on the stack. */
    fun lConst_1() = emit(Opcodes.LCONST_1)

    /**
     * Push a constant `long` on the stack.
     *
     * This method picks the most efficient instruction for the given constant.
     *
     * @param value the constant `long` value
     */
    fun lConst(value: Long) = when (value) {
        0L -> lConst_0()
        1L -> lConst_1()
        else -> ldc(java.lang.Long.valueOf(value))
    }

    /** Push constant float 0 on the stack. */
    fun fConst_0() = emit(Opcodes.FCONST_0)
    /** Push constant float 1 on the stack. */
    fun fConst_1() = emit(Opcodes.FCONST_1)
    /** Push constant float 2 on the stack. */
    fun fConst_2() = emit(Opcodes.FCONST_2)

    /**
     * Push a constant `float` on the stack.
     *
     * This method picks the most efficient instruction for the given constant.
     *
     * @param value the constant `float` value
     */
    fun fConst(value: Float) = when (value) {
        0.0f -> fConst_0()
        1.0f -> fConst_1()
        2.0f -> fConst_2()
        else -> ldc(java.lang.Float.valueOf(value))
    }

    /** Push constant double 0 on the stack. */
    fun dConst_0() = emit(Opcodes.DCONST_0)
    /** Push constant double 1 on the stack. */
    fun dConst_1() = emit(Opcodes.DCONST_1)

    /**
     * Push a constant `double` on the stack.
     *
     * This method picks the most efficient instruction for the given constant.
     *
     * @param value the constant `double` value
     */
    fun dConst(value: Double) = when(value) {
        0.0 -> dConst_0()
        1.0 -> dConst_1()
        else -> ldc(java.lang.Double.valueOf(value))
    }

    /** Push constant null on the stack. */
    fun aConst_Null() = emit(Opcodes.ACONST_NULL)

    /**
     * Push a loaded constant on the stack.
     *
     * @param value the constant value to push
     */
    fun ldc(value: Any) = when(value) {
        is JvmType -> emitLdc(Type.getType(value.descriptor))
        else -> emitLdc(value)
    }

    // ldc_w
    // ldc2_w

    /**
     * Push a constant value on the stack.
     *
     * This method picks the most efficient instruction for the given constant.
     *
     * @param value the constant value, which may be `null`
     */
    fun const(value: Any?) = when (value) {
        is Byte -> iConst(value.toInt())
        is Short -> iConst(value.toInt())
        is Int -> iConst(value)
        is Long -> lConst(value)
        is Float -> fConst(value)
        is Double -> dConst(value)
        null -> aConst_Null()
        else -> ldc(value)
    }

    //////////////////////////
    // ARITHMETIC and LOGIC //
    //////////////////////////

    ///////////
    // CASTS //
    ///////////

    /**
     * Checked cast.
     *
     * @param type the type to cast to
     */
    fun checkCast(type: JvmType) {
        TODO()
        //        methodBuilder.methodVisitor.visitTypeInsn(Opcodes.CHECKCAST, type.getInternalName());
    }

    /////////////
    // OBJECTS //
    /////////////

    /**
     * Create a new instance of the specified type.
     *
     * This method is named [newInst] because `new` is a reserved Java keyword.
     */
    fun newInst(type: JvmClassRef) = emitType(Opcodes.NEW, type)

    ////////////
    // ARRAYS //
    ////////////


    ///////////
    // JUMPS //
    ///////////

    /** Pop the top stack value and goto label if greater than 0. */
    fun ifGt(label: JvmLabel) = emitJump(Opcodes.IFGT, label)
    /** Pop the top stack value and goto label if greater than or equal to 0. */
    fun ifGe(label: JvmLabel) = emitJump(Opcodes.IFGE, label)
    /** Pop the top stack value and goto label if equal to 0. */
    fun ifEq(label: JvmLabel) = emitJump(Opcodes.IFEQ, label)
    /** Pop the top stack value and goto label if not equal to 0. */
    fun ifNe(label: JvmLabel) = emitJump(Opcodes.IFNE, label)
    /** Pop the top stack value and goto label if less than or equal to 0. */
    fun ifLe(label: JvmLabel) = emitJump(Opcodes.IFLE, label)
    /** Pop the top stack value and goto label if less than 0. */
    fun ifLt(label: JvmLabel) = emitJump(Opcodes.IFLT, label)
    /** Pop the top stack value and goto label if `null`. */
    fun ifNull(label: JvmLabel) = emitJump(Opcodes.IFNULL, label)
    /** Pop the top stack value and goto label if not `null`. */
    fun ifNonNull(label: JvmLabel) = emitJump(Opcodes.IFNONNULL, label)

    /**
     * Goto label unconditionally.
     *
     * This method is named [jump] because `goto` is a reserved Java keyword.
     */
    fun jump(label: JvmLabel) = emitJump(Opcodes.GOTO, label)

    /////////////////////////////
    // LABELS and LINE NUMBERS //
    /////////////////////////////

    /**
     * Adds an existing label for the specified line number.
     *
     * @param lineNumber the one-based line number in the source
     * @param label the label
     * @return the label
     */
    fun lineNumber(lineNumber: Int, label: JvmLabel = label()): JvmLabel {
        methodBuilder.methodVisitor.visitLineNumber(lineNumber, label.internalLabel)
        return label
    }

    /**
     * Adds an existing label.
     *
     * @param label the label
     * @return the added label
     */
    fun label(label: JvmLabel): JvmLabel {
        methodBuilder.methodVisitor.visitLabel(label.internalLabel)
        return label
    }

    /**
     * Adds a new label with the specified name.
     *
     * @param name the name of the label, for debugging; or `null` if not specified
     * @return the added label
     */
    fun label(name: String? = null): JvmLabel = label(JvmLabel(name))

    //////////////////
    // CONTROL FLOW //
    //////////////////

    /** Pop an object reference from the stack and throw it. */
    fun aThrow() = emit(Opcodes.ATHROW)

    /** Pop an `int` from the stack and return it. */
    fun iReturn() = emit(Opcodes.IRETURN)
    /** Pop a `long` from the stack and return it. */
    fun lReturn() = emit(Opcodes.LRETURN)
    /** Pop a `float` from the stack and return it. */
    fun fReturn() = emit(Opcodes.FRETURN)
    /** Pop a `double` from the stack and return it. */
    fun dReturn() = emit(Opcodes.DRETURN)
    /** Pop an object reference from the stack and return it. */
    fun aReturn() = emit(Opcodes.ARETURN)

    /**
     * Return from a `void` method.
     *
     * This method is named [vReturn] because `return` is a reserved Java keyword.
     */
    fun vReturn() = emit(Opcodes.RETURN)

    /**
     * Pop a value from the stack and return it.
     *
     * This method is named [ret] because `return` is a reserved Java keyword.
     * This method picks the correct instruction for the given type.
     *
     * Note that if the return type is the primitive `void`, this method will generate a
     * `return` instruction that takes no value from the stack. However, if the return type is
     * the [java.lang.Void] object, this method will generate an `aReturn` instruction that
     * takes an object of type [java.lang.Void] from the stack (which can only ever be `null`).
     *
     * @param type the type of value to return
     */
    fun ret(type: JvmType) = when (type.kind) {
        JvmTypeKind.Void -> vReturn()
        JvmTypeKind.Integer -> iReturn()
        JvmTypeKind.Long -> lReturn()
        JvmTypeKind.Float -> fReturn()
        JvmTypeKind.Double -> dReturn()
        JvmTypeKind.Object -> aReturn()
    }

    /**
     * Pops an integer value from the stack and looks it up in the map.
     * Jumps to the label corresponding to the value;
     * otherwise, jumps to the default label.
     *
     * @param targets the jump targets
     * @param defaultTarget the default target if none of the specified targets match
     */
    fun switch(targets: Map<Int, JvmLabel>, defaultTarget: JvmLabel) {
        checkUsable()
        if (targets.isEmpty()) {
            jump(defaultTarget)
            return
        }

        val maxCount = targets.keys.max() - targets.keys.min() + 1
        val actualCount = targets.size
        if (maxCount - actualCount > 10)
            lookupSwitch(targets, defaultTarget)
        else
            tableSwitch(targets, defaultTarget)
    }

    /**
     * Pops an integer value from the stack and looks it up in the map.
     * Jumps to the label corresponding to the value;
     * otherwise, jumps to the default label.
     *
     * This method is fast if the values are far apart.
     *
     * @param targets the jump targets
     * @param defaultTarget the default target if none of the specified targets match
     */
    fun lookupSwitch(targets: Map<Int, JvmLabel>, defaultTarget: JvmLabel) {
        checkUsable()
        if (targets.isEmpty()) {
            jump(defaultTarget)
            return
        }

        val sorted = targets.entries.sortedBy { it.key }
        methodBuilder.methodVisitor.visitLookupSwitchInsn(
            defaultTarget.internalLabel,
            sorted.map { it.key }.toIntArray(),
            sorted.map { it.value.internalLabel }.toTypedArray(),
        )
    }

    /**
     * Pops an integer value from the stack and looks it up in the map.
     * Jumps to the label corresponding to the value;
     * otherwise, jumps to the default label.
     *
     * This method is fast if the values are close together.
     *
     * @param targets the jump targets
     * @param defaultTarget the default target if none of the specified targets match
     */
    fun tableSwitch(targets: Map<Int, JvmLabel>, defaultTarget: JvmLabel) {
        checkUsable()
        if (targets.isEmpty()) {
            jump(defaultTarget)
            return
        }

        val sorted = targets.entries.sortedBy { it.key }
        val min = sorted.first().key
        val max = sorted.last().key

        // Populate the array with the labels; or null if an entry is not present
        val labels = arrayOfNulls<JvmLabel>(max - min + 1)
        for (i in min .. max) {
            labels[i - min] = targets[i]
        }

        methodBuilder.methodVisitor.visitTableSwitchInsn(
            min,
            max,
            defaultTarget.internalLabel,
            // If the entry is null, jump to the default label
            *labels.map { it?.internalLabel ?: defaultTarget.internalLabel }.toTypedArray()
        )
    }

    /////////////////////////////
    // METHOD and CONSTRUCTORS //
    /////////////////////////////

    /**
     * Invoke an instance constructor on a class.
     *
     * Pop the object reference and arguments from the stack.
     *
     * @param owner the owner of the constructor
     * @param signature the signature of the constructor to be called
     */
    fun invokeConstructor(owner: JvmClassRef, signature: JvmMethodSignature) {
        invokeMethod(JvmMethodDecl(null, owner.declaration, signature, JvmMethodModifiers.None).ref(owner))
    }

    /**
     * Invoke a static or instance method or constructor on a class or interface.
     *
     * Pop the object reference (if it's an instance method) and arguments from the stack,
     * and push the return value onto the stack (if it's not a constructor).
     *
     * @param method the reference to the method or constructor to be called
     */
    fun invokeMethod(method: JvmMethodRef) {
        checkUsable()
        when {
            method.isConstructor -> {
                // Constructor
                check(!method.owner.isInterface) { "Cannot invoke a constructor on an interface: $method" }
                if (method.isStatic) {
                    // Static constructor
                    TODO()
                } else {
                    // Instance constructor
                    methodBuilder.methodVisitor.visitMethodInsn(
                        Opcodes.INVOKESPECIAL,
                        method.owner.internalName,
                        "<init>",
                        method.descriptor,
                        false
                    )
                }
            }
            method.isStatic -> {
                // Static method
                methodBuilder.methodVisitor.visitMethodInsn(
                    Opcodes.INVOKESTATIC,
                    method.owner.internalName,
                    method.name,
                    method.descriptor,
                    method.owner.isInterface
                )
            }
            method.owner.isInterface -> {
                // Instance method on an interface
                methodBuilder.methodVisitor.visitMethodInsn(
                    Opcodes.INVOKEINTERFACE,
                    method.owner.internalName,
                    method.name,
                    method.descriptor,
                    true
                )
            }
            else -> {
                // Instance method on a class
                methodBuilder.methodVisitor.visitMethodInsn(
                    Opcodes.INVOKEVIRTUAL,
                    method.owner.internalName,
                    method.name,
                    method.descriptor,
                    false
                )
            }
        }
    }

    /**
     * Invoke a dynamic method.
     *
     * @param name X
     * @param signature X
     * @param handle X
     * @param arguments X
     */
    fun invokeDynamic(
        name: String,
        signature: JvmMethodSignature,
        handle: org.objectweb.asm.Handle,
        vararg arguments: Any,
    ) {
        checkUsable()
        methodBuilder.methodVisitor.visitInvokeDynamicInsn(
            name,
            signature.descriptor,
            handle,
            arguments
        )
    }

    ////////////
    // FIELDS //
    ////////////

    /**
     * Pop an object reference from the stack,
     * and push the value of an instance field of the given type back onto the stack.
     *
     * @param field the reference to the field
     */
    fun getField(field: JvmFieldRef) {
        require(field.owner.isClass) { "This type is not a class: " + field.owner }
        checkUsable()
        if (field.isInstance) {
            methodBuilder.methodVisitor.visitFieldInsn(
                Opcodes.GETFIELD,
                field.owner.internalName,
                field.name,
                field.signature.descriptor
            )
        } else {
            methodBuilder.methodVisitor.visitFieldInsn(
                Opcodes.GETSTATIC,
                field.owner.internalName,
                field.name,
                field.signature.descriptor
            )
        }
    }

    /**
     * Pops an object reference and a value from the stack,
     * and stores it in the instance field of the given type.
     *
     * @param field the reference to the field
     */
    fun putField(field: JvmFieldRef) {
        require(field.owner.isClass) { "This type is not a class: " + field.owner }
        checkUsable()
        if (field.isInstance) {
            methodBuilder.methodVisitor.visitFieldInsn(
                Opcodes.PUTFIELD,
                field.owner.internalName,
                field.name,
                field.signature.descriptor
            )
        } else {
            methodBuilder.methodVisitor.visitFieldInsn(
                Opcodes.PUTSTATIC,
                field.owner.internalName,
                field.name,
                field.signature.descriptor
            )
        }
    }
    
    //////////
    // MISC //
    //////////

    /**
     * Assert that the condition holds.
     *
     * This method accepts a builder lambda that takes a [JvmScopeBuilder] and a [JvmLabel].
     * The builder lambda should generate the necessary instructions to check the assertion's condition.
     * If the condition succeeds, the generated instructions should jump to the given label.
     * If the condition fails, the generated instructions should just continue without jumping away,
     * and this method will insert the necessary instructions to throw an [AssertionError]
     * if assertions are enabled.
     *
     * @param builder the condition builder
     */
    fun assertThat(builder: (JvmScopeBuilder, JvmLabel) -> Unit) {
        checkUsable()
        TODO()
        //        final JvmLabel endLabel = new JvmLabel();
//        getField(new JvmFieldRef(getThisType(), "$assertionsDisabled", true, new JvmFieldSignature(JvmTypes.Boolean)));
//        ifNe(endLabel);
//        builder.accept(this, endLabel);
//        newInst(JvmTypes.AssertionError);
//        dup();
//        invokeMethod(new JvmMethodRef(JvmTypes.AssertionError, null, true, new JvmMethodSignature(JvmTypes.Void)));
//        aThrow();
//        label(endLabel);
    }
}