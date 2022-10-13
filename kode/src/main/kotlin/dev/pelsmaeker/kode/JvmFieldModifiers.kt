package dev.pelsmaeker.kode


import dev.pelsmaeker.kode.utils.IntBitEnum
import dev.pelsmaeker.kode.utils.bits
import java.io.Serializable
import org.objectweb.asm.Opcodes.*

/**
 * Modifiers for a JVM field.
 */
@Suppress("MemberVisibilityCanBePrivate", "unused")
@JvmInline
value class JvmFieldModifiers(override val value: Int): IntBitEnum<JvmFieldModifiers> {

    init {
        require(value and mask.inv() == 0) { "Unknown values specified." }
    }

    override infix fun or(other: JvmFieldModifiers) = JvmFieldModifiers(value or other.value)

    override infix fun and(other: JvmFieldModifiers) = JvmFieldModifiers(value and other.value)

    override operator fun not(): JvmFieldModifiers = JvmFieldModifiers(value.inv() and mask)

    override operator fun iterator(): Iterator<JvmFieldModifiers> =
        value.bits().map { JvmFieldModifiers(it) }.iterator()

    override fun toString(): String =
        value.bits().joinToString(prefix = "{", postfix = "}") { names[Integer.numberOfTrailingZeros(it)]!! }

    companion object {
        /**
         * Creates a new bitwise enum from the specified members.
         *
         * @param members the members to include
         * @return the created bitwise enum
         */
        fun from(members: Iterable<JvmFieldModifiers>): JvmFieldModifiers {
            return members.fold(None) { acc, member -> acc or member }
        }

        /** All members in this enum. */
        val allMembers: List<JvmFieldModifiers> get() = members.asList().filterNotNull()

        /** No modifiers. */
        val None = JvmFieldModifiers(0)
        /** The field is accessible outside its package. */
        val Public = JvmFieldModifiers(ACC_PUBLIC)
        /** The field is accessible only from inside its class. */
        val Private = JvmFieldModifiers(ACC_PRIVATE)
        /** The field is accessible from inside its class and subclasses. */
        val Protected = JvmFieldModifiers(ACC_PROTECTED)
        /** The field is static. */
        val Static = JvmFieldModifiers(ACC_STATIC)
        /** The field cannot be overridden. */
        val Final = JvmFieldModifiers(ACC_FINAL)
        /** The field is volatile. */
        val Volatile = JvmFieldModifiers(ACC_VOLATILE)
        /** The field is transient. */
        val Transient = JvmFieldModifiers(ACC_TRANSIENT)
        /** The field is not explicitly declared in the source code. */
        val Synthetic = JvmFieldModifiers(ACC_SYNTHETIC)
        /** The field is a member of an enum. */
        val Enum = JvmFieldModifiers(ACC_ENUM)
        /** The field is mandated by the specification. */
        val Mandated = JvmFieldModifiers(ACC_MANDATED)
        /** The field is deprecated. ASM-specific. */
        val Deprecated = JvmFieldModifiers(ACC_DEPRECATED)

        /** The members, one name for each bit position (or `null` for undefined positions). */
        private val members = arrayOf(
            Public,
            Private,
            Protected,
            Static,
            Final,
            null,
            Volatile,
            Transient,
            null,
            null,
            null,
            null,
            Synthetic,
            null,
            Enum,
            Mandated,
            null,
            Deprecated,
        )

        /** The names of the members, one name for each bit position (or `null` for undefined positions). */
        private val names = arrayOf(
            "Public",
            "Private",
            "Protected",
            "Static",
            "Final",
            null,
            "Volatile",
            "Transient",
            null,
            null,
            null,
            null,
            "Synthetic",
            null,
            "Enum",
            "Mandated",
            null,
            "Deprecated"
        )

        /** The mask, which consists of all possible bit enum members. */
        private const val mask: Int =
            // @formatter:off
            ACC_PUBLIC or
            ACC_PRIVATE or
            ACC_PROTECTED or
            ACC_STATIC or
            ACC_FINAL or
            // null
            ACC_VOLATILE or
            ACC_TRANSIENT or
            // null
            // null
            // null
            // null
            ACC_SYNTHETIC or
            // null
            ACC_ENUM or
            ACC_MANDATED or
            // null
            ACC_DEPRECATED
            // @formatter:on

    }
}