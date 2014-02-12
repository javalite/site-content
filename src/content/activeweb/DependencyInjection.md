Title: Dependency injection at run time and during testing.

-   [Introduction](#Introduction)
-   [Creation of Guice module](#Creation_of_Guice_module)
-   [Injection of module into the application](#Injection_of_module_into_the_application)
-   [Consumption of injected dependencies](#Consumption_of_injected_dependencies)
-   [Mocking and testing](#Mocking_and_testing)

Introduction
============

Dependency injection is an integral part of a typical Java application. ActiveWeb supports seamless integration with [Google Guice](http://code.google.com/p/google-guice/).

At the heart of a Google Guice DI, there is a concept of a module.

Creation of Guice module
========================

Lets say we have a simple interface Greeter:

~~~~ {.prettyprint}
public interface Greeter {
    String greet();
}
~~~~

and implementation of this interface:

~~~~ {.prettyprint}
public class GreeterImpl implements Greeter{    
    public String greet() {
        return "Hello from real greeter";        
    }
}
~~~~

We can then create a new Guice module:

~~~~ {.prettyprint}
public class GreeterModule extends AbstractModule {
    protected void configure() {
        bind(Greeter.class).to(GreeterImpl.class).asEagerSingleton();
    }
}
~~~~

In this module, we are binding a `GreeterImpl` to `Greater` interface as a singleton. You can call `bind()` method many times, setting up your object graph, but for this primitive example, we are using it only once.

Injection of module into the application
========================================

The injection of a Guice module is executed as one line of code inside `AppBootstrap` class, like so:

~~~~ {.prettyprint}
public class AppBootstrap extends Bootstrap {
    public void init(AppContext context) {       
        setInjector(Guice.createInjector(new GreeterModule()));
    }
}
~~~~

The `Guice.createInjector(..)` takes a varargs, meaning you can inject multiple modules at once.

Consumption of injected dependencies
====================================

Whenever you need a service inside a controller, you will use an `@Inject` annotation provided by Google Guice:

~~~~ {.prettyprint}
1. public class HelloController extends AppController {
2.     @Inject
3.     private Greeter greeter;
4. 
5.     public void index(){
6.         view("message", greeter.greet());
7.     }
8. }
~~~~

The `greeter` (line 3) method is set by the framework and injected an instance of a `GreeterImpl` onto the `HelloController` controller just before it executes an action. Once the controller has a reference to the service, it can consume it (line 6).

Where can you inject dependencies this way? There are three general application components that are injected dependencies:

-   **Controllers**
-   **Controller Filters**
-   **Custom Tags**

The technique is exactly the same, just add @Inject annotation that requires a service from a Guice module, and you can use it in code inside the component

Mocking and testing
===================

In testing, it is typical to replace real implementation of services with mocks. For explanation of mocks and stubs, follow this link [http://martinfowler.com/articles/mocksArentStubs.html](http://martinfowler.com/articles/mocksArentStubs.html).

Why would someone want to use a mock instead of a real implementation? Here are some reasons:

-   Real implementation submits a sensitive transaction (you do not want that during a build!)
-   Real implementation requires a network connection to external resource
-   Real implementation is really... slow.
-   Real implementation does not always cover all conditions in your code at a given time
-   many others

The bottom line, is this: when you use a real implementation, your test is not only testing your code, but also the implementation of a service, mushing everything together.

Now, lets create an mock service:

~~~~ {.prettyprint}
public class GreeterMock implements Greeter{
    public String greet() {
        return "Hello from " + this.getClass().toString();  
    }
}
~~~~

and a new mock module we will use in tests:

~~~~ {.prettyprint}
public class GreeterMockModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(Greeter.class).to(GreeterMock.class).asEagerSingleton();
    }
}
~~~~

Once we have this done, we can inject the mock module during the test and test our controller"

~~~~ {.prettyprint}
1. public class HelloControllerSpec extends ControllerSpec {
2.     @Before
3.     public void before(){
4.         setInjector(Guice.createInjector(new GreeterMockModule()));
5.     }
6.     @Test
7.     public void shouldInjectMockService(){
8. 
9.         request().get("index");
10.        a(assigns().get("message")).shouldBeEqual("Hello from class app.services.GreeterMock");
11.     }
12. }
~~~~

Lets examine this test line by line:

-   Line 4 - this is where we tell the test scaffolding which module to use, and we choose a mock module
-   Line 9 - we construct a GET request for HelloController, action "index" and execute the controller.
-   Line 10 - we inspect that the controller did in fact send `message` to a view, but the value of this message will be generated by the mock service.

