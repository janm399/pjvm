#Object-oriented programming in Scala

A first glance at the Scala language shows the usual ``class`` keyword. This works exactly like classes in the Java
language; just like classes in Java, classes in Scala can be abstract. Scala includes with the usual access modifiers:
``private`` and ``protected``. Scala classes cannot have static members, to do so, one has to use Scala's singleton or
``object``. Finally, Scala uses mixin inheritance though ``trait``s, which are similar to Java interfaces. In this
chapter, we'll explore the concepts of Scala OOP.

##Classes
Classes in Scala will be familiar to all Java developers. They start with an access modifier, followed by the ``class``
keyword, followed by the class name. Then comes the constructor parameters, inheritance clauses, optional curly brace and
then the methods. Easy!

    class Worker {               (1)

      def work() {               (2)
        println("Working")
      }

    }

    val w = new Worker           (3)
    w.work()                     (4)

Well, this is like Java. The class ``Worker`` (1) is ``public``, which is the default access modifier if you do not specify any
access modifier; it contains the method (procedure in Scala speak) called ``work`` (2), with no parameters, returning ``()``.
The procedure ``work`` is public, too. We can then instantiate the class ``Worker`` and bind the instance to a variable
``w``. As you would expect, we can then call the procedure ``work()`` (4).

Moving on, let's add a field.

    class Worker {
      var message = "Working"       (1)

      def work() {
        println(message)
      }
    }

    val w = new Worker
    w.work()
    w.message = "New message"       (2)
    w.work()

As Java programmers, you might be worried about this code: it seems that line (1) defines a publicly accessible field
and that we're writing to it directly on line (2). Fortunately, that's not the case. Scala does not **ever** direct
field access. It generates getters and setters (in case of ``var``s) and getters only (in case of ``val``s); the access
modifiers of the generated getters and setters share the access modifiers of the field. So, in the code above, Scala
generated a (public) getter and setter for the ``message`` field and it automatically called the setter on line (2).

Let's add a constructor. Constructors in Scala follow slightly unusual syntax, but more importantly, use different semantics.
Scala discourages logic in constructor; Scala constructors should only be used to set fields:

    class Worker (message: String) {

      def work() {
        println(message)
      }
    }

    val w = new Worker("Hi!")
    w.work()

The class ``Worker`` with a constructor with one parameter of type ``String`` defines field ``message`` that we can use
in the ``work`` procedure, but that is inaccessible from outside. Scala's constructors also allow you to specify that the
parameter(s) should become fields, specifying their access modifiers and specifying whether the fields should be ``val``s
or ``var``s:

    class Worker (val message: String) { ... }

    val w = new Worker("Hi!")
    w.message  // calls the [public] getter

Following the similar pattern, we can write:

    class Worker(var message: String) { ... }  // defines the publicly accessible message field with getter and setter
    class Worker(val message: String) { ... }  // defines the publicly accessible message field with getter

    class Worker(protected var message: String) { ... }  // defines the protected message field with getter and setter
    class Worker(protected val message: String) { ... }  // defines the protected message field with getter

    class Worker(val message: String, pad: String) { ... }  // defines the publicly accessible message field with getter and
                                                            // inaccessible field pad

Excellent! This saves a lot of typing (or generating in your favourite Java IDE). But there's one more thing. Scala uses
strange names for the getters and setters it generates. This works perfectly well in other Scala code, but the names do
not follow the typical Java Beans naming. If we want to do that, we need to use the ``@BeanProperty`` annotation on the
fields (or constructor parameters that will become fields).

    class Worker (@BeanProperty var message: String) { ... }

    val w = new Worker("foo")

    w.setMessage("new msg")
    w.getMessage()

As you can see, the ``@BeanProperty`` annotation on the constructor parameter will generate publicly accessible getter and
setter for the underlying ``message`` field; the names for the getter and setter will now follow the Java Beans naming
convention. Thus, the class ``Worker`` now includes the methods ``def setMessage(m: String): Unit`` and
``def getMessage(): String``. (Or, in Java speak, ``public void setMessage(m: String) { }`` and ``public String getMessage()``.)
Naturally, we can use the ``@BeanProperty`` annotation on the field, which will generate the getters (and setters, if
the field is ``var``) using the Java Beans naming convention.

---

###Exercise
**(1)** Use Scala to write typical JPA-annotated entity. Use the JPA annotations ``@Entity``, on the class and
``@Id``, ``@GeneratedValue`` on the identifier, and any other JPA annotaions you see if. Ensure that the class fits the
Java Beans naming.

---

Turning to access modifiers, it Scala allows you to specify ``private`` and ``protected``. They work like their Java
counterparts: if a field or a method is ``private``, it is accessible only from the class that defines it; if it is
``protected``, it is accessible from the class that defines it and all its subclasses. Both modifiers (and the default
``public`` one make the member accessible in all classes contained within the defining class).

It would seem that Scala is missing the package access modifier--the modifier that is applied to the Java members if you
do not specify any access modifier. Such members would then be accessible only to other classes within the same package.
Scala offers more flexible approach of parametrising the scope of the ``private`` or ``protected`` modifiers with the
class or package name. So, in Scala, you can have a member that is ``private``, within a scope of a package, for example.
(This is equivalent to Java's package protected access modifier.)

    package net.cakesolutions.practicalscala.session1.oop

    private[oop] class PackageWorker {
    }

This defines a class called ``PackageWorker`` that is accessible only to classes in the ``net.cakesolutions.practicalscala.session1.oop``
package. Attempting to access this class from outside the ``...oop`` package will result in a compile error. Naturally,
you may specify the access modifier on method level, too:

    package net.cakesolutions.practicalscala.session1.oop

    class ConfigurableWorker {

      protected def configure() {}    (1)

      private[oop] def work() {       (2)
        println("Working")
      }

    }

The procedure ``configure()`` in ``ConfigurableWorker`` is accessible only in the subclasses of ``ConfigurableWorker``;
the procedure ``work()`` is accessible from other members within the ``net.cakesolutions.practicalscala.oop`` package.
The parameters of the ``private`` and ``protected`` modifiers allow you to create scopes for your classes, functions and
procedures. As with Java, I encourage you to be as strict as possible with your access modifiers. In other words, do not
allow members to be public needlessly.

##Inheritance
Inheritance in Scala works just like inheritance in Java. A class can extend exactly one other class; it may override
all non-final accessible methods. Scala classes can be abstract, too: an abstract class contains a function or procedure
with no body and is marked with the ``abstract`` keyword.

    abstract class BaseWorker {
      protected def getMessage: String

      protected def getPrefix = ""

      final def work() {
        println(getMessage)
      }
    }

    class HappyWorker extends BaseWorker {
      override protected def getPrefix = " "
      protected def getMessage = ":)"
    }

    class SadWorker extends BaseWorker {
      override protected def getPrefix = "  "
      protected def getMessage = ":("
    }

This is no surprise to seasoned Java developers. We can define an abstract class and extend it in the implementation classes.
Scala is slightly more strict with overrides--it requires you to specify the ``override`` keyword. Omitting the ``override``
keyword causes an error; so does specifying the ``override`` keyword on a procedure or function that's not actually overriding
any inherited method.

Scala includes single implementation inheritance: in other words, we can extend only a single class. But Scala brings
mixin inheritance, which can feel like multiple implementation inheritance. In fact, it is a multiple interface inheritance,
but the interfaces can include optional implementations. You may find it useful to imagine that the default implementation
gets copied-and-pasted into the concrete class that mixes in the interface (``trait`` in Scala speak).

##Traits
After the first brief introduction to Scala's classes, which introduced slight nuances between the Java and Scala code,
traits are what makes Scala scalable.




