<div class="page-header">
   <h1>ActiveJDBC & Kotlin</h1>
</div>


## How to setup it
With the same manner it's done with the Java ActiveJDBC layer, you should setup both dependency and instrumentation like explained [there](/activejdbc#getting-the-latest-version).

Then you have to add the following dependency to get the Kotlin layer available.

~~~~ {.xml}
<dependency>
    <groupId>org.javalite</groupId>
    <artifactId>activejdbc-kt</artifactId>
    <version>LATEST_VERSION</version>
</dependency>
~~~~



## How to use it ?
Unlike the Java layer, the Kotlin layer offers two ways of setting up a model, both are equivalent.


#### First usage (close to Java)
First define an entity

~~~~ {.java}
import org.javalite.activejdbc.Model;
import org.javalite.activejdbc.CompanionModel

open class Person():Model() {
    companion object:CompanionModel<Person>(Person::class)
}
~~~~



Then use it (like in Java)

~~~~ {.java}
val person:Person = Person.findById(1)
~~~~




#### Second usage (with English inflections)

The first class is only for the entity itself

~~~~ {.java}
import org.javalite.activejdbc.Model;

open class Person():Model()
~~~~



The second class concerns the table itself and its name matches English inflection (Person => People)

~~~~ {.java}
import org.javalite.activejdbc.CompanionModel

open class People {
    companion object:CompanionModel<Person>(Person::class)
}



Then use it like in Java

~~~~ {.java}
val person:Person = People.findById(1L)
~~~~
