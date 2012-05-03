#Pattern Matching
The Scala language includes pattern matching: you may think of it as very powerful case statement. Scala allows us to
match a value against specified patterns. Let's start with a trivial example, which will demonstrate the syntax, while
not departing from the features of Java too much.

    val x: Int = ... // some integer
    x match {
      case 1 => println("I")
      case 2 => println("II")
      ...
      case 10 => println("X")
    }

This is very similar to Java's ``case`` and ``switch`` keywords. The code you see in the previous listing follows similar
pattern. We take a value ``x`` and branch according to the values of ``x``. Just like any Scala expression, even ``match``
computes a value. This explains what happens if the value cannot be matched against any of the cases: Scala throws
``scala.MatchError``.

Let's take it up a step and match on types of instances: we have a function that takes parameter of ``Any`` type and we
want to have cases that depend on the type of the value.

    def fun(value: Any) {
      value match {
        case i: Int => println("Number " + i)
        case s: String => println("String " + s)
        case x => println("Some other thing " + x)
      }
    }

    fun(5)      // prints "Number 5"
    fun("5")    // prints "String 5"
    fun(true)   // prints "Some other thing true"

Before we continue, try to solve the following exercises:

---

###Exercises
**(1)** What is the type of the match expression in the example above?

**(2)** How would you prevent ``scala.MatchError`` from ever occurring?

---

To answer question (1), recall how Scala computes the type of expression by evaluating the common type of all code paths.
Taking both ``match`` expressions in the examples above, all code paths call the function ``println``. Because ``println``
returns ``()`` (``Unit``), the type of the ``match`` expressions is also ``()``. As we have discovered in the previous
chapters, ``()`` is not terribly valuable. It represents no usable value at all--similar to Java's ``void`` in that respect.
To find the answer to (2), take a look at ``case x => println("Some other thing " + x)`` case we added to the last ``match``
statement: ``x``'s type is ``Any`` (just like the type of ``value``) and it matches any value of ``value``.

Before we move on to containers of the same types, let's take a look at containers of various types--tuples. A tuple
can appear in the ``case`` pattern, and you can extract the elements of the tuple. Let's have a function that matches
its parameter against a 3-tuple and 2-tuple:

    def process(value: Any) = value match {
      case (s, i, b) =>
        // matches 3-tuples, s, i and b are Any
      case (i, j) =>
        // matches 2-tuples, i and j are Any
     }

The pattern only matches the size of the tuple, so applying ``process`` to ``(1, 2, 3)`` will match the first case; just
as ``("1", 2, false)`` or any other 3-tuple. Simlarly, the second case will match any tuple with two elements.

What if we want to verify not only the size of the tuple, but also the types it carries? We must tell Scala what types
we are expecting:

    def process(value: Any) = value match {
      case (s: String, i: Int, b: Boolean) =>
        // matches 3-tuples, s, i and b are of the appropriate types
      case (i: Int, j: Int) =>
        // matches 2-tuples, i and j are Any
     }

Now, if we apply ``process`` to ``(1, 2, true)``, we will get a ``MatchError``; we'll get the same error if we apply the
function to ``(2, true)``.

Before we take a look at matching containers, let's take a look at conditions in the patterns.

A lot of times, we are going to be matching not just scalar, but collections of things (more detailed discussion of collections
is in Session X). For now, let's write a function that computes the size of some sequence of values. How would we go about
it? Suppose that ``xs`` is some sequence of values. Its length is defined as:

- if ``xs`` is empty (``Nil``), then the length is ``0``
- if ``xs`` is not empty, it contains some first element ``h`` (called head), followed by the rest of the list ``t``
  (called tail). The length of the sequence is then 1 + length of ``t``

Right. Let's try writing the code:

    def size(xs: Seq[_]): Int = xs match {
      case (h::t) => 1 + size(t)
      case Nil => 0
    }

    size(List(1, 2))    // computes 2

Obviously, one could say. But how did it work? The first application of the ``size`` function passes in ``List(1, 2)``
as the value of ``xs``. We match that against the pattern ``case (h::t)``, which succeeds; ``h`` becomes ``1`` and ``t``
becomes ``List(2)``. (The first element of the list is the number ``1``, the tail is the ``List(2)``.) The body of the
``case`` is ``1 + size(t)``. In the recursive call, ``xs`` is ``List(2)``. The first patern ``case (h::t)`` matches again,
setting ``h`` to ``2`` and ``t`` to ``Nil`` (empty list). We call ``size`` again, passing in ``Nil`` as the ``xs`` parameter.
The first ``case`` does not match, but the second case does, which computes ``0``. So, the result is ``1``+ ``1`` + ``0``.

---

###Remark
Curious readers may have noticed that the ``size`` function we have written is recursive, but it will never run out of
stack space! The Scala compiler performs tail call optimisation, which means that the compiled form of ``size`` is not
recursive. See [ProgInScala] for details.

---

##Case classes
Scala includes ``case class``es, which not only get automatically generated ``equals`` and ``hashCode`` (from the values
in the first constructor parameter list), but as their name suggests, are particularly suited to be used in the ``case``
statements.

A brilliantly useful ``case class`` is ``Some[+A]``, a subtype of ``Option[A]``, which is Scala's way of dealing with
optional values. Let's have a motivational example in Java. We will have a map of values and we will want to find out
if the map contains the value we're looking for.

    Map<Integer, Character> map = new HashMap<Integer, Character>();
    map.put(1, 'A');
    map.put(2, 'B');
    map.put(3, 'C');

    Character value = map.get(4);
    if (value == null) {
        // value is not in the map!
    }

This is a favourite bug in Java programs! The ``get`` method in ``java.util.Map`` does indeed return ``null`` if the key
is not in the map, but what if the key exists in the map and the value for that key is indeed ``null``? The correct Java
code should be

    map.put(4, null);
    if (map.containsKey(4)) {
        Character c = map.get(4);
    }

The ``get`` method of ``java.util.Map`` uses (confusingly!) ``null`` as marker for "not there", but we cannot tell the
difference between "not there" and "there, but with value ``null``". Scala's approach is different: the ``apply`` operation
in Scala's ``Map`` returns ``Option[V]``. There are two possible instances of the ``Option[V]`` class. Either ``Some[+V]``
or ``None``, which lets us write much more readable code. (For more details of Scala's collections, refer to Session X;
for details of +V, refer to Session Y.)

    val map = Map(1 -> 'A', 2 -> 'B', ...)
    map(4) match {
      case Some(v) =>
        // the key 4 was in the map and v is the value with that key
      case None =>
        // the key 4 was not in the map
    }

Just like ``Some[+A]`` and ``None``, you can create your own case classes and use them in your pattern matching expresions.

    abstract case class Emailable() {
      def to: String
    }
    case class Address(line1: String, line2: String, line3: String, postCode: String)
    case class Recipient(name: String, to: String, address: Address) extends Emailable()
    case class Sender(name: String, to: String, address: Address) extends Emailable()
    case class Customer(email: String)

    def email(to: Any) {
      to match {
        case Customer(email) =>
          // email is now a val of type String
          doTheEmailing(email)
        case e@Emailable() =>
          // e is now a val of type Emailable
          e.to
      }
    }

    val jan = Recipient("Jan Machacek", "janm@cakesolutions.net",
                Address("Cake Solutions", "Magdalen Centre", "Oxford Science Park", "OX4 4GA"))
    val ani = Sender("Anirvan Chakraborty", "anirvanc@cakesolutions.net",
                Address("Cake Solutions", "Magdalen Centre", "Oxford Science Park", "OX4 4GA"))

    email(jan)
    email(ani)
    email(Customer("customer@gmail.com"))

So far, so good! We can use pattern matching for something even more complicated. Pattern matching almost sounds like
regular expressions. It turns out that Scala allows us to match values against regular expressions using the ``match``
keyword:

    val InternationalPhone = "(\\+\\d+\\w?)(.*)".r
    val UKMobilePhone = "(07.*)".r
    val UKLandlinePhone = "(0[1|2].*)".r

    val phoneNumber = ... // somehow get the number
    phoneNumber match {
      case InternationalPhone(countryCode, number) => println("International " + countryCode + " and " + number)
      case UKMobilePhone(number) => println("Mobile" + number)
      case UKLandlinePhone(number) => println("Landline " + number)
      case number => println("Unknown phone number " + number)
    }

Amazing! But what's even more amazing is that the regular expression support is not part of the Scala language! Scala only
provides the machinery of pattern matching, it says nothing about the work it does. But how can we turn a ``String``
that represents the regular expression into an instance of some ``case class``--and even if we could, how could the
Scala compiler know how many groups there are in the regular expression? (Remember, Scala compiler knows nothing about
regular expressions!) There is a special functions called ``unapply`` and ``unapplySeq`` that the compiler calls when it
unapplies an instance to its components.