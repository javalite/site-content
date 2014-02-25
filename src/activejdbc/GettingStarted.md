Title: This page explains how to get started with ActiveJDBC

[Pre-requisite](#Pre_requisite)

[Create a standard Maven project structure](#Create_a_standard_Maven_project_structure)

[Create a table](#Create_a_table)

[Maven configuration](#Maven_configuration)

-   [Add ActiveJDBC dependency](#Add_ActiveJDBC_dependency)
-   [Add ActiveJDBC Instrumentation plugin](#Add_ActiveJDBC_Instrumentation_plugin)

[Write a model](#Write_a_model)

[Open a connection](#Open_a_connection)

[Create a new record](#Create_a_new_record)

[Finding a single record](#Finding_a_single_record)

[Finding some records](#Finding_some_records)

[Updating a record](#Updating_a_record)

[Deleting a record](#Deleting_a_record)

[Deleting all records](#Deleting_all_records)

[Selecting all records](#Selecting_all_records)

[Sample project using Ant](#Sample_project_using_Ant)

[Conclusion](#Conclusion)

Although ActiveJDBC has advanced features, simple things are very easy. This page shows simplest cases of DB access with ActiveJDBC.

Pre-requisite
=============

-   Java (obvious)
-   Maven

Create a standard Maven project structure
=========================================

While ActiveJDBC does not have to be used with Maven, this example (as well as ActiveJDBC itself) was built with Maven. To see the project itself, you can check it out from SVN:

~~~~ {.prettyprint}
svn co https://activejdbc.googlecode.com/svn/trunk/examples/simple-example/
~~~~

The best way is to check out this project and start playing with it. Take a look at the `run.sh` script at the root of this example project.

Create a table
==============

This is an SQL statement to create a table (MySQL used for this example):

~~~~ {.prettyprint}
CREATE TABLE employees (
      id  int(11) DEFAULT NULL auto_increment PRIMARY KEY,
      first_name VARCHAR(56),
      last_name VARCHAR(56)
  );
~~~~

Maven configuration
===================

Add ActiveJDBC dependency
-------------------------

~~~~ {.prettyprint}

            <dependency>
                <groupId>org.javalite</groupId>
                <artifactId>activejdbc</artifactId>
                <version>1.4.1</version>
            </dependency>
~~~~

Add ActiveJDBC Instrumentation plugin
-------------------------------------

In the plugins section of the POM, add this:

~~~~ {.prettyprint}
            <plugin>
                <groupId>org.javalite</groupId>
                <artifactId>activejdbc-instrumentation</artifactId>
                <version>1.4.1</version>
                <executions>
                    <execution>
                        <phase>process-classes</phase>
                        <goals>
                            <goal>instrument</goal>
                        </goals>
                    </execution>
                </executions>
            </plugin>
~~~~

Write a model
=============

This is the easiest thing - writing a simple model is usually done with one line of code:

~~~~ {.prettyprint}
import activejdbc.Model;

public class Employee extends Model {}
~~~~

Please, note that the name of a table is "employees" - plural, and the name of a model is "Employee" - singular. ActiveJDBC uses inflections of English language to do conversion of plural and singular forms of words. This of course can be overridden by @Table annotation.

Open a connection
=================

~~~~ {.prettyprint}
Base.open("com.mysql.jdbc.Driver", "jdbc:mysql://localhost/test", "user1", "xxxxx");
~~~~

Replace the values for the ones that make sense for your environment. Base is a utility class that allows to perform some basic (hence the name) JDBC operations, one of them is opening a connection. The connection object is then attached to the current thread, and can be consumed by any ActiveJDBC API.

Create a new record
===================

~~~~ {.prettyprint}
Employee e = new Employee();
e.set("first_name", "John");
e.set("last_name", "Doe");
e.saveIt();
~~~~

I hope this is self-explanatory. ActiveJDBC models somewhat behave like maps. There are no setters or getters. You can still write them if you like.

Finding a single record
=======================

~~~~ {.prettyprint}
Employee e = Employee.findFirst("first_name = ?", "John");
~~~~

This line will find an instance of Employee (conditionally), if one exists, or null if one does not exist.

Finding some records
====================

~~~~ {.prettyprint}
List<Employee> employees = Employee.where("first_name = ?", "John");
~~~~

Updating a record
=================

This snippet should also be self-explanatory:

~~~~ {.prettyprint}
Employee e = Employee.findFirst("first_name = ?", "John");
e.set("last_name", "Steinbeck").saveIt();
~~~~

Deleting a record
=================

~~~~ {.prettyprint}
Employee e = Employee.findFirst("first_name = ?", "John");
e.delete();
~~~~

Deleting all records
====================

~~~~ {.prettyprint}
Employee.deleteAll();
~~~~

Selecting all records
=====================

~~~~ {.prettyprint}
List<Employee> employees = Employee.findAll();
~~~~

Sample project using Ant
========================

If you are not using Maven, you can use Ant. Example of exactly the same project with all necessary dependencies can be found here: https://activejdbc.googlecode.com/svn/trunk/examples/ant-example/ Please see README file there.

Conclusion
==========

I hope this quick introduction provides enough information to get started. It also shows how simple ActiveJDBC APIs are. The project has many advanced features, such as automatic recognition of associations, caching, validations, polymorphic associations, etc. The examples on this page can be found and executed in the included Maven project here: https://activejdbc.googlecode.com/svn/trunk/examples/simple-example/

Read the [Features](Features) section for a more in depth information on the framework.

Enjoy
