package dev.pelsmaeker.kode.types


import dev.pelsmaeker.kode.JvmParam
import java.lang.reflect.Constructor
import java.lang.reflect.Executable
import java.lang.reflect.Method
import java.lang.reflect.Modifier
import java.util.*


/**
 * A JVM method reference.
 */
// TODO: Try to make this a `data class` and remove the equals and hashcode overrides
class JvmMethodRef(
    // TODO: Back with JvmMethodDecl
    /** The name of the method; or `null` when it is a static or instance constructor. */
    override val name: String?,
    /** The owner of the method. */
    override val owner: JvmClassRef,
    /** Whether this is an instance method, which have an implicit first argument `this` with the type of the declaring class. */
    override val isInstance: Boolean,
    /** The method's signature. */
    val signature: JvmMethodSignature,
) : JvmMemberRef {

    override val debugName: String get() = name ?: if (isInstance) "<init>" else "<clinit>"
    override val internalName: String get() = if (isConstructor) "${owner.internalName}#${owner.name}(..)" else "${owner.internalName}#$name(..)"
    override val javaName: String get() = if (isConstructor) "${owner.javaName}#${owner.name}(..)" else "${owner.javaName}#$name(..)"
    override val isStatic: Boolean get() = !isInstance
    override val isConstructor: Boolean get() = name == null
    override val isField: Boolean get() = false
    override val isMethod: Boolean get() = name != null

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
         * Gets the JVM reference of the specified method.
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
            val typeParameters: List<JvmTypeParam> = executable.typeParameters.map(JvmTypeParam.Companion::of)
            val throwables = executable.exceptionTypes.map(JvmType::of)
            val signature = JvmMethodSignature(returnType, parameters, typeParameters, throwables)
            return JvmMethodRef(name, owner, isInstance, signature)
        }
    }
}