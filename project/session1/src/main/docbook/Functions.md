#Functions

Scala is object-*functional*, statically typed language. We have already covered OOP in the previous chapter, we must
now tackle the functional aspect in Scala. A function is (mathematically) a relation that takes some input and computes
some output. Let *f: A -> B* be a function and *b* = *f(a)* . *a* in **A**, *b* in **B**. This is not unusual. We say
that there is some operation called *f* that takes some *a* as its input and returns some *b* as its output.
We can have this in Java:

    B f(A a) {
        return new B(a);
    }

Lets drop in concrete types ``A`` and ``B``. Imagine that ``A`` is ``int`` and ``B`` is ``String``, for example. This
gives us a different method:

    String f(int a) {
        return String.valueOf(a);
    }

We can translate the Java code to use Scala's syntax and have

    def f(a: Int): String = {
      String.valueOf(a)
    }

In Scala, we can remove a lot of the "noise": Scala compiler will infer the return type of the method by examining all
values the function returns, making the ``: String`` return type declaration redundant. Furthermore, if the function is
one-liner, we do not need to use the braces ``{`` and ``}``. So, the new Scala way of writing the same function is

    def f(a: Int) = String.valueOf(a)

Regardless of the programming language, the functions we have declared are also functions in mathematical sense. In
mathematics, for a relation to be a function, the following must hold. Let *b1* = *f(a1)*, *b2* = *f(a2)* .
*a1*, *a2* in **A**, *b1*, *b2* in **B**; then if *b1* = *b2* => *a1* = *a2*. In human-speak, a relation is a function if
it *always* returns the same value for the same inputs. Our function ``f`` just so happens to be a function, but neither
Java nor Scala check this.

##Scala functions

If we can write functions in Java and if Java and Scala don't care about the mathematical notion of function, then what
makes Scala a functional language? *In Scala, a function is a value.* Because it is a value, it can be bound to a variable,
passed as a parameter of another function and returned from a function. Let's rename our function ``f`` to something more
meaningful and then use it in another function.

    def toString(a: Int) = String.valueOf(a)
    def concat(a1: Int, a2: Int) = toString(a1) + toString(a2)

    concat(1, 3) // evaluates to "13"

The ``toString`` function is the renamed ``f`` that converts an ``Int`` to ``String``. We then have another function,
``concat`` that concatenates the ``String`` representation of the numbers. What if you want to change the way the
numbers are converted to strings? You could change to ``toString`` function, but what if we would like to be able to
"tune" the way the numbers are converted at the point of using the ``concat`` function? In some imaginary Scala source
file, we would like to ensure that:

- On line 50, result of ``concat(1, 3)`` should be "13"
- On line 55, result of ``concat(1, 3)`` should be "0103"
- On line 99, result of ``concat(1, 3)`` should be "001003"

We want to re-use the logic in the ``concat`` function, we don't want to be creating new ``concat`` function and new
``toString`` function for line 50, 55 and 99! Let's take a different approach and add one more parameter to the ``concat``
function. The parameter will be the conversion function that takes ``Int`` as its parameter and returns a ``String``.
The syntax follows the text above. Parameter ``f``'s type is ``Int => String``. Whatever precedes the ``=>`` symbol are
the parameters of the function, what follows the ``=>`` symbol is the return type. So, the new definition and body of the
``concat`` function is

    def concat(a1: Int, a2: Int, f: Int => String) = f(a1) + f(a2)

Now we are ready to use the new ``concat`` function. We must supply its parameters. The first two ``Int``s are trivial,
but what about the third parameter? How do we construct *function from ``Int`` to ``String``*? Actually, we already have
it. It's our ``toString`` function!

    concat(1, 3, toString) // evaluates to "13"

We can now add more conversion functions, but keep the same logic in ``concat``. Returning to the same imaginary Scala
source file, we can now have:

    def toString2(a: Int) = new DecimalFormat("00").format(a)
    def toString3(a: Int) = new DecimalFormat("000").format(a)

- On line 50, we write ``concat(1, 3, toString)``
- On line 55, we write ``concat(1, 3, toString2)``
- On line 99, we write ``concat(1, 3, toString3)``

In Scala, we can even write "anonymous" functions, without having to add more ``def``s to our Scala classes. The syntax
is simply ``(param1: Type1, param2: Type2, ..., paramn: Typen) => { body }``. We could now write
``concat(1, 3, (i: Int) => { String.valueOf(i) })`` and get ``"13"``.

We can see that the body of the function is a single statement, so we can drop the curly braces, writing just
``convert(1, 3, (i: Int) => String.valueOf(i))`` and get the same result. Now, imagine that we have a function and,
within that function, we would like to use the same conversion function. We could write the anonymous function every time
we apply the ``convert`` function, but that feels like too much duplication. Recall again that in Scala, *function is
a value*. We can bind the function to a variable:

    def complexFunction = {
      val conv: Int => String = (i: Int) => "#" + String.valueOf(i)

      concat(1, 3, conv) + concat(10, 13, conv) + concat(100, 113, conv)
    }

Notice in particular the ``conv`` variable. Its type is function that takes ``Int`` as its parameter and returns ``String``.
The variable is then bound to a value ``(i: Int) => "#" + String.valueOf(i)``. Let's dissect the ``conv`` variable definition
again:

- ``val conv`` defines write-once variable called ``conv``
- ``: Int => String`` defines the type of the variable ``conv`` to be a function (because of the ``=>`` symbol) from
  ``Int`` to ``String``
- `` = `` assigns the variable to a value
- ``(i: Int)`` is the parameter and its type
- `` => `` separates the parameters and the body
- ``"#" + String.valueOf(i)`` is the function body

A lot of the information in ``val conv: Int => String = (i: Int) => "#" + String.valueOf(i)`` is duplicated. Let's remove
the duplicate information and have the Scala compiler infer it.

    val conv: Int => String = (i: Int) => { "#" + String.valueOf(i) }   (1)
    val conv: Int => String = (i: Int) =>   "#" + String.valueOf(i)     (2)
    val conv: Int => String =  i       =>   "#" + String.valueOf(i)     (3)
    val conv                = (i: Int) =>   "#" + String.valueOf(i)     (4)

All four ``conv`` variables are equivalent! On line (1), we give the fullest declaration, we hold the compiler by the
hand and spell out all the necessary types and we wrap the function body in ``{`` and ``}``. On line (2), we remove the
the ``{`` and ``}``, because the body of the function is a single expression. Moving on, we can take advantage of Scala's
type inference and replace ``(i: Int)`` with just ``i`` on line (3). Obviously: if the type of the variable is
``Int => String``, then the type of ``i`` must be ``Int``! Finally, we can remove the entire type declaration for the variable
``conv`` and simply write ``val conv = (i: Int) => "#" + String.valueOf(i)`` on line (4). The compiler will infer the type
of the variable to be a function (because it sees the ``=>`` in the value); and it is a function that takes ``Int`` as
its parameter (because we say so using ``(i: Int)``) and it returns a ``String`` (concatenating a ``String`` "#" with
another ``String`` must be a ``String``). Notice that writing ``val conv = i => "#" + String.valueOf(i)`` is insufficient:
the Scala compiler cannot infer the type the variable ``conv``, because it cannot determine the type of the parameter ``i``.

Let's complete our introduction to Scala's function by mentioning some of the syntactical sugar. This sugar allows us to
eliminate some of the syntactical noise that would otherwise plague our code. The first convenience is that if a function's
parameter list includes only one element whose type is a function, we may simply write the function body using the curly
brackets.

Also, a sequence of ``case`` statements also forms a function! Let's write a very limited function that converts an ``Int``
into its ``String`` roman representation.

    show(1, {
      case 1 => "I"
      case 2 => "II"
      case 3 => "III"
      case 4 => "IV"
      case 5 => "V"
      case 6 => "VI"
      case 7 => "VII"
      case 8 => "VIII"
      case 9 => "IX"
      case 10 => "X"
    })

The expression ``{ case 1 => ...; case 2 => ... }`` forms a function with one parameter whose type is ``Int => String``.

---

###Exercises

In ``net.cakesolutions.practicalscala.session1.fp``

**(1)** Demonstrate that you can apply the functional programming principles by completing the ``Functions`` object by:

- adding the ``main(args: Array[String])`` function
- adding the ``toString`` function that converts an ``Int`` to ``String``
- adding the ``concat`` function that takes two ``Int``s and a ``Int => String`` and returns the string concatenation
  of the numbers
- implement the three different ``toString`` functions, use them in the ``concat`` function
- use anonymous function in the application of the ``concat`` function
- create the ``complexFunction`` that re-uses the same ``conv`` variable of type ``Int => String``
- throughout the exercise, print the concatenated ``String``s

The body of the ``main`` function should contain

    concat(1, 3, toString)
    concat(1, 3, toString2)
    concat(1, 3, toString3)

    concat(1, 3, anonymous-fun)

    complexFunction

**(2)** Implement tests that verify that the functions in ``Functions`` work as expected. For now, use JUnit, but write
the tests as Scala ``FunctionsTest`` class.

**(3)** Write down at least two situations where you would apply functions in traditional Java code. Outline the code
showing the functions as ``def``s, as ``val``s and as anonymous functions. For the ``val``s, show at least two ways of
taking advantage of Scala's type inference.
*(Hint: think about the numerous callbacks in Swing, Spring and many others; use the type ``Any``, don't worry about
"variable" types just yet.)*

---

##Polymorphic functions
In the previous example, we had functions that take ``Int`` and return ``String``; and these functions were indeed very
useful. However, in some situations, having the concrete types in the functions is too restrictive. The concrete types
(``Int``s) have probably bothered you in the definition of the ``concat`` function we defined earlier. It would actually
be useful to be able to allow the compiler to deal with the type "replacements". In Scala, we can specify type variables
in the definitions of the function:

    def concat[A](a1: A, a2: A, f: A => String) = f(a1) + f(a2)

We can now use the function ``concat`` on any type, not just ``Int``s. We can now write ``concat("a", "b", (s: String) => s)``, which
will compute "ab"; similarly ``concat(1, 2, (i: Int) => "#" + i)`` computes ``"#1#2"``. Now, let's see how one might use
the polymorphic functions. When completing the exercises in the previous section, you will have no doubt encountered
problems in expressing functions that return varying types. You had to fall back to the most general ``Object`` or, if
you embraced Scala's types fully, ``Any``. Scala is strongly and statically typed language, it encourages you to write code
that refers to specific types rather than using the fallback ``Any``. Let's take a typical Java code that would be a good
candidate for polymorphic functions.

    public interface ConnectionCallback<T> {
        T doInConnection(Connection connection) throws SQLException, DataAccessException
    }

    public class JdbcTemplate {

        public <T> T execute(ConnectionCallback<T> callback) {
            // ultimately
            return callback.doInConnection(the-connection);
        }

    }

Here, we use Java generics to allow us to avoid type casts. The compiler also verifies that the type variable ``T`` is
consistently bound throughout the code. Unfortunately, Java's syntax forces us to write rather unruly code:

    JdbcTemplate t = ...
    Integer count = t.execute(new ConnectionCallback<Integer>() {
        @Override
        public Integer doInConnection(Connection connection) throws SQLException, DataAccessException {
            // execute something like "select count(*) from table
            return 0;
        }
    });

Oh, the humanity! The valuable code (the SQL statement) is lost in the noise of Java's syntax. Let's take the answer to the
thrid exercise question and add type variables and invent Scala version of the ``JdbcTemplate``.

    class JdbcTemplate {

      def execute[T](callback: Connection => T) = {
        // ultimately
        callback(the-connection)
      }
    }

The use of the new ``JdbcTemplate`` is now far easier: we avoid the syntactical noise of Java and we can now refer to concrete
types throughout our code:

    val t = new JdbcTemplate()
    val count = t.execute(c => /* something like "select count(*) from table */ 0)   (1)

In the code above the ``execute`` function returns ``Int``, because its parameter is a function ``Connection => Int``. Also
recall from the previous section that if the parameter list of a function contains exactly one parameter and if that paremter
is a function, we may omit the braces ``(`` and ``)`` and start with ``{`` and ``}``, specifying the function directly.
Applying this to our ``JdbcTemplate.execute`` function, we may write:

    val count = t.execute { c => /* select count(*) from table */ 0 }

Excellent! This approach give us the same type safety with a lot less syntactical noise; at the same time, the parameter
of the ``execute`` function remains a function. This "callback" function can be bound to a variable and re-used throughout
the code.

---

###Exercises

**(1)**

##Partially applied functions
Scala's notion of a function is very useful: it allows us to think about our code as sequence of composable operations.
Imagine now that you'd like to create a new operation by supplying some values to an existing operation, leaving the
remaining parameters to be specified when the newly created operation is applied. An example from real life might be the
operation ``add``, which takes two numbers and returns their sum. If we were to define the ``add`` function in Scala,
we'd write ``val add = (n1: Int, n2: Int) => n1 + n2``. So, we have an operation ``add``, which adds two numbers together.
It is now easy to use this "template" and create another operation, called ``add5``. You intuitively understand what the
new operation will be. It is a function ``add5``, with one of its parameters set to ``5``, taking the remaining parameter.

    val add = (n1: Int, n2: Int) => n1 + n2

We can now use the ``add`` function to create a new function, called ``add5``, which sets the first parameter of the
function ``add`` to ``5``, giving a function that takes the remaining parameter. We could write ``val add5 = (n2: Int) => add(5, n2)``,
or we could get the compiler to infer the code for us and write:

    val add5 = add(5, _: Int)

Imagine for a moment that the underscore ``_`` is a valid identifier. The Scala compiler will compute the value of ``add5``
as ``add5 = (_: Int) => add(5, _)``. You can partially apply functions with any number of parameters:

    val add(n1: Int, n2: Int, n3: Int, n4: Int, n5: Int) = n1 + n2 + n3 + n4 + n5
    val add1And2And3 = add(1, _: Int, 2, 3, _: Int)

    add1And2And3(4, 5)  // computes 15

So, where would you use partially applied functions? Well, imagine that the task is to implement a function for the
third parameter of our ``concat`` function that allows us to pad the converted ``Int`` with some padding ``String``.
We would write

    def paddedToString(pad: String, i: Int) = pad + i

Now, the third parameter of the ``concat`` function is ``Int => String``. The function ``paddedToString`` would not fit:
it is ``(String, Int) => String``. We can partially apply it, specifying the first parameter. What will remain is the
function ``Int => String``, which can be supplied as the third parameter of the ``concat`` function, like this:

    concat(1, 2, paddedToString("   ", _: Int))

*Partial function application reduces the number of parameters of a function.* We turned the function ``(String, Int) => String``
into ``(Int) => String``. We reduced the number of parameters of the function.

---

###Exercises

**(1)** Partially apply another function that pads the number with different text on either side:
``paddedToString(before: String, n: Int, after: String) = before + n + after`` and use it in the ``concat`` function.


##Curried functions
So far, the functions we have created take only one parameter list. In Scala, we can defined curried functions, that is,
functions with more than one parameter lists. Let's start with a trivial example of a curried function. We will need
two parameter lists:

    def add(i: Int)(j: Int) = i + j
    add(1)(2)  // computes 3

As you can see, we can apply the function ``add`` by specifing both parameter lists. You can imagine the computation of
``add(1)(2)`` function to proceed as follows: ``add(1)`` returns a function with the remaining parameter list. Applying
the "returned" function to ``(2)`` produces the final result, namely the integer 3.

How could this be useful? Let's go back to our ``concat[A]`` function. Suppose that we wanted to be able to specify a number
of characters to pad around each added value. This is a common piece of logic, that we would like to re-use.

    def paddedToString(pad: String)(i: Int) = pad + i + pad

We can then use it in the third parameter of the ``concat`` function. Recall that we defined the function ``concat`` as
``concat(Int, Int, Int => String)``. We can give the first two numbers as 1 and 3, for example. The last parameter is a
function from ``Int`` to ``String``. The function ``Int => String`` is the function that is left from supplying the first
parameter list of the ``paddedToString`` function. We can try it out!

    convert(1, 2, paddedToString("   "))   (1)

    val convPad = paddedToString("   ")    (2)
    convert(1, 2, convPad)                 (3)

We can apply the function ``paddedToString`` to the first parameter list, which computes a function with the remaining
parameter list. What remains is a function with one parameter list; with one parameter of type ``Int`` and returning
``String``, which is exactly the function the type of the last parameter of the ``convert`` function. Naturally, we can
make the curried functions polymorphic, allowing us to write:

    def paddedToString[A](pad: String)(a: A) = pad + a + pad
    def convert[A](a1: A, a2: A, f: A => String) = f(a1) + f(a2)

    convert(1, 2, padded("|"))             (1)
    convert("1", "2", padded("*"))         (2)

The curried function ``paddedToString`` is polymorphic, working on any type ``A``. Notice that the type variable ``A``
propagates through the compilation. The compiler will compute and verify the type ``A``.

Let's define the curried function ``add`` as a variable. It is a curried function that, when applied to the first parameter
list computes a function with the remaining parameter list; applying that procudes the value. So, we have:

    val add = (i: Int) => (j: Int) => i + j



#Pure Functions

A function is (mathematically) a relation that takes some input and computes some output. Let *f: A -> B* be a function
and *b1* = *f(a1)*, *b2* = *f(a2)* . *a1*, *a2* in **A**, *b1*, *b2* in **B**. Then *b1* = *b2* => *a1* = *a2*.

Forgetting the cryptic mathematical notation for a while, a pure function in Scala is a function that returns exactly
the same value for the same input. In other words, for a function to be pure in Scala, that function must perform any
uncertain operation and it must return some useful value.

    def muggle1 {
      println("Please enter a value")
    }
    def muggle2(a: Int) = a * readLine().toInt
    def muggle3 = math.random

In the example above, we have ``muggle1``, ``muggle2`` and ``muggle3``. Recall the Scala nomenclature:

- ``muggle1`` is a *procedure*--a function returning ``Unit``
- ``muggle2(Int): Int`` and ``muggle3`` are *functions*

These functions (and the procedure) are regular Java methods. Imagine that