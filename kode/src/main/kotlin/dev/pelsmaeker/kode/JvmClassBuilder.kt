package dev.pelsmaeker.kode

import dev.pelsmaeker.kode.*
import dev.pelsmaeker.kode.types.*
import dev.pelsmaeker.kode.utils.Eponymizer
import org.objectweb.asm.ClassWriter

/**
 * Builds a JVM class.
 *
 * Call [build] when done with this builder.
 */
class JvmClassBuilder internal constructor(
    /** The owning program builder. */
    val moduleBuilder: JvmModuleBuilder,
    /** The declaration of the class being built. */
    val declaration: JvmClassDecl,
    /** The class writer. */
    val classWriter: ClassWriter,
    /** The class eponymizer. */
    val eponymizer: Eponymizer,
) : AutoCloseable {

    /**
     * Creates a method.
     *
     * Call [JvmMethodBuilder.beginCode] to start adding instructions to the method's body.
     * Call [JvmMethodBuilder.build] when done with the returned builder.
     *
     * @param declaration the declaration of the method to build
     * @return a [JvmMethodBuilder]
     */
    fun createMethod(
        declaration: JvmMethodDecl
    ): JvmMethodBuilder {
        require(declaration.owner == this.declaration) {
            "Method is declared in ${declaration.owner}, but the class being built is ${this.declaration}."
        }
        val methodVisitor: org.objectweb.asm.MethodVisitor = classWriter.visitMethod(
            declaration.modifiers.value,
            declaration.name,
            declaration.signature.descriptor,
            null,  //declaration.signature.signature,   // TODO: When to add signature?
            declaration.signature.throwableTypes.map(JvmType::descriptor).toTypedArray()
        )
        return JvmMethodBuilder(this, declaration, methodVisitor, eponymizer.scope(declaration.debugName))
    }


    /**
     * Creates a method.
     *
     * Call [JvmMethodBuilder.beginCode] to start adding instructions to the method's body.
     * Call [JvmMethodBuilder.build] when done with the returned builder.
     *
     * @param name the name of the method; or `null` if it is a constructor
     * @param modifiers the modifiers of the method
     * @param signature the signature of the method
     * @return a [JvmMethodBuilder]
     */
    @JvmName("createMethod")
    fun createMethod(
        name: String?,
        modifiers: JvmMethodModifiers,
        signature: JvmMethodSignature,
    ): JvmMethodBuilder = createMethod(JvmMethodDecl(name, declaration, modifiers, signature))

    /**
     * Creates a method.
     *
     * Call [JvmMethodBuilder.beginCode] to start adding instructions to the method's body.
     * Call [JvmMethodBuilder.build] when done with the returned builder.
     *
     * @param name the name of the method; or `null` if it is a constructor
     * @param modifiers the modifiers of the method
     * @param returnType the return type of the method
     * @param parameters the parameters of the method
     * @param typeParameters the type parameters of the method
     * @param throwableTypes the checked throwable types of the method
     * @return a [JvmMethodBuilder]
     */
    @JvmName("createMethod")
    fun createMethod(
        name: String?,
        modifiers: JvmMethodModifiers,
        returnType: JvmType,
        parameters: List<JvmParam> = emptyList(),
        typeParameters: List<JvmTypeParam> = emptyList(),
        throwableTypes: List<JvmType> = emptyList(),
    ): JvmMethodBuilder = createMethod(
        name,
        modifiers,
        JvmMethodSignature(returnType, parameters, typeParameters, throwableTypes)
    )

//    /**
//     * Creates a method with the specified modifiers.
//     *
//     * Call [JvmMethodBuilder.beginCode] to start adding instructions to the method's body.
//     * Call [JvmMethodBuilder.build] when done with the returned builder.
//     *
//     * @param method the method's reference
//     * @param modifiers the method's modifiers
//     * @return a [JvmMethodBuilder]
//     */
//    @JvmName("createMethod")
//    fun createMethod(method: JvmMethodRef, modifiers: JvmMethodModifiers): JvmMethodBuilder {
//        require(modifiers.contains(JvmMethodModifiers.Static) == method.isStatic) {
//            if (method.isStatic) "Static method without 'static' modifier." else "Instance method with 'static' modifier."
//        }
//        val methodVisitor: org.objectweb.asm.MethodVisitor = classWriter.visitMethod(
//            modifiers.value,
//            method.name,
//            method.signature.descriptor,
//            null,  //method.signature.signature,   // TODO: When to add signature?
//            method.signature.throwableTypes.map(JvmType::descriptor).toTypedArray()
//        )
//        return JvmMethodBuilder(this, method, methodVisitor, eponymizer.scope(method.name))
//    }

    /**
     * Creates a bridge method, bridging from the specified signature to another signature with the same arity.
     *
     * Bridge methods are used for covariant return types, and when generic type erasure of a method's arguments
     * makes them differ from the actual method being invoked.
     *
     * @param declaration the bridge method
     * @param toMethod the bridged method
     * @return the bridge method declaration
     */
    private fun createBridgeMethod(
        declaration: JvmMethodDecl,
        toMethod: JvmMethodRef,
    ): JvmMethodDecl {
        require(declaration.owner == this.declaration) {
            "Bridge method is declared in ${declaration.owner}, but the class being built is ${this.declaration}."
        }
        require(toMethod.owner.declaration == this.declaration) {
            "Bridged method is declared in ${toMethod.owner.declaration}, but the class being built is ${this.declaration}."
        }
        require(declaration.signature.parameters.size == toMethod.signature.parameters.size) {
            "Signatures must have the same number of parameters: ${declaration.signature} -> ${toMethod.signature}"
        }
        require(JvmMethodModifiers.Bridge in declaration.modifiers) {
            "Bridge method needs the Bridge modifier."
        }
        require(JvmMethodModifiers.Synthetic in declaration.modifiers) {
            "Bridge method needs the Synthetic modifier."
        }
        // TODO: Should we make sure the bridge and bridged methods have the same modifiers modulo Bridge and Synthetic?
        createMethod(declaration).use { methodBuilder ->
            methodBuilder.beginCode().use { body ->
                // Load a reference to this instance onto the stack.
                val _this: JvmLocalVar =
                    body.localVar("this", this.declaration.ref() /* FIXME: Is this correct for parameterized types? */)
                body.aLoad(_this)

                // Load each of the incoming arguments onto the stack.
                val fromSignature: JvmMethodSignature = declaration.signature
                val toSignature: JvmMethodSignature = toMethod.signature
                val paramCount: Int = fromSignature.parameters.size
                for (i in 0 until paramCount) {
                    val fromParam: JvmParam = fromSignature.parameters[i]
                    val toParam: JvmParam = toSignature.parameters[i]
                    val localVar: JvmLocalVar = body.localVar(fromParam.name, fromParam.type)
                    body.aLoad(localVar)
                    if (localVar.type != toParam.type) {
                        // Load each of the incoming arguments onto the stack.
                        body.checkCast(toParam.type)
                    }
                }

                // Invoke the bridged method with the cast parameters and return.
                body.invokeMethod(toMethod)
                body.aReturn()
            }
            return methodBuilder.build()
        }
    }

    /**
     * Creates a static class constructor.
     *
     * Call [JvmMethodBuilder.beginCode] to start adding instructions to the method's body.
     * Call [JvmMethodBuilder.close] when done with the method.
     *
     * @return a [JvmMethodBuilder]
     */
    fun createStaticConstructor(): JvmMethodBuilder {
        // FIXME: Is it correct that a static constructor has no modifiers?
        return createMethod(
            null,
            JvmMethodModifiers.Static,
            JvmMethodSignature(JvmVoid)
        )
    }

    /**
     * Creates a default parameterless constructor that calls its parent constructor.
     *
     * @param modifiers the constructor's modifiers
     * @return the constructor declaration
     */
    fun createDefaultConstructor(modifiers: JvmMethodModifiers): JvmMethodDecl {
        return createConstructor(modifiers, emptyList()).apply {
            beginCode().apply {
                val `this`: JvmLocalVar = localVars.`this`
                aLoad(`this`)
                invokeMethod(
                    JvmMethodRef(
                        null,
                        JvmTypes.Object.ref(),
                        true,
                        JvmMethodSignature(JvmVoid)
                    )
                )
                ret()
            }
        }.build()
    }

    /**
     * Creates a constructor.
     *
     * @param modifiers the constructor's modifiers
     * @param parameters the constructor's parameters
     * @return a [JvmMethodBuilder]
     */
    fun createConstructor(modifiers: JvmMethodModifiers, parameters: List<JvmParam>): JvmMethodBuilder {
        require(JvmMethodModifiers.Static !in modifiers) {
            "Instance constructor must not have Static modifier."
        }
        return createMethod(
            null,
            modifiers,
            JvmMethodSignature(JvmVoid, parameters)
        )
    }

    /**
     * Creates a lambda.
     *
     * Call [JvmMethodBuilder.beginCode] to start adding instructions to the method's body.
     * Call [JvmMethodBuilder.close] when done with the method.
     *
     * @param nameHint the lambda's name hint
     * @param signature the lambda's signature
     * @param capturedVars the types of captured variables
     * @return a [JvmMethodBuilder]
     */
    fun createLambda(
        nameHint: String,
        signature: JvmMethodSignature,
        capturedVars: List<JvmLocalVar> = emptyList(),
    ): JvmMethodBuilder {
        return createLambda(nameHint, signature, capturedVars, emptyArray())
    }

    /**
     * Creates a lambda.
     *
     * Call [JvmMethodBuilder.beginCode] to start adding instructions to the method's body.
     * Call [JvmMethodBuilder.close] when done with the method.
     *
     * @param nameHint the lambda's name hint
     * @param signature the lambda's signature
     * @param capturedVars the types of captured variables
     * @param thrownExceptionType the type of one thrown checked exception
     * @return a [JvmMethodBuilder]
     */
    fun createLambda(
        nameHint: String,
        signature: JvmMethodSignature,
        capturedVars: List<JvmLocalVar> = emptyList(),
        thrownExceptionType: JvmType,
    ): JvmMethodBuilder {
        return createLambda(nameHint, signature, capturedVars, arrayOf(thrownExceptionType.descriptor))
    }

    /**
     * Creates a lambda.
     *
     * Call [JvmMethodBuilder.beginCode] to start adding instructions to the method's body.
     * Call [JvmMethodBuilder.close] when done with the method.
     *
     * @param nameHint the lambda's name hint
     * @param signature the lambda's signature
     * @param capturedVars the types of captured variables
     * @param thrownExceptionTypes the types of thrown checked exceptions
     * @return a [JvmMethodBuilder]
     */
    fun createLambda(
        nameHint: String,
        signature: JvmMethodSignature,
        capturedVars: List<JvmLocalVar> = emptyList(),
        thrownExceptionTypes: List<JvmType> = emptyList(),
    ): JvmMethodBuilder {
        return createLambda(
            nameHint,
            signature,
            capturedVars,
            thrownExceptionTypes.map(JvmType::descriptor).toTypedArray()
        )
    }

    /**
     * Creates a lambda.
     *
     * Call [JvmMethodBuilder.beginCode] to start adding instructions to the method's body.
     * Call [JvmMethodBuilder.close] when done with the method.
     *
     * @param nameHint the lambda's name hint
     * @param signature the lambda's signature
     * @param capturedVars the types of captured variables
     * @param thrownExceptionTypeDescriptors the descriptors of thrown checked exceptions
     * @return a [JvmMethodBuilder]
     */
    private fun createLambda(
        nameHint: String,
        signature: JvmMethodSignature,
        capturedVars: List<JvmLocalVar> = emptyList(),
        thrownExceptionTypeDescriptors: Array<String> = emptyArray(),
    ): JvmMethodBuilder {
        // TODO: Do something with the thrown exception types
        return createMethod(
            getFreshLambdaName(nameHint),
            JvmMethodModifiers.Private or JvmMethodModifiers.Static or JvmMethodModifiers.Synthetic,
            getLambdaSignature(signature, capturedVars),
        )
    }

    /**
     * Creates a field with the specified definition and value.
     *
     * Call [JvmFieldBuilder.build] (or [JvmFieldBuilder.close]) when done with the returned builder.
     *
     * @param field the field's definition
     * @param modifiers the field's modifiers
     * @return the field builder
     */
    fun createField(field: JvmFieldRef, modifiers: JvmFieldModifiers): JvmFieldBuilder {
        return createField(field, modifiers, null)
    }

    /**
     * Creates a field with the specified definition and value.
     *
     * Call [JvmFieldBuilder.build] (or [JvmFieldBuilder.close]) when done with the returned builder.
     *
     * @param field the field's definition
     * @param modifiers the field's modifiers
     * @param value the field's value, which may be `null`
     * @return a [JvmFieldBuilder]
     */
    fun createField(field: JvmFieldRef, modifiers: JvmFieldModifiers, value: Any?): JvmFieldBuilder {
        require(
            modifiers.contains(JvmFieldModifiers.Static) == field.isStatic
        ) { if (field.isStatic) "Static field without 'static' modifier." else "Instance field with 'static' modifier." }
        val fieldVisitor: org.objectweb.asm.FieldVisitor = classWriter.visitField(
            modifiers.value,
            field.name,
            field.signature.descriptor,
            null,  // TODO: When to add a signature? When it has a type argument? type.getSignature()
            value
        )
        return JvmFieldBuilder(this, field, fieldVisitor)
    }

    /**
     * Gets a fresh lambda name in this scope.
     *
     * @param nameHint the name hint
     * @return the fresh lambda name
     */
    private fun getFreshLambdaName(nameHint: String): String {
        return eponymizer.get("lambda$$nameHint$")
    }

    /**
     * Returns the signature of a lambda with the specified signature and captures variables.
     */
    private fun getLambdaSignature(
        signature: JvmMethodSignature,
        capturedVars: List<JvmLocalVar>,
    ): JvmMethodSignature {
        val parameters: MutableList<JvmParam> = ArrayList()
        for (v in capturedVars) {
            parameters.add(JvmParam(v.type, v.name))
        }
        parameters.addAll(signature.parameters)
        val returnType: JvmType = signature.returnType
        return JvmMethodSignature(returnType, parameters)
    }

    /** Whether this builder was closed. */
    private var closed = false

    @Deprecated("Prefer using build()")
    override fun close() {
        if (closed) return
        classWriter.visitEnd()
        eponymizer.close()
        closed = true
    }

    /**
     * Builds a compiled class from this class builder,
     * and closes the builder.
     *
     * @return the compiled class
     */
    fun build(): JvmCompiledClass {
        @Suppress("DEPRECATION")
        close()
        return JvmCompiledClass(declaration, classWriter.toByteArray())
    }
}