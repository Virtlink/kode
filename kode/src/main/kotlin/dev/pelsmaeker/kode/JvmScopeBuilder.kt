package dev.pelsmaeker.kode

import dev.pelsmaeker.kode.utils.Scoped
import dev.pelsmaeker.kode.types.*
import org.objectweb.asm.Type

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
): Scoped<JvmScopeBuilder>(name, parent), JvmScope {

    /** The label for the start of the scope. */
    override val startLabel: JvmLabel = JvmLabel(this.name + "_start")

    /** The label for the end of the scope. */
    override val endLabel: JvmLabel = JvmLabel(this.name + "_end")

    /** The local variables in this scope. */
    val localVars: JvmLocalVars = JvmLocalVars(methodBuilder.declaredLocalVars, this, name, parent?.localVars)

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
        return adoptChild(JvmScopeBuilder(methodBuilder, name ?: methodBuilder.toString(), this))
    }

    @Deprecated("Prefer using build()")
    override fun close() {
        if (!tryClose()) return

        // Add the end label
        methodBuilder.methodVisitor.visitLabel(endLabel.internalLabel)
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
    fun localVar(name: String?, type: JvmType): JvmLocalVar {
        checkUsable()
        return localVars.addLocalVar(type, name)
    }

    /**
     * Allocates space for a reference to `this` class.
     *
     * @return the local variable
     */
    fun localThis(): JvmLocalVar {
        return localVar(
            "this",
            methodBuilder.classBuilder.declaration.ref() /* FIXME: Is this correct when the type is parameterized? */
        )
    }

    //////////////////////////
    // LOAD LOCAL VARIABLES //
    //////////////////////////

    /**
     * Load an `int` value from a local variable on top of the stack.
     *
     * @param variable the local variable to load from
     */
    fun iLoad(variable: JvmLocalVar) {
        methodBuilder.methodVisitor.visitVarInsn(org.objectweb.asm.Opcodes.ILOAD, variable.index)
    }

    /**
     * Load a `long` from a local variable on top of the stack.
     *
     * @param variable the local variable to load from
     */
    fun lLoad(variable: JvmLocalVar) {
        methodBuilder.methodVisitor.visitVarInsn(org.objectweb.asm.Opcodes.LLOAD, variable.index)
    }

    /**
     * Load a `float` from a local variable on top of the stack.
     *
     * @param variable the local variable to load from
     */
    fun fLoad(variable: JvmLocalVar) {
        methodBuilder.methodVisitor.visitVarInsn(org.objectweb.asm.Opcodes.FLOAD, variable.index)
    }

    /**
     * Load a `double` from a local variable on top of the stack.
     *
     * @param variable the local variable to load from
     */
    fun dLoad(variable: JvmLocalVar) {
        methodBuilder.methodVisitor.visitVarInsn(org.objectweb.asm.Opcodes.DLOAD, variable.index)
    }

    /**
     * Load an object reference from a local variable on top of the stack.
     *
     * @param variable the local variable to load from
     */
    fun aLoad(variable: JvmLocalVar) {
        methodBuilder.methodVisitor.visitVarInsn(org.objectweb.asm.Opcodes.ALOAD, variable.index)
    }

    /**
     * Loads a value from a local variable on top of the stack.
     *
     * This method picks the correct instruction for the given type.
     *
     * @param variable the local variable to load from
     */
    fun load(variable: JvmLocalVar) {
        when (variable.type.kind) {
            JvmTypeKind.Long -> lLoad(variable)
            JvmTypeKind.Float -> fLoad(variable)
            JvmTypeKind.Double -> dLoad(variable)
            JvmTypeKind.Integer -> iLoad(variable)
            JvmTypeKind.Object -> aLoad(variable)
            else -> throw UnsupportedOperationException("Unsupported type: " + variable.type)
        }
    }

    ///////////////////////////
    // STORE LOCAL VARIABLES //
    ///////////////////////////

    /**
     * Store the `int` on top of the stack into a local variable.
     *
     * @param variable the local variable to store to
     */
    fun iStore(variable: JvmLocalVar) {
        methodBuilder.methodVisitor.visitVarInsn(org.objectweb.asm.Opcodes.ISTORE, variable.index)
    }

    /**
     * Store the `long` on top of the stack into a local variable.
     *
     * @param variable the local variable to store to
     */
    fun lStore(variable: JvmLocalVar) {
        methodBuilder.methodVisitor.visitVarInsn(org.objectweb.asm.Opcodes.LSTORE, variable.index)
    }

    /**
     * Store the `float` on top of the stack into a local variable.
     *
     * @param variable the local variable to store to
     */
    fun fStore(variable: JvmLocalVar) {
        methodBuilder.methodVisitor.visitVarInsn(org.objectweb.asm.Opcodes.FSTORE, variable.index)
    }

    /**
     * Store the `double` on top of the stack into a local variable.
     *
     * @param variable the local variable to store to
     */
    fun dStore(variable: JvmLocalVar) {
        methodBuilder.methodVisitor.visitVarInsn(org.objectweb.asm.Opcodes.DSTORE, variable.index)
    }

    /**
     * Store the object reference on top of the stack into a local variable.
     *
     * @param variable the local variable to store to
     */
    fun aStore(variable: JvmLocalVar) {
        methodBuilder.methodVisitor.visitVarInsn(org.objectweb.asm.Opcodes.ASTORE, variable.index)
    }

    /**
     * Store the value on top of the stack into a local variable.
     *
     * This method picks the correct instruction for the given type.
     *
     * @param variable the local variable to store to
     */
    fun store(variable: JvmLocalVar) {
        when (variable.type.kind) {
            JvmTypeKind.Long -> lStore(variable)
            JvmTypeKind.Float -> fStore(variable)
            JvmTypeKind.Double -> dStore(variable)
            JvmTypeKind.Integer -> iStore(variable)
            JvmTypeKind.Object -> aStore(variable)
            else -> throw UnsupportedOperationException("Unsupported type: " + variable.type)
        }
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
    fun iInc(variable: JvmLocalVar, value: Byte) {
        TODO()
    }

    ///////////
    // STACK //
    ///////////

    /** Pop the top value from the stack. */
    fun pop() {
        TODO()
    }

    /** Pop the top two values from the stack. */
    fun pop2() {
        TODO()
    }

    /** Swap the top two values on the stack. */
    fun swap() {
        TODO()
    }

    /** Duplicate the top value on the stack. */
    fun dup() {
        methodBuilder.methodVisitor.visitInsn(org.objectweb.asm.Opcodes.DUP)
    }

    /** Duplicate the top value up to two values below the top of the stack. */
    fun dup_x1() {
        TODO()
    }

    /** Duplicate the top value up to three values below the top of the stack. */
    fun dup_x2() {
        TODO()
    }

    /** Duplicate the top two values on the stack. */
    fun dup2() {
        TODO()
    }

    /** Duplicate the up to two top values up to three values below the top of the stack. */
    fun dup2_x1() {
        TODO()
    }

    /** Duplicate the up to two top values up to four values below the top of the stack. */
    fun dup2_x2() {
        TODO()
    }

    ///////////////
    // CONSTANTS //
    ///////////////

    /** Push constant integer -1 on the stack. */
    fun iConst_m1() {
        methodBuilder.methodVisitor.visitInsn(org.objectweb.asm.Opcodes.ICONST_M1)
    }

    /** Push constant integer 0 on the stack. */
    fun iConst_0() {
        methodBuilder.methodVisitor.visitInsn(org.objectweb.asm.Opcodes.ICONST_0)
    }

    /** Push constant integer 1 on the stack. */
    fun iConst_1() {
        methodBuilder.methodVisitor.visitInsn(org.objectweb.asm.Opcodes.ICONST_1)
    }

    /** Push constant integer 2 on the stack. */
    fun iConst_2() {
        methodBuilder.methodVisitor.visitInsn(org.objectweb.asm.Opcodes.ICONST_2)
    }

    /** Push constant integer 3 on the stack. */
    fun iConst_3() {
        methodBuilder.methodVisitor.visitInsn(org.objectweb.asm.Opcodes.ICONST_3)
    }

    /** Push constant integer 4 on the stack. */
    fun iConst_4() {
        methodBuilder.methodVisitor.visitInsn(org.objectweb.asm.Opcodes.ICONST_4)
    }

    /** Push constant integer 5 on the stack. */
    fun iConst_5() {
        methodBuilder.methodVisitor.visitInsn(org.objectweb.asm.Opcodes.ICONST_5)
    }

    /** Push constant long integer 0 on the stack. */
    fun lConst_0() {
        methodBuilder.methodVisitor.visitInsn(org.objectweb.asm.Opcodes.LCONST_0)
    }

    /** Push constant long integer 1 on the stack. */
    fun lConst_1() {
        methodBuilder.methodVisitor.visitInsn(org.objectweb.asm.Opcodes.LCONST_1)
    }

    /** Push constant float 0 on the stack. */
    fun fConst_0() {
        methodBuilder.methodVisitor.visitInsn(org.objectweb.asm.Opcodes.FCONST_0)
    }

    /** Push constant float 1 on the stack. */
    fun fConst_1() {
        methodBuilder.methodVisitor.visitInsn(org.objectweb.asm.Opcodes.FCONST_1)
    }

    /** Push constant float 2 on the stack. */
    fun fConst_2() {
        methodBuilder.methodVisitor.visitInsn(org.objectweb.asm.Opcodes.FCONST_2)
    }

    /** Push constant double 0 on the stack. */
    fun dConst_0() {
        methodBuilder.methodVisitor.visitInsn(org.objectweb.asm.Opcodes.DCONST_0)
    }

    /** Push constant double 1 on the stack. */
    fun dConst_1() {
        methodBuilder.methodVisitor.visitInsn(org.objectweb.asm.Opcodes.DCONST_1)
    }

    /** Push constant null on the stack. */
    fun aConst_Null() {
        methodBuilder.methodVisitor.visitInsn(org.objectweb.asm.Opcodes.ACONST_NULL)
    }

    /**
     * Push a constant `int` on the stack.
     *
     * This method picks the most efficient instruction for the given constant.
     *
     * @param value the constant `int` value
     */
    fun iConst(value: Int) {
        when (value) {
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
    }

    /**
     * Push a constant `long` on the stack.
     *
     * This method picks the most efficient instruction for the given constant.
     *
     * @param value the constant `long` value
     */
    fun lConst(value: Long) {
        when (value) {
            0L -> lConst_0()
            1L -> lConst_1()
            else -> ldc(java.lang.Long.valueOf(value))
        }
    }

    /**
     * Push a constant `float` on the stack.
     *
     * This method picks the most efficient instruction for the given constant.
     *
     * @param value the constant `float` value
     */
    fun fConst(value: Float) {
        when (value) {
            0.0f -> fConst_0()
            1.0f -> fConst_1()
            2.0f -> fConst_2()
            else -> ldc(java.lang.Float.valueOf(value))
        }
    }

    /**
     * Push a constant `double` on the stack.
     *
     * This method picks the most efficient instruction for the given constant.
     *
     * @param value the constant `double` value
     */
    fun dConst(value: Float) {
        when {
            value.toDouble() == 0.0 -> dConst_0()
            value.toDouble() == 1.0 -> dConst_1()
            else -> ldc(java.lang.Double.valueOf(value.toDouble()))
        }
    }

    /**
     * Push a constant byte value on the stack.
     *
     * @param value the constant byte value to push
     */
    fun biPush(value: Byte) {
        methodBuilder.methodVisitor.visitIntInsn(org.objectweb.asm.Opcodes.BIPUSH, value.toInt())
    }

    /**
     * Push a constant short value on the stack.
     *
     * @param value the constant short value to push
     */
    fun siPush(value: Short) {
        methodBuilder.methodVisitor.visitIntInsn(org.objectweb.asm.Opcodes.SIPUSH, value.toInt())
    }

    /**
     * Push a loaded constant on the stack.
     *
     * @param value the constant value to push
     */
    fun ldc(value: Any) {
        val a: Any = if (value is JvmType) Type.getType(value.descriptor) else value
        methodBuilder.methodVisitor.visitLdcInsn(a)
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
     * This method is named [.newInst] because `new` is a reserved Java keyword.
     *
     * @param type the type to create
     */
    fun newInst(type: JvmClassRef) {
        methodBuilder.methodVisitor.visitTypeInsn(org.objectweb.asm.Opcodes.NEW, type.internalName)
    }

    ////////////
    // ARRAYS //
    ////////////


    ///////////
    // JUMPS //
    ///////////

    /**
     * Pop the top stack value and goto label if greater than 0.
     *
     * @param label the label to jump to if the condition holds
     */
    fun ifGt(label: JvmLabel) {
        methodBuilder.methodVisitor.visitJumpInsn(org.objectweb.asm.Opcodes.IFGT, label.internalLabel)
    }

    /**
     * Pop the top stack value and goto label if greater than or equal to 0.
     *
     * @param label the label to jump to if the condition holds
     */
    fun ifGe(label: JvmLabel) {
        methodBuilder.methodVisitor.visitJumpInsn(org.objectweb.asm.Opcodes.IFGE, label.internalLabel)
    }

    /**
     * Pop the top stack value and goto label if equal to 0.
     *
     * @param label the label to jump to if the condition holds
     */
    fun ifEq(label: JvmLabel) {
        methodBuilder.methodVisitor.visitJumpInsn(org.objectweb.asm.Opcodes.IFEQ, label.internalLabel)
    }

    /**
     * Pop the top stack value and goto label if not equal to 0.
     *
     * @param label the label to jump to if the condition holds
     */
    fun ifNe(label: JvmLabel) {
        methodBuilder.methodVisitor.visitJumpInsn(org.objectweb.asm.Opcodes.IFNE, label.internalLabel)
    }

    /**
     * Pop the top stack value and goto label if less than or equal to 0.
     *
     * @param label the label to jump to if the condition holds
     */
    fun ifLe(label: JvmLabel) {
        methodBuilder.methodVisitor.visitJumpInsn(org.objectweb.asm.Opcodes.IFLE, label.internalLabel)
    }

    /**
     * Pop the top stack value and goto label if less than 0.
     *
     * @param label the label to jump to if the condition holds
     */
    fun ifLt(label: JvmLabel) {
        methodBuilder.methodVisitor.visitJumpInsn(org.objectweb.asm.Opcodes.IFLT, label.internalLabel)
    }

    /**
     * Pop the top stack value and goto label if `null`.
     *
     * @param label the label to jump to if the condition holds
     */
    fun ifNull(label: JvmLabel) {
        methodBuilder.methodVisitor.visitJumpInsn(org.objectweb.asm.Opcodes.IFNULL, label.internalLabel)
    }

    /**
     * Pop the top stack value and goto label if not `null`.
     *
     * @param label the label to jump to if the condition holds
     */
    fun ifNonNull(label: JvmLabel) {
        methodBuilder.methodVisitor.visitJumpInsn(org.objectweb.asm.Opcodes.IFNONNULL, label.internalLabel)
    }

    /**
     * Goto label unconditionally.
     *
     * This method is named [.jump] because `goto` is a reserved Java keyword.
     *
     * @param label the label to jump to
     */
    fun jump(label: JvmLabel) {
        methodBuilder.methodVisitor.visitJumpInsn(org.objectweb.asm.Opcodes.GOTO, label.internalLabel)
    }

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
    fun aThrow() {
        methodBuilder.methodVisitor.visitInsn(org.objectweb.asm.Opcodes.ATHROW)
    }

    /** Pop an `int` from the stack and return it. */
    fun iReturn() {
        methodBuilder.methodVisitor.visitInsn(org.objectweb.asm.Opcodes.IRETURN)
    }

    /** Pop a `long` from the stack and return it. */
    fun lReturn() {
        methodBuilder.methodVisitor.visitInsn(org.objectweb.asm.Opcodes.LRETURN)
    }

    /** Pop a `float` from the stack and return it. */
    fun fReturn() {
        methodBuilder.methodVisitor.visitInsn(org.objectweb.asm.Opcodes.FRETURN)
    }

    /** Pop a `double` from the stack and return it. */
    fun dReturn() {
        methodBuilder.methodVisitor.visitInsn(org.objectweb.asm.Opcodes.DRETURN)
    }

    /** Pop an object reference from the stack and return it. */
    fun aReturn() {
        methodBuilder.methodVisitor.visitInsn(org.objectweb.asm.Opcodes.ARETURN)
    }

    /**
     * Return from a `void` method.
     *
     * This method is named [.ret] because `return` is a reserved Java keyword.
     */
    fun ret() {
        methodBuilder.methodVisitor.visitInsn(org.objectweb.asm.Opcodes.RETURN)
    }

    /**
     * Pop a value from the stack and return it.
     *
     * This method is named [.ret] because `return` is a reserved Java keyword.
     * This method picks the correct instruction for the given type.
     *
     * Note that if the return type is the primitive `void`, this method will generate a
     * `return` instruction that takes no value from the stack. However, if the return type is
     * the [java.lang.Void] object, this method will generate an `aReturn` instruction that
     * takes an object of type [java.lang.Void] from the stack (which can only ever be `null`).
     *
     * @param type the type of value to return
     */
    fun ret(type: JvmType) {
        when (type.sort) {
            JvmTypeSort.Void -> ret()
            JvmTypeSort.Long -> lReturn()
            JvmTypeSort.Float -> fReturn()
            JvmTypeSort.Double -> dReturn()
            JvmTypeSort.Character, JvmTypeSort.Boolean, JvmTypeSort.Byte, JvmTypeSort.Short, JvmTypeSort.Integer -> iReturn()
            JvmTypeSort.TypeParam, JvmTypeSort.Array, JvmTypeSort.Class -> aReturn()
            else -> throw UnsupportedOperationException("Unsupported return value: $type")
        }
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
        invokeMethod(JvmMethodRef(null, owner, true, signature))
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
        if (method.isConstructor) {
            // Constructor
            check(!method.owner.isInterface) { "Cannot invoke a constructor on an interface: $method" }
            if (method.isStatic) {
                // Static constructor
                TODO()
            } else {
                // Instance constructor
                methodBuilder.methodVisitor.visitMethodInsn(
                    org.objectweb.asm.Opcodes.INVOKESPECIAL,
                    method.owner.internalName,
                    "<init>",
                    method.signature.descriptor,
                    false
                )
            }
        } else if (method.isStatic) {
            // Static method
            methodBuilder.methodVisitor.visitMethodInsn(
                org.objectweb.asm.Opcodes.INVOKESTATIC,
                method.owner.internalName,
                method.name,
                method.signature.descriptor,
                method.owner.isInterface
            )
        } else if (method.owner.isInterface) {
            // Instance method on an interface
            methodBuilder.methodVisitor.visitMethodInsn(
                org.objectweb.asm.Opcodes.INVOKEINTERFACE,
                method.owner.internalName,
                method.name,
                method.signature.descriptor,
                true
            )
        } else {
            // Instance method on a class
            methodBuilder.methodVisitor.visitMethodInsn(
                org.objectweb.asm.Opcodes.INVOKEVIRTUAL,
                method.owner.internalName,
                method.name,
                method.signature.descriptor,
                false
            )
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
        if (field.isInstance) {
            methodBuilder.methodVisitor.visitFieldInsn(
                org.objectweb.asm.Opcodes.GETFIELD,
                field.owner.internalName,
                field.name,
                field.signature.descriptor
            )
        } else {
            methodBuilder.methodVisitor.visitFieldInsn(
                org.objectweb.asm.Opcodes.GETSTATIC,
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
        if (field.isInstance) {
            methodBuilder.methodVisitor.visitFieldInsn(
                org.objectweb.asm.Opcodes.PUTFIELD,
                field.owner.internalName,
                field.name,
                field.signature.descriptor
            )
        } else {
            methodBuilder.methodVisitor.visitFieldInsn(
                org.objectweb.asm.Opcodes.PUTSTATIC,
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