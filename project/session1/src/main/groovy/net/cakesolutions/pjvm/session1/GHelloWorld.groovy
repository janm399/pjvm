package net.cakesolutions.pjvm.session1

/**
 * @author janmachacek
 */
class GHelloWorld {

  static def main(String[] args) {
    println("Hello, world")

    // from Groovy to:
    // * Java
    println(new JGreeter().greeting())	// OK
    // * Scala
    // println(new SGreeter().greeting())	// Scala follows Java, so doesn't work in the same module
    // * Clojure
    // same problem
  }

}
