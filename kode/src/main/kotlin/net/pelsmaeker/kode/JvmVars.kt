package net.pelsmaeker.kode

import net.pelsmaeker.kode.types.JvmType
import net.pelsmaeker.kode.utils.Scoped

/**
 * A variables table.
 *
 * Variables are scoped. This means that variables in different sibling scopes can have the
 * same name and/or index, as long as they are unique in the context of the parents.
 */
class JvmVars(
    /** The scope of any declared variables. */
    private val scope: JvmScope,
    /** The debug name; or `null`. */
    debugName: String? = null,
    /** The parent variables. */
    parent: JvmVars? = null,
    /** Called when a variable is added. */
    private val onAdd: (JvmVar) -> Unit = {},
): Scoped<JvmVars>(debugName, parent), Iterable<JvmVar> {

    /**
     * The offset of the first variable in this list. This is 0 if this the root [JvmVars],
     * or the next offset of the parent [JvmVars].
     */
    private val baseVarOffset: Int = parent?.nextVarOffset ?: 0

    /** The offset of the next variable in this list. */
    private var nextVarOffset: Int = baseVarOffset

    /**
     * The index of the first variable in this list. This is 0 if this the root [JvmVars],
     * or the next offset of the parent [JvmVars].
     */
    private val baseVarIndex: Int = parent?.size ?: 0

    /** All variables in this list, but not those in parent lists. */
    private val vars: MutableList<JvmVar> = ArrayList()

    /** Maps named variables in this list to their zero-based index in [vars]. */
    private val varsByName: MutableMap<String?, Int> = HashMap()

    /** Whether index 0 in this list is the `this` variable. */
    private var _localHasThis: Boolean = false

    /** The number of arguments in this list. */
    private var _localArgumentCount: Int = 0

    /** The number of local variables in this list. */
    private var _localLocalVarCount: Int = 0


    /** The total number of variables in this list and parent lists. */
    val size: Int get() = vars.size + (parent?.size ?: 0)

    /** The number of 'this' variables in this list and parent lists. */
    private val thisCount: Int get() = if (hasThis()) 1 else 0

    /** The total number of arguments in this list and parent lists. */
    val argumentCount: Int get() = _localArgumentCount + (parent?.argumentCount ?: 0)

    /** The total number of local variables in this list and parent lists. */
    val localVarCount: Int get() = _localLocalVarCount + (parent?.localVarCount ?: 0)

    /**
     * Gets the variable with the specified zero-based index.
     *
     * If the method is an instance method, the variable at index 0 is the `this` reference.
     *
     * @param index the index of the variable
     * @return the variable
     * @throws IllegalArgumentException if the index is out of bounds
     */
    operator fun get(index: Int): JvmVar {
        require(index in 0 until (baseVarOffset + vars.size))
        return if (index >= baseVarOffset) {
            vars[index - baseVarOffset]
        } else {
            // We are sure this scope has a parent scope.
            parent!![index]
        }
    }

    /**
     * Gets the variable with the specified name.
     *
     * @param name the name of the variable
     * @return the variable
     * @throws IllegalArgumentException if the name is not found
     */
    operator fun get(name: String): JvmVar {
        val index = varsByName[name]
            ?: return parent?.get(name)
            ?: throw IllegalArgumentException("No variable named $name")
        return get(index)
    }

    /**
     * Determines whether there is a variable with the specified name.
     *
     * @return `true` when there is a variable with the specified name;
     * otherwise, `false`
     */
    operator fun contains(name: String): Boolean {
        return varsByName[name] != null || parent?.contains(name) == true
    }

    /**
     * Gets the variable that is the `this` reference.
     *
     * @return the variable `this` reference; or `null` if it has none
     */
    fun getThis(): JvmVar? {
        if (!hasThis()) return null
        return get(0)
    }

    /**
     * Determines whether there is a `this` variable.
     *
     * @return `true` when there is a `this` variable;
     * otherwise, `false`
     */
    fun hasThis(): Boolean {
        return _localHasThis || parent?.hasThis() == true
    }

    /**
     * Gets the argument with the specified index.
     *
     * @param index the zero-based index of the argument
     * @return the variable for the argument
     * @throws IllegalArgumentException if the index is out of bounds
     */
    fun getArgument(index: Int): JvmVar {
        require(index in 0 until argumentCount) {
            "The index $index is out of bounds among the local variables."
        }
        val realIndex = thisCount + index
        val localIndex = realIndex - baseVarIndex
        return if (localIndex < 0) parent!!.getArgument(index)
        else vars[localIndex]
    }

    /**
     * Gets the argument with the specified name.
     *
     * @param name the name of the argument
     * @return the variable for the argument; or `null` if not found
     */
    fun getArgument(name: String): JvmVar? {
        val index = varsByName[name] ?: return parent?.getArgument(name)
        return getArgument(baseVarIndex + index - thisCount)
    }

    /**
     * Determines whether there is an argument with the specified name.
     *
     * @return `true` when there is an argument with the specified name;
     * otherwise, `false`
     */
    fun hasArgument(name: String): Boolean {
        return getArgument(name) != null
    }

    /**
     * Gets the variable with the specified index.
     *
     * @param index the zero-based index of the local variable
     * @return the variable for the local variable
     * @throws IllegalArgumentException if the index is out of bounds
     */
    fun getLocalVar(index: Int): JvmVar {
        require(index in 0 until localVarCount) {
            "The index $index is out of bounds among the local variables."
        }
        val realIndex = thisCount + argumentCount + index
        val localIndex = realIndex - baseVarIndex
        return if (localIndex < 0) parent!!.getLocalVar(index)
        else vars[localIndex]
    }

    /**
     * Gets the variable with the specified name.
     *
     * @param name the name of the local variable
     * @return the variable for the local variable; or `null` if not found
     */
    fun getLocalVar(name: String): JvmVar? {
        val index = varsByName[name] ?: return parent?.getLocalVar(name)
        return getLocalVar(baseVarIndex + index - thisCount - argumentCount)
    }

    /**
     * Determines whether there is a local variable with the specified name.
     *
     * @return `true` when there is a local variable with the specified name;
     * otherwise, `false`
     */
    fun hasLocalVar(name: String): Boolean {
        return getLocalVar(name) != null
    }

    /**
     * Adds a `this` reference.
     *
     * @param type the type of the `this` reference
     * @return the added variable
     */
    fun addThis(type: JvmType): JvmVar {
        check(!hasThis()) { "A `this` reference has already been added." }
        check(size == 0) { "Other variables have already been added." }
        _localHasThis = true
        return add(type, null)
    }

    /**
     * Adds a variable for an argument.
     *
     * @param parameter the parameter
     * @return the added variable
     */
    fun addArgument(parameter: JvmParam): JvmVar {
        return addArgument(parameter.type, parameter.name)
    }

    /**
     * Adds a variable for an argument.
     *
     * @param type the type of the argument
     * @param name the name of the argument; or `null` when it has no name
     * @return the added variable
     */
    fun addArgument(type: JvmType, name: String? = null): JvmVar {
        check(localVarCount == 0) { "Local variables have already been added." }
        _localArgumentCount += 1
        return add(type, name)
    }

    /**
     * Creates and adds a local variable with the specified name and type in the specified scope.
     *
     * @param type the type of the local variable
     * @param name the name of the local variable; or `null` when it has no name
     * @return the created local variable
     */
    fun addLocalVar(type: JvmType, name: String? = null): JvmVar {
        _localLocalVarCount += 1
        return add(type, name)
    }

    /**
     * Creates and adds a variable with the specified name and type in the specified scope.
     *
     * @param type the type of the variable
     * @param name the name of the variable; or `null` when it has no name
     * @return the created variable
     */
    private fun add(type: JvmType, name: String? = null): JvmVar {
        require(!varsByName.containsKey(name)) {
            "A variable with the name '$name' already exists."
        }
        val offset = getFreshVarOffset(type)
        val localVar = JvmVar(name, type, scope, offset)
        val index = vars.size
        vars.add(localVar)
        if (name != null) varsByName[name] = index
        onAdd(localVar)
        return localVar
    }

    /**
     * Creates a new child [JvmVars] for the specified scope.
     *
     * This scope cannot be used while any child scope is not closed.
     *
     * @param scope the child scope
     * @param debugName a debug name for the [JvmVars]; or `null`
     * @return a child [JvmVars]
     */
    fun scope(scope: JvmScope, debugName: String?): JvmVars {
        return adoptChild(JvmVars(scope, debugName, this, onAdd))
    }

    /**
     * Gets a fresh variable offset.
     *
     * @param type the type of variable
     * @return the fresh variable offset
     */
    private fun getFreshVarOffset(type: JvmType): Int {
        val index = nextVarOffset
        nextVarOffset += type.kind.category
        return index
    }

    override fun iterator(): Iterator<JvmVar> {
        return vars.iterator()
    }
}