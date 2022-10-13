package dev.pelsmaeker.kode.utils


/**
 * An interface for 32-bit integer bitwise enums.
 *
 * The enum can be iterated and inspected, or manipulated using the [or], [and], and [not] methods.
 */
interface IntBitEnum<SELF: IntBitEnum<SELF>>: Iterable<SELF>, Comparable<SELF> {

    /** The bit value of this instance. */
    val value: Int

    /** The number of values in this bitwise enum. */
    val size: Int get() = Integer.bitCount(value)

    /** Whether the bitwise enum is empty. */
    fun isEmpty(): Boolean = value == 0

    /**
     * Returns the members that are in this set or the specified set or both.
     *
     * @param other the other set
     * @return the union of members from this set and the specified set
     */
    infix fun or(other: SELF): SELF

    /**
     * Returns the members that are both in this set and the specified set.
     *
     * @param other the other set
     * @return the intersection of members from this set and the specified set
     */
    infix fun and(other: SELF): SELF

    /**
     * Returns the members that are not in this set.
     *
     * @return the members not in this set
     */
    operator fun not(): SELF

    /**
     * Determines whether all members of the specified set are in this set.
     *
     * @param members the members to look for
     * @return `true` when all the specified members are present in this set; otherwise, `false`
     */
    operator fun contains(members: SELF): Boolean {
        return value and members.value == members.value
    }

    override fun compareTo(other: SELF): Int {
        return this.value.compareTo(other.value)
    }

    /**
     * Returns an iterator that iterates over the members in this bit enum.
     *
     * @return an iterator
     */
    override operator fun iterator(): Iterator<SELF>

}