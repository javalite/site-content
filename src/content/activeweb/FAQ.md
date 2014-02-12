Title: ActiveWeb FAQ

AvtiveWeb FAQ
=============

-   [Why FreeMarker?](#Why__FreeMarker_)
-   [Why Guice?](#Why_Guice_)
-   [Why ActiveJDBC?](#Why_ActiveJDBC_)
-   [Why Maven?](#Why_Maven_)
-   [Why app.controllers?](#Why_app_controllers_)
-   [Why Carbon 5?](#Why_Carbon_5_)

Why FreeMarker?
---------------

Because FreeMarker is a great web templating engine for Java. It is mature, has easy to understand syntax, makes it trivial to extend with tags, and allows for end to end testing of a web application with `integrateViews()` method in controller tests, something JSPs cannot do due to their dependency on containers. FreeMarker templates are independent of JSTL and JSP specification, and will work universally well in any container.

Why Guice?
----------

Because Google Guice feels right when it comes to dependency injection. While Spring rules the world, we feel that binding components should be cone in code, and not in XML or annotations. Besides, Google Guice has excellent APIs, which allowed us to integrate it into the framework in record time. It also lightweight and does not bring many transitive dependencies into a project.

Why ActiveJDBC?
---------------

Because ActiveJDBC was the firsts project in an effort to make Java development productive, fast and fun. ActiveWeb is second such significant project. Besides, there are a few web-friendly pieces baked directly into ActiveJDBC, such as internationalization, validations, JSON and XML generation. However, ActiveWeb does not have a dependency on ActiveJDBC, and allows to use any database access technology available to Java developers.

Why Maven?
----------

Because Maven has a typical structure, is popular with developers, has a huge inventory of plugins, vast community and plethora of information on the Internet

Why app.controllers?
--------------------

ActiveWeb requires controllers to be in the package `app/controllers`. Please read this: [Location of controllers](http://code.google.com/p/activeweb/wiki/StructureOfActiveWebProject#Location_of_controllers)

Why Carbon 5?
-------------

ActiveWeb suggests using [Carbon 5 Migration Tool](http://code.google.com/p/c5-db-migration/) because developers of ActiveWeb have been enjoying it for significant time. Carbon 5 is a Maven plugin, and will run migrations against configured databases automatically during the build. This makes it super easy to promote migrations to other members of the team. All you have to do is create a migration, run the build locally (to migrate your local databases), and then commit your changes. When another developer updates a project with latest changes, he/she will get a new migration(or a few), after running the build, this developer too will have local databases migrated.

Another plus is absence of a special "abstraction" language as in Ruby on Rails. Migrations are written in raw SQL, which is a huge plus. This allows for any non-standard SQL for your specific database, and also helps DBAs get involved if needed.
