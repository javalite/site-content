Title: How to create new records

-   [Introduction](#Introduction)
-   [Use setter method](#Use_setter_method)
-   [save() and saveIt() methods](#save___and_saveIt___methods)
-   [Method chaining](#Method_chaining)
-   [Batching names an values](#Batching_names_an_values)
-   [Initialize from Map](#Initialize_from_Map)
-   [Initialize from varargs](#Initialize_from_varargs)
-   [Convenience create() and createIt() methods](#Convenience_create___and_createIt___methods)
-   [No setters/getters???](#No_setters_getters___)

Introduction
============

There are many ways to create new records with ActiveJDBC. Let's explore them

Use setter method
=================

~~~~ {.prettyprint}
Person p = new Person();
p.set("first_name", "John");
p.set("last_name", "Doe");
p.set("dob", "1935-12-06");
p.saveIt();
~~~~

This code should be self explanatory. As you can see, ActiveJDBC does not require to have getters and setters. You can write them, if you like, but IMHO, they are nothing but code pollution.

save() and saveIt() methods
===========================

ActiveJDBC class Model provides two methods for saving an entity: `save()` and `saveIt()`. Both methods will involve validations during saving, but in the case of the method save() will silently exit without throwing exceptions. In case validations failed, the instance will have an errors collection attached to it. This is very useful in the context of a web application. Here is an example:

~~~~ {.prettyprint}
Person person = new Person();
person.fromMap(requestParams);
if(person.save()) //<<<===  will not throw exception and will not save in case there are validation errors. 
    //show page success
else{
     request.setAttribute("errors", person.errors());
     //show errors page, or same page so that user can correct errors.
}
~~~~

More on validations , see this page: [Validations](Validations)

The `saveIt()` method will throw an exception in case there was a validation problem. The `save()` method makes more sense in the context of a web application, whereas `saveIt()` is more useful in a non-web app situations - batch inserts, tests, etc.

Method chaining
===============

The `set(name, value)` method returns reference to the same model object, which makes it possible to string method calls like this:

~~~~ {.prettyprint}
Person p = new Person();
p.set("name", "John").set("last_name", "Doe").set("dob", "1935-12-06").saveIt();
~~~~

..or make it shorter:

~~~~ {.prettyprint}
new Person().set("first_name", "Marilyn").set("last_name", "Monroe").set("dob", "1935-12-06").saveIt();
~~~~

Batching names an values
========================

There is a way to batch names and values into arrays:

~~~~ {.prettyprint}
String[] names = {"first_name", "last_name", "dob"};
Object[] values = {"John", "Doe", dob}
new Person().set(names, values).saveIt();
~~~~

Of course, the names is a String array and the two arrays need to be the same size.

Initialize from Map
===================

This method of creation is useful for web applications if request parameters are posted from a form an available in a Map instance:

~~~~ {.prettyprint}
Map values = ... initialize map
Person p = new Person();
p.fromMap(values);
p.saveIt();
~~~~

Initialize from varargs
=======================

Model also provides another convenience method for entity initialization, the set methods that accepts a varargs:

~~~~ {.prettyprint}
Person p = new Person();
p.set("first_name", "Sam", "last_name", "Margulis", "dob", "2001-01-07");
p.saveIt();
~~~~

The argument list is a string of names and corresponding values where names are interleaved with values. This makes it easy to write allows for easy reading (if you just read it aloud, it will sound as an English sentence).

Convenience create() and createIt() methods
===========================================

The class Model also provides two convenience methods for creation of records: `create()` and `createIt()`. There is a semantic difference between these two methods, and it is the same as between `save()` and `saveIt()` methods, except in this case, ActiveJDBC creates and attempts to save an object in one step.

~~~~ {.prettyprint}
Person p = Person.create("first_name", "Sam", "last_name", "Margulis", "dob", "2001-01-07");
p.saveIt();
~~~~

or:

~~~~ {.prettyprint}
Person p = Person.createIt("first_name", "Sam", "last_name", "Margulis", "dob", "2001-01-07");
~~~~

The `create()` and `createIt()` method accepts a list of arguments, where names are interleaved with values. This is similar to the varargs setter described above, but also includes semantics of the `save()` and `saveIt()` methods.

No setters/getters???
=====================

Well, no. ActiveJDBC will not provide these, and it will not generate them either. However, you can have them if you like:

~~~~ {.prettyprint}

public class Person extends Model{
   public void setFirstName(String firstName){
      set("first_name", firstName);
   }
}
~~~~

Same goes for getters. If you are starting out with ActiveJDBC, I suggest you do not write setters and getters, but rather use the provided methods. After a day or two you will be surprised you ever wrote them before.

Back to [Features](Features)
