package dev.pelsmaeker.kode


import dev.pelsmaeker.kode.types.JvmClassDecl
import dev.pelsmaeker.kode.types.JvmRef
import dev.pelsmaeker.kode.utils.Eponymizer
import org.objectweb.asm.ClassWriter
import java.util.regex.Pattern


/**
 * Builds a JVM module.
 *
 * Call [build] when done with this builder.
 */
class JvmModuleBuilder(
    /** The class version to use. */
    private val classVersion: JvmClassVersion = JvmClassVersion.Java8,
    /** The module eponymizer. */
    val eponymizer: Eponymizer = Eponymizer(),
    /** Whether to compute the maxs. */
    private val computeMaxs: Boolean = true,
    /** Whether to compute the frames. */
    private val computeFrames: Boolean = true,
): AutoCloseable {

    /**
     * Build a class or interface.
     *
     * Call [JvmClassBuilder.build] when done with the builder.
     *
     * @param declaration the class declaration
     * @param modifiers the class modifiers
     * @param signature the class signature
     * @return the class builder
     */
    fun createClass(
        declaration: JvmClassDecl,
        modifiers: JvmClassModifiers,
        signature: JvmClassSignature = JvmClassSignature(),
    ): JvmClassBuilder {
        var classFlags = 0
        if (computeMaxs) classFlags = classFlags or ClassWriter.COMPUTE_MAXS
        if (computeFrames) classFlags = classFlags or ClassWriter.COMPUTE_FRAMES
        val superClassInternalName = signature.superClass.internalName
        val superInterfacesInternalNames = signature.superInterfaces.map(JvmRef::internalName).toTypedArray()
        val classWriter = ClassWriter(classFlags)
        classWriter.visit(
            classVersion.value,
            modifiers.value,
            declaration.internalName,
            signature.getSignature(declaration.typeParameters),
            superClassInternalName,
            superInterfacesInternalNames
        )
        return JvmClassBuilder(
            this,
            declaration,
            classWriter,
            eponymizer.scope(declaration.toString())
        )
    }

    // For Java
    @JvmName("createClass")
    fun createClass(
        declaration: JvmClassDecl,
        modifiers: JvmClassModifiers,
    ): JvmClassBuilder = createClass(declaration, modifiers, JvmClassSignature())


    /** Whether this builder was closed. */
    private var closed = false

    @Deprecated("Prefer using build()")
    override fun close() {
        if (closed) return
        closed = true
    }

    /**
     * Builds Unit from this program builder,
     * and closes the builder.
     *
     * @return Unit
     */
    fun build(): Unit {
        @Suppress("DEPRECATION")
        close()
    }
}