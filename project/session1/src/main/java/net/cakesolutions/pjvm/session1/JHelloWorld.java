package net.cakesolutions.pjvm.session1;

public class JHelloWorld {

    public static void main(String[] args) {
        System.out.println("Hello, world");

        // from Java to:
        // * Groovy
        // System.out.println( new GGreeter().greeting() ); // Java goes first, so no luck
        // * Scala
        // System.out.println( new SGreeter().greeting() ); // Java goes first, so no luck
        // * Clojure
        // System.out.println( new CGreeter.greeting() );	// Java goes first, so no luck
    }

}
