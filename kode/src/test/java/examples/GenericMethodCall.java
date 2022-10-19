package examples;

public class GenericMethodCall {
    public static void main(String[] args) {
        C<? super CharSequence> c = new C<>();
        System.out.println(c.foo(42));
    }
}

class C<R> {
    public <T extends Number> R foo(T input) {
        return null;
    }
}

