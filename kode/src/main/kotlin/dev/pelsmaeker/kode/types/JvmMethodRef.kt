package dev.pelsmaeker.kode.types

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
    /** The name of the method; or `null` when it is a static or instance constructor. */
    override val name: String?,
    /** The owning class. */
    override val owner: JvmClassRef,
    /** The type of the method's return value. */
    val returnType: JvmType,
    /** The parameters of the method. */
    val parameters: List<JvmParam> = emptyList(),
    /** Whether this is an instance member. */
    override val isInstance: Boolean = false,
) : JvmMemberRef {

    override val debugName: String get() = name ?: if (isInstance) "<init>" else "<clinit>"
    // FIXME: This is probably incorrect:
    override val internalName: String get() = if (isConstructor) "${owner.internalName}#${owner.name}(..)" else "${owner.internalName}#$name(..)"
    // FIXME: This is probably incorrect:
    override val javaName: String get() = if (isConstructor) "${owner.javaName}#${owner.name}(..)" else "${owner.javaName}#$name(..)"

    override val isStatic: Boolean get() = !isInstance
    override val isConstructor: Boolean get() = name == null
    override val isField: Boolean get() = false
    override val isMethod: Boolean get() = !isConstructor

    /** The method's JVM descriptor. */
    val descriptor: String get() = StringBuilder().apply {
        parameters.joinTo(this, prefix = "(", postfix = ")") { it.type.descriptor }
        append(returnType.descriptor)
    }.toString()

    // Destructuring declarations
    operator fun component1() = name
    operator fun component2() = owner
    operator fun component3() = returnType
    operator fun component4() = parameters
    operator fun component5() = isInstance

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || javaClass != other.javaClass) return false
        val that = other as JvmMethodRef
        // @formatter:off
        return this.owner == that.owner
            && this.name == that.name
            && this.returnType == that.returnType
            && this.parameters == that.parameters
            && this.isInstance == that.isInstance
        // @formatter:on
    }

    override fun hashCode(): Int {
        // TODO: Use manual calculation
        return Objects.hash(
            owner,
            name,
            returnType,
            parameters,
            isInstance,
        )
    }

    override fun toString(): String = StringBuilder().apply {
        append(if (isInstance) "instance " else "static ")
        append(if (isConstructor) "constructor " else "method ")
        append(owner.javaName)
        append("::")
        append(name)
        parameters.joinTo(this, prefix = "(", postfix = "): ")
        append(returnType)
    }.toString()

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
            val isInstance = !Modifier.isStatic(executable.modifiers)
            val parameters: List<JvmParam> = JvmParam.fromParameters(*executable.parameters)
            return JvmMethodRef(
                name,
                owner,
                returnType,
                parameters,
                isInstance,
            )
        }
    }
}