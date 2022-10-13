package dev.pelsmaeker.kode

import dev.pelsmaeker.kode.utils.IntBitEnum
import dev.pelsmaeker.kode.utils.bits
import org.objectweb.asm.Opcodes.*

/**
 * Modifiers for a JVM class.
 */
@Suppress("MemberVisibilityCanBePrivate", "unused")
@JvmInline
value class JvmClassModifiers(override val value: Int): IntBitEnum<JvmClassModifiers> {

    init {
        require(value and mask.inv() == 0) { "Unknown values specified." }
    }

    override infix fun or(other: JvmClassModifiers) = JvmClassModifiers(value or other.value)

    override infix fun and(other: JvmClassModifiers) = JvmClassModifiers(value and other.value)

    override operator fun not(): JvmClassModifiers = JvmClassModifiers(value.inv() and mask)

    override operator fun iterator(): Iterator<JvmClassModifiers> =
        value.bits().map { JvmClassModifiers(it) }.iterator()

    override fun toString(): String =
        value.bits().joinToString(prefix = "{", postfix = "}") { names[Integer.numberOfTrailingZeros(it)]!! }

    companion object {
        
        /**
         * Gets the JVM class modifiers of the specified class.
         *
         * @param cls the class
         * @return the JVM class modifiers
         */
        fun of(cls: Class<*>): JvmClassModifiers {
            return JvmClassModifiers(cls.modifiers)
        }

        /**
         * Creates a new bitwise enum from the specified members.
         *
         * @param members the members to include
         * @return the created bitwise enum
         */
        fun from(members: Iterable<JvmClassModifiers>): JvmClassModifiers {
            return members.fold(None) { acc, member -> acc or member }
        }

        /** All members in this enum. */
        val allMembers: List<JvmClassModifiers> get() = members.asList().filterNotNull()

        /** No modifiers. */
        val None = JvmClassModifiers(0)
        /** The class is accessible outside its package. */
        val Public = JvmClassModifiers(ACC_PUBLIC)
        // Unused?
        val Private = JvmClassModifiers(ACC_PRIVATE)
        // Unused?
        val Protected = JvmClassModifiers(ACC_PROTECTED)
        /** The class cannot be subclassed. */
        val Final = JvmClassModifiers(ACC_FINAL)
        /** Treat superclass methods special when invoked with the `invokespecial` instruction. */
        val Super = JvmClassModifiers(ACC_SUPER)
        /** The class is an interface, the class is not a class. */
        val Interface = JvmClassModifiers(ACC_INTERFACE)
        /** The class cannot be instantiated. */
        val Abstract = JvmClassModifiers(ACC_ABSTRACT)
        /** The class is not explicitly declared in the source code. */
        val Synthetic = JvmClassModifiers(ACC_SYNTHETIC)
        /** The class is an annotation type. */
        val Annotation = JvmClassModifiers(ACC_ANNOTATION)
        /** The class is an enum type. */
        val Enum = JvmClassModifiers(ACC_ENUM)
        // ?
        val Module = JvmClassModifiers(ACC_MODULE)
        /** The class is a record type. */
        val Record = JvmClassModifiers(ACC_RECORD)
        /** The class is deprecated. ASM-specific. */
        val Deprecated = JvmClassModifiers(ACC_DEPRECATED)

        /** The members, one name for each bit position (or `null` for undefined positions). */
        private val members = arrayOf(
            Public,
            Private,
            Protected,
            null,
            Final,
            Super,
            null,
            null,
            null,
            Interface,
            Abstract,
            null,
            Synthetic,
            Annotation,
            Enum,
            Module,
            Record,
            Deprecated,
        )

        /** The names of the members, one name for each bit position (or `null` for undefined positions). */
        private val names = arrayOf(
            "Public",
            "Private",
            "Protected",
            null,
            "Final",
            "Super",
            null,
            null,
            null,
            "Interface",
            "Abstract",
            null,
            "Synthetic",
            "Annotation",
            "Enum",
            "Module",
            "Record",
            "Deprecated"
        )

        /** The mask, which consists of all possible bit enum members. */
        private const val mask: Int =
            // @formatter:off
            ACC_PUBLIC or
            ACC_PRIVATE or
            ACC_PROTECTED or
            // null
            ACC_FINAL or
            ACC_SUPER or
            // null
            // null
            // null
            ACC_INTERFACE or
            ACC_ABSTRACT or
            // null
            ACC_SYNTHETIC or
            ACC_ANNOTATION or
            ACC_ENUM or
            ACC_MODULE or
            ACC_RECORD or
            ACC_DEPRECATED
            // @formatter:on
    }
}