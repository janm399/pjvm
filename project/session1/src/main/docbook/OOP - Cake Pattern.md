#Functional composition
Scala is a scalable language; scalable in this context means that it allows us to deal with the complexity of our systems.
A good object-oriented approach to dealing with complexity in systems is to create small, sharply defined components. These
components contain the functionality that we need. This is good!

    public interface PasswordCodec {
      String encode(String password);
    }

    public interface Notifier {
      void notify(Address address, String message);
    }

    public class PlainPasswordCodec implements PasswordCodec {
      ...
    }

    public class EmailNotifier implements Notifier {
      ...
    }

This is how we usually deal with complexity in our code. We have identified two distinct components ``PasswordCodec`` and
``Notifier``: these components contain the functionality that we want to use in the rest of our system. Typically, we'd write

    public class UserService {
      private PasswordCodec passwordCodec;
      private Notifier notifier;

      public void register(User user) {
        user.setPassword(this.passwordCodec.encode(user.getPassword()));

        this.notifier.notify(new Address(user), "Thanks for registering");
      }
    }

We have the sharply defined components and we used the functionality within those components in other components. The
trouble is in creating these components. This has made various dependency injection frameworks shine. Frameworks like
the Spring Framework take care of instantiating the components and satisfying the dependencies between teh components.
We could certainly take the same approach in Scala, turning the interfaces into traits, and sprinkling the result with
some Spring Framework:

    trait PasswordCodec {
      def encode(password: String): String
    }

    trait Notifier {
      def notify(address: Address, message: String): Unit
    }

    class PlainPasswordCodec extends PasswordCodec {
      ...
    }

    class EmailNotifier extends Notifier {
      ...
    }

    @Service
    class UserService @Autowired() (passwordCodec: PasswordCodec, notifier: Notifier) {

      def register(user: User) {
        user.setPassword(passwordCodec.encode(user.getPassword))

        notifier.notify(new Address(user), "Thanks for registering")
      }
    }

Certainly, this would work. Unfortunately, this approach is far too complex. Recall that we're not interested in the
``Notifier`` and ``PasswordCodec`` components, we're interested in the functions within them. So, it feels wrong to require
the components to be instances. Instead, let's define the ``Notifier`` and ``PasswordCodec`` components to be simply
"containers of functions"; components that we cannot instantiate, but that we can include in wherever we wish to mixin
their functionality. Make them ``trait``s!

    trait PasswordCodec {
      def encode(password: String): String
    }

    trait Notifier {
      def notify(address: Address, message: String): Unit
    }

    trait PlainPasswordCodec extends PasswordCodec {           (1)
      ...
    }

    trait EmailNotifier extends Notifier {                     (2)
      ...
    }

The only change we made is that we turned the ``PlainPasswordCodec`` and ``EmailNotifier`` into ``trait``s (1) and (2).
We now want to be able to specify that the ``UserService`` requires the functions of the traits to be mixed in. We do so
by using the self-type annotation:

    class UserService {
      this: Notifier with PasswordCodec =>                     (1)

      def register(user: User) {
        user.setPassword(encode(user.getPassword))             (2)

        notify(new Address(user), "Thanks for registering")    (3)
      }
    }

The self-type annotation (1) essentially says, "an instance of the ``UserService`` needs to be created with the
functionality defined in the ``Notifier`` and ``PasswordCodec`` traits:

    val s = new UserService                                             (1)
    val s = new UserService with PlainPasswordCodec                     (2)
    val s = new UserService with PlainPasswordCodec with EmailNotifier  (3)

Line (1) will not compile: the self-type annotation in ``UserService`` clearly says that instances of the ``UserService``
must mix in the functionality defined in the ``Notifier`` and ``PasswordCodec`` traits. Writing just ``new UserService``
does not and so it does not compile. Line (2) does not compile either: we mix in the functionality of the ``PasswordCodec``,
but we have forgotten the functionality of the ``Notifier`` trait. Finally, line (3) works as expected: the instance
``s`` is a ``UserService`` with the ``notify`` function coming from the ``EmailNotifier`` and the ``encode`` function
mixed in from the ``PlainPasswordCodec`` trait.

---
###Exercise
**(1)** Identify two areas in your existing Java code that would benefit from functional composition. Rewrite the code
using traits.

**(2)** Outline approaches to make the functionally-composed ``UserService`` a Spring-managed bean. *(Hint: you cannot
use ``@Service`` or ``<bean class="...UserService"/>`` because the functional composition would not be satisfied.)*

---

##Controlling behaviour using functional composition
The example with encoding passwords and sending notifications began to show the the power of traits and functional
composition. Imagine that you had a component whose functionality you needed to control (perhaps you had one implementation
for your tests and one for your production environment). Let's construct a component that transforms objects from some
input format into some output format.

    trait Serializer[N, F] {
      def serialize(native: N): F

      def deserialize(foreign: F): N
    }

    class NoopSerializer implements Serializer[Any, Any] {
      def serialize(native: Any): Any = native

      def deserialize(foreign: Any): Any = foreign
    }

