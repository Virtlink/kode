package dev.pelsmaeker.kode.types


import dev.pelsmaeker.kode.JvmMethodModifiers
import dev.pelsmaeker.kode.JvmParam
import java.lang.reflect.Constructor
import java.lang.reflect.Executable
import java.lang.reflect.Method
import java.lang.reflect.Modifier
import java.util.*


/**
 * A JVM method reference.
 *
 * Use [ref] to create an instance of this class from a [Method] or [Constructor].
 */
class JvmMethodRef(
    /** The method declaration. */
    private val declaration: JvmMethodDecl,
    /** The owning class, if any; otherwise, `null`. */
    override val owner: JvmClassRef,
) : JvmMemberRef {

    init {
        require(owner.declaration == declaration.owner) {
            "The method is declared in ${declaration.owner}, but the owner is given as ${owner.declaration}."
        }
    }

    override val name: String? get() = declaration.name
    override val debugName: String get() = declaration.debugName
    // FIXME: This is probably incorrect:
    override val internalName: String get() = if (isConstructor) "${owner.internalName}#${owner.name}(..)" else "${owner.internalName}#$name(..)"
    // FIXME: This is probably incorrect:
    override val javaName: String get() = if (isConstructor) "${owner.javaName}#${owner.name}(..)" else "${owner.javaName}#$name(..)"

    override val isInstance: Boolean get() = declaration.isInstance
    override val isStatic: Boolean get() = declaration.isStatic
    override val isConstructor: Boolean get() = declaration.isConstructor
    override val isField: Boolean get() = false
    override val isMethod: Boolean get() = !declaration.isConstructor

    /** The type of the method's return value. */
    val returnType: JvmType get() = signature.returnType
    /** The parameters of the method. */
    val parameters: List<JvmParam> get() = signature.parameters
    /** The types of the method's type parameters. */
    val typeParameters: List<JvmTypeParam> get() = signature.typeParameters
    /** The types of the method's checked throwables. */
    val throwableTypes: List<JvmType> get() = signature.throwableTypes

    /** The method's signature. */
    val signature: JvmMethodSignature get() = declaration.signature

    // Destructuring declarations
    operator fun component1() = name
    operator fun component2() = owner
    operator fun component3() = signature
    operator fun component4() = isInstance

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || javaClass != other.javaClass) return false
        val that = other as JvmMethodRef
        // @formatter:off
        return this.owner == that.owner
            && this.name == that.name
            && this.isInstance == that.isInstance
            && this.signature == that.signature
        // @formatter:on
    }

    override fun hashCode(): Int {
        // TODO: Use manual calculation
        return Objects.hash(
            owner,
            name,
            isInstance,
            signature
        )
    }

    override fun toString(): String {
        return "method: ${owner.javaName}${if (isInstance) ".this" else ""}::$name$signature"
    }

    companion object {
        /**
         * Gets the JVM reference to the given Java Reflection method.
         * @return the JVM method reference
         */
        fun Method.ref(): JvmMethodRef {
            val name = this.name
            val returnType = JvmType.of(this.returnType)
            return of(name, returnType, this)
        }

        /**
         * Gets the JVM reference to the given Java Reflection constructor.
         * @return the JVM constructor reference
         */
        fun Constructor<*>.ref(): JvmMethodRef {
            return of(null, JvmVoid, this)
        }

        /**
         * Gets the JVM reference of the specified Java Reflection executable.
         *
         * @param name the name of the method; or `null` if it is a constructor
         * @param returnType the return type of the method
         * @param executable the Java executable (method/constructor)
         * @return the method reference
         */
        private fun of(name: String?, returnType: JvmType, executable: Executable): JvmMethodRef {
            val owner = JvmClassDecl.of(executable.declaringClass).ref() // FIXME: This is not correct when there are type arguments.
            val modifiers = JvmMethodModifiers(executable.modifiers)
            val parameters: List<JvmParam> = JvmParam.fromParameters(*executable.parameters)
            val typeParameters: List<JvmTypeParam> = executable.typeParameters.map(JvmTypeParam.Companion::of)
            val throwables = executable.exceptionTypes.map(JvmType::of)
            val signature = JvmMethodSignature(returnType, parameters, typeParameters, throwables)
            val declaration = JvmMethodDecl(name, owner.declaration, signature, modifiers)
            return JvmMethodRef(declaration, owner)
        }
    }
}