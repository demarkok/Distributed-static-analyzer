package foo.bar;

import foo.baz.Test1;

public class Test2 extends Test1 {


    public int foo() {
        return 2;
    }

    int x = 0;

    public int bar() {
        return foo();
    }
}
