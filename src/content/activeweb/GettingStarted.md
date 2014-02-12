Title: Three steps to get started

Introduction
============

The simple ActiveWeb project available for download here demonstrates some main principles of the framework. This is a CRUD application, which can list/add/view a book. It also shows how to write model and controller specs, and how to perform dependency injection.

Pre-requisites
==============

-   Java :)
-   Maven 2/3
-   MySQL (only required for this startup program, not a real dependency for ActiveWeb)

Create two empty schemas in MySQL DB:

-   simple\_development
-   simple\_test

Download example app
====================

Getting started is easy, download this simple application: [Simlpe ActiveWeb Project](https://activeweb.googlecode.com/svn/trunk/examples/activeweb-example-zip/)

Code modifications
==================

Modify JDBC connection parameters in:

-   class `app.config.DbConfig`
-   pom.xml

Run the application
===================

~~~~ {.prettyprint}
mvn jetty:run
~~~~

Navigate with browser
=====================

Hit this URL: [http://localhost:8080/activeweb-simple/](http://localhost:8080/activeweb-simple/)
