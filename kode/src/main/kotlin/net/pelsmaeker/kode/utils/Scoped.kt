package net.pelsmaeker.kode.utils

import java.util.*

/**
 * Base class for scoped classes.
 *
 * A scoped class can have multiple sub-scopes,
 * but cannot be used until its sub-scopes have been closed.
 *
 * Implementation should provide a constructor to create a root scope,
 * and a method that calls [adoptChild] to create a child scope.
 * Implementations should call [checkUsable] before each action,
 * to check that the object can actually be used.
 */
abstract class Scoped<SELF : Scoped<SELF>> protected constructor(
    /** The name of the scope, for debugging purposes. */
    debugName: String? = null,
    /** The parent scope; or `null`. */
    protected val parent: SELF? = null,
): AutoCloseable {

    private val _debugName: String? = debugName
    /** The name of the scope, for debugging purposes. */
    protected val debugName: String get() = _debugName ?: parent?.children?.indexOf(this)?.toString() ?: "root"

    private val _children: MutableList<SELF> = mutableListOf()
    /** Child scopes. If this set is not empty, this scope cannot be used or closed. */
    protected val children: List<SELF> get() = _children

    /** Whether this scope is closed. */
    var isClosed = false
        private set

    /**
     * Adopts the given scope as a child of this scope.
     *
     * This scope cannot be used while it has any child scopes that are not closed.
     *
     * @param child the child scope
     * @param T the type of child
     * @return the child scope
     */
    protected fun <T : SELF> adoptChild(child: T): T {
        checkNotClosed()
        _children.add(child)
        return child
    }

    /**
     * Abandons the given child.
     *
     * @param child the child to abandon
     */
    protected fun abandonChild(child: Scoped<SELF>) {
        checkNotClosed()
        _children.remove(child)
    }

    /**
     * Determines the full name of this scope (for debugging purposes)
     * up to but not including the specified ancestor, if any.
     *
     * @param maxAncestor the ancestor from which to start the name; or `null`
     * @return the full name (for debugging purposes)
     */
    protected fun getFullName(maxAncestor: Scoped<SELF>? = null): String {
        val ancestors = getAncestors().takeWhile { it != maxAncestor }.toList()
        if (ancestors.isEmpty()) return debugName
        return ancestors.asReversed().joinToString(separator = "/", postfix = "/") { it.debugName } + debugName
    }

    /**
     * Gets all ancestors of this scope, from closest (parent) to farthest.
     *
     * When both `maxHeight` and `maxAncestor` are specified,
     * the first limit that is reached determines the result.
     *
     * @return the ancestors of this scope
     */
    protected fun getAncestors(): Sequence<SELF> = sequence {
        var ancestor = parent
        while (ancestor != null) {
            yield(ancestor)
            ancestor = ancestor.parent
        }
    }

    /**
     * Gets descendants of this scope,
     * in breath-first order from closest (children) to farthest.
     *
     * @return the descendants of this scope
     */
    private fun getDescendants(): Sequence<SELF> = sequence {
        data class WithDepth(val scope: SELF, val depth: Int)

        val currentDescendants = ArrayDeque<WithDepth>()

        // Add the current node's children
        for (child: SELF in children) {
            currentDescendants.addLast(WithDepth(child, 1))
        }
        while (!currentDescendants.isEmpty()) {
            val (descendant, depth) = currentDescendants.removeFirst()
            yield(descendant)
            // Add the descendant node's children
            for (child: SELF in descendant.children) {
                currentDescendants.addLast(WithDepth(child, depth + 1))
            }
        }
    }

    /**
     * Checks that the scope has no children.
     *
     * @throws IllegalStateException if the scope has children
     */
    private fun checkChildless() {
        check(children.isEmpty()) {
            "This scope $debugName cannot be used, because it has open child scopes: " + getDescendants().joinToString(limit = 10) { it.getFullName(this) }
        }
    }

    /**
     * Checks that the scope was not closed.
     *
     * @throws IllegalStateException if the scope was closed
     */
    private fun checkNotClosed() {
        check(!isClosed) { "The scope was closed." }
    }

    /**
     * Checks that the object is usable.
     *
     * @throws IllegalStateException if the scope is not usable
     */
    protected fun checkUsable() {
        checkNotClosed()
        checkChildless()
    }

    override fun close() {
        tryClose()
    }

    /**
     * Attempts to close the scope.
     *
     * When overriding [close], call this method instead of `super.close()`
     * to know whether to execute closing logic (when this method returns `true`) or not
     * (when this method returns `false`).
     *
     * @return `true` if the scope was closed;
     * otherwise, `false` or an exception
     */
    protected fun tryClose(): Boolean {
        if (isClosed) return false
        checkChildless()
        parent?.abandonChild(this)
        isClosed = true
        return true
    }

    override fun toString(): String {
        return getFullName()
    }
}