Title: ActiveJDBC model inheritance

-   [WARNING Inheritance implementation has problems, it is not advised to use it at this time](#WARNING_Inheritance_implementation_has_problems__it_is_not_advised_to_use_it_at_this_time)
-   [Introduction](#Introduction)
-   [Inheritance diagram](#Inheritance_diagram)
-   [Inheritance usage](#Inheritance_usage)

WARNING Inheritance implementation has problems, it is not advised to use it at this time
=========================================================================================

One-sentence summary of this page.

Introduction
============

Currently ActiveJDBC does not support a feature called [Single Table Inheritance](http://en.wikipedia.org/wiki/Single_Table_Inheritance). However it does support just inheritance

Inheritance diagram
===================

Consider this diagram:

https://activejdbc.googlecode.com/svn/trunk/doc/inheritance\_umlet\_class\_diagram.png

Inheritance usage
=================

While there are total of 7 classes, only those classes that have green background are associated with tables. At the same time, a common functionality can be inherited from one class to another. Abstract classes are marked with (A).

In this diagram there are only three tables: MEALS, CAKES and SWORDS. This means that models that are not green are not backed by a table and therefore cannot be used directly.

All functionality declared in models Dessert and Pastry can be used in a model Cake. Same goes for Weapon and Sword. However, Cheese, although can exist in code, is a dud.
