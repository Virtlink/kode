package net.pelsmaeker.kode.types

// Some class structures for use in tests.

// Top-level class
open class C: B(), I {
    // Inner class
    inner class CIC
    // Nested class
    class CNC
    // Nested interface
    interface CNI
}

// Top-level interface
interface I {
    // Nested class
    class INC
    // Nested interface
    interface INI
}

// Generic top-level class
class GC<T> {
    // Generic inner class
    inner class GCIC<T, U>
    // Generic nested class
    class GCNC<T, U>
    // Nested interface
    interface GCNI<T, U>
}

// Generic top-level interface
interface GI<T> {
    // Generic nested class
    class GINC<T, U>
    // Generic nested interface
    interface GINI<T, U>
}

// Class with various kinds of type parameters
class GC2<T, U, V, W, out X, in Y>
        where U: GC<T>, U: GI<T>, V: GC<T>.GCIC<T, C>, W: GC.GCNI<C, U>, X: C, X: I, Y: C, Y: I

// Top of the hierarchy
open class A
// Subclass
open class B: A()

// Interface with covariant type parameter
interface IOT<out T>
// Interface with contravariant type parameter
interface IIT<in T>
// Interface with invariant type parameter
interface IT<T>