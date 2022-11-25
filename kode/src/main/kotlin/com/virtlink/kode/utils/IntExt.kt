package com.virtlink.kode.utils

/**
 * Returns a sequence iterating the set bits in this integer
 * from least significant to most significant (i.e., in ascending order).
 *
 * @return a sequence iterating the set bits in this integer
 */
fun Int.bits(): Sequence<Int> = sequence {
    var unseen = this@bits
    while (unseen != 0) {
        // Determine the lowest bit that is set.
        val lowestBit = unseen and -unseen
        // Remove it from the `unseen` (as it is now seen).
        unseen -= lowestBit
        // And determine what value this bit represents.
        yield(lowestBit)
    }
}