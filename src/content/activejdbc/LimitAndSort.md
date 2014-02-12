Title: This page explains how to limit and sort (order) resultsets with ActiveJDBC

-   [Introduction](#Introduction)
-   [Limiting Resultsets](#Limiting_Resultsets)
-   [Offsetting start of a page](#Offsetting_start_of_a_page)
-   [Ordering results (putting all together Fluent Interfaces style )](#Ordering_results__putting_all_together_Fluent_Interfaces_style__)
-   [Paginating for the web](#Paginating_for_the_web)

Introduction
============

Often times you do not need an entire resultset from a table. Sometimes, you only need a few records, and sometimes you want to page through a resultset. This style of data usage is usually found in web applications. Examples could be paging through a catalog of products.

Limiting Resultsets
===================

The "finder" methods, such as `find()`, `findAll()` and `where()` return an instance of a `LazyList`. This class has a method called `limit(int)`. This methods will limit a number of results in the resultset when your programs starts to iterate through a list:

~~~~ {.prettyprint}
List<Person> people = People.findAll().limit(20);
~~~~

Offsetting start of a page
==========================

Once you got a first page, you might want to get a next one. This is done with the offset method, found on the same `LazyList` class like so:

~~~~ {.prettyprint}
List<Person> people = People.findAll().limit(40).offset(20);
~~~~

The code snippet above will find all return 40 records, starting with the 41st record, inclusive.

Ordering results (putting all together Fluent Interfaces style )
================================================================

Usually, you would limit, offset and order results in one query:

~~~~ {.prettyprint}
List<Person> people = People.findAll().limit(40).offset(20).orderBy("age asc");
~~~~

Sometimes this style of programming is called [Fluent Interfaces](http://martinfowler.com/bliki/FluentInterface.html) and is credited to Martin Fowler. This style of API is concise, readable and self explanatory.

Paginating for the web
======================

Although `limit`, `offset` and `orderBy` themselves are quite simple and powerful methods, ActiveJDBC also provides a convenience class called `Paginator` especially designed for web applications:

~~~~ {.prettyprint}
Paginator p = new Paginator(Page.class, 10, "description like ?", "%Java%").orderBy("created_at desc");
List<Page> items = p.getPage(1);
List<Page> items = p.getPage(2);
~~~~

The instances of this class are super lightweight and usually attached to a session. An instance of this class can be queried for a current page displayed:

~~~~ {.prettyprint}
int currentPage = paginator.getCurrentPage();
~~~~

and for page count like this:

~~~~ {.prettyprint}
int pageCount = paginator.getPageCount();
~~~~

Using this class in a context of a web application makes it trivial to build paging through resultsets on web pages.

Back to [Features](Features)
