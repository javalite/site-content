Title: How to configure your ActiveWeb project

[Introduction](#Introduction)

[Database connections configuration](#Database_connections_configuration)

-   [What is an environment?](#What_is_an_environment_)
-   [How to specify an environment](#How_to_specify_an_environment)
-   [Default environment](#Default_environment)
-   [What is a mode?](#What_is_a_mode_)
-   [Example configuration](#Example_configuration)

Introduction
============

ActiveWeb *does not use* property files, XML, Yaml or any other text files for configuration. The amount of configuration in ActiveWeb project in general is condenced to absolute minimum, and whatever configuration still needs to be done is done in Java code. There are a few advantages of this approach: auto-suggestions by IDE, documentation at finger tips, and some level of comfort from Java compiler.

ActiveWeb uses two application classes for configuration: `app.config.AppControllerConfig` and `app.config.DbConfig`. Both of these classes are initialized from a third application level class called `app.config.AppBootstrap`.

Database connections configuration
==================================

In order to configure database connection, an application needs to provide a class called `app.config.DbConfig`. It is used to configure database connections for various **environments and modes**.

What is an environment?
-----------------------

An ActiveWeb environment is a computer where a project executes. In the process of software development there can be a number of environments where a project gets executed, such as development, continuous integration, QA, staging, production and more. The number of environments for ActiveWeb is custom for every project.

How to specify an environment
-----------------------------

An environment is specified by an environment variable: `ACTIVE_ENV` Every computer where an ActiveWeb project gets executed, needs to have this variable specified. This value is used to determine which DB connections need to be initialized.

Default environment
-------------------

In case an environment variable `ACTIVE_ENV` is not provided, the framework defaults to "development".

What is a mode?
---------------

ActiveWeb defines two modes of operation: "standard", which is also implicit, and "testing". Standard mode is used during regular run of the program, and testing used during the build when tests are executed. ActiveWeb promotes a style of development where one database used for testing, but a different one used under normal execution. When tests are executed, a "test" database is used, and when a project is run in a normal mode, a "development" database is used. Having a separate database for testing ensures safety of data in the development database.

Example configuration
---------------------

~~~~ {.prettyprint}
 1. public class DbConfig extends AbstractDBConfig {
 2.  public void init(AppContext context) {
 3.      environment("development").jndi("jdbc/kitchensink_development");
 4.      environment("development").testing().jdbc("com.mysql.jdbc.Driver", "jdbc:mysql://localhost/kitchensink_development", "root", "****");
 5.      environment("hudson").testing().jdbc("com.mysql.jdbc.Driver", "jdbc:mysql://172.30.64.31/kitchensink_hudson", "root", "****");
 6.      environment("production").jndi("jdbc/kitchensink_production");
 7.  }
 8.}
~~~~

The code above is an example from Kitchensink project. Lets examine it line by line.

-   Line 3: here we provide configuration for a "standard" mode in "development" environment. This DB connection will be used when the application is running under normal conditions in development environment.
-   Line 4: This is a configuration of DB connection for "development" environment, but for "testing" mode. This connection will be used by unit and integration tests during the build.
-   Line 5: This is a configuration of DB connection for "hudson" environment, but for "testing" mode. The "hudson" environment is a computer where this project is built by Hudson - the continuous integration server. Since Hudson computer is fully automated, and this project is not running there in "standard" mode, there is no standard configuration for hudson environment, just one for testing.
-   Line 6: This is configuration similar to one on line 3, but for "production" environment.

---- Configuration of a database connection is just that - configuration. This code only configures a connection, but does not open it. To open a connection, you need to use DBConnectionFilter, for more, see [ControllerFilters](ControllerFilters)

* * * * *
