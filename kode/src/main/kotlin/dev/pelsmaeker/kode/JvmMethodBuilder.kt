package dev.pelsmaeker.kode


import dev.pelsmaeker.kode.types.JvmMethodDecl
import dev.pelsmaeker.kode.types.JvmMethodRef
import dev.pelsmaeker.kode.utils.Eponymizer
import org.objectweb.asm.MethodVisitor


/**
 * Builds a JVM method.
 *
 * Call [beginCode] to start adding instructions to the method's body.
 * Call [build] when done with this builder.
 */
class JvmMethodBuilder internal constructor(
    /** The owning class builder. */
    val classBuilder: JvmClassBuilder,
    /** The declaration of the method being built. */
    val declaration: JvmMethodDecl,
//    /** The reference to the method being built. */
//    val reference: JvmMethodRef,
    /** The method visitor. */
    internal val methodVisitor: MethodVisitor,
    /** The method eponymizer. */
    val eponymizer: Eponymizer,
): AutoCloseable {

    /** A list of local variables declared anywhere in the method's body. */
    internal val declaredLocalVars: List<JvmLocalVar> = ArrayList()

    /** The scope for the method's body instructions; or `null` when not yet set. */
    private var bodyScope: JvmScopeBuilder? = null

    /** Whether this builder was closed. */
    private var closed = false

    /**
     * Start adding instructions to the method's body.
     *
     * Call [JvmScopeBuilder.build] (or [JvmScopeBuilder.close]) when done with the returned builder.
     *
     * @return the scope builder for this method
     */
    fun beginCode(): JvmScopeBuilder {
        checkUsable()
        methodVisitor.visitCode()
        val jvmScopeBuilder = JvmScopeBuilder(this, eponymizer = eponymizer.scope("(body)"))
        bodyScope = jvmScopeBuilder
        initializeLocalVars(jvmScopeBuilder.localVars)
        return jvmScopeBuilder
    }

    /**
     * Initializes the local variables for this method's body by adding the 'this' reference (if any),
     * and the method's arguments.
     *
     * @param localVars the local variables to initialize
     */
    private fun initializeLocalVars(localVars: JvmLocalVars) {
        // Add `this` reference.
        if (declaration.isInstance) {
            localVars.addThis(classBuilder.declaration.ref() /* FIXME: Not sure this is correct when the type is parameterized. */)
        }
        // Add the arguments in the order they are defined.
        for (parameter in declaration.signature.parameters) {
            localVars.addArgument(parameter)
        }
    }

    @Deprecated("Prefer using build()") // Prefer using build()
    override fun close() {
        if (closed) return
        checkUsable()
        closed = true

        // Add the local variables that have a name
        for (localVar in declaredLocalVars) {
            if (localVar.name == null) continue
            methodVisitor.visitLocalVariable(
                localVar.name,
                localVar.type.descriptor,
                null,  // TODO: When to add a signature? When it has a type argument? localVar.getType().getSignature()
                localVar.scope.startLabel.internalLabel,
                localVar.scope.endLabel.internalLabel,
                localVar.offset
            )
        }

        // Add the method's frame information. (Incorrect, but will be fixed by ASM library.)
        methodVisitor.visitMaxs(0, 0)
        methodVisitor.visitEnd()
        eponymizer.close()
    }

    /**
     * Builds a method from this method builder,
     * and closes the builder.
     *
     * @return the declaration of the built method
     */
    fun build(): JvmMethodDecl {
        @Suppress("DEPRECATION")
        close()
        return declaration
    }

    override fun toString(): String =
        "${declaration.owner}::${declaration.debugName}"

    /**
     * Checks that the object is usable.
     */
    private fun checkUsable() {
        checkNotClosed()
        check(bodyScope?.isClosed ?: true) {
            "The builder cannot be used, because it has an open child scope: $bodyScope"
        }
    }

    /**
     * Asserts that the scope was not closed.
     *
     * @throws IllegalStateException if the scope was closed
     */
    private fun checkNotClosed() {
        check(!closed) { "The builder was closed." }
    }
}