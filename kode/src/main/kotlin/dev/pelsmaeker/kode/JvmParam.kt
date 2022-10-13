package dev.pelsmaeker.kode

import dev.pelsmaeker.kode.types.JvmType
import java.lang.reflect.Modifier
import java.lang.reflect.Parameter
import java.util.*

/**
 * A method's parameter.
 *
 * This parameter is a *varargs* parameter if the declaring method has the [JvmMethodModifiers.Varargs] modifier
 * and this is the last parameter.
 */
data class JvmParam(
    /** The type of the parameter. */
    val type: JvmType,
    /** The name of the parameter; or `null` if not specified. */
    val name: String? = null,
    /** Modifiers for the parameter. */
    val modifiers: JvmParamModifiers = JvmParamModifiers.None,
) {
    /** Whether this parameter is implicitly declared (i.e., mandated). */
    val isImplicit: Boolean get() = modifiers.contains(JvmParamModifiers.Mandated)

    /** Whether this parameter is synthetic (i.e., neither implicitly nor explicitly declared). */
    val isSynthetic: Boolean get() = modifiers.contains(JvmParamModifiers.Synthetic)

    override fun toString(): String {
        return if (name != null) {
            "$name: $type"
        } else {
            type.toString()
        }
    }

    companion object {
        /**
         * Creates a method parameter from the given Java reflection [Parameter].
         *
         * @param parameter the Java reflection [Parameter]
         * @return the resulting [JvmParam]
         */
        fun fromParameter(parameter: Parameter): JvmParam {
            val type = JvmType.of(parameter.type)
            val name = if (parameter.isNamePresent) parameter.name else null
            var mods = JvmParamModifiers.None
            if (parameter.isImplicit) mods = mods or JvmParamModifiers.Mandated
            if (parameter.isSynthetic) mods = mods or JvmParamModifiers.Synthetic
            if (Modifier.isFinal(parameter.modifiers)) mods = mods or JvmParamModifiers.Final
            return JvmParam(type, name, mods)
        }

        /**
         * Creates a method parameter from the given local variable.
         *
         * @param localVar the local variable
         * @param modifiers the modifiers on the parameter
         * @return the resulting [JvmParam]
         */
        fun fromLocalVar(localVar: JvmLocalVar, modifiers: JvmParamModifiers = JvmParamModifiers.None): JvmParam {
            return JvmParam(localVar.type, localVar.name, modifiers)
        }

        /**
         * Creates a list of method parameters from the given Java reflection [Parameter] objects.
         *
         * @param parameters the Java reflection [Parameter] objects
         * @return the resulting list of [JvmParam]
         */
        fun fromParameters(vararg parameters: Parameter): List<JvmParam> {
            return fromParameters(parameters.toList())
        }

        /**
         * Creates a list of method parameters from the given Java reflection [Parameter] objects.
         *
         * @param parameters the Java reflection [Parameter] objects
         * @return the resulting list of [JvmParam]
         */
        fun fromParameters(parameters: Iterable<Parameter>): List<JvmParam> {
            return parameters.map { fromParameter(it) }
        }

        /**
         * Creates a list of method parameters from the given local variables.
         *
         * @param localVars the local variables
         * @return the resulting list of [JvmParam]
         */
        fun fromLocalVars(vararg localVars: JvmLocalVar): List<JvmParam> {
            return fromLocalVars(localVars.toList())
        }

        /**
         * Creates a list of method parameters from the given local variables.
         *
         * @param modifiers the modifiers on the parameters
         * @param localVars the local variables
         * @return the resulting list of [JvmParam]
         */
        fun fromLocalVars(modifiers: JvmParamModifiers, vararg localVars: JvmLocalVar): List<JvmParam> {
            return fromLocalVars(modifiers, localVars.toList())
        }

        /**
         * Creates a list of method parameters from the given local variables.
         *
         * @param localVars the local variables
         * @return the resulting list of [JvmParam]
         */
        fun fromLocalVars(localVars: Iterable<JvmLocalVar>): List<JvmParam> {
            return fromLocalVars(JvmParamModifiers.None, localVars)
        }

        /**
         * Creates a list of method parameters from the given local variables.
         *
         * @param modifiers the modifiers on the parameters
         * @param localVars the local variables
         * @return the resulting list of [JvmParam]
         */
        fun fromLocalVars(modifiers: JvmParamModifiers = JvmParamModifiers.None, localVars: Iterable<JvmLocalVar>): List<JvmParam> {
            return localVars.map { fromLocalVar(it) }
        }
    }
}