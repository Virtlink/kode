package dev.pelsmaeker.kode


import dev.pelsmaeker.kode.types.JvmClassDecl
import dev.pelsmaeker.kode.types.JvmPackageRef
import org.objectweb.asm.util.CheckClassAdapter
import org.objectweb.asm.util.TraceClassVisitor
import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassWriter
import org.objectweb.asm.Opcodes
import java.io.*
import java.lang.reflect.Constructor
import java.lang.reflect.InvocationTargetException
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

/**
 * A compiled JVM class.
 */
class JvmCompiledClass(
    /** The type of the class. */
    val type: JvmClassDecl,
    /** The compiled class data. */
    private val bytes: ByteArray
) {

    /**
     * Sanity checks the class and computes the frames.
     */
    fun check() {
        val classReader = ClassReader(bytes)
        val classWriter = ClassWriter(
            classReader,
            ClassWriter.COMPUTE_MAXS or ClassWriter.COMPUTE_FRAMES
        )
        val checkClassAdapter = CheckClassAdapter(classWriter)
        classReader.accept(checkClassAdapter, ClassReader.EXPAND_FRAMES)
    }

    /**
     * Loads this class into a class loader.
     *
     * @param classLoader the class loader to load into
     * @param T the type of class this is
     * @return the loaded class
     */
    fun <T> load(classLoader: ClassLoader = JvmCompiledClass::class.java.classLoader): Class<T> {
        val dynamicClassLoader = DynamicClassLoader(classLoader)
        @Suppress("UNCHECKED_CAST")
        return dynamicClassLoader.defineClass(type.javaName, bytes) as Class<T>
    }

    /**
     * Instantiates this class into the default class loader
     * and the class default parameterless constructor.
     *
     * @param T the type of class this is
     * @return the instantiated class
     * @throws NoSuchMethodException if the class has no parameterless constructor
     * @throws InvocationTargetException if the class cannot be instantiated
     * @throws InstantiationException if the class cannot be instantiated
     * @throws IllegalAccessException if the class cannot be instantiated
     */
    @Throws(
        NoSuchMethodException::class,
        InvocationTargetException::class,
        InstantiationException::class,
        IllegalAccessException::class
    )
    fun <T> instantiate(): T {
        return instantiate(JvmCompiledClass::class.java.classLoader)
    }

    /**
     * Instantiates this class into the specified class loader
     * and the class default parameterless constructor.
     *
     * To instantiate the class using a non-default constructor,
     * use [.load] instead, and explicitly call [Class.getDeclaredConstructor]
     * and [Constructor.newInstance] on the returned class.
     *
     * @param classLoader the class loader to load into
     * @param T the type of class this is
     * @return the instantiated class
     * @throws NoSuchMethodException if the class has no parameterless constructor
     * @throws InvocationTargetException if the class cannot be instantiated
     * @throws InstantiationException if the class cannot be instantiated
     * @throws IllegalAccessException if the class cannot be instantiated
     */
    @Throws(
        NoSuchMethodException::class,
        InvocationTargetException::class,
        InstantiationException::class,
        IllegalAccessException::class
    )
    fun <T> instantiate(classLoader: ClassLoader): T {
        val cls = load<T>(classLoader)
        return cls.getDeclaredConstructor().newInstance()
    }

    /**
     * Writes the JVM class to the specified output stream.
     *
     * @param output the output stream to write to
     * @throws IOException if an I/O exception occurred
     */
    @Throws(IOException::class)
    fun writeTo(output: OutputStream) {
        output.write(bytes)
    }

    /**
     * Writes the JVM class to the specified file.
     *
     * @param path the path of the file to write to
     * @throws IOException if an I/O exception occurred
     */
    @Throws(IOException::class)
    fun writeToFile(path: Path) {
        Files.write(path, bytes)
    }

    /**
     * Writes the JVM class to a file that corresponds to the class name,
     * in a directory that corresponds to the class' package.
     *
     * @param rootPath the root path
     * @return the path to the file where the class was written to
     * @throws IOException if an I/O exception occurred
     */
    @Throws(IOException::class)
    fun writeToFileInPackage(rootPath: Path): Path {
        val classFile = type.resolveInPath(rootPath)
        Files.createDirectories(classFile.parent)
        writeToFile(classFile)
        return classFile
    }

    override fun toString(): String {
        // Prints the whole class content in a readable form.
        val classReader = ClassReader(bytes)
        val stringWriter = StringWriter()
        classReader.accept(TraceClassVisitor(PrintWriter(stringWriter)), 0)
        return stringWriter.toString()
    }

    // FIXME: To actually use a class loaded in this class loader
    //  we might need to get its instance? Or can we just use Class.getClassLoader()?
    /**
     * Dynamic class loader.
     */
    private class DynamicClassLoader(
        /** The parent class loader; or `null`. */
        parent: ClassLoader? = null
    ) : ClassLoader(parent) {

        /**
         * Adds a class from its binary representation.
         *
         * This method loads a class into the dynamic class loader, even if another instance
         * of the class is already loaded elsewhere. This means that instances of this loaded class
         * cannot be cast to the same type but loaded by another class loader. It is, however, possible
         * to cast to a supertype that is not (exclusively) loaded by this class (e.g., [Object] or
         * some common base class or interface).
         *
         * @param name the Java name of the class (i.e., `org.example.MyClass`)
         * @param b the bytes of the class
         * @return the class
         * @throws ClassFormatError if the class data cannot be read
         */
        @Throws(ClassFormatError::class)
        fun defineClass(name: String?, b: ByteArray): Class<*> {
            return defineClass(name, b, 0, b.size)
        }
    }

    companion object {
        /**
         * Reads a compiled class from the class path.
         *
         * @param cls the class
         * @return the read compiled class
         * @throws IOException if an I/O exception occurs
         */
        @Throws(IOException::class)
        fun fromClasspath(cls: Class<*>): JvmCompiledClass {
            val classDecl = JvmClassDecl.of(cls)
            val classPath = classDecl.resolveInPath(Paths.get("/")).toString()
            val clsStream = cls.classLoader.getResourceAsStream(classPath.substring(1 /* Trim leading slash. */))
                ?: throw FileNotFoundException("Class path resource not found: $classPath")
            val bytes = clsStream.readAllBytes()
            return JvmCompiledClass(classDecl, bytes)
        }

        /**
         * Reads a compiled class from the specified file path.
         *
         * If the [classFile] path is relative, it is assumed to be relative
         * to [rootPath]. However, if [classFile] is absolute, it will
         * be made relative to [rootPath]. The relative path is used to determine
         * the package of the class.
         *
         * @param rootPath the root path
         * @param classFile the path to the class
         * @return the read compiled class
         * @throws IOException if an I/O exception occurs
         */
        @Throws(IOException::class)
        fun fromFile(rootPath: Path, classFile: Path): JvmCompiledClass {
            require(classFile.endsWith(".class")) { "Class file should end in `.class`: $classFile" }
            val relativeClassFile = if (classFile.isAbsolute) rootPath.relativize(classFile) else classFile
            require(!relativeClassFile.isAbsolute) { "Class file ($classFile) is not relative to root path: $rootPath" }
            val classFilename = relativeClassFile.fileName.toString()
            val className = classFilename.substring(0, classFilename.length - ".class".length)
            val packageName = relativeClassFile.parent.toString()
            val bytes = Files.readAllBytes(rootPath.resolve(classFile))
            val classReader = ClassReader(bytes)
            val isInterface = classReader.access and Opcodes.ACC_INTERFACE != 0

            // TODO: SuperClass, SuperInterfaces, TypeParameters
            val type = JvmClassDecl(className, JvmPackageRef(packageName), isInterface)
            return JvmCompiledClass(type, bytes)
        }
    }
}