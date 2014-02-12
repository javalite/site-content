Title: ActiveJDBC database connection management

-   [Introduction](#Introduction)
-   [Thread connection propagation](#Thread_connection_propagation)
-   [Database names](#Database_names)
-   [DB and Base](#DB_and_Base)
-   [Models associated with multiple databases](#Models_associated_with_multiple_databases)
-   [Multiple database example](#Multiple_database_example)
-   [Database connection pools](#Database_connection_pools)
-   [Specifying DB Schema](#Specifying_DB_Schema)

Introduction
============

ActiveJDBC provides two classes for connection management: [Base.java](http://code.google.com/p/activejdbc/source/browse/trunk/activejdbc/src/main/java/activejdbc/Base.java) and [DB.java](http://code.google.com/p/activejdbc/source/browse/trunk/activejdbc/src/main/java/activejdbc/DB.java).

Thread connection propagation
=============================

ActiveJDBC models when operate, utilize a connection found on a current thread. This connection is put on the local thread by Base or DB class before any DB operation. approach allows for more concise API where there is no need for DB Session or persistent managers as in other Java ORMs.

Here is a simple program:

~~~~ {.prettyprint}
1. public static void main(String[] args) {
2.    Base.open("com.mysql.jdbc.Driver", "jdbc:mysql://localhost/test", "the_user", "the_password");
3.    Employee.findAll().dump();
4.    Base.close();
5. }
~~~~

On line 2, class Base will open a new connection and attach it to the current thread. This connection will also be marked with name "default".

On line 3, connection is looked up from the thread and used by the model(and result dumped to STDIO)

On line 4, connection is closed and cleared from thread.

Database names
==============

ActiveJDBC has a concept of a "database". A database is the same as a "database" in normal understanding. However, an application can be connected to multiple databases at the same time. In this case. ActiveJDBC allows for assigning different logical names to different databases. For example, one might have an Oracle database with accounting data, and a MySQL database with inventory control data. In this case, you might want to have an "accounting" database and an "inventory" database as logical names assigned to these databases.

DB and Base
===========

Opening and closing connections is done with classes Base or DB. The DB class is used in cases where you have more than one database in the system, such as "accounting" and "inventory".

Example:

~~~~ {.prettyprint}
new DB("inventory").open("com.mysql.jdbc.Driver", "jdbc:mysql://localhost/test", "root", "p@ssw0rd");
~~~~

In this code example, there is a database connection opened, and attached to a local thread under name "inventory".

The classes Base and DB mirror one another, having exactly the same APIs, except:

-   All methods on DB are instance methods, while all methods on class Base are static ones.
-   class DB constructor accepts a DB name, while Base always operates with DB name: "default"

This means that these lines are equivalent:

~~~~ {.prettyprint}
new DB("default").open("com.mysql.jdbc.Driver", "jdbc:mysql://localhost/test", "root", "p@ssw0rd");
~~~~

and:

~~~~ {.prettyprint}
Base.open("com.mysql.jdbc.Driver", "jdbc:mysql://localhost/test", "root", "p@ssw0rd");
~~~~

**Rule:**

* * * * *

Use Base if you have only one database in the system, otherwise, use DB.

* * * * *

Models associated with multiple databases
=========================================

ActiveJDBC allows to have a mix of models in the application representing tables from different databases. By default a model belongs to a database "default", but an association of a model to a database can be overriden with annotation @DbName:

~~~~ {.prettyprint}
@DbName("corporation")
public class Employee extends Model {}
~~~~

**Recommendation:** put most models into "default" database, since this will require you to add this annotation to fewer models

Multiple database example
=========================

See sources here: [multimple-db-example](http://code.google.com/p/activejdbc/source/browse/#svn%2Ftrunk%2Fexamples%2Fmultimple-db-example).

For this example, we will have two models, one representing a table in Oracle database, while the other in MySQL

The two models are defined like this:

~~~~ {.prettyprint}
@DbName("corporation")
public class Employee extends Model {}
~~~~

and:

~~~~ {.prettyprint}
@DbName("university")
public class Student  extends Model {}
~~~~

and the main class looks like this:

~~~~ {.prettyprint}
public class Main {
    public static void main(String[] args) {
        new DB("corporation").open("com.mysql.jdbc.Driver", "jdbc:mysql://localhost/test", "root", "p@ssw0rd");
        new DB("university").open("oracle.jdbc.driver.OracleDriver", "jdbc:oracle:thin:@localhost:1521:xe", "activejdbc", "activejdbc");

        Employee.deleteAll();
        Student.deleteAll();

        Employee.createIt("first_name", "John", "last_name", "Doe");
        Employee.createIt("first_name", "Jane", "last_name", "Smith");

        Student.createIt("first_name", "Mike", "last_name", "Myers");
        Student.createIt("first_name", "Steven", "last_name", "Spielberg");

        System.out.println("*** Employees ***");
        Employee.findAll().dump();
        System.out.println("*** Students ***");
        Student.findAll().dump();

        new DB("corporation").close();
        new DB("university").close();
    }
}
~~~~

At the beginning of the application, the two named connections are opened, then we proceed to use the models associated with these connections. At the end of the program, the two named connections are closed. The class DB is super lightweight, and it is OK not to retain a reference to it, but rather to create a new instance each time. If you do want to retain a reference, there is no harm done though.

Database connection pools
=========================

ActiveJDBC does not maintain connection pools and does not integrate with any pools. Instead, it provides a few DB.open() and Base.open() methods to open connections. If a version of methods used that takes standard JDBC parameters, then no pool is used this is only a convenience method to open a brand new connection, such as:

~~~~ {.prettyprint}
Base.open("com.mysql.jdbc.Driver", "jdbc:mysql://localhost/test", "root", "p@ssw0rd");
~~~~

If however, this call is used:

~~~~ {.prettyprint}
Base.open("jdbc/test");
~~~~

it will internally use JDNI to lookup a connection. The connection looked up will correspond to configuration of a JNDI resource. Usually if this is called from within a container, the name points to a pooled JNDI DataSource, and as a result represents a connection coming from a pool.

If you want to work directly with some connection pool, you can do so by feeding a datasource to Base/DB class:

~~~~ {.prettyprint}
new DB("default").open(datasourceInstance);
~~~~

Specifying DB Schema
====================

In most cases you do not need to worry about this. However, for Oracle and PostgreSQL, some schema elements may leak into your account if a schema is not specified. If you find that your model for instance has an attribute(column) that is not part of this table description, it means that the DB mixed in this column from another table (public schema) that has the same name as your table.

In a case like this, you need to specify a schema for a database. If you use a default database ( see "Multiple database example" above, and you do not use DbName() annotation), simply add this system property:

~~~~ {.prettyprint}
-Dactivejdbc.default.schema=myschema
~~~~

where "default" is a name of a database and "myschema" is a name of your schema in Oracle or PostgreSQL.

In case you do use multiple connections to different databases and you use DbName annotation, replace "default" to your DB name. For example:

~~~~ {.prettyprint}
@DbName("university")
public class Student extends Model{}
~~~~

then you can add a property like this:

~~~~ {.prettyprint}
-Dactivejdbc.university.schema=myschema
~~~~

Back to [Features](Features)
