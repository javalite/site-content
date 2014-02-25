Title: Usage of optimistic locking

-   [Introduction](#Introduction)
-   [Creation of a new record](#Creation_of_a_new_record)
-   [Updating a record](#Updating_a_record)
-   [When collisions happen](#When_collisions_happen)
-   [How optimistic locking is engaged](#How_optimistic_locking_is_engaged)
-   [Conclusion](#Conclusion)

Introduction
============

The idea of optimistic locking is described here: [http://en.wikipedia.org/wiki/Optimistic\_concurrency\_control](http://en.wikipedia.org/wiki/Optimistic_concurrency_control)

ActiveJDBC provides support for optimistic concurrency via a simple convention: A database table needs to provide a column named `record_version` with a type that is capable to store non-decimal types, such as LONG for MySQL, NUMBER for Oracle, etc.

Creation of a new record
========================

Let's say you have a model:

~~~~ {.prettyprint}
public class Profile extends Model{}
~~~~

which backs a table PROFILES:

~~~~ {.prettyprint}
+----+--------------+------------------+
| id | profile_type | record_version   |
+----+--------------+------------------+
~~~~

When you create a new record:

~~~~ {.prettyprint}
Profile.createIt("profile_type", "home");
~~~~

a new record is inserted into the table:

~~~~ {.prettyprint}
+----+--------------+------------------+
| id | profile_type | record_version   |
+----+--------------+------------------+
| 1  |   home       |     1            |
+----+--------------+------------------+
~~~~

The value 1 in the `record_version` column signifies that this record has not been updated yet.

Updating a record
=================

When a record is updated, the value of column `record_version` is incremented by one:

~~~~ {.prettyprint}
Profile p = Profile.findById(1);
p.set("profile_type", "work");
p.saveIt();
~~~~

The resulting record in the database will look like this:

~~~~ {.prettyprint}
+----+--------------+------------------+
| id | profile_type | record_version   |
+----+--------------+------------------+
| 1  |   work       |     2            |
+----+--------------+------------------+
~~~~

As you can see, ActiveJDBC tracks versions of the same record.

When collisions happen
======================

Sometimes you might have code that reads the same record from a table in order to be updated. In those cases, the first update succeeds, but the second does not. Let's examine this situation:

~~~~ {.prettyprint}
1: Profile p1 = Profile.findById(1);
2: Profile p2 = Profile.findById(1);
3: 
4: p1.set("profile_type", "hotel");
5: p1.saveIt();
6: 
7: p2.set("profile_type", "vacation");
8: p2.saveIt(); //<<<========= This will throw a StaleModelException
~~~~

In the code snippet above, at lines 1 and 2, the same record is loaded into models. Then, at line 5, the first one is updated. This will increment the version of the record to 3, and make the model p2 stale. Henceforth, when you try to save the model p2, you will get an exception. The content of a record in the table at this point will be:

~~~~ {.prettyprint}
+----+--------------+------------------+
| id | profile_type | record_version   |
+----+--------------+------------------+
| 1  |   hotel      |     3            |
+----+--------------+------------------+
~~~~

Here is the output of the StaleModelException:

~~~~ {.prettyprint}
activejdbc.StaleModelException: Failed to update record for model 'class com.acme.Profile', with id = 1 and record_version = 2. Either this record does not exist anymore, or has been updated to have another record_version.
~~~~

This message provides enough detail to understand what happened.

How optimistic locking is engaged
=================================

The rule is very simple, ActiveJDBC finds `record_version` column and dynamically configures itself to handle optimistic locking. This means that if this column is present, optimistic locking will be engaged, if not present, it will not be engaged. If you did not have this column, and later added it, you need to restart the system, because ActiveJDBC scans database schema at the start.

Conversely, if you want to turn it off, drop column `record_version` and restart, this will turn it off.

Conclusion
==========

Application developers using optimistic locking should be aware of this exception (even though it is a RuntimeException) and build controls into their code to intercept and handle them appropriately.

Back to [Features](Features)
