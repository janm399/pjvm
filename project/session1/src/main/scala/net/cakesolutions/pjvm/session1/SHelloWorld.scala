package net.cakesolutions.pjvm.session1

/**
 * @author janmachacek
 */
object SHelloWorld {

  def main(args: Array[String]) {
    println("Hello, world")

    // from Scala to:
    // * Java
    println(new JGreeter().greeting())
    // * Groovy
    println(new GGreeter().greeting())
    // * Clojure
    // same problem as Java
  }

}
