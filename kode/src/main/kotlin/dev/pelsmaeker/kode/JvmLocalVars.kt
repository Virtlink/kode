package dev.pelsmaeker.kode


import dev.pelsmaeker.kode.types.JvmType
import dev.pelsmaeker.kode.utils.Scoped


/**
 * A local variables table.
 *
 * Local variables are scoped. This means that local variables in different sibling scopes can have the
 * same name and/or index, as long as they are unique in the context of the parents.
 */
class JvmLocalVars(
    /** The list of local variables declared in the method. */
    declaredLocalVars: List<JvmLocalVar>,
    /** The scope of any declared local variables. */
    private val scope: JvmScope,
    /** The debug name; or `null`. */
    name: String? = null,
    /** The parent local variables. */
    parent: JvmLocalVars? = null,
): Scoped<JvmLocalVars>(name, parent), Iterable<JvmLocalVar> {

    /** The list of local variables declared in the method. */
    private val declaredLocalVars: MutableList<JvmLocalVar> = declaredLocalVars.toMutableList()

    /**
     * The index of the first local variable in this list. This is 0 if this the root [JvmLocalVars],
     * or the next index of the parent [JvmLocalVars].
     */
    private val baseLocalVarIndex: Int = parent?.nextLocalVarIndex ?: 0

    /** The index of the next local variable in this list. */
    private var nextLocalVarIndex: Int = baseLocalVarIndex

    /** The local variables in this list. */
    private val localVars: MutableList<JvmLocalVar> = ArrayList()

    /** The named local variables in this list. */
    private val localVarsByName: MutableMap<String?, Int> = HashMap()

    /** The number of arguments in the list. */
    private var _argumentCount: Int = parent?.argumentCount ?: 0

    /** Whether index 0 is the `this` argument. */
    var hasThis: Boolean = parent?.hasThis ?: false
        private set

    /**
     * Gets the number of local variables in scope.
     *
     * @return the number of local variables in scope
     */
    val size: Int get() = localVars.size + (parent?.size ?: 0)

    /**
     * Gets the local variable with the specified zero-based index.
     *
     * If the method is an instance method, the local variable at index 0 is the `this` reference.
     *
     * @param index the index of the local variable
     * @return the local variable
     */
    operator fun get(index: Int): JvmLocalVar {
        require(index in 0 until (baseLocalVarIndex + localVars.size))
        return if (index >= baseLocalVarIndex) {
            localVars[index - baseLocalVarIndex]
        } else {
            // We are sure this scope has a parent scope.
            parent!![index]
        }
    }

    /**
     * Gets the local variable with the specified name.
     *
     * @param name the name of the local variable
     * @return the local variable, if found; otherwise, `null`
     */
    operator fun get(name: String): JvmLocalVar? {
        val index = localVarsByName[name] ?: return parent?.get(name)
        return get(index)
    }

    /**
     * Gets the local variable that is the `this` reference.
     *
     * @return the local variable `this` reference
     */
    val `this`: JvmLocalVar
        get() {
            check(hasThis) { "There is no `this` variable in the local variables." }
            return get(0)
        }

    /** The number of arguments in this list. */
    val argumentCount: Int get() = _argumentCount + (parent?.argumentCount ?: 0)

    /**
     * Gets the argument with the specified index.
     *
     * @param index the zero-based index of the argument
     * @return the local variable for the argument
     */
    fun getArgument(index: Int): JvmLocalVar {
        require(index in 0 until argumentCount)
        return get(index + if (hasThis) 1 else 0)
    }

    /**
     * Gets the argument with the specified name.
     *
     * @param name the name of the argument
     * @return the local variable for the argument, if found; otherwise, `null`
     */
    fun getArgument(name: String): JvmLocalVar? {
        val index = localVarsByName[name]
        if (index == null) {
            return parent?.getArgument(name)
        } else if (index >= argumentCount + if (hasThis) 1 else 0) {
            return null
        }
        return getArgument(index - if (hasThis) 1 else 0)
    }

    /**
     * Adds a `this` reference.
     *
     * @param type the type of the `this` reference
     * @return the added local variable
     */
    fun addThis(type: JvmType): JvmLocalVar {
        check(!hasThis) { "A `this` reference has already been defined." }
        check(size == 0) { "Other local variables have already been added." }
        hasThis = true
        return addLocalVar(type, null)
    }

    /**
     * Adds a local variable for an argument.
     *
     * @param parameter the parameter
     * @return the added local variable
     */
    fun addArgument(parameter: JvmParam): JvmLocalVar {
        check(size <= argumentCount + if (hasThis) 1 else 0) { "Other local variables have already been added." }
        _argumentCount += 1
        return addLocalVar(parameter.type, parameter.name)
    }

    /**
     * Creates and adds a local variable with the specified name and type in the specified scope.
     *
     * @param type the type of the local variable
     * @param name the name of the local variable; or `null` when it has no name
     * @return the created local variable
     */
    fun addLocalVar(type: JvmType, name: String? = null): JvmLocalVar {
        require(!localVarsByName.containsKey(name)) { "A local variable with the name '$name' already exists." }
        val index = freshLocalVarIndex
        val localVar = JvmLocalVar(name, type, scope, index)
        localVars.add(localVar)
        declaredLocalVars.add(localVar)
        if (name != null) localVarsByName[name] = index
        return localVar
    }

    /**
     * Creates a new child [JvmLocalVars] for the specified scope.
     *
     * This scope cannot be used while any child scope is not closed.
     *
     * @param scope the child scope
     * @param name a debug name for the [JvmLocalVars]; or `null`
     * @return a child [JvmLocalVars]
     */
    fun scope(scope: JvmScope, name: String?): JvmLocalVars {
        // We just pass the same `declaredLocalVars` on.
        // Every local variable that is added to this child JvmLocalVars
        // is also added to the `declaredLocalVars` of the method.
        return adoptChild(JvmLocalVars(declaredLocalVars, scope, name, this))
    }

    /**
     * Gets a fresh local variable index.
     *
     * @return a fresh local variable index
     */
    private val freshLocalVarIndex: Int
        get() {
            val index = nextLocalVarIndex
            nextLocalVarIndex += 1
            return index
        }

    override fun iterator(): Iterator<JvmLocalVar> {
        return localVars.iterator()
    }
}