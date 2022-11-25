package com.virtlink.kode.utils

/**
 * Gives names to things.
 */
class Eponymizer private constructor(
    /** A name for the eponymizer, only used for debugging; or `null`. */
    name: String? = null,
    /** The parent eponymizer; or `null` if this is a root eponymizer. */
    parent: Eponymizer?,
) : Scoped<Eponymizer>(name, parent) {

    /**
     * Creates a root eponymizer.
     *
     * @param name a name for the eponymizer, only used for debugging; or `null`
     * @return the created root eponymizer
     */
    constructor(name: String? = "root") : this(name, null)

    /**
     * Names with an associated index, such as `x` and `5`, forming `x5`.
     * A name with index zero gets no suffix.
     *
     * When a name occurs in this map, all the names with a lower or equal index
     * have been used already. However, it is also possible for the name to
     * occur in [knownNames], in which case it also has been used already.
     */
    private val namesWithIndex: MutableMap<String?, Int> = mutableMapOf()

    /**
     * Names that are known, but not put in the `namesWithIndex` map.
     *
     * This is specifically to track names with indices that where created outside
     * the eponymizer system. For example, if we know a name `foo3` exists
     * somewhere in the program (through a call to [put]), then we record
     * it here if its index (`3` in this case) is higher than the last known
     * name-with-index. Once we generate more names `foo` (e.g., `foo1`
     * and `foo2`), when trying to generate `foo3` we detect that this name
     * is already known. We skip it, remove `foo3` from this set, and
     * go straight to `foo4`.
     *
     * If a known name does not have an index (e.g., `bar`), then it is
     * instead added to [namesWithIndex] with index 0. If a known name
     * has index 0 (e.g., `qux0`), then it is ignored as this eponymizer
     * can never generate such a name.
     */
    private val knownNames: MutableSet<String> = mutableSetOf()

    /**
     * Gets a name for something. The name is unique in the current scope
     * and all ancestor scopes.
     *
     * The name hint may or may not be used, or may be used partially.
     * For example, if the hint is `foo007`, then it might just use
     * the `foo` part, to avoid issues in counting names.
     *
     * @param hint the name hint; or `null` to not provide one
     * @return the unique name, which may or may not have a suffix
     */
    fun get(hint: String? = null): String {
        checkUsable()
        val baseName = getBaseName(hint)
        // If no previous definition was found (i.e., the value is -1),
        // then adding 1 results in the default value 0, which is a new unused index.
        // Otherwise, adding 1 results in a new unused index. Both are fine.
        var index = getIndex(baseName) + 1

        // Generate the new name
        var name = if (index > 0) baseName + index else baseName

        // While we have already seen this name,
        // increase the index until we find one that's new.
        while (isKnownName(name)) {
            // We try to remove the name anyway,
            // even though it might not be in the local set but in an ancestor's set.
            knownNames.remove(name)
            index += 1
            name = if (index > 0) baseName + index else baseName
        }

        // Store the index of the newly generated name.
        namesWithIndex[baseName] = index
        return name
    }

    /**
     * Indicates that a name is defined in this scope,
     * to avoid generating this name.
     *
     * @param name the name to add
     */
    fun put(name: String) {
        checkUsable()
        val indexStart = indexOfIndex(name)
        if (indexStart == 0) {
            // The name contains only digits.
            // We ignore this name, as we can never generate it.
            return
        }
        val nameBase: String
        val index: Int
        if (indexStart > 0) {
            // The name contains a base name and an index
            nameBase = name.substring(0, indexStart)
            val indexPart = name.substring(indexStart)
            if (indexPart[0] == '0') {
                // The index part starts with one or more zeros, or is just zero.
                // We ignore this name, as we can never generate it.
                return
            }
            index = indexPart.toInt()
        } else {
            nameBase = name
            index = 0
        }
        val currentIndex = getIndex(nameBase)
        if (index == currentIndex + 1) {
            // We add this name with index +1, as if we have generated it now.
            // This works also if `currentIndex == -1` (i.e., not found),
            // since we'll add it with index 0 in that case.
            namesWithIndex[nameBase] = index
        } else if (currentIndex < index) {
            // We record this name, as we might generate it later.
            knownNames.add(name)
        } else {
            // We already generated this name, so we ignore it.
            return
        }
    }

    /**
     * Returns a new child [Eponymizer] that has its own scope.
     *
     * This eponymizer cannot be used while any child eponimyzer scope is not closed.
     * Close the returned eponymizer to close the scope.
     *
     * @param name a name for the child eponymizer, only used for debugging
     * @return a new child eponymizer
     */
    fun scope(name: String): Eponymizer {
        return adoptChild(Eponymizer(name, this))
    }

    /**
     * Determines whether the given name is a know name to this eponymizer or any of its ancestors.
     *
     * @param name the name to check
     * @return `true` when the name is a known name;
     * otherwise, `false`
     */
    private fun isKnownName(name: String): Boolean {
        if (knownNames.contains(name)) return true

        // Find whether a close ancestor knows this name.
        for (ancestor in getAncestors()) {
            if (ancestor.knownNames.contains(name)) return true
        }
        return false
    }

    /**
     * Gets a base name from a name hint.
     *
     * @param hint the name hint; or `null` to not provide one
     * @return the base name
     */
    private fun getBaseName(hint: String?): String {
        if (hint != null) {
            val indexStart = indexOfIndex(hint)
            if (indexStart > 0) {
                // The hint contains a base name and an index
                return hint.substring(0, indexStart)
            } else if (indexStart < 0) {
                // The hint contains only a base name
                return hint
            }
        }
        // No hint was provided, or the hint contains only digits.
        return "x"
    }

    /**
     * Gets the current index recorded for the given name.
     *
     * This method looks in the current and ancestor eponymizers.
     *
     * @param name the name to look for
     * @return the recorded index; or -1 if none was recorded
     */
    private fun getIndex(name: String?): Int {
        assert(name != null)
        var index = namesWithIndex[name]
        if (index != null) return index

        // Find the closest ancestor that knows this name.
        for (ancestor in getAncestors()) {
            index = ancestor.namesWithIndex[name]
            if (index != null) return index
        }
        return -1
    }

    /**
     * Determines the character index of the index part of a name.
     *
     * @param name the name
     * @return the zero-based character index in the string of the index part;
     * or -1 if no index part was found
     */
    private fun indexOfIndex(name: String?): Int {
        assert(name != null)
        var indexStart = -1
        for (i in 0 until name!!.length) {
            val c = name[i]
            if (c in '0'..'9') {
                if (indexStart < 0) indexStart = i
            } else {
                indexStart = -1
            }
        }
        return indexStart
    }
}