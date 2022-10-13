package dev.pelsmaeker.kode.types

import java.lang.reflect.*


/**
 * A JVM reference.
 *
 * A reference is descriptive enough to be used as generic type arguments,
 * to construct new instances of classes, to use static and instance fields,
 * and to invoke static and instance methods.
 */
interface JvmRef {
    /**
     * The fully-qualified internal name.
     *
     * Package names are separated with forward slash (`/`).
     * Class names are separated with dot (`.`) for inner classes
     * and dollar sign (`$`) for static nested classes.
     * Member names are separated with hash (`#`) from the class.
     */
    val internalName: String

    /**
     * The fully-qualified Java name.
     *
     * Internal package names are separated with dot (`.`).
     * Internal class names are separated with dollar sign (`$`) for both
     * inner classes and static nested classes.
     * Internal member names are separated with hash (`#`) from the class.
     */
    val javaName: String
}