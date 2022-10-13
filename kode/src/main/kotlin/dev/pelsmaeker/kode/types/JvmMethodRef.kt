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
    /** The name of the method; or `null` when it is a static or instance constructor. */
    name: String?,
    /** The owner of the method. */
    override val owner: JvmClassRef,
    /** Whether this is an instance method, which have an implicit first argument `this` with the type of the declaring class. */
    override val isInstance: Boolean,
    /** The method's signature. */
    val signature: JvmMethodSignature,
) : JvmMemberRef {

    private val _name: String? = name
    override val name: String get() = _name ?: if (isInstance) "<init>" else "<clinit>"
    override val internalName: String get() = "${owner.internalName}#$name(..)"
    override val javaName: String get() = "${owner.javaName}#$name(..)"
    override val isStatic: Boolean get() = !isInstance
    override val isConstructor: Boolean get() = _name == null
    override val isField: Boolean get() = false
    override val isMethod: Boolean get() = _name != null

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || javaClass != other.javaClass) return false
        val that = other as JvmMethodRef
        // @formatter:off
        return this.owner == that.owner
            && this._name == that._name
            && this.isInstance == that.isInstance
            && this.signature == that.signature
        // @formatter:on
    }

    override fun hashCode(): Int {
        return Objects.hash(
            owner,
            _name,
            isInstance,
            signature
        )
    }

    override fun toString(): String {
        return "method: ${owner.javaName}${if (isInstance) ".this" else ""}::$name$signature"
    }

    companion object {
        /**
         * Gets the JVM reference of the specified method.
         *
         * @param method the method
         * @return the method reference
         */
        fun of(method: Method): JvmMethodRef {
            val name = method.name
            val returnType = JvmType.of(method.returnType)
            return of(name, returnType, method)
        }

        /**
         * Gets the JVM reference of the specified constructor.
         *
         * @param constructor the constructor
         * @return the constructor reference
         */
        fun of(constructor: Constructor<*>): JvmMethodRef {
            return of(null, JvmVoid, constructor)
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