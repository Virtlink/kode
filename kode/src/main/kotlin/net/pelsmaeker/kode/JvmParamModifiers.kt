package net.pelsmaeker.kode

import net.pelsmaeker.kode.utils.IntBitEnum
import net.pelsmaeker.kode.utils.bits
import java.io.Serializable
import org.objectweb.asm.Opcodes.*

/**
 * Modifiers for a JVM parameter.
 */
@Suppress("MemberVisibilityCanBePrivate", "unused")
@JvmInline
value class JvmParamModifiers(override val value: Int): IntBitEnum<JvmParamModifiers> {

    init {
        require(value and mask.inv() == 0) { "Unknown values specified." }
    }

    override infix fun or(other: JvmParamModifiers) = JvmParamModifiers(value or other.value)

    override infix fun and(other: JvmParamModifiers) = JvmParamModifiers(value and other.value)

    override operator fun not(): JvmParamModifiers = JvmParamModifiers(value.inv() and mask)

    override operator fun iterator(): Iterator<JvmParamModifiers> =
        value.bits().map { JvmParamModifiers(it) }.iterator()

    override fun toString(): String =
        value.bits().joinToString(prefix = "{", postfix = "}") { names[Integer.numberOfTrailingZeros(it)]!! }

    companion object {
        /**
         * Creates a new bitwise enum from the specified members.
         *
         * @param members the members to include
         * @return the created bitwise enum
         */
        fun from(members: Iterable<JvmParamModifiers>): JvmParamModifiers {
            return members.fold(None) { acc, member -> acc or member }
        }

        /** All members in this enum. */
        val allMembers: List<JvmParamModifiers> get() = members.asList().filterNotNull()

        /** No modifiers. */
        @JvmStatic @get:JvmName("None")
        val None = JvmParamModifiers(0)
        /** The parameter cannot be assigned after its construction. */
        @JvmStatic @get:JvmName("Final")
        val Final = JvmParamModifiers(ACC_FINAL)
        /** The parameter is not explicitly declared in the source code. */
        @JvmStatic @get:JvmName("Synthetic")
        val Synthetic = JvmParamModifiers(ACC_SYNTHETIC)
        /** The parameter was not explicitly declared in the source code but implicitly mandated by the specification. */
        @JvmStatic @get:JvmName("Mandated")
        val Mandated = JvmParamModifiers(ACC_MANDATED)

        /** The members, one name for each bit position (or `null` for undefined positions). */
        private val members = arrayOf(
            null,
            null,
            null,
            null,
            Final,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            Synthetic,
            null,
            null,
            Mandated,
            null,
            null
        )

        /** The names of the members, one name for each bit position (or `null` for undefined positions). */
        private val names = arrayOf(
            null,
            null,
            null,
            null,
            "Final",
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            "Synthetic",
            null,
            null,
            "Mandated",
            null,
            null
        )

        /** The mask, which consists of all possible bit enum members. */
        private const val mask: Int =
            // @formatter:off
            // null
            // null
            // null
            // null
            ACC_FINAL or
            // null
            // null
            // null
            // null
            // null
            // null
            // null
            ACC_SYNTHETIC or
            // null
            // null
            ACC_MANDATED
            // null
            // null
            // @formatter:on
    }
}