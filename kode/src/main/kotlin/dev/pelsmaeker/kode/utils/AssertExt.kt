package dev.pelsmaeker.kode.utils

import dev.pelsmaeker.kode.types.JvmType

/**
 * Requires that the given value is the expected type.
 *
 * @param actual the actual value
 * @param expected the expected value
 */
@Suppress("NOTHING_TO_INLINE")
inline fun requireIsJvmType(expected: JvmType, actual: JvmType) {
    require(expected == actual) {
        "Expected $expected, got $actual."
    }
}