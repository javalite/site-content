<ol class=breadcrumb>
   <li><a href=/>JavaLite</a></li>
   <li><a href=/activeweb>ActiveWeb</a></li>
   <li class=active>Testing</li>
</ol>
<div class=page-header>
   <h1>Testing <small></small></h1>
</div>



ActiveWeb promotes [TDD/BDD](http://en.wikipedia.org/wiki/Test-driven_development) approach to testing of web applications.
At the heart, it uses [JUnit](http://www.junit.org) as a testing framework, but it provides a number of
test classes to be sub-classed for  various purposes. ActiveWeb allows to do a full test of any web functionality during a 
regular build. ActiveWeb provides a number of classes for testing.

All test classes have a suffix "Spec". This is a nod to [RSpec](http://rspec.info/), but
also a good practice - think of these not as tests that assert values, but as specs, or specifications of behavior.
The more you think of them as specifications/blueprints, the more you will think of writing
them before actual implementations.

> This page is not an exhaustive list of test APIs, but rather a directional guide and a set of how-to instructions

## JSpec

While ActiveWeb tests are written with the popular JUnit testing framework, traditionally expectations are written
with [JSpec](JSpec)


## DBSpec for database tests

`org.javalite.activeweb.DBSpec`  is a super-class for tests that require a database connection. It is integrated with
[database configuration](database_configuration) and will automatically open a corresponding database connection before
a test execution and close it after the test.

It is customary for ActiveWeb projects to use one database for testing and a different one for running the system locally
on a developers workstation. It makers it easy to preserve data in place in the "development" database, and still use
the full power of database CRUD access to your test database.

For example, you could have some user data in a development database which will allow you to log in, and perform other
operations, and yet you can run test logic against your test database, destroy and re-create any data in it, without
having to destroy your development database.

> Please, see more on environments and modes on [database configuration](database_configuration) page.

While DBSpec is usually used to test models, it can be used to test any code that require a database connection.
If you need to get a hold of that connection, you can use class Base from ActiveJDBC:

~~~~{.java}
java.sql.Connection connection = Base.connection();
~~~~


## Configuration

Database configuration is described on [database configuration](database_configuration) page.
DBSpec class will look for connections configured with a method `testing()`.

Example:

~~~~ {.java  }
public class DbConfig extends AbstractDBConfig {
    public void init(AppContext context) {
         environment("development").jndi("jdbc/kitchensink_development");
         environment("development").testing().jdbc("com.mysql.jdbc.Driver", "jdbc:mysql://localhost/kitchensink_development", "root", "****");
         environment("jenkins").testing().jdbc("com.mysql.jdbc.Driver", "jdbc:mysql://172.30.64.31/kitchensink_jenkins", "root", "****");
         environment("production").jndi("jdbc/kitchensink_production");
    }
}
~~~~

Specifically, line 4 configures a test connection to be used during the test.
In case you work with multiple databases, you can configure more than one test connection. DBSpec will open all connections
marked for tests.

## Transaction management

DBSpec will  start a transaction before the test and roll it back after the test, ensuring that you have:

1. integrity of data in your test
2. no conflicts of data in the database from one test to another

## Example of a DBSpec test

Lets say we are developing a blog, and we need to persist a post. A post will have title, content and author.
All these attributes are required. A test will look like this then:

~~~~ {.java}
public class PostSpec extends DBSpec {
    @Test
    public void shouldValidateRequiredAttributes() {
        Post post = new Post();
        a(post).shouldNotBe("valid");
        a(post.errors().get("author")).shouldBeEqual("Author must be provided");
        post.set("title", "fake title", "author", "fake author", "content", "fake content");
        a(post).shouldBe("valid");
        post.save();
        a(post.getId()).shouldNotBeNull();
        a(Post.count()).shouldBeEqual(1);
    }
}
~~~~

Technically speaking you can use it for any test requiring a connection, but it also easy to use for Model tests.
Model tests are tests for ActiveJDBC models.

Here is an example of a model we are testing:

~~~~ {.java}
public class Post extends Model {
    static {
        validatePresenceOf("title", "content");
        validatePresenceOf("author").message("Author must be provided");
    }
}
~~~~

## ControllerSpec - test your controllers

`org.javalite.activeweb.ControllerSpec` is a super class for controller tests. This class is used by unit tests that
test a single controller. Controllers are tested by simulating a web request to a controller (no physical network is
involved, and no container initialized).

> All APIs available to controllers in `ControllerSpec` are also available to all other controller and integration specs

### Naming convention

> **Subclass naming convention**: subclass name must be made of two words: controller short class name and word "Spec".

Here is an example:  Jf there is a controller:

~~~~{.java}
package app.controllers;
public class GreeterController extends AppController {
   ///...
}
~~~~

then the spec will look like this:

~~~~{.java}
package app.controllers;
public class GreeterControllerSpec extends ControllerSpec {
 ///...
}
~~~~


Note that the package name is the same for spec as it is for controller, since ActiveWeb will use reflection to determine
the controller to be tested


### Sending HTTP requests from specs

~~~~{.java  }
public class HelloControllerSpec extends ControllerSpec {
    @Test
    public void shouldSendGetToIndex() {
        request().get("index");
     }
}
~~~~

In a snippet above on line 4, the method `request()` allows to simulate a call to a controller `HomeController`.
This line reads like this: *Send GET request to `HomeController`, action `index`*.
There are other methods for sending different HTTP methods:

* `post(action)`
* `put(action)`
* `delete(action)`


## Sending parameters with HTTP requests

~~~~{.java}
public class HelloControllerSpec extends ControllerSpec {
    @Test
    public void shouldSendParamsToIndex() {
        request().param("first_name", "John").param("last_name", "Deere").get("index");
        a(val("message")).shouldBeEqual("Hello, John Deere, welcome back");
    }
}
~~~~

This test is a little more complex, we are sending two parameters with the request, and also checking the value
controller assigned to a view.

The above example can be simplified with a use of a ` params()` method that takes an even number of names an values for parameters:

~~~~{.java}
public class HelloControllerSpec extends ControllerSpec {
    @Test
    public void shouldSendParamsToIndex() {
         request().params("first_name", "John", "last_name", "Deere").get("index");
         a(val("message")).shouldBeEqual("Hello, John Deere, welcome back");
    }
}
~~~~


## Generating views during testing

How many times you wished when developing a web application in Java to be able to generate a full HTML from the application
in test, as if the application was running? Well, with ActiveWeb you can finally do this by using the `integrateViews()` method.

Here is a modification on the previous example, but with the `integrateViews()`


~~~~{.java  }
public class HelloControllerSpec extends ControllerSpec {
    @Test
    public void shouldSendParamsToIndexAndGenerateHTML() {
        request().params("first_name", "John", "last_name", "Deere").integrateViews().get("index");
        a(responseContent()).shouldContain("<span class="greeting">Hello, John Deere, welcome back</span>");
    }
}
~~~~


Line 4 causes the framework to execute the controller, and pass all data from it to the view and the generate HTML as
in a normal application flow.

Method `responseContent()` simply returns entire HTML generated by the view. At which point, you can use variety of
technologies in Java to test its structure (easier if you stick to XHTML in your templates), as well as content.

We simply test on line 5 that there exists a span with specific content merged by template from data passed in from controller.

## Mocking and testing

Mocking and testing of services is related to the concept of Dependency Injection and is described in
[Dependency Injection](dependency_injection#mocking-and-testing) section.


## Posting binary content

Sometimes you need to test a case when binary data is POSTed to a web application. This can be easily tested with the
`content()` method:

~~~~{.java}
public class HelloControllerSpec extends ControllerSpec {
    @Test
    public void shouldSendBytes() {
        byte[] mybytes = ...
        request().content(mybytes).post("index");
        a(responseContent()).shouldBe("<message>success</message>");
    }
}
~~~~

## Uploading files

Simulating file upload can be done with the formItem() method:

~~~~{.java}
public class HelloControllerSpec extends ControllerSpec {
    @Test
    public void shouldUploadImageFile() {
        byte[] imagebytes = ...
        request().contentType("multipart/form-data").formItem("file.png", "image", true,  "applicaiton/png", imagebytes).post("upload");
        a(responseContent()).shouldContain("<message>success</message>");
    }
}
~~~~

Most methods chained after method `request()` are chained because they all return a special instance of `RequestBuilder`.
This allows to call the same method more than once, including `formItem()` to simulate uploading of multiple files.


## Working with sessions

The `session()`  method allows to setting objects into session before a
test and also used to inspect objects in session after some action (execution of a controller)

~~~~{.java}
public class LoginControllerSpec extends ControllerSpec {
   @Test
   public void shouldLoginByIdAndPassword() {
        request().params("id", "mmonroe", "password", "kennedy").post("index");
        a(session("user")).shouldNotBeNull();
  }
}
~~~~

Conversely, you could "login" by placing a User object into a session before executing a controller of interest.

## Working with cookies

Cookies can be sent with a response using a `cookie()` method:

~~~~{.java}
public class HelloControllerSpec extends ControllerSpec {
    @Test
    public void shouldCookie() {
        request().cookie(new Cookie("app_id", "12345")).get("index");
        a(cookie("last_access")).shouldNotBeNull();
    }
}
~~~~


In this spec, we are sending one cookie with the request, but also are checking that "HelloController" sent another
cookie to the client.


## Great for TDD

ActiveWeb controller specs allow for true TDD, since they do not have a compiler dependency on controllers.
You can describe full behavior of your controller before a controller class even exists. Simplest example:

~~~~{.java}
public GreeterControllerSpec extends ControllerSpec {
    @Test
    public void shouldRespondWithGreetingMessage() {
        request().get("index");
        a(responseCode()).shouldBeEqual(200);
        a(val("message")).shouldBeEqual("Hello, earthlings");
    }
}
~~~~

In a code snippet above, a request with HTTP GET method is simulated to the `GreeterController`, `index()` action.
Controller is expected to assign an object called "message" with value "Hello, earthlings" to a view.

It is easy to describe a controller behavior in a `ControllerSpec`, making it easy to practice real TDD.



## DBControllerSpec - test controllers with DB connection

`org.javalite.activeweb.DBCOntrollerSpec` class serves as a super class for controller tests requiring database
connections. In effect, this class combines the logic of `ControllerSpec` and `DBSpec`. When it comes to naming convention
of a controller to be tested, the functionality is identical that of `ControllerSpec`, but at the same time it will
open a connection to DB before test and close after (will also roll back transaction)

## IntegrationSpec - test multiple controllers together

While `ControllerSpec` and `DBControllerSpec` allow to test a single controller, the class `IntegrationSpec` allows
to write entire scenarios for testing multiple controllers.

Example:

~~~~{.java  }
public class SimpleSpec extends IntegrationSpec {
    @Test
    public void shouldNavigateToTwoControllers() {
        controller("home").get("index");
        a(statusCode()).shouldBeEqual(200);
        controller("greeter").param("name", "Bob").integrateViews().get("index");
        a(responseContent()).shouldContain("Our special greeting is extended to Bob");
    }
}
~~~~


Lets decompose code snippet:

* **Line 4**: a controller `HomeController` is executed with HTTP GET  request which is dispatched to its action `index()`
* **Line 5**: we verify that the response code of execution was 200
* **Line 6**: controller GreeterController's index() action is executed with HTTP GET and parameter name=Bob.
Additionally, we call method `integrateViews()` which will require the framework to execute the corresponding view
after controller, which will provide us with that view's output - usually HTML, but can be XML, Json, whatever
that view is producing.
* **Line 7**: we examine the content of the produced view output.

Note that we can run this code in the absence of both controllers (of course it will fail).

Lets write a `GreetingController` (as being the most "complicated" of the two):

~~~~{.java}
public void GreeterController extends AppController {
    public void index() {
        view("name", param("name"));
    }
}
~~~~

The corresponding view might look like:

~~~~{.html}
<span>Our special greeting is extended to ${name}</span>
~~~~

and will be located in file:

~~~~{.prettyprint}
/greeter/index.ftl
~~~~


## DBIntegrationSpec - combines IntegrationSpec and DBSpec

`org.javalite.activeweb.DBIntegrationSpec` class serves as a super class for controller integration tests requiring
database connections. In effect, this class combines the logic of IntegrationSpec and DBSpec. It will allow
to write scenarios to test multiple controllers, but at the same time it will open a connection to DB before the
test and will close after (will also roll back transaction).

## AppIntegrationSpec - bootstraps ControllerFilters into test

`org.javalite.activeweb.AppIntegrationSpec` is a class that will bootstrap entire application, complete with
ControllerFilters. The only difference of running your application under AppIntegrationSpec and running it live, is that
the `DBConnectionFilter` is disabled, and instead database connection is provided exactly the same way as in `DBSpec`,
`DBControllerSpec` or `DBIntegrationSpec`.

In other words, think of `AppIntegrationSpec` as the same with `DBIntegrationSpec`, but all filters will trigger as in
a real application.


> None of the IntegrationSpecs require  the same naming convention as `ControllerSpec` or `DBControllerSpec`.


## Testing Views

It is possible to test just a view template with ActiveWeb. There is a special class for that called ViewSpec.
Here is an example of a template to be tested:

Template file name `/person/show.ftl`:

~~~~{.prettyprint}
Name: ${name}
~~~~

The view test might look something like this:

~~~~{.java}
public class PersonSpec extends ViewSpec {
    @Test
    public void shouldRenderShow() {
        a(render("/person/show", "name", "John").shouldEqual("Name: John");
     }
}
~~~~

There is also a way to test for `<@content for>` output, inject mock or real services into custom tags, etc.
In other words, one can write very stringent tests for views independent of the rest of the application,
just as if views were first grade application components.

