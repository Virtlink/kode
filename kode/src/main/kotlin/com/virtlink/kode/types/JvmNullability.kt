package com.virtlink.kode.types


/**
 * Specifies the nullability of a type.
 */
enum class JvmNullability {
    /** The type may or may not be `null`, it is unspecified. */
    Maybe,
    /** The type should not be `null`. */
    NotNull,
    /** The type can be `null`. */
    Nullable
}