package dev.pelsmaeker.kode


import dev.pelsmaeker.kode.types.JvmClassDecl
import dev.pelsmaeker.kode.types.JvmRef
import dev.pelsmaeker.kode.utils.Eponymizer
import org.objectweb.asm.ClassWriter
import java.util.regex.Pattern


/**
 * JVM methods.
 */
class Jvm(
    /** The class version to use. */
    private val classVersion: JvmClassVersion = JvmClassVersion.Java8,
    /** The root eponymizer. */
    private val eponymizer: Eponymizer = Eponymizer(),
    /** Whether to compute the maxs. */
    private val computeMaxs: Boolean = true,
    /** Whether to compute the frames. */
    private val computeFrames: Boolean = true,
) {

    /**
     * Build a class or interface.
     *
     * Call [JvmClassBuilder.build] when done with the builder.
     *
     * @param declaration the class declaration
     * @param modifiers the class modifiers
     * @param signature the class signature
     * @return the class builder
     */
    fun createClass(
        declaration: JvmClassDecl,
        modifiers: JvmClassModifiers,
        signature: JvmClassSignature = JvmClassSignature(),
    ): JvmClassBuilder {
        var classFlags = 0
        if (computeMaxs) classFlags = classFlags or ClassWriter.COMPUTE_MAXS
        if (computeFrames) classFlags = classFlags or ClassWriter.COMPUTE_FRAMES
        val superClassInternalName = signature.superClass.internalName
        val superInterfacesInternalNames = signature.superInterfaces.map(JvmRef::internalName).toTypedArray()
        val classWriter = ClassWriter(classFlags)
        classWriter.visit(
            classVersion.value,
            modifiers.value,
            declaration.internalName,
            signature.getSignature(declaration.typeParameters),
            superClassInternalName,
            superInterfacesInternalNames
        )
        return JvmClassBuilder(
            declaration,
            classWriter,
            eponymizer.scope(declaration.toString())
        )
    }

    companion object {
        /** Regex pattern asserting that an identifier is a valid JVM class name. */
        private val classPattern = Pattern.compile(
            "^[^;\\[\\]/<>\\r\\n\\p{Cc}]+$"
        )

        /**
         * Regex pattern asserting that an identifier is a valid JVM package or member name (with the exception that
         * method can be named `<init>` or `<clinit>`).
         */
        private val memberPattern = Pattern.compile(
            "^[^.;\\[/<>\\r\\n\\p{Cc}]+$"
        )

        /** Regex pattern asserting that an identifier is valid for many operating systems. */
        private val osPattern = Pattern.compile(
            "^(?!(?:COM[0-9]|CON|LPT[0-9]|NUL|PRN|AUX)(\\.|$)|\\s|[.]{2,})[^\\\\/:*\"?<>|\\r\\n\\p{Cc}]{1,254}(?<![\\s.])$",
            Pattern.CASE_INSENSITIVE
        )

        /**
         * Determines whether the specified identifier is valid as a class name.
         *
         * Valid identifiers do not include line breaks, colon (`:`), semi-colon (`;`),
         * backslash (`\`), forward slash (`/`), square brackets (`[ ]`), angled brackets (`< >`),
         * or control characters, according to the rules of the JVM.
         *
         * Additionally, valid identifiers do not contain pipe (`|`), double quote (`"`),
         * question mark (`?`), asterisk (`*`), or control characters, and do not end with a space or period (`.`),
         * according to the rules of Windows filenames.
         *
         * Similarly, valid identifiers are not named `CON`, `NUL`, `PRN`, `AUX`, or
         * a digit following `COM` or `LPT`, (in any combination of uppercase/lowercase),
         * or any of these words followed by a period, according to the rules of Windows filenames.
         *
         * Finally, all identifiers have a maximum length of 254 characters.
         *
         * @param className the class name to test
         * @return `true` when the class name is valid; otherwise, `false`
         */
        fun isValidClassName(className: String): Boolean {
            // @formatter:off
            return classPattern.matcher(className).matches()
                && osPattern.matcher(className).matches()
            // @formatter:on
        }

        /**
         * Determines whether the specified identifier is valid as a package name segment.
         *
         * Valid identifiers do not include line breaks, period (`.`), colon (`:`), semi-colon (`;`),
         * backslash (`\`), forward slash (`/`), square brackets (`[ ]`), angled brackets (`< >`),
         * or control characters, according to the rules of the JVM.
         *
         * Additionally, valid identifiers do not contain pipe (`|`), double quote (`"`),
         * question mark (`?`), asterisk (`*`), or control characters, and do not end with a space or period (`.`),
         * according to the rules of Windows filenames.
         *
         * Similarly, valid identifiers are not named `CON`, `NUL`, `PRN`, `AUX`, or
         * a digit following `COM` or `LPT`, (in any combination of uppercase/lowercase),
         * or any of these words followed by a period, according to the rules of Windows filenames.
         *
         * Finally, all identifiers have a maximum length of 254 characters.
         *
         * @param packageNameSegment the package name segment to test
         * @return `true` when the package name segment is valid; otherwise, `false`
         */
        fun isValidPackageName(packageNameSegment: String): Boolean {
            // @formatter:off
            return memberPattern.matcher(packageNameSegment).matches()
                && osPattern.matcher(packageNameSegment).matches()
            // @formatter:on
        }

        /**
         * Determines whether the specified identifier is valid as a member (field, method, local variable) name.
         *
         * Valid identifiers do not include line breaks, period (`.`), colon (`:`), semi-colon (`;`),
         * backslash (`\`), forward slash (`/`), square brackets (`[ ]`), angled brackets (`< >`),
         * or control characters, according to the rules of the JVM.
         *
         * @param memberName the member name to test
         * @param isMethod whether this is a method member
         * @return `true` when the member name is valid; otherwise, `false`
         */
        fun isValidMemberName(memberName: String, isMethod: Boolean): Boolean {
            return memberPattern.matcher(memberName).matches()
        }
    }
}