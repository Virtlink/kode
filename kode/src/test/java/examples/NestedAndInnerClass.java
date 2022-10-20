package examples;

public class NestedAndInnerClass<T> {
    static class Nested<U> {
        public String foo(U u) { return null; }
    }
    class Inner<V> {
        public T foo(V v) { return null; }
    }
}
