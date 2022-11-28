package net.pelsmaeker.kode

import org.objectweb.asm.Opcodes

/**
 * Specifies a JVM class version.
 */
@Suppress("unused")
enum class JvmClassVersion(
    /** The value for this enum constant. */
    val value: Int,
) {

    /** Java 1.1 (class version 45). */
    Java1(Opcodes.V1_1),

    /** Java 1.2 (class version 46). */
    Java2(Opcodes.V1_2),

    /** Java 1.3 (class version 47). */
    Java3(Opcodes.V1_3),

    /** Java 1.4 (class version 48). */
    Java4(Opcodes.V1_4),

    /** Java 5 (class version 49). */
    Java5(Opcodes.V1_5),

    /** Java 6 (class version 50). */
    Java6(Opcodes.V1_6),

    /** Java 7 (class version 51). */
    Java7(Opcodes.V1_7),

    /** Java 8 (class version 52). */
    Java8(Opcodes.V1_8),

    /** Java 9 (class version 53). */
    Java9(Opcodes.V9),

    /** Java 10 (class version 54). */
    Java10(Opcodes.V10),

    /** Java 11 (class version 55). */
    Java11(Opcodes.V11),

    /** Java 12 (class version 56). */
    Java12(Opcodes.V12),

    /** Java 13 (class version 57). */
    Java13(Opcodes.V13),

    /** Java 14 (class version 58). */
    Java14(Opcodes.V14),

    /** Java 15 (class version 59). */
    Java15(Opcodes.V15),

    /** Java 16 (class version 60). */
    Java16(Opcodes.V16),

    /** Java 17 (class version 61). */
    Java17(Opcodes.V17),

    /** Java 18 (class version 62). */
    Java18(Opcodes.V18);

    /** The class major version. */
    val classMajorVersion: Int
        get() = value and 0xFFFF

    /** The class minor version. */
    val classMinorVersion: Int
        get() = (value shr 16) and 0xFFFF
}