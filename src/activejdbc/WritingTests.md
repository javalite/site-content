Title: How to write model tests

Introduction
============

Writing models' tests is easy. While you can use any Java technology to do this, the AvtiveJDBC project internally as well as [ActiveWeb](http://code.google.com/p/activeweb/) use a combination JUnit and [JSpec](JSpec). Test methods are also written in a style where a name of a method is a phrase reflecting the expectation of the test.

Call tests specifications
=========================

Usually in the Java world, people call tests ... tests. In the Ruby world they are called "specs", or specifications. This name fits better if you practice a true TDD, because you start thinking of them as specifications of behavior rather than testing of code after it is written. While the change is in the name, it might be enough to have a slight shift in mentality towards TDD.

Example process of writing a specification
==========================================

Lets write a simple model:

~~~~ {.prettyprint}
public class Person extends Model{}
~~~~

Usually, at this point it is time to write a test:

~~~~ {.prettyprint}
1. public class PersonSpec{
2.   @Test
3.   public void shouldValidatePresenceOfFirstNameAndLastName(){
4.      Person p = new Person();
5.      a(p).shouldNotBe("valid");
6.   }
7. }
~~~~

When the code above runs, the spec is in red because the class Person does not specify any validations yet, and therefore it is valid. This test will fail on line 5. However, our goal for this model is not be valid if it is missing first and last name attributes. In order to make it pass, you need to add validations to the model:

~~~~ {.prettyprint}
public class Person extends Model{
   validatePresenceOf("first_name", "last_name");
}
~~~~

When you re-run test, it will pass.

At this point, we need to add values for first and last name, as well as an expectation of a valid state of the model:

~~~~ {.prettyprint}
1. public class PersonSpec{
2.   @Test
3.   public void shouldValidatePresenceOfFirstNameAndLastName(){
4.      Person p = new Person();
5.      a(p).shouldNotBe("valid");
6.      p.set("first_name", "Homer");
7.      p.set("last_name", "Simpson");
8.      a(p).shouldBe("valid");
9.   }
10.}
~~~~

At line 4, the test passes as before because the model still does not have the first and last names, but on lines 6 and 7, we add these, and on line 8 we expect the model to finally be valid.

We now have a complete specification of behavior and at the same time we built an implementation. It is typical to switch from spec to a model and back a few times until all behavior is documented in the spec and implementation is complete to satisfy it.

Example of writing a test
=========================

In a real scenario, you would also need to open a database connection before the test and close it after the test. This provides an example of a real working test from one of the example projects: [EmployeeSpec](http://code.google.com/p/activejdbc/source/browse/trunk/examples/simple-example/src/test/java/activejdbc/examples/simple/EmployeeSpec.java)

Back to [Features](Features)
