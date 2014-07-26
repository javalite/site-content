Getting started with ActiveWeb| <a href="/activeweb">ActiveWeb</a>,Getting started with ActiveWeb

# Getting started with ActiveWeb

<div id="toc"></div>

This simple ActiveWeb project available for download demonstrates main principles of the framework.
It is a CRUD application, which can list/add/view books. It also shows how to write model and controller specs (tests),
and how to perform dependency injection.

## Pre-requisites

* Java :)
* Maven 2/3
* MySQL (only required for this startup program, not a real dependency for ActiveWeb)


## Create DB  schemas (in MySQL):

* `simple_development`
* `simple_test`

## Get example app

Clone the app: [ActiveWeb simple example](https://github.com/javalite/activeweb-simple/)

## Code modifications

Modify JDBC connection parameters in:

* class `app.config.DbConfig`</li>
* pom.xml


## Start container

* Execute:

~~~~ {.prettyprint}
mvn jetty:run
~~~~

* Navigate with browser: [http://localhost:8080/activeweb-simple/](http://localhost:8080/activeweb-simple/)