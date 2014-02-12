Title: How to use one to many associations with ActiveJDBC

-   [Introduction](#Introduction)
-   [Database schema](#Database_schema)
-   [Models](#Models)
-   [Adding children](#Adding_children)
-   [How to get children](#How_to_get_children)
-   [Conditional selection of related objects](#Conditional_selection_of_related_objects)
-   [How to get Parent](#How_to_get_Parent)
-   [Deleting Parent](#Deleting_Parent)
-   [Override Conventions](#Override_Conventions)
-   [Foreign Key](#Foreign_Key)
-   [Conclusion](#Conclusion)

Introduction
============

One to many associations are pretty common in relational schemas. Examples are: university has students, library has books, etc. There are two sides to a one to many association, the "parent" **has a** "child" and a "child" **belongs to** "parent".

ActiveJDBC supports this type of a relationship in two ways: 1. Inferred and 2. Overridden

Database schema
===============

This is a database schema (for MySQL):

~~~~ {.prettyprint}
CREATE TABLE users (
id  int(11) DEFAULT NULL auto_increment PRIMARY KEY, 
first_name VARCHAR(56), 
last_name VARCHAR(56), 
email VARCHAR(56));


CREATE TABLE addresses (
id  int(11) DEFAULT NULL auto_increment PRIMARY KEY, 
address1 VARCHAR(56), 
address2 VARCHAR(56), 
city VARCHAR(56), 
state VARCHAR(56), 
zip VARCHAR(56), 
user_id int(11));
~~~~

Models
======

User:

~~~~ {.prettyprint}
public class User extends Model {}
~~~~

Address:

~~~~ {.prettyprint}
public class Address extends Model {}
~~~~

As you can see from the schema, the table ADDRESSES, has a column called `user_id`. Just because table ADDRESSES has this column, ActiveJDBC assumes that there is a one to many relationship here, and makes special arrangements. By doing so, the framework (internally) creates two associations (User has many Address(es) and Address belongs to User).

The design of ActiveJDBC tries to make usage of the APIs as clean and concise as possible.

Adding children
===============

Adding children is the same as in any other association:

~~~~ {.prettyprint}
user.add(address);
~~~~

As in other associations, the requirement is that the parent record in DB must exist already. This way, a child model is immediately saved to its appropriate table.

* * * * *

ActiveJDBC is a pass-through model. It does not retain references to child models.

* * * * *

In case the 'user' model is new (has not been save yet), the method `user.add(child)` will throw an exception.

How to get children
===================

Nothing can be simpler:

~~~~ {.prettyprint}
List<Address> addresses = user.getAll(Address.class);
~~~~

Here the Address.class needs to be passed in because a model User might have many other relationships with models other than Address.class.

Conditional selection of related objects
========================================

Sometimes you need to collect children of a model based on a selection criteria: In such cases, use the `get(type)` method:

~~~~ {.prettyprint}
List<Address> shippingAddresses = customer.get(Address.class, "address_type = ?", "shipping");
~~~~

Of course it is expected that the table ADDRESSES will have a column "address\_type".

How to get Parent
=================

~~~~ {.prettyprint}
User user = address.parent(User.class);
~~~~

Here, we have to pass a `User.class` to indicate which parent type we want. This is because a model could have multiple parents (belong to more than one parent)

Deleting Parent
===============

A simple way to delete a parent is:

~~~~ {.prettyprint}
User u = address.parent(User.class);

u.delete();
~~~~

If you have a referential integrity in your DB and table ADDRESSES has records associated with this user, then you will get an exception from DB. If you do not have child records, this user will be deleted. If you have records in the ADDRESSES table and no referential integrity constraint, the user will be deleted and you will have orphan records in the ADDRESSES table. In order to delete a user and all it's child records, execute this method:

~~~~ {.prettyprint}
u.deleteCascade();
~~~~

This method will walk over all parent/child relationships and delete the user and all child records associated with it.

There is also a convenience methods that will do the same:

~~~~ {.prettyprint}
u.delete(true);//true for cascade.
~~~~

Override Conventions
====================

In cases where a surrogate foreign key is already present and has a name that does not follow the ActiveJDBC conventions, you could easily override it like this:

~~~~ {.prettyprint}
@BelongsTo(parent = User.class, foreignKeyName = "usr_id")
public class Address extends Model {}
~~~~

The @BelongsTo annotation will ensure that API on both ends will work. ActiveJDBC does not have annotation @HasMany, since I believe this would be redundant.

In cases a model belongs to many parents, you can use this annotation:

~~~~ {.prettyprint}
@BelongsToParents({ 
@BelongsTo(foreignKeyName="key_id",parent=Keyboard.class), 
@BelongsTo(foreignKeyName="mother_id",parent=Motherboard.class) 
}) 
~~~~

As usual though, you only need it if names of foreign keys do not conform to the conventions.

Foreign Key
===========

The Foreign Key in the ADDRESSES table does not have to be a real Foreign Key constraint. ActiveJDBC(much like ActiveRecord) does not check for it's presence. As long as there is a column named according to this convention, ActiveJDBC assumes that there is a relationship. It does not hurt to have the actual constraint in the DB if you are using other means of accessing data.

Conclusion
==========

ActibeJDBC makes it very easy to setup associations. If you work with a new schema, you just need to follow conventions (by including a logical foreign key into a child table). In cases when you already have an existing schema, ActiveJDBC allows to override conventions with simple annotations
